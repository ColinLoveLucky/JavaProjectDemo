package com.quark.cobra.controller;

import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.Utils.ResponseCode;
import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.domain.SendSmsReqBo;
import com.quark.cobra.entity.User;
import com.quark.cobra.enums.SmsTypeEnums;
import com.quark.cobra.service.ICheckService;
import com.quark.cobra.service.ISmsService;
import com.quark.cobra.service.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {
	
    @Autowired
    private ISmsService smsService;

    @Autowired
    private ICheckService checkService;

    @ApiOperation("发送验证码短信")
    @PostMapping("/sendMsg")
    public ResponseData getMobileCaptcha(@RequestBody SendSmsReqBo sendSmsReq) {
    	log.info("发送短信请求参数:{}", JsonUtil.convert(sendSmsReq));
        checkService.checkSendSms(sendSmsReq);
        String imageCaptcha = sendSmsReq.getImageCaptcha();
        if (smsService.needImageCaptcha(sendSmsReq.getMobile()) && StringUtils.isEmpty(imageCaptcha)) {
            Map<String, Object> map = new HashMap<>();
            map.put("hasImageCaptcha", true);
            return ResponseData.ok(map);
        }
        smsService.sendMessage(sendSmsReq.getMobile(),sendSmsReq.getSmsType());
        smsService.increaseSendSmsCount(sendSmsReq.getClientIp());
        smsService.increaseSendSmsCount(sendSmsReq.getMobile());
        return ResponseData.ok();
    }
}
