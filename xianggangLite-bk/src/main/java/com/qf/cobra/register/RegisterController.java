package com.qf.cobra.register;

import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private CRMRegisterService crmRegisterService;

    public ResponseData<Map<String, Object>> registerUser() {
        ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();

        return responseData;
    }

    @PostMapping("/sendMsg")
    public ResponseData<Map<String, Object>> sendMsg(@RequestBody Map<String, Object> params) {
        ResponseData responseData = ResponseUtil.defaultResponse();
        try {
            Object resultData = crmRegisterService.sendMsg(params);
            responseData.setData(resultData);
        } catch (Exception e) {
            LOGGER.error("短信验证码发送失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg(e.getMessage());
        }
        return responseData;
    }

    @PostMapping("/regist")
    public ResponseData<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
        try {
            boolean result = crmRegisterService.register(params);
            if (!result) {
                throw new Exception("用户注册失败");
            }
        } catch (Exception e) {
            LOGGER.error("用户注册失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg(e.getMessage());
        }
        return responseData;
    }

}
