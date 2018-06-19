package com.qf.cobra.feign.service;

import com.qf.cobra.pojo.SysConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "${feign.crm.service-id}", configuration = FeignSimpleEncoderConfig.class)
public interface CrmService {

//    feign.crm.service-id=crm-api
//    feign.crm.user-uri=/user
//    feign.crm.token-uri=/oauth/token
//    feign.crm.sms-uri=/sms/sendMsg
//    feign.crm.register-uri=/user/registration
//    feign.crm.forgot-pwd-uri=/user/{userId}/forgotPwd
//    feign.crm.modify-pwd-uri=/user/{userId}/modifyPwd

    @RequestMapping(value = "${feign.crm.user-uri}?access_token={token}"
            , method = RequestMethod.GET)
    public String getUser(@PathVariable("token") String token);

    @RequestMapping(value = "${feign.crm.user-info-uri}/{userId}?access_token={token}"
            , method = RequestMethod.GET)
    public String getUserInfo(@PathVariable("userId") String userId, @PathVariable("token") String token);

    @RequestMapping(value = "${feign.crm.token-uri}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , headers = { "apiKey=${feign.crm.apiKey}"} )
    public String getToken(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.sms-uri}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String sendMsg(Map<String, Object> params);


    @RequestMapping(value = "${feign.crm.register-uri}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String register(Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.forgot-pwd-uri}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String forgotPwd(Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.modify-pwd-uri}?access_token={token}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String modifyPwd(@PathVariable("token") String token, @PathVariable("userId") String userId, Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.realname-uri}?access_token={token}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public String realname(@PathVariable("token") String token, Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.refresh-token-uri}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String refreshToken(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "${feign.crm.queryUsers}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    String queryUsers(Map<String,Object> parmas);
    @RequestMapping(value = "${feign.crm.querySysConfig}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    String querySysConfig(Map<String,Object> parmas);
    @RequestMapping(value = "${feign.crm.saveSysConfig}"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    String saveSysConfig(SysConfig sysConfig);
    @RequestMapping(value = "${feign.crm.saveSysConfig}"
            , method = RequestMethod.PUT
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    String editSysConfig(SysConfig sysConfig);
    @RequestMapping(value = "${feign.crm.removeSysConfig}"
            , method = RequestMethod.DELETE
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    String removeSysConfig(@PathVariable("key") String key);
    @RequestMapping(value = "${feign.crm.findSysConfigById}"
            , method = RequestMethod.GET)
    String findSysConfigById(@PathVariable("id") String id);

}
