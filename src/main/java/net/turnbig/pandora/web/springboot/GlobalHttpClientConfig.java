package net.turnbig.pandora.web.springboot;

import org.springframework.context.annotation.Bean;

import net.turnbig.pandora.utils.HttpClientFactory;

/**
 *
 * @author Woo Cubic
 * @date   2017年4月11日 下午9:42:20
 */
public class GlobalHttpClientConfig {

	@Bean("globalHttpClient")
	public HttpClientFactory buildGlobalHttpClient() throws Exception {
		HttpClientFactory factory = new HttpClientFactory();
		factory.afterPropertiesSet();
		return factory;
	}

}
