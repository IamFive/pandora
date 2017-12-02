package net.turnbig.pandora.web.cluster;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

import net.turnbig.pandora.web.cluster.HttpRedisSessionConfig.RedisSessionProperties;

/**
 *
 * @author Woo Cubic
 * @date   2017年4月12日 下午3:10:03
 */
@Configuration
@EnableConfigurationProperties(RedisSessionProperties.class)
public class HttpRedisSessionConfig {

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new JdkSerializationRedisSerializer();
		// return new GenericJackson2JsonRedisSerializer(new ObjectMapper());
	}

	@Autowired
	RedisSessionProperties properties;

	@Bean(name = "sessionRedisTemplate")
	public RedisTemplate<Object, Object> sessionRedisTemplate() {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(connectionFactory());
		return template;
	}
	
	/**
	 * support loading cookie-id from header
	 * @return
	 */
	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		return new RestfulCookieHttpSessionStrategy();
	}

	/**
	 * replace default jedis connection factory with lettuce
	 * 
	 * @return
	 */
	public LettuceConnectionFactory connectionFactory() {
		LettuceConnectionFactory factory = new LettuceConnectionFactory();
		factory.setHostName(properties.getHost());
		factory.setPort(properties.getPort());
		factory.setPassword(properties.getPassword());
		factory.setDatabase(properties.getDatabase());
		factory.afterPropertiesSet();
		return factory;
	}
	

	@ConfigurationProperties(prefix = "spring.session.redis")
	public static class RedisSessionProperties {
		String host = "127.0.0.1";
		String password;
		int database = 1;
		int port = 6379;

		/**
		 * @return the host
		 */
		public String getHost() {
			return host;
		}

		/**
		 * @param host the host to set
		 */
		public void setHost(String host) {
			this.host = host;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		public int getDatabase() {
			return database;
		}

		public void setDatabase(int database) {
			this.database = database;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(int port) {
			this.port = port;
		}

	}

	/**
	 * support loading cookie id from header
	 *
	 * @author Woo Cubic
	 * @date   2017年4月12日 下午5:45:49
	 */
	public static class RestfulCookieHttpSessionStrategy implements MultiHttpSessionStrategy {

		private String headerName = "x-auth-token";
		private CookieHttpSessionStrategy delegate = new CookieHttpSessionStrategy();

		public RestfulCookieHttpSessionStrategy() {
		}

		public RestfulCookieHttpSessionStrategy(String headerName) {
			this.headerName = headerName;
		}

		/**
		 * @param request
		 * @return
		 * @see org.springframework.session.web.http.CookieHttpSessionStrategy#getRequestedSessionId(javax.servlet.http.HttpServletRequest)
		 */
		public String getRequestedSessionId(HttpServletRequest request) {
			String header = request.getHeader(headerName);
			return header != null ? header : delegate.getRequestedSessionId(request);
		}

		/**
		 * @param session
		 * @param request
		 * @param response
		 * @see org.springframework.session.web.http.CookieHttpSessionStrategy#onNewSession(org.springframework.session.Session, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		 */
		public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
			delegate.onNewSession(session, request, response);
		}

		/**
		 * @param request
		 * @param response
		 * @see org.springframework.session.web.http.CookieHttpSessionStrategy#onInvalidateSession(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		 */
		public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
			delegate.onInvalidateSession(request, response);
		}

		/**
		 * @param request
		 * @param response
		 * @return
		 * @see org.springframework.session.web.http.CookieHttpSessionStrategy#wrapRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		 */
		public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
			return delegate.wrapRequest(request, response);
		}

		/**
		 * @param request
		 * @param response
		 * @return
		 * @see org.springframework.session.web.http.CookieHttpSessionStrategy#wrapResponse(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		 */
		public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
			return delegate.wrapResponse(request, response);
		}

	}

}
