/**
 * @(#)AA.java 2014年2月21日
 *
 * Copyright 2008-2014 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.springmvc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.multipart.MultipartFile;

import net.turnbig.pandora.conversion.StringToDateConverter;

/**
 * @author Woo Cupid
 * @date 2014年2月21日
 * @version $Revision$
 */
public class ConversionServiceFactoryBean2 extends ConversionServiceFactoryBean {

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		GenericConversionService registry = (GenericConversionService) this.getObject();
		registry.addConverter(new StringToDateConverter());
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
