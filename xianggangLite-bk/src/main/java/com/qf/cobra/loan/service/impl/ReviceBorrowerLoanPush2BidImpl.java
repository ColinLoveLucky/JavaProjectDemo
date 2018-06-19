package com.qf.cobra.loan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.IPushQAppService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;

@Service("BorrowerLoanPush2Bid")
public class ReviceBorrowerLoanPush2BidImpl extends ReviceTask{
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ReviceBorrowerLoanPush2BidImpl.class);
	
	@Autowired
	private IPushQAppService pushService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private ErrorNoticeService errorNoticeService;
	
	
	@Override
	public Boolean excute(String appId) {
		boolean isSuccess = true;
		try {
			//推送loan至标的系统bid
			pushService.pushQApp(appId);
		} catch (Exception e) {
			isSuccess = false;
			errorNoticeService.notice(appId,"流程推送数据失败","流程推送数据失败",e);
		}
		return isSuccess;
	}

	@Override
	protected void compeleteTask(TaskNodeInfo taskNodeInfo) {
		//审核完成推送进件信息给bid时更新taskNodeInfo为COMPELETE_STATUS_DONE
		loanService.updateTaskStatus(taskNodeInfo.getAppId(),ReceiveServiceEnum.BORROWER_LOAN_APPLY_PUSH_BID);
	}
	
}
