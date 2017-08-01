package com.gztydic.gateway.core.interfaces;

import java.io.Serializable;

import javax.annotation.Resource;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;

public class GeneralServiceImpl<T> implements GeneralService<T> {

	@Resource
	private HibernateGenericDao hibernateGenericDao;
	
	public void delete(Object obj) throws Exception {
		hibernateGenericDao.delete(obj);
	}

	public void save(Object obj) throws Exception{
		hibernateGenericDao.save(obj);
	}
	
	public void saveOrUpdate(Object obj) throws Exception{
		hibernateGenericDao.saveOrUpdate(obj);
	}
	
	public void update(Object obj) throws Exception{
		hibernateGenericDao.update(obj);
	}

	public T search(Class<T> clazz,Serializable id) throws Exception{
		return (T)hibernateGenericDao.getHibernateTemplate().get(clazz.getName(), id);
	}
	
	public int searchSequenceValue(String sequenceName) {
		return hibernateGenericDao.searchSequenceValue(sequenceName);
	}
}
