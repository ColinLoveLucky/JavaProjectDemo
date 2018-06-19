package com.qf.cobra.util;

/**
 * 字符串工具类
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 17:59
 */
public final class StrUtil {
	
	/** private constructor */
	private StrUtil() {}
	
	/**
	 * Object对象转字符串类型
	 * 
	 * @param obj
	 * @return
	 */
	public static String obj2Str(Object obj) {
		try {
			return (null == obj ? null : obj.toString());
		} catch (Exception e) {
			return null;
		}
	}

}
