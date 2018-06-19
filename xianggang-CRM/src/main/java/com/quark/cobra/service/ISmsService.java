package com.quark.cobra.service;

import com.quark.cobra.domain.SendSmsReqBo;
import com.quark.cobra.enums.SmsTypeEnums;

public interface ISmsService {

    /**
     * 发送手机验证码
     * @param mobile
     * @param smsType
     */
    void sendMessage(String mobile, SmsTypeEnums smsType);

    /**
     * 发送手机短信
     * @param mobile
     * @param message
     */
    void sendMessage(String mobile, String message, String besId);

    /**
     * 验证手机注册验证码
     * @param mobile
     * @param captcha
     * @param smsType
     */
    void checkCaptcha(String mobile, String captcha, SmsTypeEnums smsType);

    /**
     *
     * @param mobile
     * @return
     */
    boolean needImageCaptcha(String mobile);

    /**
     * 增加发送次数
     * @param key  this is a mobile phone or client ipv6
     */
    void increaseSendSmsCount(String key);

    /**
     * 统计当前发送次数
     * @param key  this is a mobile phone or client ipv6
     * @return
     */
    long statisticsSendSmsCount(String key);

    /**
     *查询配置参数
     * @param key
     * @return
     */
    String findSysConfigValue(String key);

    /**
     *查询配置参数
     * @param key
     * @param defaultValue
     * @return
     */
    String findSysConfigValue(String key,String defaultValue);
}
