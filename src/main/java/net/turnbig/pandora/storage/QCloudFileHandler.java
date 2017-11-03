package net.turnbig.pandora.storage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.UpdateFileRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;

import net.turnbig.pandora.mapper.JsonMapper;

/**
 *
 * @author Woo Cubic
 * @date   2017-05-19 10:00:46
 */
public class QCloudFileHandler extends CloudFileHandler implements InitializingBean {

	@Autowired
	QCloudProperties properties;

	COSClient COS; // client

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		ClientConfig config = new ClientConfig();
		config.setRegion("sh");
		Credentials credential = new Credentials(properties.getAppId(), properties.getSecretId(),
				properties.getSecretKey());
		COS = new COSClient(config, credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#upload(java.io.File, java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(File file, String name, String bucket, FileMeta meta) {
		try {
			return this.upload(FileUtils.readFileToByteArray(file), name, bucket, meta);
		} catch (IOException e) {
			throw new RuntimeException("Could not read file content from : " + file.getAbsolutePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#upload(byte[], java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(byte[] content, String targetFilePath, String bucket, FileMeta meta) {
		if (!targetFilePath.startsWith("/")) {
			targetFilePath = "/" + targetFilePath;
		}
		
		UploadFileRequest request = new UploadFileRequest(bucket, targetFilePath, content);
		request.setInsertOnly(InsertOnly.NO_OVER_WRITE);
		request.setEnableShaDigest(false);
		String response = COS.uploadFile(request);
		// {"code":-1,"message":"1.jpg is not cos file path! Tips: make sure not ends with /"}
		String reason = "Failed to upload file to COS";

		try {
			JsonNode result = JsonMapper.nonDefaultMapper().getMapper().readTree(response);
			if (result.get("code") != null) {
				Integer code = result.get("code").asInt();
				if (code == 0) { // success
					// update meta
					UpdateFileRequest updateFileMetaRequest = new UpdateFileRequest(bucket, targetFilePath);
					if (StringUtils.isNotBlank(meta.getFileName())) {
						updateFileMetaRequest.setXCosMeta("x-cos-meta-filename", meta.getFileName());
					}
					if (StringUtils.isNotBlank(meta.getContentType())) {
						updateFileMetaRequest.setContentType(meta.getContentType());
					}
					if (StringUtils.isNotBlank(meta.getContentEncoding())) {
						updateFileMetaRequest.setContentEncoding(meta.getContentEncoding());
					}

					COS.updateFile(updateFileMetaRequest);
					return result.get("data").get("resource_path").asText();
				} else {
					reason = result.get("message").toString();
				}
			}
		} catch (IOException e) {
			// should not happen?
			logger.warn("", e);
		}

		logger.warn("Failed to upload file to COS, response is: {}", response);
		throw new RuntimeException(reason);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#getUrl(java.lang.String)
	 */
	@Override
	public String getUrl(String key) {
		return null;
	}

	public void setProperties(QCloudProperties properties) {
		this.properties = properties;
	}

	@ConfigurationProperties(prefix = "qcloud.cos")
	public static class QCloudProperties {
		Long appId;
		String secretId;
		String secretKey;
		String region;

		public Long getAppId() {
			return appId;
		}

		public String getSecretId() {
			return secretId;
		}

		public String getSecretKey() {
			return secretKey;
		}

		public String getRegion() {
			return region;
		}

		public void setAppId(Long appId) {
			this.appId = appId;
		}

		public void setSecretId(String secretId) {
			this.secretId = secretId;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

		public void setRegion(String region) {
			this.region = region;
		}

	}

}
