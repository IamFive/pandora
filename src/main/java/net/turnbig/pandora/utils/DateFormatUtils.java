package net.turnbig.pandora.utils;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.Maps;

public class DateFormatUtils {

	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");
	private static Map<String, DateTimeFormatter> cache = Maps.newHashMap();
	static {
		cache.put("yyyy-MM-dd HH:mm:ss", DATETIME_FORMATTER);
		cache.put("yyyy-MM-dd", DATE_FORMATTER);
		cache.put("HH:mm:ss", TIME_FORMATTER);
	}

	public static DateTimeFormatter getFormatter(String pattern) {
		if (!cache.containsKey(pattern)) {
			cache.put(pattern, DateTimeFormat.forPattern(pattern));
		}
		return cache.get(pattern);
	}

	public static String getDatetime(Date date) {
		return DATETIME_FORMATTER.print(new DateTime(date));
	}

	public static String getDatetime(DateTime date) {
		return DATETIME_FORMATTER.print(date);
	}

	public static String getDatetime(long millis) {
		return getDatetime(new DateTime(millis));
	}

	public static String getString(DateTime date, String pattern) {
		return getFormatter(pattern).print(date);
	}

	public static String getString(Date date, String pattern) {
		return getString(new DateTime(date), pattern);
	}

	public static String getString(long millis, String pattern) {
		return getString(new DateTime(millis), pattern);
	}

	public static String getCurrentDatetime() {
		return DATETIME_FORMATTER.print(new DateTime());
	}

	public static String getCurrentDatetime(String pattern) {
		return getString(DateTime.now(), pattern);
	}

	public static String getDate(DateTime date) {
		return DATE_FORMATTER.print(date);
	}

	public static String getDate(Date date) {
		return getDate(new DateTime(date));
	}

	public static String getDate(long millis) {
		return getDate(new DateTime(millis));
	}

	public static String getCurrentDate() {
		return getDate(DateTime.now());
	}

	public static String getTime(DateTime date) {
		return TIME_FORMATTER.print(date);
	}

	public static String getTime(Date date) {
		return getTime(new DateTime(date));
	}

	public static String getCurrentTime() {
		return getTime(DateTime.now());
	}

	public static String getTime(long millis) {
		return getTime(new DateTime(millis));
	}

	public static DateTime parse(String dateString, String pattern) {
		if (dateString == null) {
			throw new NullPointerException();
		}
		return getFormatter(pattern).parseDateTime(dateString);
	}

	public static DateTime parse(String dateString) {
		if (dateString == null) {
			throw new NullPointerException();
		}
		if (dateString.contains("-") && dateString.contains(":")) {
			return DATETIME_FORMATTER.parseDateTime(dateString);
		} else if (dateString.contains("-")) {
			return DATE_FORMATTER.parseDateTime(dateString);
		} else {
			dateString = getCurrentDate() + " " + dateString;
			return DATETIME_FORMATTER.parseDateTime(dateString);
		}
	}

}
