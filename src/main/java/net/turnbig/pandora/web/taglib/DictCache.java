/**
 * @(#)DictCache.java 2013年12月30日
 *
 * Copyright 2008-2013 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.taglib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

/**
 * A JVM-based cache for dictionary
 * 
 * @author Woo Cupid
 * @date 2013年12月30日
 * @version $Revision$
 */
@Lazy(false)
@Component(DictCache.BEAN_NAME)
public class DictCache implements ApplicationContextAware {

	public static final String BEAN_NAME = "_Dict_Cache_";
	private static final Logger logger = LoggerFactory.getLogger(DictCache.class);

	private static ApplicationContext applicationContext;
	private static DictCache instance = null;

	@Autowired
	@Value("${pro.dict.tablename}")
	private String tableName;

	@Autowired
	@Value("${pro.dict.field.key}")
	private String keyField;

	@Autowired
	@Value("${pro.dict.field.display}")
	private String displayField;

	@Autowired
	@Value("${pro.dict.field.value}")
	private String valueField;

	@Resource(name = "dataSource")
	DataSource dataSource;

	private HashMap<String, LinkedHashMap<String, String>> mapper = Maps.newLinkedHashMap();

	// private ReadWriteLock lock = new ReentrantReadWriteLock();

	@PostConstruct
	public synchronized void init() {
		logger.info("=== Start init Dict Cache center ===");
		String sql = "select %s as key, %s as value, %s as display from %s order by priority asc, %s asc";
		List<Map<String, Object>> list = new JdbcTemplate(dataSource)
				.queryForList(String.format(sql, keyField, valueField, displayField, tableName, valueField));
		for (Map<String, Object> entry : list) {
			String key = entry.get("key").toString();
			String value = entry.get("value").toString();
			String display = entry.get("display").toString();
			addItem(key, value, display);
			logger.debug("add dict record : {}::{}-->{}", key, value, display);
		}
		logger.info("=== Init Dict Cache center done ===");
	}

	public void reload() {
		mapper.clear();
		init();
	}

	/**
	 * @param key
	 * @param value
	 * @param display
	 */
	private void addItem(String key, String value, String display) {
		if (!mapper.containsKey(key)) {
			LinkedHashMap<String, String> map = Maps.newLinkedHashMap();
			mapper.put(key, map);
		}
		mapper.get(key).put(value, display);
	}

	public LinkedHashMap<String, String> get(String key) {
		LinkedHashMap<String, String> map = mapper.get(key);
		if (map == null) {
			this.reload();
			return mapper.get(key);
		} else {
			return map;
		}
	}

	public String getDisplay(String key, String value) {
		HashMap<String, String> map = mapper.get(key);
		if (map == null) {
			this.reload();
			map = mapper.get(key);
		}
		return map.get(value);
	}

	public String getValue(String key, String display) {
		HashMap<String, String> map = mapper.get(key);
		if (map == null) {
			this.reload();
			map = mapper.get(key);
		}

		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals(display)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @return the mapper
	 */
	public HashMap<String, LinkedHashMap<String, String>> getMapper() {
		return mapper;
	}

	public static DictCache instance() {
		if (instance == null) {
			instance = (DictCache) DictCache.applicationContext.getBean(DictCache.BEAN_NAME);
		}
		return instance;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		DictCache.applicationContext = applicationContext;
	}

}
