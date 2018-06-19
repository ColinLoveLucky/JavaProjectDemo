package com.qf.cobra.loan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteResult;
import com.qf.cobra.exception.AuthOutOfBoundsException;
import com.qf.cobra.feign.service.BpmsService;
import com.qf.cobra.feign.service.NCobraService;
import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.IAuditService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.IPushQAppService;
import com.qf.cobra.loan.service.IVersionConfigService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.CallBackResult;
import com.qf.cobra.pojo.CarrieroperatorResult;
import com.qf.cobra.pojo.Dict;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.pojo.LoanBpmRelation;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;
import com.qf.cobra.pojo.VersionConfig;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.MsgEncodeUtil;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.SessionUtil;
import com.qf.cobra.util.SystemOperation;

@Service
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LoanServiceImpl implements LoanService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanServiceImpl.class);

	@Autowired
	private BpmsService bpmsService;
	@Autowired
	private MongoOperate mongoTemplate;
	@Autowired
	private MongoTemplate springMongoTemplate;
	@Autowired
	private IPushQAppService pushService;
	@Autowired
	private NCobraService nCobraService;

	@Autowired
	private ErrorNoticeService errorNoticeService;
	@Autowired
	IAuditService auditService;
	@Value("${system.clientId}")
	private String clientId;

	// public Boolean compeleteTask(String appId, String taskId,
	// String taskDefinitionKey, String... result) throws Exception {
	// LoanApply loanApply = queryLoanInfoByAppId(appId);
	// if (StringUtils.isBlank(loanApply.getProcessDefId())) {
	// return compeleteTask(appId, taskId, result);
	// }
	// ProcessVariablesConfig processVariablesConfig = versionConfigService
	// .queryProcessVariablesConfig(loanApply.getProcessDefId(),
	// taskDefinitionKey, TaskAction.COMPLETE.getCode());
	// if(processVariablesConfig == null){
	// throw new Exception("流程提交失败-缺少流程变量配置信息,请联系管理员");
	// }
	// String paramsJson = processVariablesConfig.getProcessParams();
	// if(result.length > 0){
	// paramsJson = String.format(paramsJson, result);
	// }
	// Map<String,Object> params = JsonUtil.convert(paramsJson, Map.class);
	// return bpmsService.actionTask(taskId, params);
	// }

	/**
	 * 适用于loanapply中尚未存储processDefId的数据
	 * 
	 * @param appId
	 * @param taskId
	 * @param result
	 * @return
	 */
	@Override
	public Boolean compeleteTask(String appId, String taskId, String... result) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (String s : result) {
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("name", DictItem.VARIABLES_NAME);
			variables.put("value", s);
			list.add(variables);
		}

		return compeleteTask(appId, taskId, list);
	}

	@Override
	public TaskNodeInfo updateTaskStatus(String appId, ReceiveServiceEnum serviceEnum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appId", appId);
		params.put("serviceCode", serviceEnum.getServiceCode());
		params.put("compeleteStatus", DictItem.COMPELETE_STATUS_UNDO);

		Map<String, Object> setParams = new HashMap<String, Object>();
		setParams.put("compeleteStatus", DictItem.COMPELETE_STATUS_DONE);

		TaskNodeInfo taskNodeInfo = (TaskNodeInfo) mongoTemplate.findAndModify(params, setParams, TaskNodeInfo.class);

		return taskNodeInfo;
	}

	@Override
	public List<LoanApply> queryLoanInfosByStatusAndType(String type, String status) {
		Map<String, Object> params = new HashMap<String, Object>();
		// if (!StringUtils.isEmpty(nodeId)) {
		params.put("status", status);
		params.put("type", type);
		// }
		return mongoTemplate.findAll(params, LoanApply.class);
	}

	@Override
	public LoanApply queryLoanInfoByAppId(String appId) {

		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		return (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
	}

	@Override
	public String reCompleteLoanIn(String appId) {
		// 1 判断是否有进件录入记录，确认有效性
		String taskId = null;
		Query hisquery = Query
				.query(Criteria.where("appId").is(appId).and("operate").is(LoanAuditOperation.LOAN_INPUT.getValue()));
		List<LoanAuditHistory> loanAuditHistorys = springMongoTemplate.find(hisquery, LoanAuditHistory.class);
		if (CollectionUtils.isEmpty(loanAuditHistorys)) {
			return "状态不匹配";
		} else {
			LoanApply loanApply = queryLoanInfoByAppId(appId);
			taskId = getFirstTaskIdByInstanceId(loanApply.getProcessInstanceId());
			if (StringUtils.isBlank(taskId)) {
				return "获取流程任务信息失败！";
			}
		}

		// 2 重新提交bpms
		Boolean compeleteTask = false;
		compeleteTask = compeleteTask(appId, taskId);
		String applyStatus = AppStatusEnum.WAIT_FIRST_AUDIT.getCode();
		if (compeleteTask) {
			auditService.updateAppStatus(appId, applyStatus);
		}
		// 对进件录入信息新增推送kafka功能
		LoanApply loanInfo = queryLoanInfoByAppId(appId);
		LOGGER.info("进件录入信息推送kafka开始，进件编号：{}", appId);
		kafkaService.sendLoanApplyInfo(loanInfo);
		LOGGER.info("进件录入信息推送kafka结束，进件编号：{}", appId);
		return null;
	}

	@Override
	public String reCompeleteReciveTask(String appId, String transactionId, String policyCode) {
		String instanceId = null;
		String taskId = null;
		String result = null;

		// 1获取流程id
		LoanApply loanApply = queryLoanInfoByAppId(appId);
		if (null == loanApply) {
			return "借款不存在!";
		} else {
			instanceId = loanApply.getProcessInstanceId();
		}

		// 2获取ndes结果
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appId", appId);
		params.put("transactionId", transactionId);
		params.put("policyCode", policyCode);

		LoanNdesRelation loanNdesRelation = (LoanNdesRelation) mongoTemplate.findOne(params, LoanNdesRelation.class);
		if (null == loanNdesRelation) {
			return "状态不正确!";
		} else {
			result = loanNdesRelation.getPolicyResult();
			result = StringUtils.equals(result, "PASS") ? DictItem.VARIABLES_VALUE_PREAUDIT_PASS
					: DictItem.VARIABLES_VALUE_PREAUDIT_REJECT;
		}

		// 不是进件规则的结果，都默认通过
		if (!DictItem.POLICY_CODE_APPLY.equals(policyCode)) {
			result = DictItem.VARIABLES_VALUE_PREAUDIT_PASS;
		}

		// 3获取任务id
		params.clear();
		;
		params.put("appId", appId);
		// 此处需根据bpm回调时传入的taskId做处理，后续还要考虑多版本配置，暂时写死
		if (ReceiveServiceEnum.LOAN_APPLY_RULE.getPolicyCode().equals(policyCode)) {
			params.put("serviceCode", ReceiveServiceEnum.LOAN_APPLY_RULE.getServiceCode());
		} else if (ReceiveServiceEnum.NON_MOBILE_RULE.getPolicyCode().equals(policyCode)) {
			params.put("serviceCode", ReceiveServiceEnum.NON_MOBILE_RULE.getServiceCode());
		} else if (ReceiveServiceEnum.CALL_REPORT_POLICY_RULE.getPolicyCode().equals(policyCode)) {
			params.put("serviceCode", ReceiveServiceEnum.CALL_REPORT_POLICY_RULE.getServiceCode());
		} else {
			return "参数不正确!";
		}

		Map<String, Object> setParams = new HashMap<String, Object>();
		setParams.put("compeleteStatus", DictItem.COMPELETE_STATUS_DONE);

		TaskNodeInfo taskNodeInfo = (TaskNodeInfo) mongoTemplate.findOne(params, TaskNodeInfo.class);

		if (taskNodeInfo != null) {
			taskId = taskNodeInfo.getTaskId();
		} else {
			return "获取流程任务信息失败！";
		}

		if (!compeleteReciveTask("", instanceId, taskId, result)) {
			return "任务异常";
		}
		return null;
	}

	@Override
	public Boolean compeleteReciveTask(String appId, String processInstanceId, String taskId, String... result) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (String s : result) {
			Map<String, String> variables = new HashMap<String, String>();
			variables.put("name", DictItem.VARIABLES_NAME);
			variables.put("value", s);
			list.add(variables);
		}
		// LOGGER.info("结束非运营商规则回调任务,参数有:appId:{},processInstanceId:{},taskId:{},流程变量:{}",appId,processInstanceId,taskId,list);

		Boolean isSuccessCommit = true;
		if (StringUtils.isNotBlank(appId) && StringUtils.isBlank(processInstanceId)) {
			processInstanceId = queryProcessIdByAppId(appId);
		}

		if (StringUtils.isBlank(processInstanceId) || StringUtils.isBlank(taskId)) {
			isSuccessCommit = false;
		} else {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("processInstanceId", processInstanceId);
			param.put("taskId", taskId);
			if (list != null && list.size() > 0) {
				param.put("variables", list);
			}

			try {
				LOGGER.info("提交回调任务,流程编号:{},任务编号:{},请求参数:{}", processInstanceId, taskId, param);
				String resultJson = bpmsService.compeleteReciveTask(param);
				resultJson = resultJson == null ? "" : resultJson;
				LOGGER.info("提交回调任务,流程编号:{},任务编号:{},返回结果:{}", processInstanceId, taskId, resultJson);

				Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
				String code = MapUtils.getString(resultObj, "responseCode", "");
				if (!StringUtils.equals(code, DictItem.BPMS_SUCCESS)) {
					isSuccessCommit = false;
					errorNoticeService.notice(appId, "提交回调任务",
							String.format("提交回调任务-任务失败,流程编号:%s,任务编号:%s", processInstanceId, taskId),
							new Exception(resultJson));
				}
			} catch (Exception e) {
				isSuccessCommit = false;
				LOGGER.error("提交回调任务,流程编号:{},任务编号:{},返回结果:任务异常", processInstanceId, taskId, e);
				errorNoticeService.notice(appId, "提交回调任务",
						String.format("提交回调任务-任务异常,流程编号:%s,任务编号:%s", processInstanceId, taskId), e);
			}

		}
		return isSuccessCommit;
	}

	private String queryProcessIdByAppId(String appId) {
		String processInstanceId = null;
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		LoanBpmRelation loanBpmRelation = (LoanBpmRelation) mongoTemplate.findOne(queryParam, LoanBpmRelation.class);
		if (loanBpmRelation == null || StringUtils.isBlank(loanBpmRelation.getProcessInstanceId())) {
			LOGGER.info("进件编号:{} 未获的相应的流程编号", appId);
		} else {
			processInstanceId = loanBpmRelation.getProcessInstanceId();
			LOGGER.info("进件编号:{}相应的流程编号:{}", appId, processInstanceId);
		}
		return processInstanceId;
	}

	@Override
	public LoanNdesRelation queryRuleResultByAppId(String appId) {
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		return (LoanNdesRelation) mongoTemplate.findOne(queryParam, LoanNdesRelation.class);
	}

	@Override
	public List<LoanNdesRelation> queryAllRuleResultByAppId(String appId) {
		List<LoanNdesRelation> ndesList = new ArrayList<>();
		Query queryParam = new Query();
		queryParam.addCriteria(Criteria.where("appId").is(appId));
		ndesList = springMongoTemplate.find(queryParam, LoanNdesRelation.class);
		return ndesList;
	}

	@Override
	public void searchUserApply(Pagination<Map<String, Object>> pagination) throws Exception {
		Query loanApplyQuery = new Query();
		loanApplyQuery.with(new Sort(new Order(Direction.DESC, "timestamp")));
		// 添加当前用户的门店过滤(获取当前用户可访问的数据权限范围)
		LoginUser user = SessionUtil.getCurrentUser();

		String userId = user.getUserId();
		loanApplyQuery.addCriteria(Criteria.where("userId").is(userId));

		// 分页前先查出满足条件的记录总数;
		int count = (int) springMongoTemplate.count(loanApplyQuery, LoanApply.class);
		pagination.setTotal(count);
		// 数据库分页
		int size = pagination.getPageSize();
		int start = (pagination.getCurrentPage() - 1) * size;

		loanApplyQuery.skip(start);
		loanApplyQuery.limit(size);

		List<LoanApply> loanApplyList = springMongoTemplate.find(loanApplyQuery, LoanApply.class);

		if (!CollectionUtils.isEmpty(loanApplyList)) {
			List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
			for (LoanApply apply : loanApplyList) {
				Map<String, Object> personalInfo = (Map<String, Object>) apply.getLoanData().get("personalInfo");
				Map<String, Object> loanAppInfo = (Map<String, Object>) apply.getLoanData().get("loanAppInfo");
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("processInstanceId", apply.getProcessInstanceId());
				record.put("appId", apply.getAppId());
				record.put("timestamp", apply.getTimestamp());
				// 进件状态
				record.put("appStatus", apply.getAppStatus());
				if (personalInfo != null) {
					record.put("name", personalInfo.get("name"));
					record.put("card", personalInfo.get("idCard"));
					record.put("mobilePhone", personalInfo.get("mobilePhone"));
				}
				if (loanAppInfo != null) {
					record.put("appAmount", loanAppInfo.get("appAmount"));
					record.put("loanMaturity", loanAppInfo.get("loanMaturity"));
					// 城市
					record.put("appCity", MapUtils.getString(loanAppInfo, "appCity"));
				}
				// 获取提交终审时间
				Map<String, Object> firstAudit = MapUtils.getMap(apply.getLoanData(), "firstAudit", null);
				Map<String, Object> finalAudit = MapUtils.getMap(apply.getLoanData(), "finalAudit", null);

				if (StringUtils.equals(apply.getAppStatus(), AppStatusEnum.FIRST_AUDIT_PASS.getCode())
						|| MapUtils.isNotEmpty(finalAudit)) {
					record.put("starFinalTime", MapUtils.getString(firstAudit, "auditTime", ""));
				}

				record.put("enableCancel", enableStoreReject(apply));
				currentPageRecordList.add(record);
			}
			pagination.setData(currentPageRecordList);
		}
	}

	@Override
	public void searchAllApply(Pagination<Map<String, Object>> pagination) throws Exception {
		Query loanApplyQuery = new Query();
		Map<String, Object> condition = pagination.getCondition();
		String appStore = "";
		if (condition != null) {
			String appId = (String) condition.get("appId");
			String name = (String) condition.get("name");
			String startTime = (String) condition.get("startTime");
			String endTime = (String) condition.get("endTime");
			String loanAppStatus = (String) condition.get("appStatus");
			appStore = (String) condition.get("appStore");

			if (!StringUtils.isEmpty(appId)) {
				loanApplyQuery.addCriteria(Criteria.where("appId").is(appId));
			}
			if (!StringUtils.isEmpty(name)) {
				Pattern namePattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
				loanApplyQuery.addCriteria(Criteria.where("loanData.personalInfo.name").regex(namePattern));
			}
			Criteria timeCriteria = null;
			if (!StringUtils.isEmpty(startTime)) {
				startTime = DateUtil.formatUTCDate(startTime);
				timeCriteria = Criteria.where("timestamp").gte(startTime);
			}
			if (!StringUtils.isEmpty(endTime)) {
				endTime = DateUtil.formatUTCDate(endTime);
				if (timeCriteria != null) {
					timeCriteria.andOperator(Criteria.where("timestamp").lte(endTime));
				} else {
					timeCriteria = Criteria.where("timestamp").lte(endTime);
				}
			}
			if (null != timeCriteria) {
				loanApplyQuery.addCriteria(timeCriteria);
			}
			if (StringUtils.isNotBlank(loanAppStatus)) {
				loanApplyQuery.addCriteria(Criteria.where("appStatus").is(loanAppStatus));
			}
		}
		loanApplyQuery.with(new Sort(new Order(Direction.DESC, "timestamp")));
		// 添加当前用户的门店过滤(获取当前用户可访问的数据权限范围)
		LoginUser user = SessionUtil.getCurrentUser();
		List<Map<String, List<Map<String, Object>>>> storeListMap = user.getStoreList();
		List<String> storeList = storeListMap.stream()
				.flatMap(store -> store.get("authorized").stream().map(item -> item.get("dataAlias").toString()))
				.collect(Collectors.toList());
		// if (storeListMap != null) {
		// for (int i = 0; i < storeListMap.size(); i++) {
		// List<Map<String, Object>> authorizedList = (List<Map<String,
		// Object>>) storeListMap.get(i)
		// .get("authorized");
		// for (int j = 0; j < authorizedList.size(); j++) {
		// storeList.add((String) authorizedList.get(j).get("dataAlias"));
		// }
		// }
		// }
		if (StringUtils.isNotBlank(appStore) && !storeList.contains(appStore)) {
			throw new AuthOutOfBoundsException("抱歉,您没有该门店的数据访问权限!");
		}
		if (StringUtils.isNotBlank(appStore)) {
			loanApplyQuery.addCriteria(Criteria.where("loanData.loanAppInfo.appStore").is(appStore));
		} else {
			loanApplyQuery.addCriteria(Criteria.where("loanData.loanAppInfo.appStore").in(storeList));
		}

		// 分页前先查出满足条件的记录总数;
		int count = (int) springMongoTemplate.count(loanApplyQuery, LoanApply.class);
		pagination.setTotal(count);
		// 数据库分页
		int size = pagination.getPageSize();
		int start = (pagination.getCurrentPage() - 1) * size;

		loanApplyQuery.skip(start);
		loanApplyQuery.limit(size);

		List<LoanApply> loanApplyList = springMongoTemplate.find(loanApplyQuery, LoanApply.class);

		if (!CollectionUtils.isEmpty(loanApplyList)) {
			List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
			for (LoanApply apply : loanApplyList) {
				Map<String, Object> personalInfo = (Map<String, Object>) apply.getLoanData().get("personalInfo");
				Map<String, Object> loanAppInfo = (Map<String, Object>) apply.getLoanData().get("loanAppInfo");
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("processInstanceId", apply.getProcessInstanceId());
				record.put("appId", apply.getAppId());
				record.put("timestamp", apply.getTimestamp());
				// 进件状态
				record.put("appStatus", apply.getAppStatus());
				if (personalInfo != null) {
					record.put("name", personalInfo.get("name"));
					record.put("card", personalInfo.get("idCard"));
					record.put("mobilePhone", personalInfo.get("mobilePhone"));
				}
				if (loanAppInfo != null) {
					record.put("appAmount", loanAppInfo.get("appAmount"));
					record.put("loanMaturity", loanAppInfo.get("loanMaturity"));
					// 城市
					record.put("appCity", MapUtils.getString(loanAppInfo, "appCity"));
				}
				// 获取提交终审时间
				Map<String, Object> firstAudit = MapUtils.getMap(apply.getLoanData(), "firstAudit", null);
				Map<String, Object> finalAudit = MapUtils.getMap(apply.getLoanData(), "finalAudit", null);

				if (StringUtils.equals(apply.getAppStatus(), AppStatusEnum.FIRST_AUDIT_PASS.getCode())
						|| MapUtils.isNotEmpty(finalAudit)) {
					record.put("starFinalTime", MapUtils.getString(firstAudit, "auditTime", ""));
				}

				record.put("enableCancel", enableStoreReject(apply));
				currentPageRecordList.add(record);
			}
			pagination.setData(currentPageRecordList);
		}
	}

	/**
	 * 判断是否能执行拒件操作
	 * 
	 * @param apply
	 * @return
	 */
	private boolean enableStoreReject(LoanApply apply) {
		boolean enableCancel = false;

		// AppStatus为待补全
		// version=1代表进件成功(为2.0版本)
		String version = null;
		if (apply.getLoanData().containsKey("extras")) {
			version = MapUtils.getString(MapUtils.getMap(apply.getLoanData(), "extras"), "version");
		}
		if (StringUtils.isNoneEmpty(version)
				&& StringUtils.equals(apply.getAppStatus(), AppStatusEnum.WAIT_COMPLEMENT.getCode())
				&& "0".compareTo(version) < 0) {
			// LoanAuditHistory没有签收日志
			Map<String, Object> queryAuditParam = new HashMap<String, Object>();
			queryAuditParam.put("appId", apply.getAppId());
			queryAuditParam.put("operate", SystemOperation.CLAIM_TASK.getValue());
			if (mongoTemplate.findOne(queryAuditParam, LoanAuditHistory.class) == null) {
				enableCancel = true;
			}

			// LoanNdesRelation非运营商规则运行
			if (enableCancel) {
				Query hisquery = Query.query(Criteria.where("appId").is(apply.getAppId()).and("policyCode")
						.is(DictItem.POLICY_CODE_NON_MOBILE).and("policyResult").exists(true));
				if (springMongoTemplate.findOne(hisquery, LoanNdesRelation.class) == null) {
					enableCancel = false;
				}
			}
		}
		return enableCancel;
	}

	@Override
	public String loanDetail(String appIds) throws Exception {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotBlank(appIds)) {
			String[] newAppIds = appIds.split(",");
			for (int i = 0; i < newAppIds.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = queryLoanDetail(newAppIds[i]);
				lists.add(map);
			}
			if (lists.size() != 0) {
				return JsonUtil.convert(lists);
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> queryLoanDetail(String appId) throws Exception {
		LoanApply data = queryLoanInfoByAppId(appId);
		Map<String, Object> loanData = data.getLoanData();
		Map<String, Object> loanAppInfo = MapUtils.getMap(loanData, "loanAppInfo");
		Map<String, Object> personalInfo = MapUtils.getMap(loanData, "personalInfo");
		Map<String, Object> salesInfo = MapUtils.getMap(loanData, "salesInfo");
		Map<String, Object> firstAudit = MapUtils.getMap(loanData, "firstAudit");
		List<Map<String, Object>> contactInfo = (List<Map<String, Object>>) loanData.get("contactInfo");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// cobra申请编号
		resultMap.put("appId", appId);
		// 产品Logo
		resultMap.put("productType", loanAppInfo.get("productType"));
		// 产品编码
		resultMap.put("productCode", loanAppInfo.get("productCode"));
		// 产品名称
		resultMap.put("productName", "");
		// 申请日期
		resultMap.put("timestamp", data.getTimestamp());
		// 申请金额
		resultMap.put("appAmount", loanAppInfo.get("appAmount"));
		// 期数
		resultMap.put("loanMaturity", loanAppInfo.get("loanMaturity"));
		// 进件状态代码
		resultMap.put("appStatus", data.getAppStatus());
		// 进件状态名称
		resultMap.put("appStatusName", AppStatusEnum.valueOf(data.getAppStatus()).getMessage());
		// 借款金额
		resultMap.put("appAmount", loanAppInfo.get("appAmount"));
		// 销售经理代码
		resultMap.put("customerManagerCode", salesInfo.get("customerManagerCode"));
		// 销售经理名称
		resultMap.put("customerManagerName", salesInfo.get("customerManagerName"));
		// 生效时间，进件审核通过或者拒件时间
		if (firstAudit != null) {
			resultMap.put("auditTime", firstAudit.get("auditTime"));
		}
		// 联系人信息
		resultMap.put("contactInfo", contactInfo);
		// 进件城市
		resultMap.put("appCity", loanAppInfo.get("appCity"));
		// 借款用途
		resultMap.put("purpose", loanAppInfo.get("purpose"));
		// 婚姻状况
		resultMap.put("maritalStatus", personalInfo.get("maritalStatus"));
		// 教育程度
		resultMap.put("educateLevel", personalInfo.get("educateLevel"));
		// 现居城市
		resultMap.put("residenceAddressCity", personalInfo.get("residenceAddressCity"));
		// 详细地址
		resultMap.put("residenceAddressDetails", personalInfo.get("residenceAddressDetails"));

		return resultMap;
	}

	@Autowired
	private IVersionConfigService versionConfigService;

	@Override
	public void receiveLoanApply(String json) {
		LoanApply loanInfo = JsonUtil.convert(json, LoanApply.class);

		LoginUser loginUser = SessionUtil.getCurrentUser();

		// 检查进件是否已保存
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", loanInfo.getAppId());
		LoanApply loanInfoQuery = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
		if (loanInfoQuery == null) {
			loanInfo.setAppStatus(AppStatusEnum.WAIT_COMPLEMENT.getCode());
			mongoTemplate.save(loanInfo);

			LOGGER.info("开始-borrowerlite推送loanApp回调之后，borrowerLite之中不存在的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanInfo.getAppId(), loanInfo.getAppStatus());
      kafkaService.sendBorrowerLiteLoanApplyInfo(loanInfo);
      LOGGER.info("结束-borrowerlite推送loanApp回调之后，borrowerLite之中不存在的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanInfo.getAppId(), loanInfo.getAppStatus());

      LoanAuditHistory loanaudithistory = new LoanAuditHistory();
			loanaudithistory.setAppId(loanInfo.getAppId());
			loanaudithistory.setUserId(loginUser == null ? "" : loginUser.getUserId());
			loanaudithistory.setTimestamp(loanInfo.getTimestamp());
			loanaudithistory.setOperate(LoanAuditOperation.LOAN_INPUT.getValue());
			loanaudithistory.setBizData(loanInfo.getLoanData());
			mongoTemplate.save(loanaudithistory);
		}

		// loanApp进件详情已通知
		// TaskNodeInfo taskNodeInfo =
		// this.updateTaskStatus(loanInfo.getAppId(),
		// ReceiveServiceEnum.BORROWER_LOAN_APPLY_DETAIL_LY);
		// this.compeleteReciveTask(loanInfo.getAppId(),
		// taskNodeInfo.getProcessInstanceId(), taskNodeInfo.getTaskId(),
		// DictItem.VARIABLES_VALUE_PREAUDIT_PASS);
		if (loanInfoQuery == null || StringUtils.isBlank(loanInfoQuery.getProcessInstanceId())) {
			// 开启流程
			try {
				startProcess(loanInfo);
			} catch (Exception e) {
				errorNoticeService.notice(loanInfo.getAppId(), "流程开启失败", "流程开启失败-" + e.getMessage(), e);
			}
		}
	}

	@Override
	public void receiveLoanApplyDetail(String json) {
		LoanApply loanInfo = JsonUtil.convert(json, LoanApply.class);

		LoginUser loginUser = SessionUtil.getCurrentUser();

		// 检查进件是否已保存
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", loanInfo.getAppId());
		LoanApply loanInfoQuery = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
		if (loanInfoQuery == null) {
			mongoTemplate.save(loanInfo);

			LoanAuditHistory loanaudithistory = new LoanAuditHistory();
			loanaudithistory.setAppId(loanInfo.getAppId());
			loanaudithistory.setUserId(loginUser == null ? "" : loginUser.getUserId());
			loanaudithistory.setTimestamp(loanInfo.getTimestamp());
			loanaudithistory.setOperate(LoanAuditOperation.LOAN_INPUT.getValue());
			loanaudithistory.setBizData(loanInfo.getLoanData());
			mongoTemplate.save(loanaudithistory);

			if (needSendQA(loanInfo)) {
				// 推送loan至标的系统bid
				pushService.pushQApp(loanInfo.getAppId());
			}
		} else if (AppStatusEnum.LOANAPPLY_RULE_SUCCESS.getCode().equals(loanInfoQuery.getAppStatus())) {
			Update update = new Update();
			update.set("appStatus", loanInfo.getAppStatus());
			update.set("loanData", loanInfo.getLoanData());
			update.set("channel", loanInfo.getChannel());

			springMongoTemplate.updateFirst(new Query(Criteria.where("appId").is(loanInfo.getAppId())), update,
					LoanApply.class);

			if (clientId.equals(loanInfoQuery.getClientId())) {
				// loanApp进件详情已通知
				TaskNodeInfo taskNodeInfo = this.updateTaskStatus(loanInfo.getAppId(),
						ReceiveServiceEnum.BORROWER_LOAN_APPLY_DETAIL_LY);
				this.compeleteReciveTask(loanInfo.getAppId(), taskNodeInfo.getProcessInstanceId(),
						taskNodeInfo.getTaskId(), DictItem.VARIABLES_VALUE_PREAUDIT_PASS);
			}
		}
	}

	private Boolean needSendQA(LoanApply loanInfo) {
		boolean result = false;
		// 如果是岭勇进件，则直接推送申请 ----channel判断，终审通过，客户端判断
		if (DictItem.APPLY_CHANNEL_LY.equals(loanInfo.getChannel()) 
				&& AppStatusEnum.FINAL_AUDIT_PASS.getCode().equals(loanInfo.getAppStatus())) {
			result = true;
		}
		return result;
	}

	@Override
	public void reStartProcess(String appId) throws Exception {
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		LoanApply loanInfo = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
		if (StringUtils.isBlank(loanInfo.getProcessInstanceId())) {
			startProcess(loanInfo);
		}
	}

	public void startProcess(LoanApply loanInfo) throws Exception {
		Map<String, Object> extras = MapUtils.getMap(loanInfo.getLoanData(), "extras");
		String appVersion = MapUtils.getString(extras, "version");
		// VersionConfig versionConfig =
		// versionConfigService.queryVersionConfig(loanInfo.getTenantId(),
		// loanInfo.getClientId(), appVersion);
		// if (versionConfig == null) {
		// Exception e = new
		// Exception(String.format("缺少版本配置文件,tenantId=%s,clientId=%s,appVersion=%s",
		// loanInfo.getTenantId(), loanInfo.getClientId(), appVersion));
		// throw e;
		// }

		// String processDefId = versionConfig.getProcessDefId();
		// ProcessVariablesConfig processVariablesConfig = versionConfigService
		// .queryProcessVariablesConfig(processDefId,
		// TaskDefinitionKey.START.getCode(),
		// TaskAction.START.getCode());
		// if (processVariablesConfig == null) {
		// Exception e = new Exception(String.format(
		// "缺少流程变量配置文件,processDefId=%s", processDefId));
		// errorNoticeService.notice(loanInfo.getAppId(), "流程开启失败",
		// "流程开启失败--缺少流程变量配置文件", e);
		// return;
		// }

		String paramsJson = "{\"processDefKey\":\"QYJ_Borrower_Audit\",\"variables\":[{\"name\":\"varProductCode\",\"value\":\"Q易借\"},{\"name\":\"varLoanAppStatus\",\"value\":\"1001\"}]}";
		Map<String, Object> param = JsonUtil.convert(paramsJson, Map.class);
		// param.put("processVersion", versionConfig.getProcessVersion());
		param.put("appId", loanInfo.getAppId());
		LOGGER.info("进件编号:{},调用BPM发起流程,请求参数为:{}", loanInfo.getAppId(), param);
		String resultJson = bpmsService.startProcess(param);
		resultJson = resultJson == null ? "" : resultJson;
		LOGGER.info("进件编号:{},调用BPM发起流程,返回结果为:{}", loanInfo.getAppId(), resultJson);
		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> result = MapUtils.getMap(resultObj, "responseBody");

		String processInstanceId = MapUtils.getString(result, "processInstanceId");
		String processDefId = MapUtils.getString(result, "processDefId");

		if (StringUtils.isBlank(processInstanceId)) {
			throw new Exception(resultJson);
		} else {
			LoanBpmRelation loanBpmRelation = new LoanBpmRelation();
			loanBpmRelation.setAppId(loanInfo.getAppId());
			loanBpmRelation.setProcessInstanceId(processInstanceId);
			loanBpmRelation.setProcessDefId(processDefId);
			mongoTemplate.save(loanBpmRelation);

			Map<String, String> params = new HashMap<String, String>();
			params.put("appId", loanInfo.getAppId());
			Map<String, String> setParams = new HashMap<String, String>();
			setParams.put("processInstanceId", processInstanceId);
			setParams.put("processDefId", processDefId);
			mongoTemplate.findAndModify(params, setParams, LoanApply.class);
		}

	}

	@Override
	public LoanApply addLoanApply(JSONObject jsonObject) {
		LoginUser loginUser = SessionUtil.getCurrentUser();

		LoanApply loanApply = JsonUtil.convert(jsonObject.toJSONString(), LoanApply.class);
		loanApply.setAppStatus(AppStatusEnum.WAIT_COMPLEMENT.getCode());
		Map params = new HashMap();
		params.put("appId", loanApply.getAppId());
		LoanApply loanInfoquery = (LoanApply) mongoTemplate.findOne(params, LoanApply.class);
		if (null == loanInfoquery) {
			loanApply.setUserId(loginUser == null ? "" : loginUser.getUserId());
			mongoTemplate.save(loanApply);
;
      LOGGER.info("开始-borrowerlite推送新增进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());
      kafkaService.sendBorrowerLiteLoanApplyInfo(loanApply);
      LOGGER.info("结束-borrowerlite推送新增进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());
			LoanAuditHistory loanaudithistory = new LoanAuditHistory();
			loanaudithistory.setAppId(loanApply.getAppId());
			loanaudithistory.setUserId(loginUser == null ? "" : loginUser.getUserId());
			loanaudithistory.setTimestamp(loanApply.getTimestamp());
			loanaudithistory.setOperate(LoanAuditOperation.LOAN_INPUT.getValue());
			loanaudithistory.setBizData(loanApply.getLoanData());
			mongoTemplate.save(loanaudithistory);
		}

		return loanApply;
	}

	@Override
	public void saveCarrieroperatorResult(CarrieroperatorResult carrieroperatorResult) throws Exception {
		carrieroperatorResult.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(carrieroperatorResult);
	}

	@Override
	public List<CarrieroperatorResult> getCarrieroperatorByAppId(String appId) throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("appId").is(appId));
		query.with(new Sort(Direction.DESC, "timestamp"));
		List<CarrieroperatorResult> list = springMongoTemplate.find(query, CarrieroperatorResult.class);
		return list;
	}

	@Autowired
	IKafkaService kafkaService;

	@Override
	public void rePushKafka(String appId, String transactionId, String policyCode) {
		LoanNdesRelation relation = new LoanNdesRelation();
		relation.setAppId(appId);
		relation.setPolicyCode(policyCode);
		relation.setTransactionId(transactionId);
		mongoTemplate.save(relation);

		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("appId", appId);
		LoanApply loanInfo = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);

		if (loanInfo != null) {
			kafkaService.sendLoanIn(loanInfo, transactionId, policyCode);
		}
	}

  @Override
  public void rePushKafka(String appId) {
    Map<String, Object> queryParam = new HashMap<String, Object>();
    queryParam.put("appId", appId);
    LoanApply loanInfo = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
    if (loanInfo != null) {
      kafkaService.sendBorrowerLiteLoanApplyInfo(loanInfo);
    }
  }

	@Override
	public LoanNdesRelation queryAndUpdateNdes(Map<String, Object> params, Map<String, Object> setParams) {
		LoanNdesRelation ndesRelation = (LoanNdesRelation) mongoTemplate.findAndModify(params, setParams,
				LoanNdesRelation.class);
		return ndesRelation;
	}

	@Override
	public List<CallBackResult> queryCallBackResult(String appId, String type) {
		Query query = new Query();
		query.addCriteria(Criteria.where("appId").is(appId));
		query.addCriteria(Criteria.where("type").is(type));
		query.with(new Sort(new Order(Direction.ASC, "timestamp")));
		return springMongoTemplate.find(query, CallBackResult.class);
	}

	@Override
	public List<Map<String, Object>> getTasksByInstanceId(String instanceId) {
		Map<String, Object> bpmsparams = new HashMap<String, Object>();
		bpmsparams.put("processInstanceId", instanceId);
		String resultJson = bpmsService.queryList(bpmsparams);
		if (resultJson == null) {
			return null;
		}
		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> map = MapUtils.getMap(resultObj, "responseBody");
		return (List<Map<String, Object>>) MapUtils.getObject(map, "data");
	}

	@Override
	public String getFirstTaskIdByInstanceId(String instanceId) {
		if (StringUtils.isBlank(instanceId)) {
			return null;
		}

		Map<String, Object> bpmsparams = new HashMap<String, Object>();
		bpmsparams.put("processInstanceId", instanceId);
		String resultJson = bpmsService.queryList(bpmsparams);
		if (resultJson == null) {
			return null;
		}
		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> map = MapUtils.getMap(resultObj, "responseBody");
		List<Map<String, Object>> bpmTaskList = (List<Map<String, Object>>) MapUtils.getObject(map, "data");

		if (!CollectionUtils.isEmpty(bpmTaskList)) {
			Map<String, Object> bpmTask = bpmTaskList.get(0);
			return (String) bpmTask.get("taskId");
		} else {
			return null;
		}
	}

	@Override
	public void checkoutAppStore() {
		// 查询出appCity 对应的门店,添加需要更新的数据;
		Dict dict = springMongoTemplate.findOne(new Query(Criteria.where("value").is("APP_CITY")), Dict.class);
		List<Dict> appCities = dict.getChildren();
		for (Dict appCity : appCities) {
			List<Dict> storeList = appCity.getChildren();
			if (null != storeList && storeList.size() > 0) {
				// 目前城市和门店一对一关系,默认取第一个;
				Dict store = storeList.get(0);
				if (null != store && !StringUtils.isBlank(store.getLabel()) && null != store.getValue()) {
					// 更新相关数据
					Update update = Update.update("loanData.loanAppInfo.appStore", store.getValue())
							.set("loanData.loanAppInfo.appStoreName", store.getLabel());
					int count;
					do {
						List<Object> conditions = new ArrayList<Object>();
						conditions.add(null);
						conditions.add("");
						Query query = new Query(Criteria.where("loanData.loanAppInfo.appCity").is(appCity.getValue())
								.and("loanData.loanAppInfo.appStore").in(conditions)).limit(300);
						WriteResult writeResult = springMongoTemplate.updateMulti(query, update, LoanApply.class);
						count = writeResult.getN();
					} while (count == 300);
				}
			}
		}
	}

	/**
	 * 检查更新loanApply记录中的auditor;
	 * (当auditor为null的时候,默认记录为审核人,该接口为了修复丢失auditor的异常记录)
	 */
	@Override
	public void checkoutAuditor() {
		// 查询出所有需要修改的进件
		Query firstAuditorExceptionQuery = new Query(Criteria.where("loanData.firstAudit.auditor").is("审核人"));
		List<LoanApply> firstAuditorExceptionLoanApplies = springMongoTemplate.find(firstAuditorExceptionQuery,
				LoanApply.class);
		for (LoanApply loanApply : firstAuditorExceptionLoanApplies) {
			String appId = loanApply.getAppId();
			Query firstAuditClaimQuery = new Query(Criteria.where("appId").is(appId))
					.addCriteria(Criteria.where("bizData.taskType").is("UT_Risk_Audit"));
			LoanAuditHistory loanAuditHistory = springMongoTemplate.findOne(firstAuditClaimQuery,
					LoanAuditHistory.class);
			String userId = loanAuditHistory.getUserId();
			// 修改进件 和 进件历史记录;
			Update update = Update.update("bizData.firstAudit.auditor", userId == null ? "审核人" : userId);
			update.set("userId", userId == null ? "审核人" : userId);
			springMongoTemplate.updateFirst(
					new Query(Criteria.where("appId").is(appId))
							.addCriteria(Criteria.where("operate").is(LoanAuditOperation.FIRST_AUDIT.getValue())),
					update, LoanAuditHistory.class);
			springMongoTemplate.updateFirst(new Query(Criteria.where("appId").is(appId)),
					Update.update("loanData.firstAudit.auditor", userId), LoanApply.class);
		}
		Query lastAuditorExceptionQuery = new Query(Criteria.where("loanData.finalAudit.auditor").is("审核人"));
		List<LoanApply> lastAuditorExceptionLoanApplies = springMongoTemplate.find(lastAuditorExceptionQuery,
				LoanApply.class);
		for (LoanApply loanApply : lastAuditorExceptionLoanApplies) {
			String appId = loanApply.getAppId();
			Query lastAuditClaimQuery = new Query(Criteria.where("appId").is(appId))
					.addCriteria(Criteria.where("bizData.taskType").is(DictItem.TASK_AUDIT_FINAL));
			LoanAuditHistory loanAuditHistory = springMongoTemplate.findOne(lastAuditClaimQuery,
					LoanAuditHistory.class);
			String userId = loanAuditHistory.getUserId();
			// 修改进件 和 进件历史记录;

			Update update = Update.update("bizData.firstAudit.auditor", userId == null ? "审核人" : userId);
			update.set("userId", userId == null ? "审核人" : userId);
			springMongoTemplate.findAndModify(
					new Query(Criteria.where("appId").is(appId))
							.addCriteria(Criteria.where("operate").is(LoanAuditOperation.FINAL_AUDIT.getValue())),
					update, LoanAuditHistory.class);
			springMongoTemplate.findAndModify(new Query(Criteria.where("appId").is(appId)),
					Update.update("loanData.finalAudit.auditor", userId), LoanApply.class);
		}
	}

	@Override
	public boolean enableStoreReject(String applyId) {
		return enableStoreReject(queryLoanInfoByAppId(applyId));
	}

	@Override
	public Boolean compeleteTask(String appId, String taskId, List<Map<String, String>> variables) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("action", "complete");
		if (variables != null && variables.size() > 0) {
			param.put("variables", variables);
		}

		boolean isSuccessCommit = true;
		try {
			LOGGER.info("提交用户任务,进件编号:{},任务编号:{},请求参数:{}", appId, taskId, param);
			String resultJson = bpmsService.actionProcess(param, taskId);
			resultJson = resultJson == null ? "" : resultJson;
			LOGGER.info("提交用户任务,进件编号:{},任务编号:{},返回结果:{}", appId, taskId, resultJson);

			Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
			String code = MapUtils.getString(resultObj, "responseCode", "");
			if (!StringUtils.equals(code, DictItem.BPMS_SUCCESS)) {
				isSuccessCommit = false;
			}
		} catch (Exception e) {
			isSuccessCommit = false;
			LOGGER.error("提交用户任务,进件编号:{},任务编号:{},返回结果:任务异常", appId, taskId, e);
		}
		return isSuccessCommit;
	}

	//////////////////////////
	@Override
	public void saveLoanAppStatus(String appId, AppStatusEnum status) {
		Query query = Query.query(Criteria.where("appId").is(appId));
		Update update = new Update();
		update.set("appStatus", status.name());
		springMongoTemplate.updateFirst(query, update, LoanApply.class);

  }

  @Override
  public void saveLoanAppStatusXdt(String appId, AppStatusEnum status) {
    Query query = Query.query(Criteria.where("appId").is(appId));
    Update update = new Update();
    update.set("appStatus", status.name());
    springMongoTemplate.updateFirst(query, update, LoanApply.class);
    LoanApply loanApply = springMongoTemplate.findOne(query, LoanApply.class);
    LOGGER.info("开始-borrowerlite推送小贷放款信息之后的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());
    kafkaService.sendBorrowerLiteLoanApplyInfo(loanApply);
    LOGGER.info("结束-borrowerlite推送小贷放款信息之后的进件信息到Kafka， 进件编码：{}，进件状态：{}", loanApply.getAppId(), loanApply.getAppStatus());

  }

	@Override
	public boolean pushLoanAppToNCobra(String appId) {
		Map<String, String> queryParam = new HashMap<String, String>();
		queryParam.put("appId", appId);
		LoanApply loanApply = (LoanApply) mongoTemplate.findOne(queryParam, LoanApply.class);
		LOGGER.info("borrowerLite推送ncobra的进件编码：{}, 进件详情：{}", appId, loanApply);
		String response = nCobraService.pushLoanApp(loanApply);
		LOGGER.info("ncobra接收的borrowerLite推送的进件编码：{}, 返回信息：{}", appId, response);
		ResponseData responseData = JsonUtil.convert(response, ResponseData.class);
		if (responseData.getCode() == 2000) {
			TaskNodeInfo taskNodeInfo = this.updateTaskStatus(appId,
					ReceiveServiceEnum.BORROWER_LOAN_APPLY_PUSH_NCOBRA);
			if (taskNodeInfo == null) {
				return false;
			}
			Map<String, Object> map = (Map<String, Object>) responseData.getData();
			Integer auditStatus = (Integer) map.get("auditStatus");// 1001通过
			if (1001 == auditStatus) {
				this.saveLoanAppStatus(appId, AppStatusEnum.FINAL_AUDIT_PASS);
				this.compeleteReciveTask(appId, taskNodeInfo.getProcessInstanceId(), taskNodeInfo.getTaskId(),
						DictItem.VARIABLES_VALUE_PREAUDIT_PASS);
			} else {
				this.compeleteReciveTask(appId, taskNodeInfo.getProcessInstanceId(), taskNodeInfo.getTaskId(),
						DictItem.VARIABLES_VALUE_AUDIT_REJECT);
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void startBorrowerProcess(LoanApply loanInfo) throws Exception {
		VersionConfig versionConfig = null;
		Map<String, Object> extras = MapUtils.getMap(loanInfo.getLoanData(), "extras");
		if (extras != null) {
			String appVersion = MapUtils.getString(extras, "version");
			versionConfig = versionConfigService.queryVersionConfig(loanInfo.getTenantId(), loanInfo.getClientId(),
					appVersion);
		}
		String paramsJson = "{\"processDefKey\":\"QYJ_Borrower_Audit\",\"variables\":[{\"name\":\"varLoanAppStatus\",\"value\":\"1001\"}]}";
		Map<String, Object> param = JsonUtil.convert(paramsJson, Map.class);
		if (versionConfig != null) {
			param.put("processVersion", versionConfig.getProcessVersion());
		}
		param.put("appId", loanInfo.getAppId());

		LOGGER.info("进件编号:{},调用BPM Borrower发起流程,请求参数为:{}", loanInfo.getAppId(), param);
		String resultJson = bpmsService.startProcess(param);
		resultJson = resultJson == null ? "" : resultJson;
		LOGGER.info("进件编号:{},调用BPM Borrower发起流程,返回结果为:{}", loanInfo.getAppId(), resultJson);

		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> result = MapUtils.getMap(resultObj, "responseBody");

		String processInstanceId = MapUtils.getString(result, "processInstanceId");
		String processDefId = MapUtils.getString(result, "processDefId");

		if (StringUtils.isBlank(processInstanceId)) {
			throw new Exception(resultJson);
		} else {
			LoanBpmRelation loanBpmRelation = new LoanBpmRelation();
			loanBpmRelation.setAppId(loanInfo.getAppId());
			loanBpmRelation.setProcessInstanceId(processInstanceId);
			loanBpmRelation.setProcessDefId(processDefId);
			mongoTemplate.save(loanBpmRelation);

			Map<String, String> params = new HashMap<String, String>();
			params.put("appId", loanInfo.getAppId());
			Map<String, String> setParams = new HashMap<String, String>();
			setParams.put("processInstanceId", processInstanceId);
			setParams.put("processDefId", processDefId);
			mongoTemplate.findAndModify(params, setParams, LoanApply.class);
		}

	}

	@Override
	public String getEncodeLoanMsg(String appId) {
		LoanApply loanApply = springMongoTemplate.findOne(new Query(Criteria.where("appId").is(appId)),
				LoanApply.class);
		String loanApplyJson = JsonUtil.convert(loanApply);
		return MsgEncodeUtil.msgEncode(loanApplyJson);
	}
}
