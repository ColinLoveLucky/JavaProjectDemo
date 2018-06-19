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
* @Title: IPushKafkaService.java 
* @Package com.qf.cobra.loan.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月18日 下午5:00:57 
* @version V1.5   
*/
package com.qf.cobra.loan.service;

import java.util.List;

/** 
 * @ClassName: IPushKafkaService 
 * @Description: 手动同步Q易借之中的历史进件到Kafka之中的topic：kafka_loan_in_add之中
 * @author YabinLi
 * @date 2017年7月18日 下午5:00:57 
 * @version 1.5 
 */
public interface IPushKafkaService {
	/**
	 * @Title: sendLoanApply 
	 * @Description: 将数据库之中的申请单录入之后的历史进件推送到Kafka之中
	 * @param @param appIds    设定文件 
	 * @return void    返回类型 
	 * @date 2017年7月24日 下午5:14:04
	 * @author YabinLi
	 * @throws
	 */
	public void sendLoanApply(List<String> appIds);
	/**
	 * @Title: sendLoanApplyFinal 
	 * @Description: 将数据库之中的审核结束之后的历史进件推送到Kafka之中
	 * @param @param appIds    设定文件 
	 * @return void    返回类型 
	 * @date 2017年7月24日 下午5:33:13
	 * @author YabinLi
	 * @throws
	 */
	public void sendLoanApplyFinal(List<String> appIds);
}
