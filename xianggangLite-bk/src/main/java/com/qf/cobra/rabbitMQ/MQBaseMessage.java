/**
 * 
 */
package com.qf.cobra.rabbitMQ;

import java.util.List;

/**
 * MQ消息格式
 * 
 * @author LongjunLu
 *
 */
public class MQBaseMessage {

	private String appId;

	private String category;

	private String tenantId;

	private String clientId;

	private String productId;

	private String topicName;

	private List<Object> messageObjects;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public List<Object> getMessageObjects() {
		return messageObjects;
	}

	public void setMessageObjects(List<Object> messageObjects) {
		this.messageObjects = messageObjects;
	}

}
