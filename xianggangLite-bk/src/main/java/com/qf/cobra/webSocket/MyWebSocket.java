package com.qf.cobra.webSocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.CarrieroperatorResult;
import com.qf.cobra.util.JsonUtil;

/**
 * 第一种 ： 原始的方法 Description : 使用 ServerEndpoint 注释的类必须有一个公共的无参数构造函数 Date :
 * 2017/5/27 Time : 11:06 注 ：要将此类加入到spring容器中
 * <p>
 * 使用spring-boot的唯一区别是要@Component声明下，而使用独立容器是由容器自己管理websocket的，但在spring-boot中连容器都是spring管理的
 * 虽然@Component默认是单例模式的，但spring-boot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来
 *
 */

@Component
@ServerEndpoint(value = "/ws/carrieroperatorResult")
public class MyWebSocket implements ApplicationContextAware {
	private Logger logger = LoggerFactory.getLogger(MyWebSocket.class);

	private Session session;
	private static ApplicationContext applicationContext;

	/**
	 * webSocket 连接的监听方法
	 *
	 * @param session
	 *            本次连接的会话bean
	 * @param endpointConfig
	 *            webSocket端点的设置
	 */
	@OnOpen
	public void open(Session session, EndpointConfig endpointConfig) {
//		logger.info("打开WebSocket会话id = {}", session.getId());
		this.session = session;
	}

	/**
	 * webSocket 关闭连接时的监听方法
	 *
	 * @param session
	 *            本次连接的会话bean
	 * @param closeReason
	 *            关闭的理由
	 */
	@OnClose
	public void close(Session session, CloseReason closeReason) {
//		logger.info("关闭WebSocket会话id = {}", session.getId());
		this.session = null;
//		logger.info("关闭的连接的原因 = {} {}", closeReason.getCloseCode(), closeReason.toString());
	}

	/**
	 * 连接出错的监听
	 *
	 * @param session
	 *            本次连接的会话bean
	 * @param throwable
	 *            异常信息
	 */
	@OnError
	public void error(Session session, Throwable throwable) {
		logger.error("WebSocket会话id {} 出错了，错误原因 = {}", session.getId(), throwable.getMessage());
	}

	/**
	 * 监听客户端的消息 如果返回值为void ，可以使用标准的send() 方法，发送消息到客户端，有返回值，直接返回到当前的连接客户端
	 * @param appId
	 * @return
	 */
	@OnMessage
	public void message(String appId) {
		try {
//			logger.info("发送webSocket会话id {} 状态消息到客户端", session.getId());
			LoanService loanService = applicationContext.getBean(LoanService.class);
			List<CarrieroperatorResult> list = loanService.getCarrieroperatorByAppId(appId);
			if (list.size() != 0) {
				Map<String, String> resultMap = new HashMap<String, String>();
				for (CarrieroperatorResult carrieroperatorResult : list) {
					if ("AUTH".equals(carrieroperatorResult.getType())) {
						if(!"AUTH_SUC".equals(resultMap.get("authStatus"))){
							resultMap.put("authStatus", carrieroperatorResult.getStatus());
						}
					} else {
						if(!"DATA_READY".equals(resultMap.get("dataStatus"))){
							resultMap.put("dataStatus", carrieroperatorResult.getStatus());
						}
					}
				}
				this.session.getBasicRemote().sendText(JsonUtil.convert(resultMap).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	    if(null == MyWebSocket.applicationContext){
            MyWebSocket.applicationContext = applicationContext;
        }
	}

}
