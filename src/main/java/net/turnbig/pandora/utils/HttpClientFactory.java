package net.turnbig.pandora.utils;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * global Singleton HTTP-client 
 *
 * @author Woo Cubic
 * @date   2017-06-05 15:12:26
 */
public class HttpClientFactory implements FactoryBean<CloseableHttpClient>, InitializingBean {

	static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
			+ "Chrome/57.0.2987.98 Safari/537.36";
	static final String ENCODING = "UTF-8";

	CloseableHttpClient client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// add connection pool configuration
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		httpBuilder.setUserAgent(USER_AGENT);
		httpBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		httpBuilder.setMaxConnPerRoute(50);
		httpBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
		// httpBuilder.setMaxConnTotal(200);

		//@off
		RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(20 * 1000)
				.setConnectTimeout(10 * 1000)
				.setCookieSpec(CookieSpecs.DEFAULT)
				.build();//@on
		httpBuilder.setDefaultRequestConfig(config);

		httpBuilder.setServiceUnavailableRetryStrategy(new DefaultServiceUnavailableRetryStrategy(5, 10));
		httpBuilder.setRetryHandler(new StandardHttpRequestRetryHandler(3, true));

		try {
			// ignore SSL verify
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					return true;
				}
			}).build();
			httpBuilder.setSSLContext(sslContext);
		} catch (Exception e) {
			// ignore, should not happen
		}

		this.client = httpBuilder.build();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public CloseableHttpClient getObject() throws Exception {
		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return CloseableHttpClient.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

}
