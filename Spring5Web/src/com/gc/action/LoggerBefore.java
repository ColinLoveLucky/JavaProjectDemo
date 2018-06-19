package com.gc.action;

import java.lang.reflect.Method;

import org.springframework.aop.MethodBeforeAdvice;

public class LoggerBefore implements MethodBeforeAdvice {

	private Logger log = new Logger();

	public void before(Method method, Object[] args, Object target) throws Throwable {
		log.write("before writing" + args[0]);
	}
}
