package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwCheckRuleVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwCheckRuleVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwCheckRuleVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwCheckRuleDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwCheckRuleDAO.class);
	// property constants
	public static final String FIELD_ID = "fieldId";
	public static final String SERVICE_ID = "serviceId";
	public static final String FIELD_TYPE = "fieldType";
	public static final String CHECK_TYPE = "checkType";
	public static final String CHECK_RULE = "checkRule";

	public void save(GwCheckRuleVO transientInstance) {
		log.debug("saving GwCheckRule instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(GwCheckRuleVO persistentInstance) {
		log.debug("deleting GwCheckRuleVO instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public GwCheckRuleVO findById(java.lang.Long id) {
		log.debug("getting GwCheckRuleVO instance with id: " + id);
		try {
			GwCheckRuleVO instance = (GwCheckRuleVO) getSession().get(
					"com.gztydic.gateway.core.vo.GwCheckRuleVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwCheckRuleVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwCheckRuleVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFieldId(Object fieldId) {
		return findByProperty(FIELD_ID, fieldId);
	}

	public List findByServiceId(Object serviceId) {
		return findByProperty(SERVICE_ID, serviceId);
	}

	public List findByFieldType(Object fieldType) {
		return findByProperty(FIELD_TYPE, fieldType);
	}

	public List findByCheckType(Object checkType) {
		return findByProperty(CHECK_TYPE, checkType);
	}

	public List findByCheckRule(Object checkRule) {
		return findByProperty(CHECK_RULE, checkRule);
	}

	public List findAll() {
		log.debug("finding all GwCheckRuleVO instances");
		try {
			String queryString = "from GwCheckRuleVO";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public int deleteCheckRule(Long serviceId) throws Exception{
		return super.executeSql("delete gw_check_rule where service_id=?", new Long[]{serviceId});
	}
}