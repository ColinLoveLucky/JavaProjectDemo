package com.qf.cobra.mc.constant;

/**
 * 常量类
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 14:30
 */
public interface McMessageConstant {

	/** 来源类型 */
	String SOURCE_TYPE_KEY = "sourceType";
	/** 客户端来源 */
	String CLIENT_SOURCE_KEY = "clientSource";
	/** 客户端产品类型 */
	String APP_TYPE = "appType";
	/** 事件类型 */
	String EVENT_TYPE_KEY = "eventType";
	/** 创建时间 不同类型对应不同含义,注册事件代表注册时间,实名事件代表实名时间 */
	String CREATE_DATE_KEY = "createDate";
	/** 用户ID */
	String USER_ID_KEY = "userId";
	
	/** 注册用 */
	/** 手机号 */
	String MOBILE_KEY = "mobile";
	
	/** 预审结果用 */
	/** 进件编号 */
	String APP_ID_KEY = "appId";
	/** 审核结果 */
	String AUDIT_RESULT_KEY = "auditResult";
	
	/** 审核通过 */
	String AUDIT_PASS = "PASS";
	/** 审核拒绝 */
	String AUDIT_REJECT = "REJECT";
}
