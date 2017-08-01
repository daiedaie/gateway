package com.gztydic.gateway.core.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectUtils {
	
	 /**
     * 在页面显示时，将字符串类型的null转换成空
     * @param obj
     * @throws Exception
     */
	public static void null2Empty(Object obj) throws Exception{
    	if(obj == null) return;
    	Field[] fields = obj.getClass().getDeclaredFields();
    	for (int i = 0; i < fields.length; i++) {
    		String fieldName = fields[i].getName();
    		
    		if("java.lang.String".equals(fields[i].getType().getName())){
    			String getMethodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
    			Method method = obj.getClass().getMethod(getMethodName,null);
    			
    			if(method.invoke(obj, null) == null){
	    			String setMethodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
	    			method = obj.getClass().getMethod(setMethodName,new Class[]{String.class});
	    			method.invoke(obj, new String[]{""});
    			}
    		}
		}
    }
}
