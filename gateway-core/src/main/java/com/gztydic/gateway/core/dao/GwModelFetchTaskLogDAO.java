package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModelFetchTaskLogVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelFetchTaskLog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelFetchTaskLogVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelFetchTaskLogDAO extends HibernateGenericDao {
	private static final Log log = LogFactory
			.getLog(GwModelFetchTaskLogDAO.class);
	// property constants
	public static final String USER_ID = "userId";
	public static final String MODEL_ID = "modelId";
	public static final String ACTION = "action";
	public static final String LOG_INFO = "logInfo";

	public GwModelFetchTaskLogVO findById(java.lang.Long id) {
		log.debug("getting GwModelFetchTaskLogVO instance with id: " + id);
		try {
			GwModelFetchTaskLogVO instance = (GwModelFetchTaskLogVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwModelFetchTaskLogVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelFetchTaskLogVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelFetchTaskLogVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}

	public List findByModelId(Object modelId) {
		return findByProperty(MODEL_ID, modelId);
	}

	public List findByAction(Object action) {
		return findByProperty(ACTION, action);
	}

	public List findByLogInfo(Object logInfo) {
		return findByProperty(LOG_INFO, logInfo);
	}

	public List findAll() {
		log.debug("finding all GwModelFetchTaskLogVO instances");
		try {
			String queryString = "from GwModelFetchTaskLogVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public int saveTaskFetchLog(Long taskId,Long serviceId,Long userId,String action,String logInfo){
		
		return 0;
	}
}