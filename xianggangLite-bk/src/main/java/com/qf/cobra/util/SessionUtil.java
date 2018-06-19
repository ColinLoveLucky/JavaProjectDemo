package com.qf.cobra.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import com.qf.cobra.pojo.LoginUser;

/**
 * @author Cobra Team
 */
public class SessionUtil {

	private static final ThreadLocal<String> currentToken = new ThreadLocal<String>();
	private static StringRedisTemplate stringRedisTemplate;
	
	public static void setCurrentToken(String token) {
		currentToken.set(token);
	}
	
	public static void clearCurrentToken() {
		currentToken.set(null);
	}
	
	public static LoginUser getCurrentUser() {
		String sessionUserStr = getStringRedisTemplate().opsForValue().get(DictItem.SESSION_USER_PREFFIX + currentToken.get());
		if (StringUtils.isEmpty(sessionUserStr)) {
			return null;
		}
		LoginUser loginUser = JsonUtil.convert(sessionUserStr, LoginUser.class);
		return loginUser;
	}
	
	public static String getCurrentToken() {
		return currentToken.get();
	}
	
	private static StringRedisTemplate getStringRedisTemplate(){
		if (stringRedisTemplate == null) {
			stringRedisTemplate = SpringBeanLocator.getBean(StringRedisTemplate.class);
		}
		return stringRedisTemplate;
	}
	
}
