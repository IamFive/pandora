/**
 * @(#)StringToDateConverter.java 2013年12月5日
 *
 * Copyright 2008-2013 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Woo Cupid
 * @date 2013年12月5日
 * @version $Revision$
 */
public class StringToDateConverter implements Converter<String, Date> {

	private DateFormat[] getDateFormats(Locale locale) {
		DateFormat ls = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat ss = new SimpleDateFormat("yyyy-MM-dd");

		DateFormat dt1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
		DateFormat dt2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
		DateFormat dt3 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

		DateFormat d1 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		DateFormat d2 = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
		DateFormat d3 = DateFormat.getDateInstance(DateFormat.LONG, locale);

		DateFormat rfc3399 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return new DateFormat[] { ls, ss, dt1, dt2, dt3, rfc3399, d1, d2, d3 };
	}

	private Date doConvertToDate(Object value, Locale locale) {
		Date result = null;

		if (value instanceof String) {
			if (StringUtils.isNumeric(value.toString())) {
				return new Date(Long.parseLong(value.toString()));
			}

			// TODO we could guess date-formatter from value to prevent DateFormat creating and improve performance
			// example: if value length is 10 and contains two "-",
			// then format should be yyyy-MM-dd
			DateFormat[] dfs = getDateFormats(locale);
			for (DateFormat df1 : dfs) {
				try {
					result = df1.parse(value.toString());
					if (result != null) {
						break;
					}
				} catch (ParseException ignore) {
				}
			}
		} else if (value instanceof Object[]) {
			// let's try to convert the first element only
			Object[] array = (Object[]) value;
			if (array.length >= 1) {
				Object v = array[0];
				result = doConvertToDate(v, locale);
			}
		} else if (Date.class.isAssignableFrom(value.getClass())) {
			result = (Date) value;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public Date convert(String source) {
		return doConvertToDate(source, Locale.CHINA);
	}

}
