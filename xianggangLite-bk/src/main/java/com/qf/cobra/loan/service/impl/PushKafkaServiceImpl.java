/**   
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
* @Title: PushKafkaServiceImpl.java 
* @Package com.qf.cobra.loan.service.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月18日 下午5:05:19 
* @version V1.5   
*/
package com.qf.cobra.loan.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.IKafkaService;
import com.qf.cobra.loan.service.IPushKafkaService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanApply.AppStatusEnum;

/** 
 * @ClassName: PushKafkaServiceImpl 
 * @Description: 实现手动同步Q易借之中的历史进件到Kafka之中的topic：kafka_loan_in_add之中
 * @author YabinLi
 * @date 2017年7月18日 下午5:05:19 
 * @version 1.5 
 */
@Service
public class PushKafkaServiceImpl implements IPushKafkaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushKafkaServiceImpl.class);
	@Autowired
	private IKafkaService kafkaService;
	@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public void sendLoanApply(List<String> appIds) {
		List<LoanApply> loanAppList = new ArrayList<LoanApply>();
		if(appIds != null && appIds.size() > 0){
			Criteria criteria = Criteria.where("appId").in(appIds);
			Query query = Query.query(criteria);
			loanAppList.addAll(mongoTemplate.find(query, LoanApply.class));
		}else{
			List<String> appStatusList = new ArrayList<String>();
			appStatusList.add(AppStatusEnum.WAIT_COMPLEMENT.getCode());
			Criteria criteria = Criteria.where("appStatus").nin(appStatusList);
			Query query = Query.query(criteria);
			loanAppList.addAll(mongoTemplate.find(query, LoanApply.class));
		}
		for(int i = 0; i < loanAppList.size(); i++){
			LOGGER.info("开始推送申请单录入之后的历史进件，进件编号：{}, 到Kafka之中。", loanAppList.get(i).getAppId());
			kafkaService.sendLoanApplyInfo(loanAppList.get(i));
			LOGGER.info("成功推送申请单录入之后的历史进件，进件编号：{}, 到Kafka之中。", loanAppList.get(i).getAppId());
		}
	}
	@Override
	public void sendLoanApplyFinal(List<String> appIds) {
		List<LoanApply> loanAppList = new ArrayList<LoanApply>();
		if(appIds != null && appIds.size() > 0){
			Criteria criteria = Criteria.where("appId").in(appIds);
			Query query = Query.query(criteria);
			loanAppList.addAll(mongoTemplate.find(query, LoanApply.class));
		}else{
			List<String> appStatusList = new ArrayList<String>();
			appStatusList.add(AppStatusEnum.FIRST_AUDIT_REJECT.getCode());
			appStatusList.add(AppStatusEnum.FINAL_AUDIT_PASS.getCode());
			appStatusList.add(AppStatusEnum.FINAL_AUDIT_REJECT.getCode());
			Criteria criteria = Criteria.where("appStatus").in(appStatusList);
			Query query = Query.query(criteria);
			loanAppList.addAll(mongoTemplate.find(query, LoanApply.class));
		}
		for(int i = 0; i < loanAppList.size(); i++){
			LOGGER.info("开始推送审核结束之后的历史进件，进件编号：{}, 到Kafka之中。", loanAppList.get(i).getAppId());
			kafkaService.sendLoanCredit(loanAppList.get(i));
			LOGGER.info("成功推送审核结束之后的历史进件，进件编号：{}, 到Kafka之中。", loanAppList.get(i).getAppId());
		}
	}
}