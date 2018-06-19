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
* @Title: IQyjapiService.java 
* @Package com.qf.cobra.qyjapi 
* @Description: 管理APP运营商认证接口调用 
* @author YabinLi  
* @date 2017年6月7日 下午3:28:53 
* @version V1.0   
*/
package com.qf.cobra.qyjapi.service;

/** 
 * @ClassName: IQyjapiService 
 * @Description: 管理APP运营商认证接口调用 
 * @author YabinLi
 * @date 2017年6月7日 下午3:28:53 
 * @version 1.0 
 */
public interface IQyjapiService {
	/**
	 * @Title: triggerCertifyTask 
	 * @Description: 调用APP运营商接口，触发认证流程
	 * @param @param appId
	 * @param @param requestType
	 * @param @return    设定文件 
	 * @return Boolean    返回类型 
	 * @date 2017年6月1日 下午4:40:38
	 * @author YabinLi
	 * @throws Exception
	 */
	public boolean triggerCertifyTask(String appId, String requestType) throws Exception ;
	
	/**
	 * 发送进件详情到app后端
	 * @param appId
	 */
	public void sendLoanDetail(String appId);
}
