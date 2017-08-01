package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUserModel entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUserServiceVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUserServiceDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUserServiceDAO.class);
	public static final String SERVICE_ID = "serviceId";
	// property constants

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUserModel instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwUserServiceVO as s where s."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List findByServiceIdAndUserId(String userId, String sericeId) throws Exception {
		String sql="select * from gw_user_service s where s.user_id='"+userId+"' and s.service_id='"+sericeId+"'";
		return super.findListBySql(sql, null, null);
	}

	public List findAll() {
		log.debug("finding all GwUserServiceVO instances");
		try {
			String queryString = "from GwUserServiceVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public void deleteByUserId(Long userId) throws Exception{
		String sql = "delete gw_user_service where user_id=?";
		super.executeSql(sql, new Object[]{userId});
	}
}