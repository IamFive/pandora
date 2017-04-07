/**
 * @(#)TempFolderUtils.java 2015年7月10日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 *
 */
package net.turnbig.pandora.storage;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

/**
 * @author Woo Cupid
 * @date 2015年7月10日
 * @version $Revision$
 */
@Component
public class TempFolderUtils {

	private static final Logger logger = LoggerFactory.getLogger(TempFolderUtils.class);

	@Resource(name = "properties")
	Properties props;

	static String tempFolderPath;

	@PostConstruct
	public void init() {
		logger.info("Initial System temp folder path ...");
		boolean contains = props.containsKey("project.temp.folder");
		tempFolderPath = contains ? props.get("project.temp.folder").toString()
				: System.getProperty("user.home") + File.separator + "whisper";
		File file = new File(tempFolderPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		logger.info("System temp folder path: {}", file.getAbsoluteFile().getAbsolutePath());
	}

	/**
	 * item image 上传的临时文件
	 * @param orderItemId
	 * @param fileName
	 * @return
	 */
	public static String getItemImageFilepath(String fileName) {
		return build(Lists.newArrayList(tempFolderPath, "item", fileName));
	}

	public static String build(List<String> segments) {
		return FilenameUtils.normalize(StringUtils.join(segments, File.separator).trim());
	}

	public static String getRandomFileName(String prefix, String ext) {
		String file = StringUtils.appendIfMissing(StringUtils.defaultString(prefix, ""), File.separator)
				+ RandomStringUtils.randomAlphanumeric(12);
		return file + (StringUtils.isBlank(ext) ? "" : "." + ext);
	}

	public static void main(String[] args) {
		System.out.println(StringUtils.defaultIfBlank(String.valueOf(null), "aa"));
	}
}
