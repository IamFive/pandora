/**
 * @(#)TempFolderUtils.java 2015年7月10日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 *
 */
package net.turnbig.pandora.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
	
	private static final int RANDOM_LEN = 30;

	@Resource
	Properties props;

	@Value("${project.temp.folder:}")
	String configedTempFolder;

	public static String tempFolderPath;

	@PostConstruct
	public void init() {
		if (tempFolderPath == null) {
			logger.info("Initial System temp folder path ...");
			String defaultTempFolder = System.getProperty("user.home") + File.separator + "temp";
			tempFolderPath = StringUtils.isNotBlank(configedTempFolder) ? configedTempFolder : defaultTempFolder;
			File file = new File(tempFolderPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			logger.info("System temp folder path: {}", file.getAbsoluteFile().getAbsolutePath());
		}
	}

	public static String getAbsTempFilePath(String... relativePath) {
		ArrayList<String> segments = Lists.newArrayList(relativePath);
		segments.add(0, tempFolderPath);
		return build(segments);
	}

	public static String build(List<String> segments) {
		return FilenameUtils.normalize(StringUtils.join(segments, File.separator).trim());
	}

	public static String getRandomFileName(String prefix, String ext) {
		String file = StringUtils.appendIfMissing(StringUtils.defaultString(prefix, ""), File.separator)
				+ RandomStringUtils.randomAlphanumeric(RANDOM_LEN);
		return file + (StringUtils.isBlank(ext) ? "" : "." + ext);
	}

	public static void main(String[] args) {
	}
}
