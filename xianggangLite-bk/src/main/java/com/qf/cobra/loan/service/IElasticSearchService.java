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
* @Title: IElasticSearchService.java 
* @Package com.qf.cobra.loan.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月11日 下午3:08:04 
* @version V1.5   
*/
package com.qf.cobra.loan.service;

import java.util.List;
import java.util.Map;

/** 
 * @ClassName: IElasticSearchService 
 * @Description: 调用Elasticsearch接口获取对应的历史进件手机号码和电话号码信息接口
 * @author YabinLi
 * @date 2017年7月11日 下午3:08:04 
 * @version 1.5 
 */
public interface IElasticSearchService {
	/**
	 * @Title: getMobileChecked 
	 * @Description: 进件手机号历史校验
	 * @param @param appId
	 * @param @return    设定文件 
	 * @return List<Map<String, Object>>     返回类型 
	 * @date 2017年7月11日 下午3:16:18
	 * @author YabinLi
	 * @throws
	 */
	public List<Map<String, Object>> getMobileChecked(String appId);
}
