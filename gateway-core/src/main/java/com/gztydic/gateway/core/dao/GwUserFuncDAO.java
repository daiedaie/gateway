package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUserFunc entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUserFuncVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUserFuncDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUserFuncDAO.class);

	// property constants

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUserFunc instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwUserFuncVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all GwUserFunc instances");
		try {
			String queryString = "from GwUserFuncVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public void deleteByUserType(String  userType) throws Exception{
		String sql = "delete gw_user_func where user_type=?";
		super.executeSql(sql, new String[]{userType});
	}
}