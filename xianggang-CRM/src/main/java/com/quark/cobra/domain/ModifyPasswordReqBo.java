package com.quark.cobra.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.quark.cobra.constant.CrmConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 修改密码请求实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/03 18:09
 */
@Getter
@Setter
public class ModifyPasswordReqBo {
	
	/** 原密码 */
	@NotEmpty(message = "原密码不能为空")
	private String password;
	
	/** 新密码 */
	@NotEmpty(message = "新密码不能为空")
//	@Length(min = CrmConstants.PASSWORD_MIN_LENGTH, max = CrmConstants.PASSWORD_MAX_LENGTH,
//		message = "新密码长度限制"+CrmConstants.PASSWORD_MIN_LENGTH+"~"+CrmConstants.PASSWORD_MAX_LENGTH)
	private String newPassword;
	
}
