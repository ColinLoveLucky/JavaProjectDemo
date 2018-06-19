package com.quark.cobra.service;

import com.quark.cobra.AppTest;
import com.quark.cobra.domain.SendSmsReqBo;
import com.quark.cobra.enums.SmsTypeEnums;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ISmsServiceTest extends AppTest {
    String mobile = "";
    @Autowired
    private ISmsService smsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void sendRegistrationMessage() throws Exception {
    	SendSmsReqBo sendSmsReqBo = new SendSmsReqBo();
    	sendSmsReqBo.setMobile(mobile);
    	sendSmsReqBo.setSmsType(SmsTypeEnums.REGISTRATION);
        smsService.sendMessage(sendSmsReqBo.getMobile(),sendSmsReqBo.getSmsType());
        System.out.println(stringRedisTemplate.opsForValue().get("REG_" + mobile));
    }
}
