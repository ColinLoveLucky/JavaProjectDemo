package com.qf.cobra.loan.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.SystemOperation;

@Service("OverDueReviceTask")
public class ReviceTaskOverDueImpl extends ReviceTask {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Value("${default.reject.userId:}")
	private String userId;
	@Autowired
	private IKafkaService kafkaService;
	@Autowired
	private LoanService loanService;

	@SuppressWarnings("unchecked")
	@Override
	public Boolean excute(String appId) {
		// 获取任务接收人
		Query query = Query.query(Criteria.where("appId").is(appId));
		query.addCriteria(Criteria.where("operate").is(SystemOperation.CLAIM_TASK.getValue()));
		query.addCriteria(Criteria.where("bizData.taskType").is(DictItem.TASK_AUDIT_RISK));
		LoanAuditHistory loanAudit = mongoTemplate.findOne(query, LoanAuditHistory.class);
		String userIdSec = (loanAudit != null) ? loanAudit.getUserId() : userId;

		// 1.更新初审意见
		Query querySec = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = (LoanApply) mongoTemplate.findOne(querySec, LoanApply.class);

		Map<String, Object> loanData = loanApply.getLoanData();
		Map<String, Object> firstAudit = MapUtils.getMap(loanData, "firstAudit", new HashMap<String, Object>());

		firstAudit.put("auditResult", "REJECT");
		firstAudit.put("typeCode", "OTHER");
		firstAudit.put("subTypeCode", "9004");
		firstAudit.put("rejectLevel", "REJECT_90_DAYS");
		firstAudit.put("auditTime", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		firstAudit.put("opinion", "超时拒绝");
		firstAudit.put("remark", "");
		firstAudit.put("auditor", userIdSec);
		loanData.put("firstAudit", firstAudit);

		Update update = new Update();
		update.set("loanData", loanData);
		update.set("appStatus", AppStatusEnum.FIRST_AUDIT_REJECT.getCode());
		mongoTemplate.updateFirst(querySec, update, LoanApply.class);

		// 2.保存历史进件信息
		LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
		loanAuditHistory.setUserId(userIdSec);
		loanAuditHistory.setOperate(LoanAuditOperation.FIRST_AUDIT.getValue());
		loanAuditHistory.setBizData(loanData);
		loanAuditHistory.setAppId(appId);
		loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(loanAuditHistory);
		
		// 3.发送kafka
		kafkaService.sendLoanCredit(loanApply);
		return true;
	}
	
	@Override
	protected void compeleteTask(TaskNodeInfo taskNodeInfo) {
		//初审超时更新taskNodeInfo为COMPELETE_STATUS_DONE
		loanService.updateTaskStatus(taskNodeInfo.getAppId(),ReceiveServiceEnum.OVER_DUE);
	}
}
