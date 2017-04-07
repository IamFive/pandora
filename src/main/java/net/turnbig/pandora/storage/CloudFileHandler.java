/**
 * @(#)FileHandler.java 2016年3月25日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.storage;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Woo Cupid
 * @date 2016年3月25日
 * @version $Revision$
 */
public abstract class CloudFileHandler {

	static final Logger logger = LoggerFactory.getLogger(CloudFileHandler.class);

	/**
	 * 上传文件
	 * 
	 * @param content	文件
	 * @param name		文件名称
	 * @param bucket	文件存储空间（可以是File-System的相对路径，或者是云存储的bucket）
	 */
	public abstract String upload(File file, String name, String bucket);

	/**
	 * 上传文件
	 * 
	 * @param content	文件内容
	 * @param name		文件名称
	 * @param bucket	文件存储空间（可以是File-System的相对路径，或者是云存储的bucket）
	 */
	public abstract String upload(byte[] content, String name, String bucket);

	/**
	 * 获取访问地址
	 * @param key
	 * @return
	 */
	public abstract String getUrl(String key);

}
