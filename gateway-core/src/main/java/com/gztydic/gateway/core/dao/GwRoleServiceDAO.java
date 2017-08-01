package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwRoleServiceVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwRoleModel entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwRoleServiceVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwRoleServiceDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwRoleServiceDAO.class);
	public static final String ROLE_CODE = "roleCode";

	// property constants

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwRoleServiceVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwRoleServiceVO as model where model."
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
		log.debug("finding all GwRoleServiceVO instances");
		try {
			String queryString = "from GwRoleServiceVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List findByRoleCode(Object roleCode) {
		return findByProperty(ROLE_CODE, roleCode);
	}
	
	public boolean searchList(GwRoleServiceVO vo) throws Exception{
		String sql = "select count(1) from gw_role_service role_Code=? and service_id=?";
		Integer count = super.findCountBySql(sql, new Object[]{vo.getRoleCode(),vo.getServiceId()});
		return count > 0 ? true : false;
	}
	
	public List<GwServiceVO> searchServiceListByRoleCode(String roleCode) throws Exception{
		String hql = "from GwServiceVO s where s.serviceId in (select serviceId from GwRoleServiceVO r where r.roleCode=?)";
		return super.findByHql(hql, new String[]{roleCode});
	}
	public void deleteByRoleCode(String roleCode) throws Exception{
		String sql = "delete gw_role_service where role_code=?";
		super.executeSql(sql, new String[]{roleCode});
	}
	public List searchServiceListbyRole(String roleCode)throws Exception{
		String sql="select s.* " +
				"from gw_service s inner join gw_role_service r " +
				"on s.service_id=r.service_id " +
				"where r.role_code=? ";
		return super.findListBySql(sql, new String[]{roleCode}, GwServiceVO.class);
	}
}