package net.turnbig.pandora.web.springboot;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 *
 * @author Woo Cubic
 * @date   2017年4月11日 下午9:42:20
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.addDefaultHttpMessageConverters(messageConverters);
		// remove default string message converter,
		// and replace it with UTF-8 encoded string message converter
		messageConverters.remove(1);
		messageConverters.add(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));

	}
}
