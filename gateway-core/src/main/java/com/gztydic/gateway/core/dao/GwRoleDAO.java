package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwRoleVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwRole entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwRoleVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwRoleDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwRoleDAO.class);
	// property constants
	public static final String ROLE_CODE = "roleCode";
	public static final String ROLE_NAME = "roleName";
	public static final String ROLE_DESC = "roleDesc";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwRoleVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwRoleVO as model where model."
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

	public List findByRoleName(Object roleName) {
		return findByProperty(ROLE_NAME, roleName);
	}

	public List findByRoleDesc(Object roleDesc) {
		return findByProperty(ROLE_DESC, roleDesc);
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

	public List findByRemark(Object remark) {
		return findByProperty(REMARK, remark);
	}

	public List findAll() {
		log.debug("finding all GwRoleVO instances");
		try {
			String queryString = "from GwRoleVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public PageObject searchRoleList(GwRoleVO vo,PageObject obj) throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("from GwRoleVO r where 1=1 ");
		List<Object> params = new ArrayList<Object>(0);
		if(vo!=null){
			if ((!StringUtils.isBlank(vo.getRoleCode()))) {
		        hql.append(" and r.roleCode like ? ");
		        params.add("%"+vo.getRoleCode()+"%");
		    }
			if ((!StringUtils.isBlank(vo.getRoleName()))) {
		        hql.append(" and r.roleName like ? ");
		        params.add("%"+vo.getRoleName()+"%");
		    }
		}
		obj.setDefaultSort("r.createTime");
		return super.findByPage(vo.getClass(), hql.toString(), obj,null,params.toArray());
	}
	
	//根据群组编码或者群组名称查询群组列表
	public List searchRoleByCodeOrName(GwRoleVO vo) throws Exception{
		String hql="from GwRoleVO r where r.roleCode=? or r.roleName=? ";
		Object[] obj=new Object[2];
		obj[0]=vo.getRoleCode();
		obj[1]=vo.getRoleName();
		return super.findByHql(hql, obj);	
	}

	//查询群组列表，并关联用户ID
	public List searchRoleListByUserId(Long userId) throws Exception{
		String sql = "select r.role_code,r.role_name,r.role_desc,u.user_id from gw_role r " +
				"left join gw_user_role u on u.role_code=r.role_code and u.user_id=? " +
				"where r.status=1 order by r.create_time";	
		return super.findListBySql(sql, new Object[]{userId}, null);
	}
	
	//根据群组编码删除群组
	public void deleteRole(String roleCode)throws Exception{
		String sql="delete gw_role where role_code=?";
		super.executeSql(sql, new String[]{roleCode});
	}
}