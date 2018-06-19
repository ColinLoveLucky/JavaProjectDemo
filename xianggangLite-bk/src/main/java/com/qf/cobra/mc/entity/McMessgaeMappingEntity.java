package com.qf.cobra.mc.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 系统字段与MC需要字段映射实体类
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:23
 */
@Document(collection = "mcMessageMapping")
public class McMessgaeMappingEntity {

	/** 事件类型 */
	private String eventType;
	
	/** MQ消息与系统字段映射关系 */
	private String mappingRelation;
	
	/** 常量字段 */
	private String constFileds;
	
	/**
	 * @param eventType
	 * @param mappingRelation
	 */
	public McMessgaeMappingEntity(String eventType, String mappingRelation) {
		super();
		this.eventType = eventType;
		this.mappingRelation = mappingRelation;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the mappingRelation
	 */
	public String getMappingRelation() {
		return mappingRelation;
	}

	/**
	 * @param mappingRelation the mappingRelation to set
	 */
	public void setMappingRelation(String mappingRelation) {
		this.mappingRelation = mappingRelation;
	}

	/**
	 * @return the constFileds
	 */
	public String getConstFileds() {
		return constFileds;
	}

	/**
	 * @param constFileds the constFileds to set
	 */
	public void setConstFileds(String constFileds) {
		this.constFileds = constFileds;
	}
	
}
