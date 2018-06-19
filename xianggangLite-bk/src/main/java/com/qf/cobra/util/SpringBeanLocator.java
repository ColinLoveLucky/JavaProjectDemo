package com.qf.cobra.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanLocator implements BeanFactoryAware {

	private static BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		SpringBeanLocator.beanFactory = beanFactory;
	}

	public static <T> T getBean(String beanName) {
		return (T) beanFactory.getBean(beanName);
	} 

	public static <T> T getBean(Class beanType) {
		return (T) beanFactory.getBean(beanType);
	} 
	
}