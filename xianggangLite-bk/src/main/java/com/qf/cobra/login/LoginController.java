package com.qf.cobra.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qf.cobra.log.service.ISystemLogService;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.pojo.SystemLog;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.SystemOperation;

import java.util.Map;

@RestController
public class LoginController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private CRMLoginService crmLoginService;
	@Autowired
	private ISystemLogService systemLogService;
	
	@PostMapping("/login")
	public ResponseData<LoginUser> login(@RequestBody Map<String, Object> params) {
		ResponseData<LoginUser> responseData = ResponseUtil.defaultResponse();
		try {
			responseData.setData(crmLoginService.login(params));
		} catch (Exception e) {
			LOGGER.error("用户登录失败!", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("用户登录失败");
		}
		return responseData;
	}

	@GetMapping("/logout")
	public ResponseData logout() {
		ResponseData<LoginUser> responseData = ResponseUtil.defaultResponse();
		try {
			crmLoginService.logout();
		} catch (Exception e) {
			LOGGER.error("用户退出失败!", e);
		}
		return ResponseUtil.defaultResponse();
	}

	@PostMapping("/forgotPwd")
	public ResponseData<Map<String, Object>> forgotPwd(@RequestBody Map<String, Object> params) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			boolean result = crmLoginService.forgotPwd(params);
			if (!result) {
				throw new Exception("忘记密码验证失败");
			}
		} catch (Exception e) {
			LOGGER.error("忘记密码验证失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	@PostMapping("/modifyPwd")
	public ResponseData<Map<String, Object>> modifyPwd(@RequestBody Map<String, Object> params) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			boolean result = crmLoginService.modifyPwd(params);
			if (!result) {
				throw new Exception("修改密码失败");
			}
		} catch (Exception e) {
			LOGGER.error("修改密码失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	@GetMapping("/userInfo/{userId}")
	public ResponseData<Map<String, Object>> userInfo(@PathVariable("userId") String userId) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> result = crmLoginService.userInfo(userId);
			responseData.setData(result);
		} catch (Exception e) {
			LOGGER.error("查询用户信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	@PostMapping("/realname")
	public ResponseData<Map<String, Object>> realname(@RequestBody Map<String, Object> params) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			boolean result = crmLoginService.realname(params);
			if (!result) {
				throw new Exception("实名认证失败");
			}
		} catch (Exception e) {
			LOGGER.error("实名认证失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

}
