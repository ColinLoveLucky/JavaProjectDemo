package com.qf.cobra.loan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.IPushQAppService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;

@Service("PushQappReviceTask")
public class ReviceTaskPushQappImpl extends ReviceTask {
	// private Logger log = LoggerFactory.getLogger(getClass());

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
			pushService.pushQApp(appId);
		} catch (Exception e) {
			isSuccess = false;
			errorNoticeService.notice(appId,"流程推送数据失败","流程推送数据失败",e);
		}
		return isSuccess;
	}

	@Override
	protected void compeleteTask(TaskNodeInfo taskNodeInfo) {
		//审核完成推送进件信息给QAPP时更新taskNodeInfo为COMPELETE_STATUS_DONE
		loanService.updateTaskStatus(taskNodeInfo.getAppId(),ReceiveServiceEnum.PUSH_QAPP);
	}

}
