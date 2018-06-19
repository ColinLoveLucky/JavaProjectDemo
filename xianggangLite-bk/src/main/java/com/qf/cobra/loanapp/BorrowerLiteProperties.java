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
* @Title: BorrowerLiteProperties.java
* @Package com.qf.cobra.loanapp 
* @Description: TODO(用一句话描述该文件做什么) 
* @author YabinLi  
* @date 2017年7月21日 下午6:30:52 
* @version V1.5   
*/
package com.qf.cobra.loanapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 
 * @ClassName: BizappQyiProperties 
 * @Description: Q易借之中的配置参数
 * @author YabinLi
 * @date 2017年7月21日 下午6:30:52 
 * @version 1.5 
 */
@Configuration
@ConfigurationProperties(prefix = "borrowerLite")
public class BorrowerLiteProperties {

	private int sessionTimeout;

	private int tokenTimeout;

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getTokenTimeout() {
		return tokenTimeout;
	}

	public void setTokenTimeout(int tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}
}
