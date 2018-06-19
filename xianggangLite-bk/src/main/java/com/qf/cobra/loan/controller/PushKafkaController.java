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
* @Title: PushKafkaController.java 
* @Package com.qf.cobra.loan.controller 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月18日 下午4:59:42 
* @version V1.5   
*/
package com.qf.cobra.loan.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.service.IPushKafkaService;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

/** 
 * @ClassName: PushKafkaController 
 * @Description: 手动推送历史进件到Kafka中，为新增进件手机号和电话号校验做数据准备
 * @author YabinLi
 * @date 2017年7月18日 下午4:59:42 
 * @version 1.5 
 */
@RestController
public class PushKafkaController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushKafkaController.class);
	@Autowired
	private IPushKafkaService pushKafkaService;
	/**
	 * @Title: enteringInformations 
	 * @Description: 手动推送Q易借申请单录入之后的历史进件
	 * @param @param params
	 * @param @return    设定文件 
	 * @return ResponseData<?>    返回类型 
	 * @author YabinLi
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pushLoanApply/toKafka", method = RequestMethod.POST)
	public ResponseData<?> enteringInformations(@RequestBody Map<String, Object> params) {
		ResponseData<?> responseData = ResponseUtil.defaultResponse();
		try {
			LOGGER.info("开启手动推送Q易借申请单录入之后的历史进件！ 接口调用参数：{}", params);
			List<String> appIds = (List<String>) params.get("appIds");
			pushKafkaService.sendLoanApply(appIds);
			LOGGER.info("结束手动推送Q易借申请单录入之后的历史进件！");
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("失败");
		}
		return responseData;
	}
	/**
	 * @Title: enteringInformations 
	 * @Description: 手动推送Q易借审核结束之后的历史进件
	 * @param @param params
	 * @param @return    设定文件 
	 * @return ResponseData<?>    返回类型 
	 * @author YabinLi
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pushLoanApply/toKafkaFinal", method = RequestMethod.POST)
	public ResponseData<?> enteringInformationsFinal(@RequestBody Map<String, Object> params) {
		ResponseData<?> responseData = ResponseUtil.defaultResponse();
		try {
			LOGGER.info("开启手动推送Q易借审核结束之后的历史进件！ 接口调用参数：{}", params);
			List<String> appIds = (List<String>) params.get("appIds");
			pushKafkaService.sendLoanApplyFinal(appIds);
			LOGGER.info("结束手动推送Q易借审核结束之后的历史进件！");
		} catch (Exception e) {
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("失败");
		}
		return responseData;
	}
}
