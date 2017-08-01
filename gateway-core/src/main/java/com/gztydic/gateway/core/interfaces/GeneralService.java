package com.gztydic.gateway.core.interfaces;

import java.io.Serializable;

public interface GeneralService<T> {

	public void save(Object obj) throws Exception;
	
	public void saveOrUpdate(Object obj) throws Exception;
	
	public void update(Object obj) throws Exception;
	
	public void delete(Object obj) throws Exception;
	
	public T search(Class<T> clazz,Serializable id) throws Exception;
	
	public int searchSequenceValue(String sequenceName) ;
}
