package com.qf.cobra.rabbitMQ;

import java.util.Map;

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
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.util.JsonUtil;

@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory"
, bindings = @QueueBinding(value = @Queue(value = "qyj.fee.qkpie", durable = "true")
, exchange = @Exchange(value = "qyj.fee", type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true",durable="true"), key = "qyj.fee.*"))
public class XdtRabbitLister {
	private static final Logger LOGGER = LoggerFactory.getLogger(XdtRabbitLister.class);
	@Autowired
	private LoanService loanService;

	// {
	// "batch_id": "008",
	// "interface_id": "QUARKLOAN-API-0076",
	// "bill_no": "test",
	// "pact_show_no": "test",
	// "total": "10",
	// "amount": "10",
	// "pact_amt": "1000",
	// "code":"", 3-扣费成功 4-部分扣费成功 5-扣费失败
	// "msg":""
	// }
	@SuppressWarnings("unchecked")
	@RabbitHandler
	public void process(String message) {
		try {
			LOGGER.info("接收xdt放款结果：{}", message);
			Map<String, String> requestMap = JsonUtil.convert(message, Map.class);
			String billNo = requestMap.get("bill_no");// 进件号
			String code = requestMap.get("code");//  3-扣费成功 4-部分扣费成功 5-扣费失败

			if ("3".equals(code)) {
				LoanApply.AppStatusEnum status = LoanApply.AppStatusEnum.PAYMENT_STATUS_SUCCESS;
				loanService.saveLoanAppStatusXdt(billNo, status);
			}
			
		} catch (Exception e) {
			LOGGER.error("接收xdt放款结果或保存异常,:{}", message, e);
		}
	}

}
