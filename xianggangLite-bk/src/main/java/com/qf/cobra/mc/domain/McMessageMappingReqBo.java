package com.qf.cobra.mc.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qf.cobra.mc.enums.McEventTypeEnum;

/**
 * 系统字段与MC需要字段映射请求实体类
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:46
 */
public class McMessageMappingReqBo {

	/** 事件类型 */
	private McEventTypeEnum eventType;
	
	/** MQ消息与系统字段映射关系 */
	private ObjectNode mappingRelation;
	
	/** 常量字段 */
	private ObjectNode constFileds;

	/**
	 * @return the eventType
	 */
	public McEventTypeEnum getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(McEventTypeEnum eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the mappingRelation
	 */
	public ObjectNode getMappingRelation() {
		return mappingRelation;
	}

	/**
	 * @param mappingRelation the mappingRelation to set
	 */
	public void setMappingRelation(ObjectNode mappingRelation) {
		this.mappingRelation = mappingRelation;
	}

	/**
	 * @return the constFileds
	 */
	public ObjectNode getConstFileds() {
		return constFileds;
	}

	/**
	 * @param constFileds the constFileds to set
	 */
	public void setConstFileds(ObjectNode constFileds) {
		this.constFileds = constFileds;
	}
	
}
