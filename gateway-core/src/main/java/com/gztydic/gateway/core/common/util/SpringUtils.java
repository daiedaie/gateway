package com.gztydic.gateway.core.common.util;

import org.springframework.context.ApplicationContext;

public class SpringUtils {

	public static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringUtils.applicationContext = applicationContext;
	}

	public Object getBean(String beanName){
		return applicationContext.getBean(beanName);
	}
}
