package com.quark.cobra.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;

import com.quark.cobra.enums.SmsTypeEnums;

import lombok.Getter;
import lombok.Setter;

/**
 * 发送短信请求实体类
 * 
 * @author: XianjiCai
 * @date: 2018/02/03 14:59
 */
@Getter
@Setter
@ApiModel
public class SendSmsReqBo {
	
	/** 用户ID */
	private String userId;

	/** 手机号 */
	@NotBlank
	@ApiModelProperty("手机号")
	@NonNull
	private String mobile;
	
	/** 手机号前缀 例如:+86 */
	@ApiModelProperty(value = "手机号前缀")
	private String prefix;
	
	/** 短信类型 */
	@ApiModelProperty(value = "短信类型")
	@NonNull
	private SmsTypeEnums smsType;
	
	/** 图片验证码 */
	@ApiModelProperty(value = "图片验证码")
	private String imageCaptcha;

	@ApiModelProperty(value = "IP地址")
	@NonNull
	private String clientIp;
	
}
