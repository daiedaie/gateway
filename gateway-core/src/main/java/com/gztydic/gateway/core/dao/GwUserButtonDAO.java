package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUserButton entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUserButtonVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUserButtonDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUserButtonDAO.class);

	// property constants

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUserButtonVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwUserButtonVO as model where model."
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
		log.debug("finding all GwUserButton instances");
		try {
			String queryString = "from GwUserButtonVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public void deleteByUserType(String userType) throws Exception{
		String sql = "delete gw_user_button where user_type=?";
		super.executeSql(sql, new String[]{userType});
	}
	
	public List<String> searchUserButton(GwUserVO loginUser) throws Exception{
		String sql = "select button_code from gw_user_button ub where ub.user_type=?";
		return super.findListBySql(sql, new String[]{loginUser.getUserType()}, null);
	}
	
	/**
	 * 
	 * @Title: searchPlanTypeByUserType 
	 * @Description: TODO(根据用户类型查询待办类型字典编码) 
	 * @param userType
	 * @throws Exception    设定文件 
	 * @return List<String>   返回类型 
	 */
	public List<String> searchPlanTypeByUserType(String userType) throws Exception{
		String sql = "select b.task_type_no from gw_user_button a,gw_task_type_info b where a.button_code=b.task_type_name and a.user_type=?";
		return super.findListBySql(sql, new String[]{userType}, null);
	}
	
	public List<String> searchLimitPlanTypeByUserType(String userType) throws Exception{
		String sql = "select b.task_type_no from gw_user_button a,gw_task_type_info b where a.button_code=b.task_type_name and a.user_type=? and b.task_type_no in('1','2','3','15','17')";
		return super.findListBySql(sql, new String[]{userType}, null);
	}
	
	/**
	 * 
	 * @Title: searchPlanTypeByUserType 
	 * @Description: TODO(根据待办类型查询拥有该待办待办类型的用户) 
	 * @param userType
	 * @throws Exception    设定文件 
	 * @return List<String>   返回类型 
	 */
	public List<String> searchUserByPlanType(String planType) throws Exception{
		String sql = "select u.moblie from gw_user u,gw_user_button a,gw_task_type_info b where a.button_code=b.task_type_name and a.user_type=u.user_type and u.online_status=1 and b.task_type_no=?";
		return super.findListBySql(sql, new String[]{planType}, null);
	}
	
	/**
	 * 
	 * @Title: searchUserButton 
	 * @Description: TODO(根据任务类型和用户类型查询是否有权限) 
	 * @param @param planType
	 * @param @param userType
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<String>    返回类型 
	 * @throws
	 */
	public List<String> searchUserButton(String planType,String userType)throws Exception{
		String sql = "select a.task_type_name from gw_task_type_info a,gw_user_button b where a.task_type_name=b.button_code and task_type_no=? and b.user_type=?";
		return super.findListBySql(sql, new String[]{planType,userType}, null);
	}
}