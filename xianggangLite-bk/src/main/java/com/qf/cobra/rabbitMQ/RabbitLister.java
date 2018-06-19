package com.qf.cobra.rabbitMQ;

import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.CarrieroperatorResult;
import com.qf.cobra.util.JsonUtil;

//@Component
//@RabbitListener(containerFactory = "rabbitListenerContainerFactory",
//		bindings = @QueueBinding(value = @Queue(value = "cobra.status.thirdparty",durable="true"), 
//		exchange = @Exchange(value = "notice", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"), 
//		key = "ndes.status.thirdparty.*"))
public class RabbitLister {
	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitLister.class);
	@Autowired
	private LoanService loanService;

	// message
	// {"serviceCode":"","extId":"","result":{"stateCode":"","stateDesc":""}}
	@SuppressWarnings("unchecked")
	@RabbitHandler
	public void process(String message) {
		try {
//			String message = new String(m,"UTF-8");
			LOGGER.info("接收N-des运营商认证结果：{}", message);
			Map<String, Object> requestMap = JsonUtil.convert(message, Map.class);
			Map<String, String> result = (Map<String, String>) requestMap.get("result");
			CarrieroperatorResult carrieroperatorResult = new CarrieroperatorResult();
			String stateCode = result.get("stateCode");
			carrieroperatorResult.setAppId(requestMap.get("extId").toString());
//			carrieroperatorResult.setPolicyCode(requestMap.get("policyCode").toString());
			carrieroperatorResult.setServiceCode(requestMap.get("serviceCode").toString());
			carrieroperatorResult.setStatus(stateCode);
			if ("AUTH_FAIL".equals(stateCode) || "AUTH_SUC".equals(stateCode)) {
				carrieroperatorResult.setType("AUTH");
			} else {
				carrieroperatorResult.setType("DATA");
			}
			carrieroperatorResult.setMessageDesc(result.get("stateDesc"));
			carrieroperatorResult.setTimestamp(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
			loanService.saveCarrieroperatorResult(carrieroperatorResult);
			//noticeClientUpdateStatus(carrieroperatorResult);
		} catch (Exception e) {
			LOGGER.error("接收N-des运营商认证结果或保存异常,:{}", message, e);
		}
	}

}
