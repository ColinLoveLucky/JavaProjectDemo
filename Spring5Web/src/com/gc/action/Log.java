package com.gc.action;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Log implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = arg0;
	}

	public int log(String log) {
		LogEvent event = new LogEvent(log);
		this.applicationContext.publishEvent(event);
		return 0;
	}

}
