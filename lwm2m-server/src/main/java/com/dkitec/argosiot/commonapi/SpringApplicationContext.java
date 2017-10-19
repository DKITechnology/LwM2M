package com.dkitec.argosiot.commonapi;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;
	
	public SpringApplicationContext() {
	}
	
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}
	
	public static <T> T getBean(Class<T> requiredType) {
		return CONTEXT.getBean(requiredType);
	}
	
	public static <T> T getBean(String beanName, Class<T> requiredType) {
		return CONTEXT.getBean(beanName, requiredType);
	}
}
