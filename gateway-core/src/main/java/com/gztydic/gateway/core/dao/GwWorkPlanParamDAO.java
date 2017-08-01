package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwWorkPlanParamVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwWorkPlanParamVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwWorkPlanParamDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwWorkPlanParamDAO.class);
	// property constants
	public static final String PLAN_ID = "planId";
	public static final String PARAM_NAME = "paramName";
	public static final String PARAM_VALUE = "paramValue";

	public GwWorkPlanParamVO findById(java.lang.Long id) {
		log.debug("getting GwWorkPlanParamVO instance with id: " + id);
		try {
			GwWorkPlanParamVO instance = (GwWorkPlanParamVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwWorkPlanParamVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwWorkPlanParamVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwWorkPlanParamVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByPlanId(Object planId) {
		return findByProperty(PLAN_ID, planId);
	}

	public List findByParamName(Object paramName) {
		return findByProperty(PARAM_NAME, paramName);
	}

	public List findByParamValue(Object paramValue) {
		return findByProperty(PARAM_VALUE, paramValue);
	}

	public List findAll() {
		log.debug("finding all GwWorkPlanParamVO instances");
		try {
			String queryString = "from GwWorkPlanParamVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<GwWorkPlanParamVO> findByPlan(Long planId) throws Exception{
		String hql = "from GwWorkPlanParamVO where planId=?";
		return super.findByHql(hql, new Long[]{planId});
	}
	
	public GwWorkPlanParamVO findByPlan(Long planId,String paramName) throws Exception{
		String hql = "from GwWorkPlanParamVO where planId=? and paramName=?";
		List<GwWorkPlanParamVO> list = super.findByHql(hql, new Object[]{planId,paramName});
		return list.size() > 0 ? list.get(0) : null;
	}
	
}