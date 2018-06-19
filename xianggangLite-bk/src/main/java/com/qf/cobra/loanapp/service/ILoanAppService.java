package com.qf.cobra.loanapp.service;

import java.util.Map;

public interface ILoanAppService {
	
	/**
	 * 借款申请提交
	 * @param request
	 * @param header
	 * @return
	 */
	Map<String, Object> sendLoanApply(Map<String, Object> request);
	
	/**
	 * 借款申请编号
	 * @return token
	 */
	Map<String, Object> getAppIdFromLoanApp();
}
