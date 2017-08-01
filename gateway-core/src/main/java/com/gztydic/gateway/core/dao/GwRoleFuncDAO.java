package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwRoleFuncVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwRoleFunc entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwRoleFuncVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwRoleFuncDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwRoleFuncDAO.class);
    
	// property constants
	public static final String ROLE_CODE = "roleCode";
	
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwRoleFuncVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwRoleFuncVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List findByRoleCode(Object roleCode) {
		return findByProperty(ROLE_CODE, roleCode);
	}

	public List findAll() {
		log.debug("finding all GwRoleFuncVO instances");
		try {
			String queryString = "from GwRoleFuncVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	
	public void deleteByRoleCode(String roleCode) throws Exception{
		String sql = "delete gw_role_func where role_code=?";
		super.executeSql(sql, new String[]{roleCode});
	}
}