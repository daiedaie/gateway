package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModifyRecord entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModifyRecordVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModifyRecordDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModifyRecordDAO.class);
	// property constants
	public static final String RECORD_CODE = "recordCode";
	public static final String TABLE_CODE = "tableCode";
	public static final String COLUMS_CODE = "columsCode";
	public static final String BEFORE_VALUE = "beforeValue";
	public static final String AFTER_VALUE = "afterValue";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String BATCH_ID = "batchId";

	public GwModifyRecordVO findById(java.lang.Long id) {
		log.debug("getting GwModifyRecordVO instance with id: " + id);
		try {
			GwModifyRecordVO instance = (GwModifyRecordVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwModifyRecordVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModifyRecordVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModifyRecordVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public List findByBatchId(Object batchId) {
		return findByProperty(BATCH_ID, batchId);
	}

	public List findByRecordCode(Object recordCode) {
		return findByProperty(RECORD_CODE, recordCode);
	}

	public List findByTableCode(Object tableCode) {
		return findByProperty(TABLE_CODE, tableCode);
	}

	public List findByColumsCode(Object columsCode) {
		return findByProperty(COLUMS_CODE, columsCode);
	}

	public List findByBeforeValue(Object beforeValue) {
		return findByProperty(BEFORE_VALUE, beforeValue);
	}

	public List findByAfterValue(Object afterValue) {
		return findByProperty(AFTER_VALUE, afterValue);
	}

	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByCreator(Object creator) {
		return findByProperty(CREATOR, creator);
	}

	public List findByModifier(Object modifier) {
		return findByProperty(MODIFIER, modifier);
	}

	public List findAll() {
		log.debug("finding all GwModifyRecordVO instances");
		try {
			String queryString = "from GwModifyRecordVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
}