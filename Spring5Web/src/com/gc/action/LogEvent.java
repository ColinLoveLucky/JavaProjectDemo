package com.gc.action;

import org.springframework.context.ApplicationEvent;

public class LogEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogEvent(Object msg) {
		super(msg);
	}
}
