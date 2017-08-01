package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelDataCycle entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelDataCycleVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelDataCycleDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModelDataCycleDAO.class);
	// property constants
	public static final String FILE_TYPE = "fileType";
	public static final String CYCLE_TYPE = "cycleType";
	public static final String CYCLE_NUM = "cycleNum";
	public static final String CREATE_USER = "createUser";
	public static final String UPDATE_USER = "updateUser";


	public GwModelDataCycleVO findById(java.lang.Long id) {
		log.debug("getting GwModelDataCycle instance with id: " + id);
		try {
			GwModelDataCycleVO instance = (GwModelDataCycleVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwModelDataCycleVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelDataCycle instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelDataCycleVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFileType(Object fileType) {
		return findByProperty(FILE_TYPE, fileType);
	}

	public List findByCycleType(Object cycleType) {
		return findByProperty(CYCLE_TYPE, cycleType);
	}

	public List findByCycleNum(Object cycleNum) {
		return findByProperty(CYCLE_NUM, cycleNum);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByUpdateUser(Object updateUser) {
		return findByProperty(UPDATE_USER, updateUser);
	}

	public List findAll() {
		log.debug("finding all GwModelDataCycle instances");
		try {
			String queryString = "from GwModelDataCycleVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

}