package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwFuncVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwFunc entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwFuncVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwFuncDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwFuncDAO.class);
	// property constants
	public static final String FUNC_NAME = "funcName";
	public static final String FUNC_URL = "funcUrl";
	public static final String ICON_URL = "iconUrl";
	public static final String FUNC_DESC = "funcDesc";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";

	public GwFuncVO findById(java.lang.String id) {
		log.debug("getting GwFuncVO instance with id: " + id);
		try {
			GwFuncVO instance = (GwFuncVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwFuncVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwFuncVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwFuncVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFuncName(Object funcName) {
		return findByProperty(FUNC_NAME, funcName);
	}

	public List findByFuncUrl(Object funcUrl) {
		return findByProperty(FUNC_URL, funcUrl);
	}

	public List findByIconUrl(Object iconUrl) {
		return findByProperty(ICON_URL, iconUrl);
	}

	public List findByFuncDesc(Object funcDesc) {
		return findByProperty(FUNC_DESC, funcDesc);
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
		log.debug("finding all GwFuncVO instances");
		try {
			String queryString = "from GwFuncVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/**
	 * 查询所有有效的菜单
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GwFuncVO> searchFuncList() throws Exception{
		String hql = "from GwFuncVO where status=? order by funcSort";	
		return super.findByHql(hql, new Object[]{CommonState.VALID});
	}
	
	/**
	 * 未审核通过的，只显示用户查看页面
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GwFuncVO> searchFuncListByNoPass() throws Exception{
		String hql = "from GwFuncVO where status=? and funcCode in ('searchUser','userManage') order by funcSort";	
		return super.findByHql(hql, new Object[]{CommonState.VALID});
	}
	
	/**
	 * 根据群组编码查询所有有效的菜单
	 * @return
	 * @throws Exception
	 */
	public List searchFuncListByRoleCode(String roleCode) throws Exception{
		String sql = "select f.func_code,f.func_name,f.parent_code,r.role_code from gw_func f " +
				"left join gw_role_func r on r.func_code=f.func_code and r.role_code=? " +
				"where f.status=1 order by f.parent_code";	
		return super.findListBySql(sql, new String[]{roleCode}, null);
	}
	
	/**
	 * 根据用户类型查询所有有效的菜单
	 * @return
	 * @throws Exception
	 */
	public List searchFuncListByUserType(String userType,String source) throws Exception{
		String sql = "select f.func_code,f.func_name,fc.func_name parent,u.user_type from gw_func f " +
				"left join gw_func fc on f.parent_code=fc.func_code ";
		if("userTypeFuncAuth".equals(source)){
			sql+="left join gw_user_func u on u.func_code=f.func_code and u.user_type=? " +
					"where f.parent_code not in('-1') and f.status=1 order by f.parent_code ";
		}else if("userTypeFuncDetail".equals(source)){
			sql+="inner join gw_user_func u on u.func_code=f.func_code and u.user_type=? " +
					"where f.parent_code not in('-1') and f.status=1 order by f.parent_code ";
		}
		return super.findListBySql(sql, new String[]{userType}, null);
	}
	
	
	
	/**
	 * 查询用户类型关联的菜单列表
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GwFuncVO> searchFuncList(String userType) throws Exception{
		String hql = "from GwFuncVO f where f.status=1 and f.funcCode in (select uf.funcCode from GwUserFuncVO uf where uf.userType=?) " +
					 "order by funcSort";	
		return super.findByHql(hql, new String[]{userType});
	}
	
	//查询群组拥有的菜单
	public List<GwFuncVO> searchFuncListWithRoleCode(String roleCode) throws Exception{
		String hql = "from GwFuncVO f where f.funcCode in (select funcCode from GwRoleFuncVO r where r.roleCode=?)";
		return super.findByHql(hql, new String[]{roleCode});
	}
}