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

@Service("CallReportPolicyRuleService")
public class ReviceTaskCallReportRuleImpl extends ReviceTask{
	
	private static final Logger LOGGER=LoggerFactory.getLogger(ReviceTaskCallReportRuleImpl.class);
	
	@Autowired
	private NdesService ndesService;
	@Autowired
	IKafkaService kafkaService;
	@Autowired
	MongoOperate mongoTemplate;
	
	@Autowired
	private ErrorNoticeService errorNoticeService;
	
	/**
	 * 调用N-DES决策polic
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean excute(String appId) {
		//调用决策规则
		String policyCode = DictItem.POLICY_BORROWER_LOAN_APPLY;
		boolean isSuccess=true;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appId", appId);
			LoanApply loanApply = (LoanApply) mongoTemplate.findOne(params,
					LoanApply.class);

			Map<String, Object> personalInfo = MapUtils.getMap(
					loanApply.getLoanData(), "personalInfo");
			String idNo = MapUtils.getString(personalInfo, "idCard");
      String borrowerName = MapUtils.getString(personalInfo, "name");
      String actionType = "LAUNCH";
      String channel = "qyj";
      String idType = "Id1";
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("extId", appId);
			param.put("policyCode", policyCode);
			param.put("actionType", actionType);
			param.put("channel", channel);

			Map<String, String> variables = new HashMap<String, String>();
			variables.put("HISTORY_KEY", idNo);
			variables.put("BLACKLIST_KEY", idNo);
			variables.put("BORROWER_IDENTITY_NO", idNo);
      //身份证
      variables.put("BORROWER_CREDENTIAL_NO", idNo);
      //证件类型
      variables.put("BORROWER_CREDENTIAL_TYPE", idType);
      //进件号
      variables.put("BORROWER_LOAN_NO", appId);
      //BORROWER_NAME
      variables.put("BORROWER_NAME", borrowerName);
      param.put("requestParams", variables);

			LOGGER.info("borrower-lite调用ndes系统,进件编号:{},policy编号:{},请求参数:{}", appId,
					policyCode, param);
			String resultJson = ndesService.startProcess(param);
			resultJson = resultJson == null ? "" : resultJson;
			LOGGER.info("borrower-lite调用ndes系统,进件编号:{},policy编号:{},返回结果:{}", appId,
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
				errorNoticeService.notice(appId, "borrower-lite调用ndes系统",
						String.format("borrower-lite调用ndes系统-任务异常,借款编号:%s,policy编号:%s", appId, policyCode),
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
			isSuccess=false;
			LOGGER.error("borrower-lite调用ndes系统,进件编号:{},policy编号:{},返回结果:任务异常", appId,
					policyCode, e);
			errorNoticeService.notice(appId, "borrower-lite调用ndes系统", String.format(
					"borrower-lite调用ndes系统-任务异常,进件编号:%s,policy编号:%s", appId, policyCode), e);
		}
		return isSuccess;
	}
	
	

}
