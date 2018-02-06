/**
 * @(#)Q.java 2016年3月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * @author Woo Cupid
 * @date 2016年3月28日
 * @version $Revision$
 */
public class Q {

	public static HashMap<String, Object> newHashMap(Object... keyValues) {
		assert (keyValues.length / 2) == 0;
		assert (keyValues.length / 2) == 0;
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < keyValues.length; i += 2) {
			map.put(keyValues[i].toString(), keyValues[i + 1]);
		}
		return map;
	}

	/**
	 * @return
	 */
	public static ArrayList<Object> newArrayList(Object... objects) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object object : objects) {
			list.add(object);
		}
		return list;
	}

	public static String like(String o) {
		if (StringUtils.isNotBlank(o)) {
			return "%" + StringUtils.defaultString(o, "") + "%";
		}
		return null;
	}

	public static String llike(String o) {
		if (StringUtils.isNotBlank(o)) {
			return "%" + StringUtils.defaultString(o, "");
		}
		return null;
	}

	public static String rlike(String o) {
		if (StringUtils.isNotBlank(o)) {
			return StringUtils.defaultString(o, "") + "%";
		}
		return null;
	}

	public static Date withTimeAtStartOfDay(Date original) {
		if (original == null) {
			return null;
		}
		return new DateTime(original).withTimeAtStartOfDay().toDate();
	}

	public static Date withTimeAtEndOfDay(Date original) {
		if (original == null) {
			return null;
		}
		return new DateTime(original).plusDays(1).withTimeAtStartOfDay().minusSeconds(1).toDate();
	}
}
