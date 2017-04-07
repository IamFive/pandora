/**
 * @(#)Qiniu.java 2016年3月23日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.storage;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * @author Woo Cupid
 * @date 2016年3月23日
 * @version $Revision$
 */
public class QiniuFileHandler extends CloudFileHandler {

	private static final Logger logger = LoggerFactory.getLogger(QiniuFileHandler.class);

	// 设置好账号的ACCESS_KEY和SECRET_KEY
	// String ACCESS_KEY = "7hQGZsEjxagQvq80oldGO2k3Zj1dUghibpbBSXTl";
	// String SECRET_KEY = "Go1IPp3B4BJuTHOWYqL_cqrr5UmwBoo1FR6zSarf";
	// String bucketname = "etickets";
	// String key = "my-java.png";
	// String FilePath = "/.../...";
	// Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	// UploadManager uploadManager = new UploadManager();

	@Value("${qiniu.ak}")
	String ak;

	@Value("${qiniu.sk}")
	String sk;

	@Value("${qiniu.cdn}")
	String cdn;

	Auth auth;
	UploadManager uploader;

	@PostConstruct
	public void init() {
		auth = Auth.create(ak, sk);
		uploader = new UploadManager(null);
	}

	// 简单上传，使用默认策略，只需要设置上传的空间名就可以了
	public String getUploadToken(String bucket) {
		return auth.uploadToken(bucket);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woo.whisper.storage.FileHandler#upload(byte[], java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(File file, String name, String bucket) {
		try {
			Response res = uploader.put(file.getAbsolutePath(), name, getUploadToken(bucket));
			if (res.isOK()) {
				StringMap result = res.jsonToMap();
				return result.get("key").toString();
			} else {
				logger.error("Failed to upload file to Qiniu Cloud, response is {}", res);
				return null;
			}
		} catch (QiniuException e) {
			logger.error("Failed to upload file to Qiniu Cloud", e);
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woo.whisper.storage.CloudFileHandler#upload(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(byte[] content, String name, String bucket) {
		try {
			Response res = uploader.put(content, name, getUploadToken(bucket));
			if (res.isOK()) {
				StringMap result = res.jsonToMap();
				return result.get("key").toString();
			} else {
				logger.error("Failed to upload file to Qiniu Cloud, response is {}", res);
				return null;
			}
		} catch (QiniuException e) {
			logger.error("Failed to upload file to Qiniu Cloud", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param url		http://bucketdomain/key
	 * @param bucket
	 * @return
	 */
	public String getRequireTokenUrl(String url) {
		String downloadUrl = auth.privateDownloadUrl(url, 3600);
		return downloadUrl;
	}

	/**
	 * 获取访问url
	 * @param key
	 * @return
	 */
	public String getUrl(String key) {
		String url = cdn + key;
		return url;
	}

	public static void main(String args[]) throws IOException {
		// new QiniuFileHandler().upload();
	}
}
