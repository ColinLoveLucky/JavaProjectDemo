package com.qf.cobra.loan.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Objects;
import com.mongodb.WriteResult;
import com.qf.cobra.feign.service.BpmsService;
import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.CallBackResult;
import com.qf.cobra.pojo.CallBackResult.CallBackType;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.SessionUtil;

@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AuditServiceImpl implements IAuditService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private BpmsService bpmsService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private IKafkaService kafkaService;

	@Override
	public ResponseData<?> loanAudit(String appId, String auditLevel, Map<String, Object> auditParams) {
		Object loanMaturity = auditParams.remove("loanMaturity");
		String productCode = (String) auditParams.remove("productCode");
		Object appAmount = auditParams.remove("appAmount");
		String isSave = (String) auditParams.remove("isSave");
		auditParams = (Map<String, Object>) auditParams.get(auditLevel);
		auditParams.put("auditor",
				SessionUtil.getCurrentUser() == null ? "审核人" : SessionUtil.getCurrentUser().getUserId());

		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
		Map<String, Object> loanData = loanApply.getLoanData();

		// 检查初审岗位是否已超时拒绝
		if (Objects.equal("firstAudit", auditLevel) && StringUtils
				.equals(MapUtils.getString(MapUtils.getMap(loanData, "firstAudit"), "subTypeCode"), "9004")) {
			ResponseData<Map> response = ResponseUtil.defaultResponse();
			response.setCode(ResponseCode.SYSTEM_ERROR);
			response.setMsg("流程已超时,请刷新任务");
			return response;
		}

		// 修改期数 和 申请金额
		Map<String, Object> loanAppInfo = (Map<String, Object>) loanData.get("loanAppInfo");
		if (loanMaturity != null) {
			loanAppInfo.put("loanMaturity", loanMaturity);
			loanAppInfo.put("productCode", productCode);
		}
		if (appAmount != null) {
			loanAppInfo.put("appAmount", appAmount);
		}
		Update update = new Update();
		auditParams.put("auditTime", DateUtil.formatCurrentDateTime());
		String auditHistoryOperat = "";
		if (Objects.equal("firstAudit", auditLevel)) {// 初审
			loanData.put("firstAudit", auditParams);
			if (StringUtils.equalsIgnoreCase(isSave, DictItem.YES)) {
				auditHistoryOperat = LoanAuditOperation.FIRST_SAVE.getValue();
			} else {
				auditHistoryOperat = LoanAuditOperation.FIRST_AUDIT.getValue();
			}
		} else if (Objects.equal("finalAudit", auditLevel)) {// 终审
			loanData.put("finalAudit", auditParams);
			auditHistoryOperat = LoanAuditOperation.FINAL_AUDIT.getValue();
		}
		update.set("loanData", loanData);
		WriteResult upsert = mongoTemplate.updateFirst(query, update, LoanApply.class);
		// 操作数据每一次都保存一次
		LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
		loanAuditHistory.setOperate(auditHistoryOperat);
		loanAuditHistory
				.setUserId(SessionUtil.getCurrentUser() == null ? "审核人" : SessionUtil.getCurrentUser().getUserId());
		auditParams.put("appAmount", appAmount);
		auditParams.put("loanMaturity", loanMaturity);
		loanAuditHistory.setAppId(appId);
		loanAuditHistory.setBizData(auditParams);
		loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(loanAuditHistory);

		ResponseData<Map> resVal = ResponseUtil.defaultResponse();
		if (upsert.isUpdateOfExisting()) {
			resVal.setMsg("审核信息提交成功！");
		} else {
			resVal.setCode(ResponseCode.SYSTEM_ERROR);
			resVal.setMsg("审核信息保存失败！");
		}
		return resVal;
	}

	@Override
	public void updateAppStatus(String appId, String auditStatus) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		Update update = new Update();
		update.set("appStatus", auditStatus);
		mongoTemplate.updateFirst(query, update, LoanApply.class);
  }

  @Override
  public void updateAppStatusNdes(String appId, String auditStatus) {
    Query query = Query.query(Criteria.where("appId").is(appId));
    Update update = new Update();
    update.set("appStatus", auditStatus);
    mongoTemplate.updateFirst(query, update, LoanApply.class);

    LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
    LOGGER.info("开始-borrowerlite推送NDES回调之后的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());
    kafkaService.sendBorrowerLiteLoanApplyInfo(loanApply);
    LOGGER.info("结束-borrowerlite推送NDES回调之后的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());
  }

	@Override
	public void updateLoanAppClientId(String appId, String clientId, String tenantId) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		Update update = new Update();
		update.set("clientId", clientId);
		update.set("tenantId", tenantId);
		mongoTemplate.updateFirst(query, update, LoanApply.class);
	}

	@Override
	public Map<String, Object> loanAuditFallBack(String appId, String taskId, String callBackRemark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("name", "varRollbackStatus");
			variables.put("value", "0");
			list.add(variables);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("action", "complete");
			param.put("variables", list);
			LOGGER.info("处理任务,任务编号:{},请求参数:{}", taskId, param);
			String result = bpmsService.actionProcess(param, taskId);
			LOGGER.info("处理任务,任务编号:{},返回结果:{}", taskId, result);
			if (StringUtils.isBlank(result)) {
				LOGGER.error("处理任务,任务编号:{},返回结果:{}, 调用bpms接口异常", taskId, result);
			} else {
				resultMap = JsonUtil.convert(result, Map.class);
			}
			CallBackResult callBackResult = new CallBackResult();
			callBackResult.setAppId(appId);
			callBackResult.setRemark(callBackRemark);
			callBackResult.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			callBackResult.setType(CallBackType.CALL_BACK);
			callBackResult.setOperation(SessionUtil.getCurrentUser().getUserName());
			mongoTemplate.save(callBackResult);
		} catch (Exception e) {
			LOGGER.info("处理任务,任务编号:{},返回结果:任务异常", taskId, e);
		}
		LOGGER.info("进件编号:{},任务编号:{},流程变量:{},结束进件回退流程任务结果:{}", appId, taskId, list, resultMap);
		return resultMap;
	}

	@Override
	public void loanAuditFallBackUpdate(String appId) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
		Map<String, Object> loanData = loanApply.getLoanData();
		loanData.remove("finalAudit");
		Update update = new Update();
		update.set("loanData", loanData);
		mongoTemplate.updateFirst(query, update, LoanApply.class);
		// 保存录入信息进入历史数据
		LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
		loanAuditHistory.setUserId(SessionUtil.getCurrentUser().getUserId());
		loanAuditHistory.setOperate(LoanAuditOperation.FINAL_AUDIT_FALLBACK.getValue());
		loanAuditHistory.setBizData(loanData);
		loanAuditHistory.setAppId(appId);
		loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(loanAuditHistory);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String loanCanncel(String appId, String processInstanceId) {
		String result = "";
		String userId = SessionUtil.getCurrentUser().getUserId();

		/**
		 * 1.获取taskid 2.调用bpms 3.修改业务状态 4.日志记录
		 */
		try {
			// 获取对应的taskid（拒件节点暂不需要考虑并行task的情况）
			String taskId = loanService.getFirstTaskIdByInstanceId(processInstanceId);
			if (StringUtils.isBlank(taskId)) {
				result = "获取流程任务信息失败！";
				return result;
			}

			// step 2
			// 签收
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("action", "claim");
			param.put("userId", userId);
			LOGGER.info("签收用户任务,进件编号:{},任务编号:{},请求参数:{}", appId, taskId, param);
			String resultJson = bpmsService.actionProcess(param, taskId);
			resultJson = resultJson == null ? "" : resultJson;
			LOGGER.info("签收用户任务,进件编号:{},任务编号:{},返回结果:{}", appId, taskId, resultJson);

			Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
			String code = MapUtils.getString(resultObj, "responseCode", "");
			if (!StringUtils.equals(code, DictItem.BPMS_SUCCESS)) {
				result = "签收用户任务失败！";
				return result;
			}

			// 拒件
			loanService.compeleteTask(appId, taskId, DictItem.VARIABLES_VALUE_PREAUDIT_REJECT);
			LOGGER.info("审批提交流程结束,进件编号有:{}", appId);
		} catch (Exception e) {
			result = "bpms调用失败！";
			LOGGER.error("bpms调用失败,进件编号:{},实例编号:{},返回结果:任务异常", appId, processInstanceId, e);
			return result;
		}

		// step 3
		updateAppStatus(appId, AppStatusEnum.STORE_REJECT.getCode());
		// step 4
		saveLoanAuditHistory(appId, LoanAuditOperation.FINAL_STORE_REJECT);

		return result;
	}

	@Override
	public void saveLoanAuditHistory(String appId, LoanAuditOperation loanAuditOperation) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
		Map<String, Object> loanData = loanApply.getLoanData();
		// 保存录入信息进入历史数据
		LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
		loanAuditHistory
				.setUserId(SessionUtil.getCurrentUser() == null ? "" : SessionUtil.getCurrentUser().getUserId());
		loanAuditHistory.setOperate(loanAuditOperation.getValue());
		loanAuditHistory.setBizData(loanData);
		loanAuditHistory.setAppId(appId);
		loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(loanAuditHistory);
	}

	@Override
	public String rePushBpms(String appId) {
		Boolean isNeedSendKafka = true;
		Boolean auditResult = false;
		String applyStatus = null;
		String auditLevel = null;
		Map<String, Object> auditParams = null;
		String auditHistoryOperat = null;
		String result = null;
		String taskId = null;

		// 1 校验数据有效性，提取用户信息
		LoanApply loanApply = loanService.queryLoanInfoByAppId(appId);
		Map<String, Object> loanData = loanApply.getLoanData();

		// 判断审批节点
		if (loanData.containsKey("finalAudit")) {
			auditLevel = "finalAudit";
			auditHistoryOperat = LoanAuditOperation.FINAL_AUDIT.getValue();
		} else if (loanData.containsKey("firstAudit")) {
			auditLevel = "firstAudit";
			auditHistoryOperat = LoanAuditOperation.FIRST_AUDIT.getValue();

			// 检查初审岗位是否已超时拒绝
			if (StringUtils.equals(MapUtils.getString(MapUtils.getMap(loanData, "firstAudit"), "subTypeCode"),
					"9004")) {
				return "流程已超时,请刷新任务";
			}
		} else {
			return "状态不匹配";
		}
		auditParams = (Map<String, Object>) loanData.get(auditLevel);
		result = (String) auditParams.get("auditResult");

		// 获取审批日志，判断是否提交审批
		Query hisquery = Query.query(Criteria.where("appId").is(appId).and("operate").is(auditHistoryOperat));
		List<LoanAuditHistory> loanAuditHistorys = mongoTemplate.find(hisquery, LoanAuditHistory.class);
		if (CollectionUtils.isEmpty(loanAuditHistorys)) {
			return "状态不匹配";
		} else {
			taskId = loanService.getFirstTaskIdByInstanceId(loanApply.getProcessInstanceId());
			if (StringUtils.isBlank(taskId)) {
				return "获取流程任务信息失败！";
			}
		}

		// 2 模拟页面操作，继续bpms调用
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
		LOGGER.info("审批提交流程开始,进件编号有:{}", appId);
		result = StringUtils.equals(result, "PASS") ? DictItem.VARIABLES_VALUE_PREAUDIT_PASS
				: DictItem.VARIABLES_VALUE_PREAUDIT_REJECT;
		auditResult = loanService.compeleteTask(appId, taskId, result);
		LOGGER.info("审批提交流程结束,进件编号有:{}", appId);
		if (auditResult) {
			updateAppStatus(appId, applyStatus);
		}

		if (isNeedSendKafka) {
			LoanApply loanInfo = loanService.queryLoanInfoByAppId(appId);
			kafkaService.sendLoanCredit(loanInfo);
		}
		return null;
	}

}
