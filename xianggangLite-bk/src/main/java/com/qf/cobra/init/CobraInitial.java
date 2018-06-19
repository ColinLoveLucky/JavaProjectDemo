package com.qf.cobra.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.qf.cobra.system.service.IDictService;

import java.util.Date;

@Component
public class CobraInitial implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CobraInitial.class);
	
	@Autowired
	private IDictService dictService;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
//		try {
//			LOGGER.info("COBRA项目启动，系统字典刷新成功，开始时间：{}", new Date());
//			dictService.refreshDict("APP_CITY");
//			LOGGER.info("COBRA项目启动，系统字典刷新成功，结束时间：{}", new Date());
//		} catch (Exception e) {
//			LOGGER.error("COBRA项目启动，系统字典刷新异常",e);
//		}
	}

}
