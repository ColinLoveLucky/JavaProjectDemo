package com.quark.cobra.domain;

import com.quark.cobra.enums.SmsTypeEnums;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class RegistrationReqBo {
    @NonNull
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty(value = "手机号前缀")
    private String prefix;
    @NonNull
    @ApiModelProperty(value = "短信类型")
    private SmsTypeEnums smsType;
    @ApiModelProperty(value = "图片验证码")
    private String imageCaptcha;
    @NonNull
    @ApiModelProperty(value = "短信验证码")
    private String captcha;
    @NonNull
    @ApiModelProperty(value = "密码")
    private String password;
}
