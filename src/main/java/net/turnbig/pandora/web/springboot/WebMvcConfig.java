package net.turnbig.pandora.web.springboot;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.turnbig.pandora.conversion.StringToDateConverter;

/**
 *
 * @author Woo Cubic
 * @date   2017年4月11日 下午9:42:20
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	@Value("${spring.jackson.serialization.indent_output:false}")
	boolean indentOutput = false;

	/**
	 * override message converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		messageConverters.add(new ByteArrayHttpMessageConverter());

		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		stringConverter.setWriteAcceptCharset(false);
		messageConverters.add(stringConverter);
		// messageConverters.add(new ResourceHttpMessageConverter());
		// messageConverters.add(new SourceHttpMessageConverter<Source>());
		// messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		// customer jackson object mapper
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().simpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.indentOutput(indentOutput).serializationInclusion(Include.NON_NULL).timeZone(TimeZone.getDefault())
				.applicationContext(getApplicationContext()).build();

		// TODO we could add a feature date deserializer like StringToDateConverter
		// SimpleModule module = new SimpleModule();
		// module.addDeserializer(Date.class, new DateDeserializer());
		// objectMapper.registerModule(module);

		messageConverters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}

	/**
	 * http request string to Object converter registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(String.class, Date.class, new StringToDateConverter());
		registry.addConverter(String.class, String.class, new Converter<String, String>() {
			@Override
			public String convert(String source) {
				if (source != null) {
					String trim = source.trim();
					return "".equals(trim) ? null : trim;
				}
				return null;
			}
		});

		// fix ajax submit empty file input will commit as string issue
		registry.addConverter(String.class, MultipartFile.class, new Converter<String, MultipartFile>() {
			@Override
			public MultipartFile convert(String source) {
				if (StringUtils.isBlank(source)) {
					return null;
				}
				return null;
			}
		});
	}

}
