package com.quark.cobra.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.quark.cobra.constant.CrmConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 忘记密码请求实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/03 17:23
 */
@Getter
@Setter
public class ForgotPasswordReqBo {
	
	/** 手机号 */
	@NotEmpty(message = "手机号不能为空")
	private String mobile;
	
	/** 短信验证码 */
	@NotEmpty(message = "短信验证码不能为空")
	private String captcha;
	
	/** 密码 */
	@NotEmpty(message = "密码不能为空")
//	@Length(min = CrmConstants.PASSWORD_MIN_LENGTH, max = CrmConstants.PASSWORD_MAX_LENGTH,
//		message = "密码长度限制"+CrmConstants.PASSWORD_MIN_LENGTH+"~"+CrmConstants.PASSWORD_MAX_LENGTH)
	private String password;
	
}
