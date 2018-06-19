package com.quark.cobra.service;

import com.quark.cobra.domain.SendSmsReqBo;
import org.springframework.validation.BindingResult;

import com.quark.cobra.domain.RegistrationReqBo;
import com.quark.cobra.domain.UserInfoReqBo;

public interface ICheckService {
    /**
     * 注册检查
     * 1. 手机号是否符合格式要求
     * 2. 手机验证码是否输入正确
     * 3. 手机号是否已注册
     */
    void checkRegistration(RegistrationReqBo registrationReqBo);

    /**
     * 短信发送检查
     * 1. ip 黑白名单检查
     * 2. ip 频率检查
     * 3. 手机号黑名单检查
     * 4. 短信(带图片验证码) 频率检查
     * 5. 用户状态检查, 注册仅限未注册用户,忘记密码仅限已注册用户
     */
    void checkSendSms(SendSmsReqBo sendSmsReqBo);
    
    /**
     * 检查用户基本信息
     * 
     * @param userInfoReqBo
     * @param bindingResult
     */
    String checkUserInfo(UserInfoReqBo userInfoReqBo, BindingResult bindingResult);
}
