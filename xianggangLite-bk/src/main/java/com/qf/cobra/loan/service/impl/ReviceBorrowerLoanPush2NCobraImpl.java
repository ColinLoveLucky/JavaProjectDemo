package com.qf.cobra.loan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.ReviceTask;

@Service("BorrowerLoanPush2NCobra")
public class ReviceBorrowerLoanPush2NCobraImpl extends ReviceTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReviceBorrowerLoanPush2NCobraImpl.class);

	@Autowired
	private LoanService loanService;
	@Autowired
	private ErrorNoticeService errorNoticeService;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean excute(String appId) {
		boolean isSuccess = true;
		try {
			// 推送loan至ncobra审核
			isSuccess = loanService.pushLoanAppToNCobra(appId);
		} catch (Exception e) {
			isSuccess = false;
			errorNoticeService.notice(appId, "流程推送数据失败", "流程推送数据失败", e);
		}
		return isSuccess;
	}

}
