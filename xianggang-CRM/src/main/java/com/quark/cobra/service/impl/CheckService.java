package com.quark.cobra.service.impl;

import com.quark.cobra.Utils.IDCardUtil;
import com.quark.cobra.Utils.ResponseCode;
import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.Utils.ValidateUtil;
import com.quark.cobra.constant.CrmConstants;
import com.quark.cobra.domain.RegistrationReqBo;
import com.quark.cobra.domain.SendSmsReqBo;
import com.quark.cobra.domain.UserInfoReqBo;
import com.quark.cobra.entity.User;
import com.quark.cobra.enums.SexEnums;
import com.quark.cobra.enums.SmsTypeEnums;
import com.quark.cobra.service.ICheckService;
import com.quark.cobra.service.ISmsService;
import com.quark.cobra.service.IUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import reactor.core.support.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CheckService implements ICheckService {
    @Autowired
    private IUserService userService;
    @Autowired
    private ISmsService smsService;

    @Override
    public void checkRegistration(RegistrationReqBo registrationReqBo) {
        String mobile = registrationReqBo.getMobile();
        checkMobileRegex(mobile);
        String captcha = registrationReqBo.getCaptcha();
        smsService.checkCaptcha(mobile, captcha, registrationReqBo.getSmsType());
        List<User> userList = userService.findByMobile(mobile);
        Assert.isTrue(CollectionUtils.isEmpty(userList), "用户已注册");
    }

    private void checkMobileRegex(String mobile) {
		String regex = "^1\\d{10}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobile);
        Assert.isTrue(m.matches(), "手机号格式不正确");
    }

	@Override
	public void checkSendSms(SendSmsReqBo sendSmsReqBo) {
		checkMobileRegex(sendSmsReqBo.getMobile());
		checkIp(sendSmsReqBo.getClientIp());
		checkMobile(sendSmsReqBo.getMobile());
		List<User> userList = userService.findByMobile(sendSmsReqBo.getMobile());
		if(SmsTypeEnums.REGISTRATION.name().equals(sendSmsReqBo.getSmsType().name())){
			Assert.state(CollectionUtils.isEmpty(userList), "该手机号码已注册!");
		}else if(SmsTypeEnums.FORGOT_PASSWORD.name().equals(sendSmsReqBo.getSmsType().name())){
			Assert.state(CollectionUtils.isNotEmpty(userList), "该手机号码未注册!");
		}else{
			Assert.state(false, "输入的参数有误");
		}
	}

	private void checkIp(String clientIp){
		List<String> blackList = Arrays.asList(smsService.findSysConfigValue(CrmConstants.IP_BLACK_LIST).split(","));
		boolean isBlackIP = blackList.contains(clientIp);
		Assert.state(!isBlackIP,"IP黑名单");
		List<String> whiteList = Arrays.asList(smsService.findSysConfigValue(CrmConstants.IP_WHITE_LIST).split(","));
		boolean isWhiteIp = whiteList.contains(clientIp);
		long maxCount = Long.valueOf(smsService.findSysConfigValue(CrmConstants.IP_MAX_COUNT,"0"));
		if(!isWhiteIp && maxCount > 0){
			long sendCount = smsService.statisticsSendSmsCount(clientIp);
			Assert.state(sendCount < maxCount,"IP发送次数达到上限");
		}
	}

	private void checkMobile(String mobile){
		List<String> blackList = Arrays.asList(smsService.findSysConfigValue(CrmConstants.MOBILE_BLACK_LIST).split(","));
		boolean isBlackMobile = blackList.contains(mobile);
		Assert.state(!isBlackMobile,"手机号黑名单");
		List<String> whiteList = Arrays.asList(smsService.findSysConfigValue(CrmConstants.MOBILE_WHITE_LIST).split(","));
		boolean isWhiteIp = whiteList.contains(mobile);
		long maxCount = Long.valueOf(smsService.findSysConfigValue(CrmConstants.MOBILE_MAX_COUNT,"0"));
		if(!isWhiteIp && maxCount > 0){
			long sendCount = smsService.statisticsSendSmsCount(mobile);
			Assert.state(sendCount < maxCount,"手机号发送次数达到上限");
		}
	}


	@Override
	public String checkUserInfo(UserInfoReqBo userInfoReqBo, BindingResult bindingResult) {
		// 解析绑定参数校验结果
		String result = ValidateUtil.processBindResult(bindingResult);
		if(StringUtils.isNoneBlank(result)) {
			return result;
		}
		
		// 判断身份证号格式是否正确
		boolean validateIDCard = IDCardUtil.validateIDCard(userInfoReqBo.getIdCard());
		if(!validateIDCard) {
			return "身份证号格式不正确";
		}
		
		// 判断性别是否与身份证号中性别相符
		SexEnums sex = IDCardUtil.extractSexFromIDCard(userInfoReqBo.getIdCard());
		if(!sex.name().equals(userInfoReqBo.getSex().name())) {
			return "性别不相符";
		}
		
		// 判断年龄是否和身份证中一致
		boolean validAgeByIdCard = IDCardUtil.validAgeByIdCard(userInfoReqBo.getIdCard(),
				userInfoReqBo.getAge());
		if(!validAgeByIdCard) {
			return "年龄不相符";
		}
		
		return null;
	}
    
}
