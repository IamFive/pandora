package net.turnbig.pandora.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

	private static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");

	public static String getDatetime(Date date) {
		return DATETIME_FORMATTER.format(date);
	}

	public static String getDatetime(long millis) {
		return getDatetime(new Date(millis));
	}

	public static String getString(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	public static String getString(long millis, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(new Date(millis));
	}

	public static String getCurrentDatetime() {
		return DATETIME_FORMATTER.format(new Date());
	}

	public static String getCurrentDatetime(String pattern) {
		return getString(new Date(), pattern);
	}

	public static String getDate(Date date) {
		return DATE_FORMATTER.format(date);
	}

	public static String getDate(long millis) {
		return getDate(new Date(millis));
	}

	public static String getCurrentDate() {
		return getDate(new Date());
	}

	public static String getTime(Date date) {
		return TIME_FORMATTER.format(date);
	}

	public static String getCurrentTime() {
		return getTime(new Date());
	}

	public static String getTime(long millis) {
		return getTime(new Date(millis));
	}

	public static Date parse(String dateString, String pattern) {
		if (dateString == null) {
			throw new NullPointerException();
		}

		try {
			SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(pattern);
			return DATE_FORMATTER.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse date from " + dateString + " with pattern " + pattern);
		}
	}

	public static Date parse(String dateString) {
		if (dateString == null) {
			throw new NullPointerException();
		}

		try {
			if (dateString.contains("-") && dateString.contains(":")) {
				return DATETIME_FORMATTER.parse(dateString);
			} else if (dateString.contains("-")) {
				return DATE_FORMATTER.parse(dateString);
			} else {
				dateString = getCurrentDate() + " " + dateString;
				return DATETIME_FORMATTER.parse(dateString);
			}
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse date from " + dateString);
		}
	}

}
