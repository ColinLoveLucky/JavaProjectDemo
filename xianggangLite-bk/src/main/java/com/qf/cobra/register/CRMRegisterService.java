package com.qf.cobra.register;

import com.qf.cobra.feign.service.CrmService;
import com.qf.cobra.mc.constant.McMessageConstant;
import com.qf.cobra.mc.enums.McAppTypeEnum;
import com.qf.cobra.mc.enums.McClientSourceEnum;
import com.qf.cobra.mc.enums.McSourceTypeEnum;
import com.qf.cobra.mc.service.McMessageService;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.StrUtil;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CRMRegisterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CRMRegisterService.class);

    @Autowired
    private CrmService crmService;
    
    @Autowired
    private McMessageService mcMessageService;

    public Object sendMsg(Map<String, Object> params) throws Exception {
        Object resultData = null;
        try {
            String rspStr = crmService.sendMsg(params);
            LOGGER.info("调用CRM发送短信验证码，请求参数：{}，返回：{}", params, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                resultData = rspMap.get("data");
            } else {
                throw new Exception(String.valueOf(rspMap.get("msg")));
            }
        } catch (Exception e) {
            LOGGER.error("短信验证码发送失败", e);
            throw e;
        }
        return resultData;
    }

    public boolean register(Map<String, Object> params) throws Exception {
        boolean result = false;
        try {
            String rspStr = crmService.register(params);
            LOGGER.info("调用CRM用户注册，请求参数：{}，返回：{}", params, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                result = true;
                Map<String, Object> dataMap = MapUtils.getMap(rspMap, "data");
                if(null != dataMap) {
                	// 注册成功发送MQ消息到MC平台
                    mcMessageService.sendRegisterMqMessage(StrUtil.obj2Str(dataMap.get(McMessageConstant.USER_ID_KEY)),
                    		StrUtil.obj2Str(params.get(McMessageConstant.MOBILE_KEY)),
                    		McSourceTypeEnum.BORROW.toString(), McClientSourceEnum.WEB.toString(), McAppTypeEnum.QYJ.toString());
                }
            } else {
                throw new Exception(String.valueOf(rspMap.get("msg")));
            }
        } catch (Exception e) {
            LOGGER.error("用户注册失败", e);
            throw e;
        }
        return result;
    }

}
