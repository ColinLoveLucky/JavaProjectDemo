package com.qf.cobra.loan.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.feign.service.NdesService;
import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;

@Service("LoanApplyRuleReviceTask")
public class ReviceTaskLoanApplyRuleImpl extends ReviceTask{
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ReviceTaskLoanApplyRuleImpl.class);
	
	@Autowired
	private NdesService ndesService;
	@Autowired
	IKafkaService kafkaService;
	@Autowired
	MongoOperate mongoTemplate;
	@Autowired
	private ErrorNoticeService errorNoticeService;
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean excute(String appId) {
		//进件规则code
		String policyCode = DictItem.POLICY_CODE_APPLY;
		boolean isSuccess = true;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appId", appId);
			LoanApply loanApply = (LoanApply) mongoTemplate.findOne(params,
					LoanApply.class);

			Map<String, Object> personalInfo = MapUtils.getMap(
					loanApply.getLoanData(), "personalInfo");
			String idNo = MapUtils.getString(personalInfo, "idCard");
			String actionType = "START";
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("extId", appId);
			param.put("policyCode", policyCode);
			param.put("actionType", actionType);

			Map<String, String> variables = new HashMap<String, String>();
			variables.put("HISTORY_KEY", idNo);
			variables.put("BLACKLIST_KEY", idNo);
			variables.put("BORROWER_IDENTITY_NO", idNo);
			param.put("requestParams", variables);

			LOGGER.info("调用ndes系统,进件编号:{},policy编号:{},请求参数:{}", appId,
					policyCode, param);
			String resultJson = ndesService.startProcess(param);
			resultJson = resultJson == null ? "" : resultJson;
			LOGGER.info("调用ndes系统,进件编号:{},policy编号:{},返回结果:{}", appId,
					policyCode, resultJson);

			Map<String, Object> resultObj = JsonUtil.convert(resultJson,
					Map.class);
			Map<String, Object> dataObj = MapUtils.getMap(resultObj,
					"responseBody");
			//响应code
			String code = MapUtils.getString(resultObj, "responseCode", "");

			String transactionId = MapUtils.getString(dataObj, "transactionId");
			if (StringUtils.isBlank(transactionId) || !StringUtils.equals(code, ResponseCode.NDES_SUCCESS.toString())) {
				isSuccess = false;
				errorNoticeService.notice(appId, "调用ndes系统",
						String.format("调用ndes系统-任务异常,借款编号:%s,policy编号:%s", appId, policyCode),
						new Exception(resultJson));
			} else {
				LoanNdesRelation relation = new LoanNdesRelation();
				relation.setAppId(appId);
				relation.setPolicyCode(policyCode);
				relation.setTransactionId(transactionId);
				mongoTemplate.save(relation);

				kafkaService.sendLoanIn(loanApply, transactionId, policyCode);
			}
		} catch (Exception e) {
			isSuccess = false;
			LOGGER.error("调用ndes系统,进件编号:{},policy编号:{},返回结果:任务异常", appId,
					policyCode, e);
			errorNoticeService.notice(appId, "调用ndes系统", String.format(
					"调用ndes系统-任务异常,进件编号:%s,policy编号:%s", appId, policyCode), e);
		}
		return isSuccess;
	}
	

}
