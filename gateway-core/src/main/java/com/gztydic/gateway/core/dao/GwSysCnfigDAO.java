package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceVO entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwSysCnfigDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceDAO.class);
	// property constants
	public static final String CONFIG_TYPE = "configType";

	public GwSysCnfigVO findById(Long id) {
		log.debug("getting GwSysCnfigVO instance with id: " + id);
		try {
			GwSysCnfigVO instance = (GwSysCnfigVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwSysCnfigVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwSysCnfigVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwSysCnfigVO as config where config."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByConfigType(Object configType) {
		return findByProperty(CONFIG_TYPE, configType);
	}

}