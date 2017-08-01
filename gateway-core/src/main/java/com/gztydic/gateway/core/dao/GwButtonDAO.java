package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwFuncVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwButton entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwButtonVO
 * @author MyEclipse Persistence Tools
 */

@Repository
public class GwButtonDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwButtonDAO.class);
	// property constants
	public static final String FUNC_CODE = "funcCode";
	public static final String OPERATE_TYPE = "operateType";
	public static final String OPERATE_DESC = "operateDesc";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";


	public GwButtonVO findById(java.lang.String id) {
		log.debug("getting GwButtonVO instance with id: " + id);
		try {
			GwButtonVO instance = (GwButtonVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwButtonVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwButtonVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwButtonVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFuncCode(Object funcCode) {
		return findByProperty(FUNC_CODE, funcCode);
	}

	public List findByOperateType(Object operateType) {
		return findByProperty(OPERATE_TYPE, operateType);
	}

	public List findByOperateDesc(Object operateDesc) {
		return findByProperty(OPERATE_DESC, operateDesc);
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
		log.debug("finding all GwButtonVO instances");
		try {
			String queryString = "from GwButtonVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	
	/**
	 * 根据群组编码查询所有有效的按钮
	 * @return
	 * @throws Exception
	 */
	public List searchButtonListByRoleCode(String roleCode) throws Exception{
		String sql = "select b.button_code,b.func_code,b.operate_Desc,r.role_code from gw_button b " +
				"left join gw_role_button r on r.button_code=b.button_code and r.role_code=? " +
				"where b.status=1 order by b.create_time";	
		return super.findListBySql(sql, new String[]{roleCode}, null);
	}
	
	/**
	 * 根据用户类型查询所有有效的按钮
	 * @return
	 * @throws Exception
	 */
	public List searchButtonListByUserType(String UserType)throws Exception{
		String sql = "select b.button_code,b.func_code,b.operate_Desc,u.user_type from gw_button b " +
				"left join gw_user_button u on u.button_code=b.button_code and u.user_type=? " +
				"where b.status=1 order by b.button_code";
		return super.findListBySql(sql, new String[]{UserType}, null);
	}
	
	//查询菜单对应已拥有的按钮
	public List<GwButtonVO> searchButtonListByFuncCode(String roleCode,String funcCode) throws Exception{
		String sql = "from GwButtonVO b where b.buttonCode in (select buttonCode from GwRoleButtonVO r where r.roleCode=?) and b.funcCode=?";
		return super.findByHql(sql, new String[]{roleCode,funcCode});
	}
}