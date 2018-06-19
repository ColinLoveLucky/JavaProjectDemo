package com.gc.action;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LogAround implements MethodInterceptor {

	private Logger log = new Logger();

	public Object invoke(MethodInvocation mi) throws Throwable {
		log.write("start writing....");
		try {
			Object result = mi.proceed();
			return result;
		} finally {
			log.write("end writing...");
		}
	}

}
