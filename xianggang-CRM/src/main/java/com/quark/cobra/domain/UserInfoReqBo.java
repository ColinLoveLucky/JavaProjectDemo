package com.quark.cobra.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.quark.cobra.constant.CrmConstants;
import com.quark.cobra.enums.EducationLevelEnums;
import com.quark.cobra.enums.MaritalStatusEnums;
import com.quark.cobra.enums.SexEnums;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户基本信息请求实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 11:49
 */
@Getter
@Setter
public class UserInfoReqBo {
	
	/** 用户ID */
	@NotBlank(message = "用户ID不能为空")
	private String userId;
	
	/** 姓名 */
	@NotBlank(message = "姓名不能为空")
	@Length(min = 0, max = CrmConstants.REALNAME_MAX_LENGTH,
		message="姓名最大限制输入"+CrmConstants.REALNAME_MAX_LENGTH+"个字符")
	private String realname;
	
	/** 身份证号 */
	@NotBlank(message = "身份证号不能为空")
	private String idCard;
	
	/** 性别 */
	@NotNull(message = "性别不能为空")
	private SexEnums sex;
	
	/** 年龄 */
	@Range(min=1, max=150, message="年龄输入范围为1到150")
	private Integer age;
	
	/** 婚姻状况 */
	@NotNull(message = "婚姻状况不能为空")
	private MaritalStatusEnums maritalStatus;
	
	/** 教育程度 */
	@NotNull(message = "教育程度不能为空")
	private EducationLevelEnums educationLevel;
	
}
