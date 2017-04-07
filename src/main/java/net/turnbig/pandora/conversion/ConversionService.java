/**
 * @(#)ConversionService.java 2013年12月5日
 *
 * Copyright 2008-2013 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.conversion;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * @author Woo Cupid
 * @date 2013年12月5日
 * @version $Revision$
 */
public class ConversionService {

	private static DefaultConversionService cs = new DefaultConversionService();

	static {
		cs.addConverter(String.class, Date.class, new StringToDateConverter());
		// cs.addConverter(String.class, Timestamp.class, new StringToDateConverter());
	}

	/**
	 * @param converter
	 * @see org.springframework.core.convert.support.GenericConversionService#addConverter(org.springframework.core.convert.converter.Converter)
	 */
	public static void addConverter(Converter<?, ?> converter) {
		cs.addConverter(converter);
	}

	/**
	 * @param sourceType
	 * @param targetType
	 * @param converter
	 * @see org.springframework.core.convert.support.GenericConversionService#addConverter(java.lang.Class, java.lang.Class, org.springframework.core.convert.converter.Converter)
	 */
	public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType,
			Converter<? super S, ? extends T> converter) {
		cs.addConverter(sourceType, targetType, converter);
	}

	/**
	 * @param converter
	 * @see org.springframework.core.convert.support.GenericConversionService#addConverter(org.springframework.core.convert.converter.GenericConverter)
	 */
	public static void addConverter(GenericConverter converter) {
		cs.addConverter(converter);
	}

	/**
	 * @param factory
	 * @see org.springframework.core.convert.support.GenericConversionService#addConverterFactory(org.springframework.core.convert.converter.ConverterFactory)
	 */
	public static void addConverterFactory(ConverterFactory<?, ?> factory) {
		cs.addConverterFactory(factory);
	}

	/**
	 * @param sourceType
	 * @param targetType
	 * @see org.springframework.core.convert.support.GenericConversionService#removeConvertible(java.lang.Class, java.lang.Class)
	 */
	public static void removeConvertible(Class<?> sourceType, Class<?> targetType) {
		cs.removeConvertible(sourceType, targetType);
	}

	/**
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#canConvert(java.lang.Class, java.lang.Class)
	 */
	public static boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return cs.canConvert(sourceType, targetType);
	}

	/**
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#canConvert(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	public static boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return cs.canConvert(sourceType, targetType);
	}

	/**
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#canBypassConvert(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	public static boolean canBypassConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return cs.canBypassConvert(sourceType, targetType);
	}

	/**
	 * @param source
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#convert(java.lang.Object, java.lang.Class)
	 */
	public static <T> T convert(Object source, Class<T> targetType) {
		return cs.convert(source, targetType);
	}

	/**
	 * @param source
	 * @param sourceType
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	public static Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return cs.convert(source, sourceType, targetType);
	}

	/**
	 * @param source
	 * @param targetType
	 * @return
	 * @see org.springframework.core.convert.support.GenericConversionService#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor)
	 */
	public static Object convert(Object source, TypeDescriptor targetType) {
		return cs.convert(source, targetType);
	}

	public static void main(String[] args) {
		System.out.println(ConversionService.convert("1", Boolean.class));
		System.out.println(ConversionService.convert("yes", Boolean.class));

		System.out.println(ConversionService.convert("1000000", Integer.class));
		System.out.println(ConversionService.convert("1", Float.class));

		System.out.println(ConversionService.convert("2010-10-12T00:00:00", Date.class));
		System.out.println(ConversionService.convert("2010-10-12", Timestamp.class));
		System.out.println(ConversionService.convert(new String[] { "11", "22" }, Long.class));
	}

}
