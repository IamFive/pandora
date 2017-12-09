package net.turnbig.pandora.spring.env.redis;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * use redis as property source
 *
 * @author QianBiao.NG
 * @date   2017-12-09 19:39:08
 */
public class RedisPropertySource extends PropertySource<RedisTemplate<String, Object>> {

	private static final Logger logger = LoggerFactory.getLogger(RedisPropertySource.class);

	public static String DEFAULT_NAME = "Redis-Based-Property-Source";
	public static String DEFAULT_KEY = "property:source";
	public static String INIT_PROPERTY_FILE_PATH = "classpath:redis-property-source.properties";

	StringRedisTemplate redisTemplate;
	String key = DEFAULT_KEY;

	public RedisPropertySource(ConfigurableApplicationContext context) {
		this(context, DEFAULT_NAME, DEFAULT_KEY);
	}

	public RedisPropertySource(ConfigurableApplicationContext context, String name) {
		this(context, name, DEFAULT_KEY);
	}

	public RedisPropertySource(ConfigurableApplicationContext context, String name, String key) {
		super(name);
		this.key = (key == null ? DEFAULT_KEY : key);
		initialRedisTemplate(context);
		initialRedisPropertySource(context);
	}

	/**
	 * @param context
	 */
	public void initialRedisPropertySource(ConfigurableApplicationContext context) {
		if (redisTemplate != null) {
			Resource resource = context.getResource(INIT_PROPERTY_FILE_PATH);
			if (resource.exists()) {
				try {
					ResourcePropertySource propertySource = new ResourcePropertySource(resource);
					String[] propertyNames = propertySource.getPropertyNames();
					for (String propertyName : propertyNames) {
						if (!this.containsProperty(propertyName)) {
							String property = propertySource.getProperty(propertyName).toString();
							redisTemplate.opsForHash().put(this.key, propertyName, property);
						}
					}
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * @param context
	 */
	public void initialRedisTemplate(ConfigurableApplicationContext context) {
		Map<String, StringRedisTemplate> beans = context.getBeansOfType(StringRedisTemplate.class);
		if (beans.size() == 1) {
			redisTemplate = context.getBean(StringRedisTemplate.class);
		} else if (beans.size() > 1) {
			// try to load RedisTemplate named "redisTemplate"
			redisTemplate = context.getBean("stringRedisTemplate", StringRedisTemplate.class);
		} else if (beans.size() == 0) {
			logger.warn(
					"No qualifying bean of type 'org.springframework.data.redis.core.StringRedisTemplate<' available, "
							+ "expect at least one");
		} else {
			logger.warn(
					"No qualifying bean of type 'org.springframework.data.redis.core.StringRedisTemplate' available, "
							+ "more than one found, but none named 'stringRedisTemplate'");
		}
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Object getProperty(String name) {
		if (redisTemplate != null) {
			return redisTemplate.opsForHash().get(this.key, name);
		}
		return null;
	}

	@Override
	public boolean containsProperty(String name) {
		return super.containsProperty(name);
	}

	public void setKey(String key) {
		this.key = key;
	}

}
