package com.quark.cobra.constant;

import java.util.Iterator;
import java.util.Map;

/**
 * CRM常量类
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 13:01
 */
public final class CrmConstants {
	
	/** 实名认证请求参数 */
	/** 查询系统 */
	public static final String QUERY_SYSTEM = "loan-CRM";
	/** 查询用户 */
	public static final String QUERY_USER = "cobra";
	
	/** 实名认证成功状态 */
	public static final String REALNAME_AUTH_SUCCESS_STATUS = "OK";
	public static final String REALNAME_AUTH_SUCCESS_DESC = "一致";
	
	/** CRM系统Redis存储key前缀 */
	public static final String CRM_REDIS_KEY_PREFIX = "crm:";
	/** 短信相关Redis存储目录 */
	public static final String SMS_DIR_REDIS_KEY = "sms:";
	
	/** 密码最小与最大长度 */
	public static final int PASSWORD_MIN_LENGTH = 6;
	public static final int PASSWORD_MAX_LENGTH = 50;

	public static final String IP_BLACK_LIST = "IP_BLACK_LIST";
	public static final String IP_WHITE_LIST = "IP_WHITE_LIST";
	public static final String MOBILE_BLACK_LIST = "MOBILE_BLACK_LIST";
	public static final String MOBILE_WHITE_LIST = "MOBILE_WHITE_LIST";
	public static final String IP_MAX_COUNT = "IP_MAX_COUNT";
	public static final String MOBILE_MAX_COUNT = "MOBILE_MAX_COUNT";
	public static final String NEED_IMAGE_CAPTCHA_COUNT = "NEED_IMAGE_CAPTCHA_COUNT";
	public static final String CAPTCHA_MAX_TIMEOUT = "CAPTCHA_MAX_TIMEOUT";
	public static final String COUNT_MAX_TIMEOUT = "COUNT_MAX_TIMEOUT";

	/** 姓名最大长度 */
	public static final int REALNAME_MAX_LENGTH = 20;

	public static void main(String[] args) {
		Map map = System.getenv();
		Iterator it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			System.out.print(entry.getKey()+"=");
			System.out.println(entry.getValue());
		}
	}
	
}
