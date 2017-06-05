/**
 * @(#)FileSystemFileHandler.java 2016年4月24日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.storage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;

/**
 * 操作系统文件存储
 * 
 * 需要 properties 属性 pro.storage
 * 
 * @author Woo Cupid
 * @date 2016年4月24日
 * @version $Revision$
 */
public class FileSystemFileHandler extends CloudFileHandler {

	@Value(value = "${pro.storage}")
	private String storage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#upload(java.io.File, java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(File file, String name, String bucket, FileMeta meta) {
		String relatedPath = TempFolderUtils.build(Lists.newArrayList(StringUtils.defaultIfBlank(bucket, ""), name));
		String filepath = TempFolderUtils.build(Lists.newArrayList(storage, relatedPath));
		try {
			FileUtils.copyFile(file, new File(filepath));
		} catch (IOException e) {
			logger.error("Could not upload file to bucket[{}], name[{}]", bucket, name);
			logger.error("Failed to save file to local storage", e);
		}
		if (relatedPath.startsWith("/")) {
			return StringUtils.replace(relatedPath, "\\", "/");
		} else {
			return "/" + StringUtils.replace(relatedPath, "\\", "/");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#upload(byte[], java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(byte[] content, String name, String bucket, FileMeta meta) {
		String relatedPath = TempFolderUtils.build(Lists.newArrayList(StringUtils.defaultIfBlank(bucket, ""), name));
		String filepath = TempFolderUtils.build(Lists.newArrayList(storage, relatedPath));
		try {
			FileUtils.writeByteArrayToFile(new File(filepath), content);
		} catch (IOException e) {
			logger.error("Could not upload file to bucket[{}], name[{}]", bucket, name);
			logger.error("Failed to save file to local storage", e);
		}

		if (relatedPath.startsWith("/")) {
			return StringUtils.replace(relatedPath, "\\", "/");
		} else {
			return "/" + StringUtils.replace(relatedPath, "\\", "/");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.turnbig.pandora.storage.CloudFileHandler#getUrl(java.lang.String)
	 */
	@Override
	public String getUrl(String key) {
		throw new RuntimeException("not implemented");
	}

}
