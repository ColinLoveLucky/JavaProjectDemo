package com.qf.cobra.loan.controller;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RequestMapping("/rest")
@RestController
@SuppressWarnings("rawtypes")
public class BpmController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmController.class);

	@Autowired
	BeanFactory beanFactory;
	@Autowired
	private ThreadPoolTaskExecutor poolTask;
	@Autowired
	private ThreadPoolTaskExecutor poolTaskMobile;

	/**
	 * 进件规则
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/receiveTaskLoanApplyPolicy", method = RequestMethod.POST)
	public ResponseData receiveTaskLoanApplyPolicy(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {

			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.LOAN_APPLY_RULE.getServiceCode());
				receiveTask(taskNodeInfo, "LoanApplyRuleReviceTask");
				return ResponseUtil.defaultResponse();
			}

		});
		return ResponseUtil.defaultResponse();
	}

	/**
	 * 非运营商规则
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/reviceTaskNonMobile", method = RequestMethod.POST)
	public ResponseData receiveTaskNonMobile(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.NON_MOBILE_RULE.getServiceCode());
				receiveTask(taskNodeInfo, "NonMobileRuleReviceTask");
				return ResponseUtil.defaultResponse();
			}
		});

		return ResponseUtil.defaultResponse();
	}

	/**
	 * 通话详单决策规则
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/receiveTaskCallReportPolicy", method = RequestMethod.POST)
	public ResponseData receiveTaskCallReportPolicy(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.CALL_REPORT_POLICY_RULE.getServiceCode());
				receiveTask(taskNodeInfo, "CallReportPolicyRuleService");
				return ResponseUtil.defaultResponse();
			}
		});
		return ResponseUtil.defaultResponse();
	}

	/**
	 * 初审拒绝、终审通过/拒绝 推送进件信息至Qapp
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/reviceTaskPushQapp", method = RequestMethod.POST)
	public ResponseData reviceTaskPushQapp(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.PUSH_QAPP.getServiceCode());
				receiveTask(taskNodeInfo, "PushQappReviceTask");
				return ResponseUtil.defaultResponse();
			}
		});
		return ResponseUtil.defaultResponse();
	}

	/**
	 * 初审超时处理（拒绝）
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/reviceTaskOverDue", method = RequestMethod.POST)
	public ResponseData reviceTaskOverDue(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.OVER_DUE.getServiceCode());
				receiveTask(taskNodeInfo, "OverDueReviceTask");
				return ResponseUtil.defaultResponse();
			}
		});
		return ResponseUtil.defaultResponse();
	}

	private void receiveTask(TaskNodeInfo taskNodeInfoReq, String serviceNameReq) {
		final TaskNodeInfo taskNodeInfo = taskNodeInfoReq;
		final String serviceName = serviceNameReq;
		LOGGER.info("接收流程任务,请求参数为:{}", JsonUtil.convert(taskNodeInfoReq));
		ReviceTask reviceTaskService = beanFactory.getBean(serviceName, ReviceTask.class);
		reviceTaskService.excute(taskNodeInfo);
	}
	
	
	
	//////////////////////////
	/**
	 * 进件规则
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/borrower/receiveLoanApplyPolicy", method = RequestMethod.POST)
	public ResponseData receiveLoanApplyPolicy(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {

			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.BORROWER_LOAN_APPLY_RULE.getServiceCode());
				receiveTask(taskNodeInfo, "BorrowerLoanPolicy");
				return ResponseUtil.defaultResponse();
			}

		});
		return ResponseUtil.defaultResponse();
	}
	
	/**
	 * 岭勇详情录入
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/borrower/receiveLoanAppDetailInput", method = RequestMethod.POST)
	public ResponseData receiveLoanAppDetailInput(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.BORROWER_LOAN_APPLY_DETAIL_LY.getServiceCode());
				receiveTask(taskNodeInfo, "BorrowerLoanDetailInput");
				return ResponseUtil.defaultResponse();
			}
		});

		return ResponseUtil.defaultResponse();
	}
	
	/**
	 * 推送NCobra
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/borrower/receivePushLoanAppToNCobra", method = RequestMethod.POST)
	public ResponseData receivePushLoanAppToNCobra(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.BORROWER_LOAN_APPLY_PUSH_NCOBRA.getServiceCode());
				receiveTask(taskNodeInfo, "BorrowerLoanPush2NCobra");
				return ResponseUtil.defaultResponse();
			}
		});

		return ResponseUtil.defaultResponse();
	}
	
	/**
	 * 推送BID
	 * 
	 * @param taskNodeInfo
	 * @return
	 */
	@RequestMapping(value = "/borrower/receivePushLoanAppToBid", method = RequestMethod.POST)
	public ResponseData receivePushLoanAppToBid(@RequestBody TaskNodeInfo taskNodeInfo) {
		poolTask.submit(new Callable<ResponseData>() {
			@Override
			public ResponseData call() throws Exception {
				Thread.sleep(2000);
				taskNodeInfo.setServiceCode(ReceiveServiceEnum.BORROWER_LOAN_APPLY_PUSH_BID.getServiceCode());
				receiveTask(taskNodeInfo, "BorrowerLoanPush2Bid");
				return ResponseUtil.defaultResponse();
			}
		});

		return ResponseUtil.defaultResponse();
	}
}
