package com.qf.cobra.loanapp.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.qf.cobra.exception.RespondFailedException;
import com.qf.cobra.exception.TokenException;
import com.qf.cobra.loanapp.LoanappProperties;
import com.qf.cobra.loanapp.service.ILoanAppService;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.util.HttpClientUtil;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.SessionUtil;

@Service
public class LoanAppServiceImpl implements ILoanAppService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoanAppServiceImpl.class);

	@Autowired
	LoanappProperties loanappProperties;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private static final String LOAN_APP_TOKEN = "LOAN_APP_TOKEN";

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> sendLoanApply(Map<String, Object> request) {
		Map<String, Object> resultObj = null;

		String token = getLoanAppToken();
		Map<String, String> headers = getDefaultHeadMap(token);

		Map<String, Object> param = request;
		String result = null;
		String url = loanappProperties.getApplyUrl();
		
		try {
			result = HttpClientUtil.doPostWithJson(url, param, headers);
			result = (result == null) ? "" : result;
			LOGGER.info("调用loanapp系统,请求参数为:{},返回结果为:{}", param, result);
		} catch (Exception e) {
			LOGGER.error("调用loanapp系统失败,出现异常,请求参数为:{}", param, e);
			throw new RespondFailedException(e);
		}
		
		if (result.contains("expired_accessToken")) {
			throw new TokenException("expired_accessToken");
		}
		if (StringUtils.isNotBlank(result)) {
			resultObj = JsonUtil.convert(result, Map.class);
		}
		return resultObj;
	}

	private String getLoanAppToken() {
		String result = null;
		try {
			String token = stringRedisTemplate.opsForValue().get(LOAN_APP_TOKEN);

			if (org.springframework.util.StringUtils.isEmpty(token)) {
				Map<String, Object> param = ImmutableMap.of("username",loanappProperties.getUsername(),
						"password",loanappProperties.getPassword(),
						"client_id",loanappProperties.getCasClientId(),
						"grant_type","password");
				Map headers = new HashMap();
				headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			    headers.put("apiKey", loanappProperties.getApiKey());
			    
				result = HttpClientUtil.doPostWithJson(loanappProperties.getTokenUrl(), param, headers);
				LOGGER.info("调用loanapp获取token,请求参数为:{},返回结果为:{}", param, result);
				Map<String, Object> resultObj = JsonUtil.convert(result, Map.class);
				if ("2000".equals(String.valueOf(resultObj.get("responseCode")))) {
					Map<String, String> rspBody = (Map<String, String>) resultObj.get("responseBody");
					token = rspBody.get("access_token");
					stringRedisTemplate.opsForValue().set(LOAN_APP_TOKEN, token, 7000, TimeUnit.SECONDS);
				} else {
					throw new TokenException("get_token_fail");
				}
			}
			return token;
		} catch (Exception e) {
			LOGGER.error("获取loanApp token失败", e);
			throw new TokenException("get_token_fail");
		}
	}

	private Map<String, String> getDefaultHeadMap(String token) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.put("tenantId", loanappProperties.getTenantId());
		headers.put("clientId", loanappProperties.getClientId());
		headers.put("productId", loanappProperties.getProductId());
		headers.put("apiKey", loanappProperties.getApiKey());
		headers.put("Authorization", token);
		return headers;
	}

	@Override
	public Map<String, Object> getAppIdFromLoanApp() {
		Map<String, Object> resultObj = null;
		LoginUser user = SessionUtil.getCurrentUser();
		String token = user == null ? "" : user.getToken();
		Map<String, String> headers = getDefaultHeadMap(token);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", loanappProperties.getProductId());
		String result = null;
		String url = loanappProperties.getAppIdUrl();
		try {
			result = HttpClientUtil.doPostWithJson(url, param, headers);
			result = (result == null) ? "" : result;
			LOGGER.info("调用loanapp系统,请求参数为:{},请求头为:{},返回结果为:{}", param,headers, result);
		} catch (Exception e) {
			LOGGER.error("调用loanapp系统失败,出现异常,请求参数为:{}", param, e);
			throw new RespondFailedException(e);
		}
		if (result.contains("expired_accessToken")) {
			throw new TokenException("expired_accessToken");
		}
		// Pattern pattern = Pattern
		// .compile("(?<=\"responseBody\":\")(.*?)(?=\")");
		// Matcher matcher = pattern.matcher(result);
		// if (matcher.find()) {
		// appId = StringUtils.trim(matcher.group(1));
		// }
		if (StringUtils.isNotBlank(result)) {
			resultObj = JsonUtil.convert(result, Map.class);
		}

		return resultObj;
	}

}
