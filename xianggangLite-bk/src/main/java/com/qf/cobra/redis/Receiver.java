package com.qf.cobra.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.util.JsonUtil;

@Component
@Deprecated
public class Receiver {

	private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

	@Autowired
	private LoanService loanService;

	public void receiveMessage(String message) {
		LOGGER.info("receive message:" + message);
		Map<String, String> paramMap = JsonUtil.convert(message, Map.class);
//		loanService.compeletePreAuditCallbackUserTask(paramMap);
	}
}
