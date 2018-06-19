package com.qf.cobra.login;

import com.google.common.collect.ImmutableMap;
import com.qf.cobra.feign.service.CrmService;
import com.qf.cobra.mc.enums.McAppTypeEnum;
import com.qf.cobra.mc.enums.McClientSourceEnum;
import com.qf.cobra.mc.enums.McSourceTypeEnum;
import com.qf.cobra.mc.service.McMessageService;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.SessionUtil;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CRMLoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CRMLoginService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CrmService crmService;

    @Value("${borrowerLite.sessionTimeout}")
    private int sessionTimeout;
    @Value("${borrowerLite.tokenTimeout}")
    private int tokenTimeout;

    @Value("${borrowerLite.crm.client_id}")
    private String clientId;
    @Value("${borrowerLite.crm.client_secret}")
    private String clientSecret;

    @Value("${feign.crm.service-id}")
    private String crmServiceId;
    @Value("${feign.crm.token-uri}")
    private String crmTokenUri;
    
    @Autowired
    private McMessageService mcMessageService;

    /*{
        "access_token": "de35b140-90d4-48df-ad6f-e1719f8befe3",
            "token_type": "bearer",
            "refresh_token": "3484e34e-8981-4b50-a71c-942d3adf50b0",
            "expires_in": 7199,
            "scope": "read write openid"
    }*/
    public LoginUser login(Map<String, Object> params) throws Exception {
        LoginUser loginUser = null;
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8"));

        String rspStr = crmService.getToken(params);
        LOGGER.info("调用CRM登录，请求参数：{}，返回：{}", params, rspStr);
        Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
        String accessToken = String.valueOf(rspMap.get("access_token"));
        String refreshToken = String.valueOf(rspMap.get("refresh_token"));
        if (!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(refreshToken)) {
            loginUser = new LoginUser();
            loginUser.setToken(accessToken);
            loginUser.setRefreshToken(refreshToken);
            getUserDetail(loginUser);

            String sessionUserKey = DictItem.SESSION_USER_PREFFIX + accessToken;
            String sessionTokenKey = DictItem.SESSION_TOKEN_PREFFIX + loginUser.getToken();
            stringRedisTemplate.opsForValue().set(sessionUserKey, JsonUtil.convert(loginUser));
            stringRedisTemplate.expire(sessionUserKey, sessionTimeout, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set(sessionTokenKey, "1");
            stringRedisTemplate.expire(sessionTokenKey, (tokenTimeout - sessionTimeout - 60), TimeUnit.SECONDS);
        }
        return loginUser;
    }

    private void getUserDetail(LoginUser loginUser) throws Exception {
        String rspStr = crmService.getUser(loginUser.getToken());
        LOGGER.info("调用CRM获取用户信息，请求参数：{}，返回：{}", loginUser.getToken(), rspStr);
        Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
        if ("20000".equals(String.valueOf(rspMap.get("code")))) {
            Map<String, String> userDetail = (Map<String, String>) rspMap.get("data");
            loginUser.setDisplayName(userDetail.get("idName"));
            loginUser.setUserId(userDetail.get("id"));
        } else {
            throw new Exception("获取用户信息失败");
        }
    }

    public void logout() {
        LoginUser loginUser = SessionUtil.getCurrentUser();
        String sessionUserKey = DictItem.SESSION_USER_PREFFIX + loginUser.getToken();
        String sessionTokenKey = DictItem.SESSION_TOKEN_PREFFIX + loginUser.getToken();
        stringRedisTemplate.delete(sessionUserKey);
        stringRedisTemplate.delete(sessionTokenKey);
    }

    public boolean forgotPwd(Map<String, Object> params) throws Exception {
        boolean result = false;
        try {
            String rspStr = crmService.forgotPwd(params);
            LOGGER.info("调用CRM忘记密码，请求参数：{}，返回：{}", params, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                result = true;
            } else {
                throw new Exception(String.valueOf(rspMap.get("msg")));
            }
        } catch (Exception e) {
            LOGGER.error("忘记密码验证失败", e);
            throw e;
        }
        return result;
    }

    public boolean modifyPwd(Map<String, Object> params) throws Exception {
        boolean result = false;
        try {
            String userId = SessionUtil.getCurrentUser().getUserId();
            String rspStr = crmService.modifyPwd(SessionUtil.getCurrentToken(), userId, params);
            LOGGER.info("调用CRM修改密码，请求参数：{}，返回：{}", params, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                result = true;
            } else {
                throw new Exception(String.valueOf(rspMap.get("msg")));
            }
        } catch (Exception e) {
            LOGGER.error("修改密码失败", e);
            throw e;
        }
        return result;
    }

    public Map<String, Object> userInfo(String userId) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String rspStr = crmService.getUserInfo(userId, SessionUtil.getCurrentToken());
            LOGGER.info("调用CRM获取用户信息，请求参数：{}，返回：{}", userId, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                result = (Map<String, Object>) rspMap.get("data");
            } else {
                throw new Exception(String.valueOf(rspMap.get("msg")));
            }
        } catch (Exception e) {
            LOGGER.error("获取用户信息失败", e);
            throw e;
        }
        return result;
    }



    public boolean realname(Map<String, Object> params) throws Exception {
        boolean result = false;
        try {
            String rspStr = crmService.realname(SessionUtil.getCurrentToken(), params);
            LOGGER.info("调用CRM实名认证，请求参数：{}，返回：{}", params, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if (!"20000".equals(String.valueOf(rspMap.get("code"))) || !"OK".equalsIgnoreCase(MapUtils.getString(MapUtils.getMap(rspMap, "data"), "authStatus"))) {
            	throw new Exception("实名失败");
            } else {
            	result = true;
            	LoginUser loginUser = SessionUtil.getCurrentUser();
            	if(null != loginUser) {
            		Map<String, Object> userInfoMap = this.userInfo(loginUser.getUserId());
            		// 实名认证成功发送MQ消息到MC平台
                	mcMessageService.sendRealnameMqMessage(userInfoMap, McSourceTypeEnum.BORROW.toString(), McClientSourceEnum.WEB.toString(),
                			McAppTypeEnum.QYJ.toString());
            	}
            }
        } catch (Exception e) {
            LOGGER.error("实名认证失败", e);
            throw e;
        }
        return result;
    }


    /*{
        "access_token": "4563a38b-3296-430d-ad84-fc84121ce8f1",
            "token_type": "bearer",
            "refresh_token": "3c60ea77-e535-4005-9fef-b864212d147a",
            "expires_in": 7199,
            "scope": "read write openid"
    }*/
    /*grant_type:refresh_token
        refresh_token:3c60ea77-e535-4005-9fef-b864212d147a
        client_id:quark_qyj-borrower_qyj
        client_secret:secret*/
    public String refreshToken(String refreshToken) {
        Map<String, Object> params = ImmutableMap.of("grant_type","refresh_token",
                        "refresh_token",refreshToken,
                        "client_id",clientId,
                        "client_secret",clientSecret);

        String rspStr = crmService.refreshToken(params);
        LOGGER.info("调用CRM刷新token，请求参数：{}，返回：{}", params, rspStr);
        Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
        String accessToken = null;
        if (!org.springframework.util.StringUtils.isEmpty(rspMap.get("access_token"))) {
            accessToken = String.valueOf(rspMap.get("access_token"));
        }
        return accessToken;
    }



}
