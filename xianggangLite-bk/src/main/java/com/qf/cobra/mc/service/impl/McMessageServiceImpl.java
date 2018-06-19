package com.qf.cobra.mc.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.mc.constant.McMessageConstant;
import com.qf.cobra.mc.domain.McMessageMappingReqBo;
import com.qf.cobra.mc.entity.McMessgaeMappingEntity;
import com.qf.cobra.mc.enums.McEventTypeEnum;
import com.qf.cobra.mc.service.McMessageService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.StrUtil;

/**
 * MC消息服务接口实现类
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:42
 */
@Service
public class McMessageServiceImpl implements McMessageService {
	
	private static final Logger log = LoggerFactory.getLogger(McMessageServiceImpl.class);
	
	@Autowired
	private MongoOperate<McMessgaeMappingEntity> mcMessageRepository;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	@Qualifier("mcRabbitTemplate")
	private RabbitTemplate rabbitTemplate;
	
	@Value("${mc.rabbitmq.tradeTransferFanoutExchange}")
	private String tradeTransferFanoutExchange;
	
	@Autowired
	private ErrorNoticeService errorNoticeService;

	@SuppressWarnings("unchecked")
	@Override
	public ResponseData<String> insertMappingRelation(McMessageMappingReqBo mcMessageMappingReqBo) {
		ResponseData<String> responseData = ResponseUtil.defaultResponse();
		try {
			
			Map<String, Object> queryParams = new HashMap<>(4);
			queryParams.put("eventType", mcMessageMappingReqBo.getEventType());
			McMessgaeMappingEntity dbEntity = mcMessageRepository.findOne(queryParams, McMessgaeMappingEntity.class);
			if(null != dbEntity) {
				this.updateMappingRelation(mcMessageMappingReqBo);
			} else {
				McMessgaeMappingEntity entity = new McMessgaeMappingEntity(mcMessageMappingReqBo.getEventType().toString(),
						mcMessageMappingReqBo.getMappingRelation().toString());
				entity.setConstFileds(mcMessageMappingReqBo.getConstFileds().toString());
				mcMessageRepository.save(entity);
			}
			return responseData;
		} catch (Exception e) {
			log.error("新增MC消息映射系统字段信息发生异常!", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("新增MC消息映射系统字段信息失败");
			return responseData;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseData<String> updateMappingRelation(McMessageMappingReqBo mcMessageMappingReqBo) {
		ResponseData<String> responseData = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> queryParams = new HashMap<>(4);
			queryParams.put("eventType", mcMessageMappingReqBo.getEventType());
			
			Map<String, Object> updateParams = new HashMap<>(4);
			if(null != mcMessageMappingReqBo.getMappingRelation()) {
				updateParams.put("mappingRelation", mcMessageMappingReqBo.getMappingRelation().toString());
			}
			if(null != mcMessageMappingReqBo.getConstFileds()) {
				updateParams.put("constFileds", mcMessageMappingReqBo.getConstFileds().toString());
			}
			
			mcMessageRepository.findAndModify(queryParams, updateParams, McMessgaeMappingEntity.class);
			return responseData;
		} catch (Exception e) {
			log.error("更新MC消息映射系统字段信息发生异常!", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("更新MC消息映射系统字段信息失败");
			return responseData;
		}
	}
	
	/**
	 * 获取MQ消息映射系统字段信息
	 * 
	 * @param eventType 事件类型
	 * @return
	 */
	private McMessgaeMappingEntity findByEventType(String eventType) {
		try {
			Map<String, Object> queryParams = new HashMap<>(4);
			queryParams.put("eventType", eventType);
			return mcMessageRepository.findOne(queryParams, McMessgaeMappingEntity.class);
		} catch (Exception e) {
			log.error("获取MC消息映射系统字段发生异常!", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void sendRegisterMqMessage(String userId, String mobile, String sourceType, String clientSource, String appType) {
		try {
			if(StringUtils.isBlank(userId)) {
				log.warn("发送注册成功MQ消息到MC平台-用户ID为空!");
				return;
			}
			
			if(StringUtils.isBlank(mobile)) {
				log.warn("发送注册成功MQ消息到MC平台-注册手机号为空!");
				return;
			}
			
			// 注册事件类型
			String eventType = McEventTypeEnum.REGISTER.toString();
			
			// 注册所能提供参数Map
			Map<String, Object> registerParamsMap = new HashMap<>();
			
			// 构建共通MQ消息所需参数
			this.buildCommonMqMessage(registerParamsMap, sourceType, clientSource, appType, eventType, System.currentTimeMillis(), userId);
			registerParamsMap.put(McMessageConstant.MOBILE_KEY, mobile);
			
			// 获取MQ消息映射系统字段信息
			McMessgaeMappingEntity entity = this.findByEventType(eventType);
			if(null != entity) {
				Map<String, Object> registerMqMessageMap = new HashMap<>();
				
				Map<String, String> constFieldsMap = JsonUtil.convert(entity.getConstFileds(), Map.class);
				if(MapUtils.isNotEmpty(constFieldsMap)) {
					registerParamsMap.putAll(constFieldsMap);
				}
				
				Map<String, String> mappingRelation = JsonUtil.convert(entity.getMappingRelation(), Map.class);
				for(Map.Entry<String, String> entry : mappingRelation.entrySet()) {
					registerMqMessageMap.put(entry.getValue(), registerParamsMap.get(entry.getKey()));
				}
				
				// 发送MQ消息到MC平台
				this.sendMqMessage(eventType, JsonUtil.convert(registerMqMessageMap));
			}
		} catch (Exception e) {
			log.error("发送注册成功MQ消息到MC平台发生异常!手机号:{}", mobile, e);
			errorNoticeService.notice("", "BorrowerLite系统-发送MQ消息到MC平台", 
					String.format("发送注册成功MQ消息到MC平台发生异常,手机号:%s", mobile), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void sendRealnameMqMessage(Map<String, Object> userInfoMap, String sourceType, String clientSource, String appType) {
		String idCard = null;
		String realname = null;
		try {
			String userId = MapUtils.getString(userInfoMap, "id");
			if(StringUtils.isBlank(userId)) {
				log.warn("发送实名认证成功MQ消息到MC平台-用户ID不存在!");
				return;
			}
			
			idCard= StrUtil.obj2Str(userInfoMap.get("idNo"));
			realname= StrUtil.obj2Str(userInfoMap.get("idName"));
			
			// 实名认证事件类型
			String eventType = McEventTypeEnum.REAL_NAME.toString();
			
			// 构建共通MQ消息所需参数
			this.buildCommonMqMessage(userInfoMap, sourceType, clientSource, appType, eventType, System.currentTimeMillis(), userId);
			
			// 获取MQ消息映射系统字段信息
			McMessgaeMappingEntity entity = this.findByEventType(eventType);
			if(null != entity) {
				Map<String, Object> realnameMqMessageMap = new HashMap<>();
				Map<String, String> mappingRelation = JsonUtil.convert(entity.getMappingRelation(), Map.class);
				
				for(Map.Entry<String, String> entry : mappingRelation.entrySet()) {
					realnameMqMessageMap.put(entry.getValue(), userInfoMap.get(entry.getKey()));
				}
				
				// 发送MQ消息到MC平台
				this.sendMqMessage(eventType, JsonUtil.convert(realnameMqMessageMap));
			}
		} catch (Exception e) {
			log.error("发送实名成功MQ消息到MC平台发生异常!姓名:{},身份证号:{}", realname, idCard, e);
			errorNoticeService.notice("", "BorrowerLite系统-发送MQ消息到MC平台", 
					String.format("发送实名认证成功MQ消息到MC平台发生异常,姓名:%s", realname), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Async
	@Override
	public void sendPreAuditMqMessage(String appId, String auditResult, String sourceType, String clientSource, String appType) {
		try {
			// 预审结果事件类型
			String eventType = McEventTypeEnum.PRE_AUDIT.toString();
			
			// 获取借款申请信息
			LoanApply loanApply = loanService.queryLoanInfoByAppId(appId);
			if(null == loanApply) {
				log.warn("发送预审结果MQ消息到MC平台-借款申请信息不存在!借款编号:{}", appId);
				return;
			}
			
			// 注册所能提供参数Map
			Map<String, Object> preAuditParamsMap = new HashMap<>();
			
			// 构建共通MQ消息所需参数
			this.buildCommonMqMessage(preAuditParamsMap, sourceType, clientSource, appType, eventType, System.currentTimeMillis(), loanApply.getUserId());
			preAuditParamsMap.put(McMessageConstant.APP_ID_KEY, appId);
			preAuditParamsMap.put(McMessageConstant.AUDIT_RESULT_KEY, auditResult);
			
			// 获取MQ消息映射系统字段信息
			McMessgaeMappingEntity entity = this.findByEventType(eventType);
			if(null != entity) {
				Map<String, Object> preAuditMqMessageMap = new HashMap<>();
				
				Map<String, String> mappingRelation = JsonUtil.convert(entity.getMappingRelation(), Map.class);
				for(Map.Entry<String, String> entry : mappingRelation.entrySet()) {
					preAuditMqMessageMap.put(entry.getValue(), preAuditParamsMap.get(entry.getKey()));
				}
				
				// 发送MQ消息到MC平台
				this.sendMqMessage(eventType, JsonUtil.convert(preAuditMqMessageMap));
			}
		} catch (Exception e) {
			log.error("发送预审结果MQ消息到MC平台发生异常!进件编号:{},审核结果:{}", appId, auditResult, e);
			errorNoticeService.notice(appId, "BorrowerLite系统-发送MQ消息到MC平台", 
					String.format("发送预审结果MQ消息到MC平台发生异常,审核结果:%s", auditResult), e);
		}
	}

	/**
	 * 构建共通MQ消息所需参数
	 * 
	 * @param params 参数Map
	 * @param sourceType 客户来源类型
	 * @param clientSource 客户端来源
	 * @param appType 客户端产品类型
	 * @param eventType 时间类型
	 * @param createDate 创建时间
	 * @param userId 用户ID
	 */
	private void buildCommonMqMessage(Map<String, Object> params, String sourceType, String clientSource,
			String appType, String eventType, Long createDate, String userId) {
		params.put(McMessageConstant.SOURCE_TYPE_KEY, sourceType);
		params.put(McMessageConstant.CLIENT_SOURCE_KEY, clientSource);
		params.put(McMessageConstant.APP_TYPE, appType);
		params.put(McMessageConstant.EVENT_TYPE_KEY, eventType);
		params.put(McMessageConstant.CREATE_DATE_KEY, createDate);
		params.put(McMessageConstant.USER_ID_KEY, userId);
	}

	@Override
	public void sendMqMessage(String eventType, String msg) {
		log.info("发送MQ消息到MC平台,消息类型:{},消息:{}", eventType, msg);
		rabbitTemplate.convertAndSend(tradeTransferFanoutExchange, "", msg);
	}

}
