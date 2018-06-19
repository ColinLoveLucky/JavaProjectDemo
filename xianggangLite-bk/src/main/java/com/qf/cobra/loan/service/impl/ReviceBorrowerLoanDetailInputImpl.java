package com.qf.cobra.loan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;

@Service("BorrowerLoanDetailInput")
public class ReviceBorrowerLoanDetailInputImpl extends ReviceTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReviceBorrowerLoanDetailInputImpl.class);
	@Autowired
	private LoanService loanService;

	@Override
	public Boolean excute(String appId) {
		boolean isSuccess = true;
		try {
			// 进件详情录入修改进件状态为补全中
//			loanService.saveLoanAppStatus(appId, AppStatusEnum.COMPLEMENTING);
		} catch (Exception e) {
			isSuccess = false;
		}
		return isSuccess;
	}

}
