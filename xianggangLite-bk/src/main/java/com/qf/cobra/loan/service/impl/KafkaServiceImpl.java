package com.qf.cobra.loan.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.kafka.SimpleKafkaService;
import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.util.JsonUtil;

@Service
public class KafkaServiceImpl implements IKafkaService {

	@Autowired
	private SimpleKafkaService kafkaService;
	@Autowired
	private MongoOperate mongoTemplate;
	
	@Override
	public void sendLoanIn(LoanApply loanInfo, String transactionId,
			String policyId) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("appId", loanInfo.getAppId());
		msgMap.put("dataKey", "cobra");
		msgMap.put("transactionId", transactionId);
		msgMap.put("policyId", policyId);
		msgMap.put("timestamp", DateTime.now().toString("yyyyMMddHHmmssSSS"));
		msgMap.put("data", loanInfo.getLoanData());
		kafkaService.doSend("kafka_loan_in", JsonUtil.convert(msgMap));
	}

	@Override
	public void sendLoanCredit(LoanApply loanInfo) {
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("appId", loanInfo.getAppId());
		msgMap.put("dataKey", "cobra");
		msgMap.put("transactionId", "");
		msgMap.put("policyId", "");
		msgMap.put("timestamp", DateTime.now().toString("yyyyMMddHHmmssSSS"));

		Map<String,Object> data = loanInfo.getLoanData();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appId", loanInfo.getAppId());
		List<LoanNdesRelation> list = mongoTemplate.findAll(params, LoanNdesRelation.class);
		data.put("rules", list);
		msgMap.put("data", data);
		
		kafkaService.doSend("kafka_loan_credit", JsonUtil.convert(msgMap));
	}
	/**
     * @Title: sendLoanApplyInfo 
     * @Description: 推送进件录入信息结果
     * @param @param loanInfo    设定文件 
     * @return void    返回类型 
     * @date 2017年7月12日 上午11:13:38
     * @author YabinLi
     * @throws
     */
    @Override
    public void sendLoanApplyInfo(LoanApply loanInfo){
    	Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("appId", loanInfo.getAppId());
		msgMap.put("dataKey", "cobra");
		msgMap.put("transactionId", "");
		msgMap.put("policyId", "");
		msgMap.put("timestamp", DateTime.now().toString("yyyyMMddHHmmssSSS"));
		Map<String,Object> data = loanInfo.getLoanData();
		msgMap.put("data", data);
		kafkaService.doSend("kafka_loan_in_add", JsonUtil.convert(msgMap));
    }

  /**
   * @Title: sendBorrowerLiteLoanApplyInfo
   * @Description: 推送borrowerlite进件信息
   * @param @param loanInfo    设定文件
   * @return void    返回类型
   * @date 2018年5月10日 下午16:51:38
   * @author YabinLi
   * @throws
   */
  @Override
  public void sendBorrowerLiteLoanApplyInfo(LoanApply loanInfo){
    Map<String, Object> msgMap = new HashMap<String, Object>();
    msgMap.put("appId", loanInfo.getAppId());
    msgMap.put("dataKey", "borrowerlite");
    msgMap.put("transactionId", "");
    msgMap.put("policyId", "");
    msgMap.put("timestamp", DateTime.now().toString("yyyyMMddHHmmssSSS"));
    Map<String,Object> data = loanInfo.getLoanData();
    data.put("appStatus", loanInfo.getAppStatus());
    msgMap.put("data", data);
    kafkaService.doSend("kafka_loan_credit", JsonUtil.convert(msgMap));
  }
}
