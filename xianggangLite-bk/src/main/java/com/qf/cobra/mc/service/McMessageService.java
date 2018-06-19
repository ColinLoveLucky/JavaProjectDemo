package com.qf.cobra.mc.service;

import java.util.Map;

import com.qf.cobra.mc.domain.McMessageMappingReqBo;
import com.qf.cobra.util.ResponseData;

/**
 * MC消息服务接口
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:38
 */
public interface McMessageService {

	/**
	 * 新增MC消息字段映射
	 * 
	 * @param mcMessageMappingReqBo
	 * @return
	 */
	ResponseData<String> insertMappingRelation(McMessageMappingReqBo mcMessageMappingReqBo);
	
	/**
	 * 更新MC消息字段映射
	 * 
	 * @param mcMessageMappingReqBo
	 * @return
	 */
	ResponseData<String> updateMappingRelation(McMessageMappingReqBo mcMessageMappingReqBo);
	
	/**
	 * 注册成功发送MQ消息到MC平台
	 * 
	 * @param userId 用户ID
	 * @param mobile 手机号
	 * @param sourceType 来源类型 区分是lender端还是borrow端
	 * @param clientSource 客户端来源
	 * @param appType 客户端产品类型
	 */
	void sendRegisterMqMessage(String userId, String mobile, String sourceType, String clientSource, String appType);
	
	/**
	 * 实名认证成功发送MQ消息到MC平台
	 * 
	 * @param userInfoMap 用户信息
	 * @param sourceType 来源类型 区分是lender端还是borrow端
	 * @param clientSource 客户端来源
	 * @param appType 客户端产品类型
	 */
	void sendRealnameMqMessage(Map<String, Object> userInfoMap, String sourceType, String clientSource, String appType);
	
	/**
	 * 发送预审结果MQ消息到MC平台
	 * 
	 * @param appId 进件编号
	 * @param auditResult 审核结果
	 * @param sourceType 来源类型 区分是lender端还是borrow端
	 * @param clientSource 客户端来源
	 * @param appType 客户端产品类型
	 */
	void sendPreAuditMqMessage(String appId, String auditResult, String sourceType, String clientSource, String appType);
	
	/**
	 * 发送MQ消息到MC平台
	 * 
	 * @param eventType 事件类型
	 * @param msg 消息
	 */
	void sendMqMessage(String eventType, String msg);
}
