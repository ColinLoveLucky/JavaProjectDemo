package com.qf.cobra.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static String formatCurrentDateTime () {
		return formatDateWithPattern(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
	} 
	
	public static String formatDateTime (Date date) {
		return formatDateWithPattern(date, "yyyy-MM-dd HH:mm:ss.SSS");
	} 
	
	public static String formatDateWithPattern(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static String formatUTCDate(String dateTime) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return formatDateTime(df.parse(dateTime));
	}
}
