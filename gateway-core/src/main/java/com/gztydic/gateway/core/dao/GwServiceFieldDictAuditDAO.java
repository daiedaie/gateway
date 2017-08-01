package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceFieldDictAuditVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceFieldDictAuditDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceFieldDictAuditDAO.class);

	// property constants

	public GwServiceFieldDictAuditVO findById(GwServiceFieldDictAuditVO id) {
		log.debug("getting GwServiceFieldDictAuditVO instance with id: " + id);
		try {
			GwServiceFieldDictAuditVO instance = (GwServiceFieldDictAuditVO) getSession().get(
					"com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwServiceFieldDictAuditVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwServiceFieldDictAuditVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all GwServiceFieldDictAuditVO instances");
		try {
			String queryString = "from GwServiceFieldDictAuditVO";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/**
	 * 查询字段与字典关联表数据
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public List searchListByBatch(Long batch) throws Exception{
		String hql = "from GwServiceFieldDictAuditVO where batch=?";
		return super.findByHql(hql, new Object[]{batch});
	}
}