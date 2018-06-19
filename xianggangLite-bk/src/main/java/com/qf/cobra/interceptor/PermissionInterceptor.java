package com.qf.cobra.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qf.cobra.annotation.Permission;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.SessionUtil;

public class PermissionInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionInterceptor.class);

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean flag = true;
		// 如果不是映射到方法直接通过
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Permission perm = handlerMethod.getMethod().getAnnotation(Permission.class);
		if (perm != null) {
			LoginUser loginUser = SessionUtil.getCurrentUser();
			if (!loginUser.getPermList().contains(perm.value())) {
				ResponseData<LoginUser> responseData = ResponseUtil.defaultResponse();
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg("没有操作权限!");
				response.setContentType("application/json; charset=utf-8");
				response.setCharacterEncoding("utf-8");
				response.getWriter().write(JsonUtil.convert(responseData));
				flag = false;
			}
		}
		return flag;
	}

}
