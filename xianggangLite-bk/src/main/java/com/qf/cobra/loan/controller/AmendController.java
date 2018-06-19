package com.qf.cobra.loan.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.IPushQAppService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.impl.ReviceTaskCallReportRuleImpl;
import com.qf.cobra.loan.service.impl.ReviceTaskLoanApplyRuleImpl;
import com.qf.cobra.loan.service.impl.ReviceTaskMobileAuthImpl;
import com.qf.cobra.loan.service.impl.ReviceTaskNonMobileRuleImpl;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RequestMapping("/rest")
@RestController
@SuppressWarnings("unchecked")
public class AmendController {
	@Autowired
	private IPushQAppService pushService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private IKafkaService kafkaService;
	@Autowired
	private IAuditService auditService;
	
	@Autowired
	ReviceTaskCallReportRuleImpl callReportRule;
	
	@Autowired
	ReviceTaskNonMobileRuleImpl  nonMobileRule;
	
	@Autowired
	ReviceTaskLoanApplyRuleImpl  loanApplyRule;
	
	@Autowired
	ReviceTaskMobileAuthImpl mobileAuth;

	private static final Logger LOGGER=LoggerFactory.getLogger(AmendController.class);

	/**
	 * 重新推送数据至标的系统
	 * 
	 * @param appId
	 * @return
	 */
	@RequestMapping("/repushQapp/{appId}")
	public ResponseData<Map<String, Object>> repush(
			@PathVariable("appId") String appId) {
		
		LOGGER.info("开启手动重新推送数据至标的系统，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil
				.defaultResponse();
		try {
			//查询终审是否通过
			Map<String, Object> result=pushService.queryFinalAuditResult(appId);
			Boolean isFinalAuditPass=MapUtils.getBoolean(result, "isPass");
			Map<String, Object> data = new HashMap<String, Object>();
			if(isFinalAuditPass){
				pushService.pushQApp(appId);
			}else{
				String reason=MapUtils.getString(result, "reason");
				data.put("reason", reason);
			}
			responseData.setData(data);
			LOGGER.info("手动重新推送数据至标的系统结束，对应的进件编号：{}", appId);
		} catch (Exception e) {
			LOGGER.error("手动重新推送数据至标的系统失败，对应的进件编号：{}", appId,e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		
		return responseData;
	}

	/**
	 * 重启流程
	 * <适用于进件信息已经入库,但未成功开启流程的进件>
	 * @param appId
	 * @return
	 */
	@RequestMapping("/reStart/{appId}")
	public ResponseData<Map<String, Object>> reStart(
			@PathVariable("appId") String appId) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil
				.defaultResponse();
		try {
			loanService.reStartProcess(appId);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	/**
	 * 重新推送kafka
	 * <适用于n-des系统实际成功调用,但是本系统未获得其正确结果的>
	 * @param appId
	 * @param transactionId
	 * @param policyCode
	 * @return
	 */
	@RequestMapping("/rePushKafka/{appId}")
	public ResponseData<Map<String, Object>> reStart(
			@PathVariable("appId") String appId,
			@RequestParam("transactionId") String transactionId,
			@RequestParam("policyCode") String policyCode) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil
				.defaultResponse();
		try {
			loanService.rePushKafka(appId, transactionId, policyCode);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}
	
	/**
	 * 重新推送ndes回调至Bpms
	 * <适用于n-des系统实际成功调用,但是本系统未获得其正确结果的>
	 * @param appId
	 * @param transactionId
	 * @param policyCode
	 * @return
	 */
	@RequestMapping("/rePushNdesCallBackPushBpms/{appId}")
	public ResponseData<Map<String, Object>> restNdesCallBackPushBpms(
			@PathVariable("appId") String appId,
			@RequestParam("transactionId") String transactionId,
			@RequestParam("policyCode") String policyCode) {
		
		LOGGER.info("开启ndes回调结果重新推送bpms，对应的进件编号：{},transactionId:{},policyCode:{}", appId,transactionId,policyCode);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			String errorMsg =loanService.reCompeleteReciveTask(appId, transactionId, policyCode);
			if(StringUtils.isNotBlank(errorMsg)){
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg(errorMsg);
			}
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("ndes回调结果重新推送bpms结束，对应的进件编号：{},transactionId:{},policyCode:{}", appId,transactionId,policyCode);
		return responseData;
	}
	
	/**
	 * 审批结果重新推送Bpms
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushApprovalBpms/{appId}")
	public ResponseData<Map<String, Object>> restApprovaPushBpms(@PathVariable("appId") String appId){
		LOGGER.info("开启手动审批结果重新推送bpms，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			String errorMsg =auditService.rePushBpms(appId);
			if(StringUtils.isNotBlank(errorMsg)){
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg(errorMsg);
			}
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动审批结果重新推送bpms结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * 进件录入完成重新推送Bpms
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushLoanInBpms/{appId}")
	public ResponseData<Map<String, Object>> restLoanInPushBpms(@PathVariable("appId") String appId){
		LOGGER.info("开启进件录入完成重新推送Bpms，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			String errorMsg =loanService.reCompleteLoanIn(appId);
			if(StringUtils.isNotBlank(errorMsg)){
				responseData.setCode(ResponseCode.SYSTEM_ERROR);
				responseData.setMsg(errorMsg);
			}
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("进件录入完成重新推送Bpms结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * 审批结果重新推送kafka
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushApprovalKafk/{appId}")
	public ResponseData<Map<String, Object>> restApprovaPushKafka(@PathVariable("appId") String appId){
		LOGGER.info("开启手动审批结果重新推送kafka，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			LoanApply loanApply=pushService.pushLoanApply(appId);
			if(loanApply!=null){
				Map<String, Object> firstAudit=MapUtils.getMap(loanApply.getLoanData(), "firstAudit",null);
				Map<String, Object> finalAudit=MapUtils.getMap(loanApply.getLoanData(), "finalAudit",null);
				String auditResult=MapUtils.getString(firstAudit, "auditResult","");
				if(StringUtils.equalsIgnoreCase(auditResult, DictItem.REJECT) || MapUtils.isNotEmpty(finalAudit)){
					kafkaService.sendLoanCredit(loanApply);
				}
			}
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动审批结果重新推送kafka结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * ndes重新推送kafka（Loan数据重新推送kafka）
	 * 仅适用于调用N-DES超时，导致Loan数据未推送kafka
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushApprovalKafk/{appId}/{transactionId}/{policyCode}")
	public ResponseData<Map<String, Object>> restNdesPushKafka(@PathVariable("appId") String appId,
			@PathVariable("transactionId") String transactionId,@PathVariable("policyCode") String policyCode){
		LOGGER.info("开启手动ndes重新推送kafka，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			LoanApply loanApply=pushService.pushLoanApply(appId);
			LoanNdesRelation ndesRelation=pushService.findNdesRelation(appId, transactionId, policyCode);
			if(loanApply!=null && ndesRelation!=null){
				kafkaService.sendLoanIn(loanApply,transactionId,policyCode);
			}
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动ndes重新推送kafka结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	
	/**
	 * 重发非运营商规则
	 * 仅适用于调用N-DES失败，并导致获取Loan数据未推送kafka
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushNdesMobileRule/{appId}")
	public ResponseData<Map<String, Object>> rePushNdesMobileRule(@PathVariable("appId") String appId){
		LOGGER.info("开启手动重发非运营商规则，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			Boolean isSuccess=nonMobileRule.excute(appId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("isSuccess", isSuccess);
			responseData.setData(data);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动重发非运营商规则结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * 重发通知运营商认证
	 * 仅适用于通知运营商认证失败
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushMobileAuth/{appId}")
	public ResponseData<Map<String, Object>> rePushMobileAuth(@PathVariable("appId") String appId){
		LOGGER.info("开启手动重发运营商认证，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			Boolean isSuccess=mobileAuth.excute(appId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("isSuccess", isSuccess);
			responseData.setData(data);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动重发运营商认证结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * 重发进件规则
	 * 仅适用于调用N-DES失败，并导致获取Loan数据未推送kafka
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushLoanApplyRule/{appId}")
	public ResponseData<Map<String, Object>> rePushLoanApplyRule(@PathVariable("appId") String appId){
		LOGGER.info("开启手动重发进件规则，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			Boolean isSuccess=loanApplyRule.excute(appId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("isSuccess", isSuccess);
			responseData.setData(data);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动重发进件规则结束，对应的进件编号：{}", appId);
		return responseData;
	}
	
	/**
	 * 重发决策规则---borrowerlite进件规则
	 * 仅适用于调用N-DES失败，并导致获取Loan数据未推送kafka
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushCallReportRule/{appId}")
	public ResponseData<Map<String, Object>> rePushCallReportRule(@PathVariable("appId") String appId){
		LOGGER.info("开启手动重发决策规则，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			Boolean isSuccess=callReportRule.excute(appId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("isSuccess", isSuccess);
			responseData.setData(data);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动重发决策规则结束，对应的进件编号：{}", appId);
		return responseData;
	}

	/**
	 * 根据进件编号，重新推送进件信息到kafka之中Topic：kafka_loan_credit
	 * @param appId
	 * @return
	 */
	@GetMapping("/rePushBWLToKafka/{appId}")
	public ResponseData<Map<String, Object>> rePushBWLToKafka(@PathVariable("appId") String appId){
		LOGGER.info("开启手动审批结果重新推送kafka，对应的进件编号：{}", appId);
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
      loanService.rePushKafka(appId);
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("手动审批结果重新推送kafka结束，对应的进件编号：{}", appId);
		return responseData;
	}

	/**
	 * 检查并修复门店信息
	 * @return
	 */
	@GetMapping(value = "/checkout/loanApply_appStore")
	public ResponseData<Map<String, Object>> updateAppStore() {
		LOGGER.info("开启数据库appStore校验，校验开始时间：{}", new Date());
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			loanService.checkoutAppStore();
		} catch (Exception e) {
			LOGGER.error("数据库appStore校验失败",e);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("数据库appStore校验结束，校验结束时间：{}", new Date());
		return responseData;
	}

	/**
	 * 检查并修复审核人信息
	 * @return
	 */
	@GetMapping(value = "/checkout/loanApply_auditor")
	public ResponseData<Map<String, Object>> updateAuditor() {
		LOGGER.info("开启数据库auditor校验，校验开始时间：{}", new Date());
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			loanService.checkoutAuditor();
		} catch (Exception e) {
			LOGGER.error("数据库auditor校验失败",e);
			responseData.setMsg(e.getMessage());
		}
		LOGGER.info("数据库auditor校验结束，校验结束时间：{}", new Date());
		return responseData;
	}

}
