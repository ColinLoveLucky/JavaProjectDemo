package com.quark.cobra.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * 日期工具类
 * 
 * @author: XianjiCai
 * @date: 2018/02/06 14:23
 */
public final class DateUtil {
	
	/** 日期格式 */
	public static final String DF_YMD ="yyyyMMdd";
	public static final String DF_YMDHM = "yyyyMMddHHmm";
	public static final String DF_YMDHMS_M = "yyyy-MM-dd HH:mm:ss";
	
	/** private constructor */
	private DateUtil() {}
	
	/**
	 * 格式化日期类型转换为指定格式字符串
	 * 
	 * @param date java.lang.util.Date
	 * @param format 日期格式
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		return DateTimeFormat.forPattern(format).print(new DateTime(date));
	}
	
	/**
	 * 格式化当前日期转换为指定格式字符串
	 * 
	 * @param format 日期格式
	 * @return
	 */
	public static String formatCurrentDate(String format) {
		return formatDateTime(DateTime.now(), format);
	}
	
	/**
	 * 格式化日期类型转换为字符串
	 * 
	 * @param dateTime org.joda.time.DateTime
	 * @param format 日期格式
	 * @return
	 */
	public static String formatDateTime(DateTime dateTime, String format) {
		return DateTimeFormat.forPattern(format).print(dateTime);
	}
	
	/**
	 * 获取指定日期格式的当前日期字符串
	 * 
	 * @param format 格式
	 * @return
	 */
	public static String getCurrentDateTimeStr(String format) {
		return formatDateTime(DateTime.now(), format);
	}
	
	public static Date parseDateStr(String date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 判断两个日期之间相差年份
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param minusYear
	 * @return
	 */
	public static int calcDiffYear(Date dateFrom, Date dateTo) {
		return new DateTime(dateTo).getYear() - new DateTime(dateFrom).getYear();
	}
	
	public static void main(String[] args) {
		Date dateForm = parseDateStr("1998-02-06", "yyyy-MM-dd");
		int diffDate = calcDiffYear(dateForm, new Date());
		System.out.println(diffDate);
	}
	
}
