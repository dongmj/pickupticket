package com.account.pickupticket.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeUtils {
	private static final Log _log = LogFactory.getLog(TimeUtils.class);
	private TimeUtils() {}
	
	public static String getCurrentDateWithCommonFormat() {
		Date current = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(current);
	}
	
	public static String getCurrentWithFormat(String format) {
		Date current = new Date();
		DateFormat df = new SimpleDateFormat(format);
		return df.format(current);
	}
	
	public static Date convertString2Date(String source, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			Date result = df.parse(source);
			return result;
		} catch (ParseException e) {
			_log.error("parser fail: " + source, e);
			return null;
		}
	}
	
	public static String convertDate2String(Date source, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(source);
	}
}
