package com.qf.cobra.mc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.mc.domain.McMessageMappingReqBo;
import com.qf.cobra.mc.enums.McAppTypeEnum;
import com.qf.cobra.mc.enums.McClientSourceEnum;
import com.qf.cobra.mc.enums.McSourceTypeEnum;
import com.qf.cobra.mc.service.McMessageService;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

/**
 * MC消息服务控制器
 * 
 * @author: XianjiCai
 * @date: 2018/03/09 11:48
 */
@RestController
@RequestMapping("/mcmessage")
public class McMessageController {
	
	@Autowired
	private McMessageService mcMessageService;
	
	@Autowired
	@Qualifier("mcRabbitTemplate")
	private RabbitTemplate rabbitTemplate;
	
	@Value("${mc.rabbitmq.tradeTransferFanoutExchange}")
	private String tradeTransferFanoutExchange;
	
	/**
	 * 新增MC消息字段映射
	 * 
	 * @param mcMessageMappingReqBo
	 * @return
	 */
	@PostMapping("/mapping")
	public ResponseData<String> insertMappingRelation(@RequestBody McMessageMappingReqBo mcMessageMappingReqBo) {
		// 新增MC消息字段映射
		ResponseData<String> responseData = mcMessageService.insertMappingRelation(mcMessageMappingReqBo);
		return responseData;
	}
	
	/**
	 * 新增MC消息字段映射
	 * 
	 * @param mcMessageMappingReqBo
	 * @return
	 */
	@PutMapping("/mapping")
	public ResponseData<String> updateMappingRelation(@RequestBody McMessageMappingReqBo mcMessageMappingReqBo) {
		// 更新MC消息字段映射
		ResponseData<String> responseData = mcMessageService.updateMappingRelation(mcMessageMappingReqBo);
		return responseData;
	}
	
	@PostMapping("/send/register")
	public ResponseData<String> sendRegisterMcMessage() {
		mcMessageService.sendRegisterMqMessage("5a7ab76c8615e42534c91b28", "133xxxxxxxx", McSourceTypeEnum.BORROW.toString(), McClientSourceEnum.WEB.toString(),
				McAppTypeEnum.QYJ.toString());
		System.out.println("send register mq message");
		return ResponseUtil.defaultResponse();
	}
	
	@PostMapping("/send/realname")
	public ResponseData<String> sendRealnameMcMessage() {
		Map<String, Object> userInfoMap = new HashMap<>(8);
		userInfoMap.put("id", "5a7ab76c8615e42534c91b28");
		userInfoMap.put("mobile", "18671687316");
		userInfoMap.put("idNo", "620722198403041180");
		userInfoMap.put("idName", "金贝儿");
		userInfoMap.put("age", "30");
		userInfoMap.put("sex", "genderF");
		userInfoMap.put("maritalStatus", "marrageSingle");
		mcMessageService.sendRealnameMqMessage(userInfoMap, McSourceTypeEnum.BORROW.toString(), McClientSourceEnum.WEB.toString(), McAppTypeEnum.QYJ.toString());
		System.out.println("send realname mq message");
		return ResponseUtil.defaultResponse();
	}
	
	@PostMapping("/send/preAudit")
	public ResponseData<String> sendPreAuditMcMessage(@RequestBody String str) {
		Map<String, String> body = JsonUtil.convert(str, Map.class);
		mcMessageService.sendPreAuditMqMessage(body.get("appId"), body.get("auditResult"),
				McSourceTypeEnum.BORROW.toString(), McClientSourceEnum.WEB.toString(), McAppTypeEnum.QYJ.toString());
		System.out.println("send preAudit mq message");
		return ResponseUtil.defaultResponse();
	}
}
