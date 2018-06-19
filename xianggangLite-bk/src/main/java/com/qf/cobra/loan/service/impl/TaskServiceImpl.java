package com.qf.cobra.loan.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.netfinworks.common.lang.StringUtil;
import com.qf.cobra.feign.service.BpmsService;
import com.qf.cobra.loan.service.ITaskService;
import com.qf.cobra.log.service.ISystemLogService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.qyjapi.service.IQyjapiService;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.pojo.LoginUser;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.pojo.ProcessVariablesConfig.TaskDefinitionKey;
import com.qf.cobra.pojo.SystemLog;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.SessionUtil;
import com.qf.cobra.util.SystemOperation;

@Service
public class TaskServiceImpl implements ITaskService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
	private BpmsService bpmsService;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ISystemLogService systemLogService;
	@Autowired
	private IQyjapiService qyjapiService;

	@Override
	public void queryTaskPool(Pagination pagination) throws Exception {
		Map<String, Object> condition = pagination.getCondition();
		String groupId = String.valueOf(condition.get("groupId"));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("groupId", groupId);
		String resultJson = bpmsService.queryList(params);
		resultJson = resultJson == null ? "" : resultJson;
//		LOGGER.info("任务列表查询bpms返回结果："+resultJson);
		
		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> map = MapUtils.getMap(resultObj, "responseBody");
		List<Map<String, Object>> bpmTaskList = (List<Map<String, Object>>) MapUtils.getObject(map, "data");
		filterAndPageTaskList(pagination, bpmTaskList);
	}

	@Override
	public void queryUserTask(Pagination pagination) {
		int start = (pagination.getCurrentPage() - 1) * pagination.getPageSize();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", SessionUtil.getCurrentUser().getUserId());
		params.put("start", String.valueOf(start));
		params.put("size", String.valueOf(pagination.getPageSize()));
		String resultJson = bpmsService.queryList(params);
		resultJson = resultJson == null ? "" : resultJson;

		Map<String, Object> resultObj = JsonUtil.convert(resultJson, Map.class);
		Map<String, Object> bpmTaskResult = MapUtils.getMap(resultObj, "responseBody");

		pagination.setData((List) MapUtils.getObject(bpmTaskResult, "data"));
		pagination.setTotal(MapUtils.getIntValue(bpmTaskResult, "total"));
		constructBizAndBpm(pagination);
	}

	private void constructBizAndBpm(Pagination pagination) {
		List<String> processInstanceIdList = new ArrayList<String>();
		List<Map<String, Object>> bpmTaskList = pagination.getData();
		if (CollectionUtils.isEmpty(bpmTaskList)) {
			return;
		}
		for (Map<String, Object> bpmTask : bpmTaskList) {
			processInstanceIdList.add(String.valueOf(bpmTask.get("processInstanceId")));
		}
		Query query = Query.query(Criteria.where("processInstanceId").in(processInstanceIdList));
		List<LoanApply> loanApplyList = mongoTemplate.find(query, LoanApply.class);
		if (!CollectionUtils.isEmpty(loanApplyList)) {
			Map<String, LoanApply> loanApplyMap = loanApplyList.stream()
					.collect(Collectors.toMap(LoanApply::getProcessInstanceId, loanApply -> loanApply));
			for (Map<String, Object> bpmTask : bpmTaskList) {
				String processId = String.valueOf(bpmTask.get("processInstanceId"));
				LoanApply loanApply = loanApplyMap.get(processId);
				if (loanApply != null) {
					Map<String, Object> personalInfo = (Map<String, Object>) loanApply.getLoanData()
							.get("personalInfo");
					Map<String, Object> loanAppInfo = (Map<String, Object>) loanApply.getLoanData().get("loanAppInfo");
					bpmTask.put("appId", loanApply.getAppId());
					bpmTask.put("timestamp", loanApply.getTimestamp());
					if (personalInfo != null) {
						bpmTask.put("name", personalInfo.get("name"));
						bpmTask.put("card", personalInfo.get("idCard"));
						bpmTask.put("mobilePhone", personalInfo.get("mobilePhone"));
					}
					if (loanAppInfo != null) {
						bpmTask.put("appAmount", loanAppInfo.get("appAmount"));
						bpmTask.put("loanMaturity", loanAppInfo.get("loanMaturity"));
					}
				}
			}
		}
	}

	private void filterAndPageTaskList(Pagination pagination, List<Map<String, Object>> bpmTaskList) throws Exception {
		Map<String, String> condition = pagination.getCondition();
		String groupId = String.valueOf(condition.get("groupId"));
		String sortName = condition.get("sortField");
		if(StringUtil.isBlank(sortName)){
			  sortName="firstAuditTime";
			}
		String sortOrder = condition.get("sortOrder");
		List<LoanApply> loanApplyList = getLoanApplyByCondition(pagination, bpmTaskList, groupId);
		Map<String, LoanApply> loanApplyMap = loanApplyList.stream().collect(Collectors.toMap((loanApply) -> {
			if (!StringUtils.isEmpty(loanApply.getProcessInstanceId())) {
				return loanApply.getProcessInstanceId();
			} else {
				return UUID.randomUUID().toString();
			}
		}, loanApply -> loanApply));
		if (!CollectionUtils.isEmpty(loanApplyList)) {
			// 获得需要显示的字段
			List<Map<String, Object>> matchLoanApplyList = groupShowMatchLoanApply(bpmTaskList, loanApplyMap);
			if(!"finalappeal".equals(groupId)){
			    pagination.setTotal(matchLoanApplyList.size());
			    int size = pagination.getPageSize();
  				int start = (pagination.getCurrentPage()-1) * size;
  				int limit = (start + size) > matchLoanApplyList.size() ? matchLoanApplyList.size() : (start + size);
  				pagination.setData(matchLoanApplyList.subList(start, limit));
			}else{
			  Collections.sort(matchLoanApplyList, new MyComparator(sortOrder, sortName));
			  pagination.setData(matchLoanApplyList);
			}
		}
//		if (!CollectionUtils.isEmpty(loanApplyList)) {
//			// 获得需要显示的字段
//			List<Map<String, Object>> matchLoanApplyList = groupShowMatchLoanApply(bpmTaskList, loanApplyMap);
//			// 排序
//			if ("finalappeal".equals(groupId)) {
//				if (matchLoanApplyList != null) {
//					Collections.sort(matchLoanApplyList, new MyComparator(sortOrder, sortName));
//				}
//			}
//			pagination.setTotal(matchLoanApplyList.size());
//			int size = pagination.getPageSize();
//			int start = (pagination.getCurrentPage() - 1) * size;
//			int limit = (start + size) > matchLoanApplyList.size() ? matchLoanApplyList.size() : (start + size);
//			pagination.setData(matchLoanApplyList.subList(start, limit));
//		}
	}

	// 根据查询条件筛选
	private List<LoanApply> getLoanApplyByCondition(Pagination pagination,
			List<Map<String, Object>> bpmTaskList, String groupId) throws Exception {
		Map<String, String> condition = pagination.getCondition();
		String city = SessionUtil.getCurrentUser().getCity();
		if (StringUtils.isEmpty(city)) {
			city = "";
		}
		Query query = new Query();
		/********************** 进件城市查询条件过滤（终审不过滤） *********************/
		if (!"finalappeal".equals(groupId)) {
			query.addCriteria(Criteria.where("loanData.loanAppInfo.appCityName").is(city.split(",")[0]));
		}
		/********************** 进件城市查询条件过滤（终审不过滤） *********************/
		boolean flag = false;
		if (condition != null) {
			String name = condition.get("name");
			String appId = condition.get("appId");
			String startTime = condition.get("startTime");
			String endTime = condition.get("endTime");
			if(name ==  null && appId == null && startTime == null && endTime == null){
				flag = true;
			}
			if (!CollectionUtils.isEmpty(bpmTaskList)) {
				List<String> processIdList = new ArrayList<String>();
				for (Map<String, Object> bpmTask : bpmTaskList) {
					String processId = (String) bpmTask.get("processInstanceId");
					if (!StringUtils.isEmpty(processId)) {
						processIdList.add(processId);
					}
				}
				query.addCriteria(Criteria.where("processInstanceId").in(processIdList));
			}
			if (!StringUtils.isEmpty(name)) {
				Pattern namePattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
				query.addCriteria(Criteria.where("loanData.personalInfo.name").regex(namePattern));
			}
			if (!StringUtils.isEmpty(appId)) {
				query.addCriteria(Criteria.where("appId").is(appId));
			}
			if (!StringUtils.isEmpty(startTime) || !StringUtils.isEmpty(endTime)) {
				Criteria criteria = null;
				if (!StringUtils.isEmpty(startTime)) {
					startTime = DateUtil.formatUTCDate(startTime);
					criteria = Criteria.where("timestamp").gte(startTime);
				}
				if (!StringUtils.isEmpty(endTime)) {
					endTime = DateUtil.formatUTCDate(endTime);
					if (criteria == null) {
						criteria = Criteria.where("timestamp").lte(endTime);
					} else {
						criteria.andOperator(Criteria.where("timestamp").lte(endTime));
					}
				}
				query.addCriteria(criteria);
			}
		}
		if ("finalappeal".equals(groupId)) {
			query.addCriteria(Criteria.where("appStatus").in(AppStatusEnum.FIRST_AUDIT_PASS.getCode()));
			if(flag){
				pagination.setTotal(bpmTaskList.size());
			}else{
				pagination.setTotal(Integer.valueOf(String.valueOf(mongoTemplate.count(query,LoanApply.class))));
			}
			String sortName = "loanData.firstAudit.auditTime";
			String sortOrder = condition.get("sortOrder")==null?"ascend":condition.get("sortOrder");
			if(sortOrder.equalsIgnoreCase(Direction.DESC+"end")){
				query.with(new Sort(new Order(Direction.DESC, sortName)));
			}else{
				query.with(new Sort(new Order(Direction.ASC, sortName)));
			}

			int size = pagination.getPageSize();
			int start = (pagination.getCurrentPage()-1) * size;
			query.skip(start);
			query.limit(size);
		}
		List<LoanApply> loanApplyList = mongoTemplate.find(query, LoanApply.class);
		return loanApplyList;
	}

	// 组合返回参数
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> groupShowMatchLoanApply(List<Map<String, Object>> bpmTaskList,
			Map<String, LoanApply> loanApplyMap) {
		List<Map<String, Object>> matchLoanApplyList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> bpmTask : bpmTaskList) {
			String processId = String.valueOf(bpmTask.get("processInstanceId"));
			LoanApply loanApply = loanApplyMap.get(processId);
			if (loanApply != null) {
				Map<String, Object> personalInfo = (Map<String, Object>) loanApply.getLoanData().get("personalInfo");
				Map<String, Object> loanAppInfo = (Map<String, Object>) loanApply.getLoanData().get("loanAppInfo");
				Map<String, Object> firstAudit = (Map<String, Object>) loanApply.getLoanData().get("firstAudit");
				bpmTask.put("appId", loanApply.getAppId());
				bpmTask.put("timestamp", loanApply.getTimestamp());
				if (personalInfo != null) {
					bpmTask.put("name", personalInfo.get("name"));
					bpmTask.put("card", personalInfo.get("idCard"));
					bpmTask.put("mobilePhone", personalInfo.get("mobilePhone"));
				}
				if (loanAppInfo != null) {
					bpmTask.put("appAmount", loanAppInfo.get("appAmount"));
					bpmTask.put("loanMaturity", loanAppInfo.get("loanMaturity"));
				}
				// 到终审时间
				if (firstAudit != null) {
					bpmTask.put("firstAuditTime", firstAudit.get("auditTime"));
				}
				matchLoanApplyList.add(bpmTask);
			}
		}
		return matchLoanApplyList;

	}

	class MyComparator implements Comparator<Map<String, Object>> {
		private String sortOrder;
		private String sortName;

		MyComparator(String sortOrder, String sortName) {
			this.sortName = sortName;
			this.sortOrder = sortOrder;
		}

		// 这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			if (o1 != null && o2 != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				try {
					if (this.sortOrder != null && "descend".equals(this.sortOrder)) {
						if (sdf.parse((String) o2.get(sortName)).getTime() < sdf
								.parse((String) o1.get(sortName)).getTime()) {
							return -1;
						} else {
							return 0;
						}
					} else {
						if (sdf.parse((String) o2.get(sortName)).getTime() > sdf
								.parse((String) o1.get(sortName)).getTime()) {
							return -1;
						} else {
							return 0;
						}
					}
				} catch (ParseException e) {
					return 0;
				}
			}
			return 0;
		}
	}

	private void updateOperateMap(LoanAuditHistory loanAuditHistory, Map<String, String> operateMap) {
		if (!operateMap.containsKey(loanAuditHistory.getAppId())
				|| StringUtils.isEmpty(loanAuditHistory.getOperate())) {
			operateMap.put(loanAuditHistory.getAppId(), loanAuditHistory.getOperate());
		} else if (LoanAuditOperation.FIRST_SAVE.getValue().equals(loanAuditHistory.getOperate())) {
			if (LoanAuditOperation.LOAN_INPUT.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))) {
				operateMap.put(loanAuditHistory.getAppId(), loanAuditHistory.getOperate());
			}
		} else if (LoanAuditOperation.FIRST_AUDIT.getValue().equals(loanAuditHistory.getOperate())) {
			if (LoanAuditOperation.LOAN_INPUT.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))
					|| LoanAuditOperation.FIRST_SAVE.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))) {
				operateMap.put(loanAuditHistory.getAppId(), loanAuditHistory.getOperate());
			}
		} else if (LoanAuditOperation.FINAL_AUDIT.getValue().equals(loanAuditHistory.getOperate())) {
			if (LoanAuditOperation.LOAN_INPUT.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))
					|| LoanAuditOperation.FIRST_SAVE.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))
					|| LoanAuditOperation.FIRST_AUDIT.getValue().equals(operateMap.get(loanAuditHistory.getAppId()))) {
				operateMap.put(loanAuditHistory.getAppId(), loanAuditHistory.getOperate());
			}
		}
	}

	@Override
	public void queryHistoryTask(Pagination pagination) throws Exception {
		Map<String, String> condition = pagination.getCondition();
		List<String> operateList = new ArrayList<String>();
		operateList.add(LoanAuditOperation.LOAN_INPUT.getValue());
		operateList.add(LoanAuditOperation.FIRST_SAVE.getValue());
		operateList.add(LoanAuditOperation.FIRST_AUDIT.getValue());
		operateList.add(LoanAuditOperation.FINAL_AUDIT.getValue());
		Query query = Query.query(Criteria.where("userId").is(SessionUtil.getCurrentUser().getUserId())
				.andOperator(Criteria.where("operate").in(operateList)));
		if (condition != null) {
			String appId = condition.get("appId");
			if(StringUtils.isNoneBlank(appId)){
				query.addCriteria(Criteria.where("appId").is(appId));
			}
		}
		List<LoanAuditHistory> loanAuditHistoryList = mongoTemplate.find(query, LoanAuditHistory.class);

		if (!CollectionUtils.isEmpty(loanAuditHistoryList)) {
			Map<String, String> operateMap = new HashMap<String, String>();
			List<String> appIdList = new ArrayList<String>();
			for (LoanAuditHistory loanAuditHistory : loanAuditHistoryList) {
				appIdList.add(String.valueOf(loanAuditHistory.getAppId()));
				updateOperateMap(loanAuditHistory, operateMap);
			}
			Query loanApplyQuery = Query.query(Criteria.where("appId").in(appIdList));
			if (condition != null) {
				String name = condition.get("name");
				String startTime = condition.get("startTime");
				String endTime = condition.get("endTime");
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
			}
			// 添加当前用户的门店过滤
			LoginUser user = SessionUtil.getCurrentUser();
//			List<String> storeList = new ArrayList<String>();
//			if (StringUtils.isNotEmpty(user.getStore())) {
//				storeList.add(user.getStore());
//			}
//			List<Map<String, Object>> storeListMap = user.getStoreList();
			List<Map<String, List<Map<String,Object>>>> storeListMap = user.getStoreList();
			List<String> storeList = storeListMap.stream().flatMap(store -> store.get("authorized").stream().map(item -> item.get("dataAlias").toString()))
					.collect(Collectors.toList());
//			if (storeListMap != null) {
//				for (int i = 0; i < storeListMap.size(); i++) {
//					List<Map<String, Object>> authorizedList = (List<Map<String, Object>>) storeListMap.get(i)
//							.get("authorized");
//					for (int j = 0; j < authorizedList.size(); j++) {
//						storeList.add((String) authorizedList.get(j).get("dataAlias"));
//					}
//				}
//			}
			loanApplyQuery.addCriteria(Criteria.where("loanData.loanAppInfo.appStore").in(storeList));
			List<LoanApply> loanApplyList = mongoTemplate.find(loanApplyQuery, LoanApply.class);
			pagination.setTotal(loanApplyList.size());
			int size = pagination.getPageSize();
			int start = (pagination.getCurrentPage() - 1) * size;
			int limit = (start + size) > loanApplyList.size() ? loanApplyList.size() : (start + size);
			List<LoanApply> currentPageLoanApplyList = loanApplyList.subList(start, limit);
			List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
			for (LoanApply apply : currentPageLoanApplyList) {
				Map<String, Object> personalInfo = (Map<String, Object>) apply.getLoanData().get("personalInfo");
				Map<String, Object> loanAppInfo = (Map<String, Object>) apply.getLoanData().get("loanAppInfo");
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("processInstanceId", apply.getProcessInstanceId());
				record.put("appId", apply.getAppId());
				record.put("timestamp", apply.getTimestamp());
				if (personalInfo != null) {
					record.put("name", personalInfo.get("name"));
					record.put("card", personalInfo.get("idCard"));
					record.put("mobilePhone", personalInfo.get("mobilePhone"));
				}
				if (loanAppInfo != null) {
					record.put("appAmount", loanAppInfo.get("appAmount"));
					record.put("loanMaturity", loanAppInfo.get("loanMaturity"));
				}
				record.put("operate", operateMap.get(apply.getAppId()));
				currentPageRecordList.add(record);
			}
			pagination.setData(currentPageRecordList);
		}
	}

    /**
     * 签收任务
     * @param claimMap
     */
    @Override
    public void claimTask(Map<String, String> claimMap) {
        String appId = claimMap.get("appId");
        String taskId = claimMap.get("taskId");
        String taskType = claimMap.get("taskType");

		LOGGER.info("签收任务获取当前token：" + SessionUtil.getCurrentToken());
		String userId = SessionUtil.getCurrentUser().getUserId();
		LOGGER.info("签收任务获取当前userId：" + userId + ",taskId：" + taskId);
		LOGGER.info("签收任务获取当前用户信息：" + JsonUtil.convert(SessionUtil.getCurrentUser()));

		boolean result = true;
		try {
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
				result = false;
			}else if(StringUtils.equals(code, DictItem.BPMS_SUCCESS)){
				if(TaskDefinitionKey.UPLOAD.getCode().equals(taskType)){
					qyjapiService.triggerCertifyTask(appId, DictItem.QYJ_APP_CERTIFY);
				}
				
				Map<String, Object> extData = new HashMap<String, Object>();
				extData.put("taskId", taskId);
				extData.put("taskType", taskType);
				extData.put("appId", appId);
				extData.put("result", result);
				systemLogService.writeLog(new SystemLog(userId, SystemOperation.CLAIM_TASK.getValue(),
						DateUtil.formatCurrentDateTime(), extData));
				LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
				loanAuditHistory.setAppId(appId);
				loanAuditHistory.setUserId(userId);
				loanAuditHistory.setOperate(SystemOperation.CLAIM_TASK.getValue());
				loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
				loanAuditHistory.setBizData(extData);
				mongoTemplate.save(loanAuditHistory);
			}
		} catch (Exception e) {
			result = false;
			LOGGER.error("签收用户任务,进件编号:{},任务编号:{},返回结果:任务异常", appId, taskId, e);
		}
	}

}
