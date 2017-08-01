package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUserRole entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUserRoleVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUserRoleDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUserRoleDAO.class);

	// property constants
	public static final String USER_ID = "userId";
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUserRoleVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwUserRoleVO as model where model."
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
	
	public List findAll() {
		log.debug("finding all GwUserRoleVO instances");
		try {
			String queryString = "from GwUserRoleVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public void deleteByUserId(Long userId) throws Exception{
		String sql = "delete gw_user_role where user_id=?";
		super.executeSql(sql, new Object[]{userId});
	}
}