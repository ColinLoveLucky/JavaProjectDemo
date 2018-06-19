package com.quark.cobra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 短信类型枚举
 * 
 * @author: XianjiCai
 * @date: 2018/02/03 14:56
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SmsTypeEnums {

	REGISTRATION("注册", "CAPTCHA_REG", "sms/registration.ftl"),
	FORGOT_PASSWORD("忘记密码", "CAPTCHA_FGT_PASS", "sms/forgot_password.ftl");
	
	/** 标签 */
	private String label;
	
	/** 缓存key前缀 */
	private String cacheKeyPrefix;
	
	/** 短信模板地址 */
	private String smsTemplatePath;
	
}
