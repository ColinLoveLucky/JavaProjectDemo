package com.qf.cobra.loan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.annotation.Permission;
import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.qyjapi.service.IQyjapiService;
import com.qf.cobra.system.service.IDictService;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

/**
 * <门店审核> <控制层>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月19日 下午7:41:37
 */
@RestController
@RequestMapping("/audit")
public class AuditController {
	@Autowired
	IAuditService auditService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private IDictService dictService;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private IKafkaService kafkaService;
	@Autowired
	private IQyjapiService qyjapiService;
	
	private static final String BPMS_SUCCESS = "2000";
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditController.class);
	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @author HongguangHu
	 * @param appId
	 * @param request
	 *            获取header中字段
	 * @param taskId
	 *            任务id
	 * @param auditLevel
	 *            审核级别 [初审(firstAudit)，终审(finalAudit)]
	 * @param params
	 *            审核参数
	 * @return
	 * @since 2017年4月24日 上午10:13:23
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/commit", method = RequestMethod.POST)
	public ResponseData<?> commitInfo(@RequestBody Map<String, Object> params, @RequestHeader Map<String, Object> header) {
		LOGGER.info("审批记录开始,参数有:{}",params);
		// String tenant = request.getHeader(DictItem.LOAN_TENANT);
		// String client = request.getHeader(DictItem.LOAN_CLIENT);

		ResponseData<?> loanAudit = ResponseUtil.defaultResponse();
		try {
			String isSave = (String) params.get("isSave");
			Map<String, Object> object = (Map<String, Object>) params.get("firstAudit");
			String appId = (String) params.remove("appId");
			String taskId = (String) params.remove("taskId");
			String auditLevel = "firstAudit";
			if (object == null) {
				auditLevel = "finalAudit";
				object = (Map<String, Object>) params.get("finalAudit");
			}

			String result = (String) object.get("auditResult");
			Boolean isNeedSendKafka = true;
			// 判断是拒绝级别
			dictService.matchRejectLevel(result, object);
			loanAudit = auditService.loanAudit(appId, auditLevel, params);
			// 若完成当前任务继续下一流程
			if (!StringUtils.equalsIgnoreCase(isSave, DictItem.YES)) {
				String applyStatus = "";
				if (Objects.equals(ResponseCode.SUCCESS, loanAudit.getCode())) {
					Boolean auditResult = false;
					//如果是初审,结果通过,修改申请状态,不推送
					if (StringUtils.equalsIgnoreCase(auditLevel, "firstAudit")) {
						if (StringUtils.equalsIgnoreCase(result, "PASS")) {
							applyStatus = AppStatusEnum.FIRST_AUDIT_PASS.getCode();
							isNeedSendKafka = false;
						} else {
							applyStatus = AppStatusEnum.FIRST_AUDIT_REJECT.getCode();
						}
					} else if (StringUtils.equalsIgnoreCase(auditLevel, "finalAudit")) {
						if (StringUtils.equalsIgnoreCase(result, "PASS")) {
							applyStatus = AppStatusEnum.FINAL_AUDIT_PASS.getCode();
						} else {
							applyStatus = AppStatusEnum.FINAL_AUDIT_REJECT.getCode();
						}
					}
					LOGGER.info("审批提交流程开始,进件编号有:{}",appId);
					result = StringUtils.equals(result, "PASS") ? DictItem.VARIABLES_VALUE_PREAUDIT_PASS : DictItem.VARIABLES_VALUE_PREAUDIT_REJECT;
					
					List<Map<String, String>> variables = new ArrayList<Map<String, String>>();
					Map<String, String> variable = new HashMap<String, String>();
					variable.put("name", DictItem.VARIABLES_NAME);
					variable.put("value", result);
					variables.add(variable);
					
					//终审通过或拒绝时需增加以下流程变量
					if(AppStatusEnum.FINAL_AUDIT_REJECT.getCode().equals(applyStatus)||
							AppStatusEnum.FINAL_AUDIT_PASS.getCode().equals(applyStatus)){
						variable = new HashMap<String, String>();
						variable.put("name", "varRollbackStatus");
						variable.put("value", "1");
						variables.add(variable);
					}
					
					auditResult = loanService.compeleteTask(appId, taskId, variables);
					LOGGER.info("审批提交流程结束,进件编号有:{}",appId);
					if (auditResult) {
						auditService.updateAppStatus(appId, applyStatus);
					}

					//初审拒绝，终审拒绝、终审通过时，发送进件信息至kafka，并通知App后端
					if (auditResult && isNeedSendKafka) {
						 Query query = Query.query(Criteria.where("appId").is(appId));
						 LoanApply loanInfo = mongoTemplate.findOne(query,LoanApply.class);
						 kafkaService.sendLoanCredit(loanInfo);
						 
						 qyjapiService.sendLoanDetail(appId);
					}
				}
			} else {
				String auditResult = "";
				if (StringUtils.equalsIgnoreCase(auditLevel, "firstAudit")) {
					auditResult = AppStatusEnum.FIRST_AUDITING.getCode();
				}
				auditService.updateAppStatus(appId, auditResult);
			}
		} catch (Exception e) {
			LOGGER.error("审核信息保存/提交失败", e);
			loanAudit.setCode(ResponseCode.SYSTEM_ERROR);
			loanAudit.setMsg("审核信息保存/提交失败");
		}
		
		LOGGER.info("审批记录结束,参数有:{},审批结果:{}",params,loanAudit.getMsg());
		return loanAudit;
	}
	
	/**
	 * @Title: commitFallBack 
	 * @Description: 进件终审回退
	 * @param @param params
	 * @param @param header
	 * @param @return    设定文件 
	 * @return ResponseData<?>    返回类型 
	 * @date 2017年6月30日 下午2:58:56
	 * @author YabinLi
	 * @throws
	 */
	@RequestMapping(value = "/commitFallBack", method = RequestMethod.POST)
	public ResponseData<?> commitFallBack(@RequestBody Map<String, Object> params, @RequestHeader Map<String, Object> header) {
		LOGGER.info("进件终审回退开始,参数有:{}",params);
		@SuppressWarnings("unchecked")
		ResponseData<Map<String, Object>> loanAudit = ResponseUtil.defaultResponse();
		Map<String, Object> auditResult = new HashMap<String, Object>();
		try {
			String appId = (String) params.remove("appId");
			String taskId = (String) params.remove("taskId");
			String callBackRemark = (String) params.remove("callBackRemark");
			auditResult = auditService.loanAuditFallBack(appId, taskId, callBackRemark);
			loanAudit.setData(auditResult);
			if(auditResult.get("responseCode").toString().equals(BPMS_SUCCESS)){
				auditService.updateAppStatus(appId, AppStatusEnum.FIRST_AUDITING.getCode());
				auditService.loanAuditFallBackUpdate(appId);
			}
		} catch (Exception e) {
			LOGGER.error("借款终审回退失败", e);
			loanAudit.setCode(ResponseCode.SYSTEM_ERROR);
			loanAudit.setMsg("借款终审回退失败");
		}
		return loanAudit;
	}
	
	@Permission("qyj_loanList_storeReject")
	@RequestMapping(value = "/canncel", method = RequestMethod.POST)
	public ResponseData<?> canncel(@RequestBody Map<String, Object> params, @RequestHeader Map<String, Object> header) {
		LOGGER.info("拒件记录开始,参数有:{}",params);
		ResponseData<?> loanAudit = ResponseUtil.defaultResponse();
		String appId = (String) params.get("appId");
		String processInstanceId = (String) params.remove("processInstanceId");
		
		if(StringUtils.isEmpty(appId) || StringUtils.isEmpty(processInstanceId)) {
			loanAudit.setCode(ResponseCode.PARAM_DATA_IS_NULL);
			return loanAudit;
		}
		
		if(!loanService.enableStoreReject(appId)){
			loanAudit.setCode(ResponseCode.SYSTEM_ERROR);
			loanAudit.setMsg("该借款无法进行拒件操作，请刷新页面!");
			return loanAudit;
		}
		
		String errorMessage = auditService.loanCanncel(appId,processInstanceId);
		if(StringUtils.isNotBlank(errorMessage)){
			loanAudit.setCode(ResponseCode.SYSTEM_ERROR);
			loanAudit.setMsg(errorMessage);
		}else{
			//门店拒绝操作成功则通知app后端刷新数据
			qyjapiService.sendLoanDetail(appId);
		}
		
		return loanAudit;
	}
}
