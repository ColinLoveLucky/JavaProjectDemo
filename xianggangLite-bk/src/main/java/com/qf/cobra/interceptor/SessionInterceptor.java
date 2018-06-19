package com.qf.cobra.interceptor;

import com.qf.cobra.loanapp.BorrowerLiteProperties;
import com.qf.cobra.login.CRMLoginService;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class SessionInterceptor implements HandlerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionInterceptor.class);
	
	private static StringRedisTemplate stringRedisTemplate;
//	private static QAuthenticationService qAuthenticationService;
	private static BorrowerLiteProperties borrowerLiteProperties;

	private static CRMLoginService crmLoginService;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		SessionUtil.clearCurrentToken();
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView modelAndView)
			throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		String token = request.getHeader("authorization");
		LOGGER.info("拦截器【请求路径：{}，请求主机：{}，token：{}】", new Object[]{request.getRequestURL().toString(), request.getRemoteHost(), token});
		String sessionUserKey = DictItem.SESSION_USER_PREFFIX + token;
		String sessionTokenKey = DictItem.SESSION_TOKEN_PREFFIX + token;
		getStringRedisTemplate().expire(sessionUserKey, getBorrowerLiteProperties().getSessionTimeout(), TimeUnit.SECONDS);
		if (getStringRedisTemplate().hasKey(sessionUserKey)) {
			SessionUtil.setCurrentToken(token);
			if (!getStringRedisTemplate().hasKey(sessionTokenKey)) {
				String newToken = getCRMLoginService().refreshToken(SessionUtil.getCurrentUser().getRefreshToken());
				String sessionUserStr = getStringRedisTemplate().opsForValue().get(sessionUserKey);
				LoginUser loginUser = JsonUtil.convert(sessionUserStr, LoginUser.class);
				loginUser.setToken(newToken);
				String newSessionUserKey = DictItem.SESSION_USER_PREFFIX + newToken;
				String newSessionTokenKey = DictItem.SESSION_TOKEN_PREFFIX + newToken;
				getStringRedisTemplate().opsForValue().set(newSessionUserKey, JsonUtil.convert(loginUser));
				getStringRedisTemplate().expire(newSessionUserKey, getBorrowerLiteProperties().getSessionTimeout(), TimeUnit.SECONDS);
				getStringRedisTemplate().opsForValue().set(newSessionTokenKey, "1");
				getStringRedisTemplate().expire(newSessionTokenKey, (getBorrowerLiteProperties().getTokenTimeout() - getBorrowerLiteProperties().getSessionTimeout() - 60), TimeUnit.SECONDS);
				SessionUtil.setCurrentToken(newToken);
				getStringRedisTemplate().delete(sessionUserKey);
				getStringRedisTemplate().delete(sessionTokenKey);
			}
			return true;
		} else {
			ResponseData<LoginUser> responseData = ResponseUtil.defaultResponse();
			responseData.setCode(ResponseCode.TOKEN_INVALID);
			responseData.setMsg("用户会话超时");
			response.setContentType("application/json; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.convert(responseData));
			return false;
		}
	}

	
	private static StringRedisTemplate getStringRedisTemplate(){
		if (stringRedisTemplate == null) {
			stringRedisTemplate = SpringBeanLocator.getBean(StringRedisTemplate.class);
		}
		return stringRedisTemplate;
	}
	
	private static BorrowerLiteProperties getBorrowerLiteProperties(){
		if (borrowerLiteProperties == null) {
			borrowerLiteProperties = SpringBeanLocator.getBean(BorrowerLiteProperties.class);
		}
		return borrowerLiteProperties;
	}

	private static CRMLoginService getCRMLoginService(){
		if (crmLoginService == null) {
			crmLoginService = SpringBeanLocator.getBean(CRMLoginService.class);
		}
		return crmLoginService;
	}
}
