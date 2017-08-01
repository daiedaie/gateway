package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwDesenServiceInfoView;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwDesensitizationModelInfo entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.gztydic.gateway.core.vo.GwDesenServiceInfoVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwDesenServiceInfoDAO extends HibernateGenericDao {
	private static final Log log = LogFactory
			.getLog(GwDesenServiceInfoDAO.class);
	// property constants
	public static final String USER_ID = "userId";
	public static final String CREATE_USER = "createUser";
	public static final String UPDATE_USER = "updateUser";

	public GwDesenServiceInfoVO findById(java.lang.Long id) {
		log.debug("getting GwDesenServiceInfoVO instance with id: " + id);
		try {
			GwDesenServiceInfoVO instance = (GwDesenServiceInfoVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwDesenServiceInfoVO",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwDesenServiceInfoVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwDesenServiceInfoVO as model where model."
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

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByUpdateUser(Object updateUser) {
		return findByProperty(UPDATE_USER, updateUser);
	}

	public List findAll() {
		log.debug("finding all GwDesensitizationModelInfo instances");
		try {
			String queryString = "from GwDesensitizationModelInfo";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/**
	 * 根据用户帐号查询服务信息脱敏列表
	 * @param loginName
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchDesenServiceInfoList(String loginName,PageObject pageObject) throws Exception{
		String sql = "select u.user_id,u.login_name,u.user_name,m.model_id,t.service_id,t.service_code,t.service_name,t.service_type,t.cycle_type,t.cycle_day,m.model_code,m.model_name,i.info_dese_id,i.model_info,i.service_input_info " +
					 "from gw_user_service us " +
					 "inner join gw_service t on t.service_id=us.service_id and t.status=1 " +
					 "inner join gw_user org on org.user_type=? and org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
					 "inner join gw_user u on u.org_id = org.org_id and u.user_type=? " +
					 "left join gw_model m on m.model_id=t.model_id and m.status=1 " +
					 "left join gw_desen_service_info i on i.service_id=t.service_id and i.user_id=u.user_id " +
					 "where 1=1 ";	
		List<String> params = new ArrayList<String>();
		params.add(GwUserType.ORG_USER);
		params.add(GwUserType.DATA_USER);
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_name = ? ";
			params.add(loginName);
		}
		pageObject = super.findListBySql(sql, params.toArray(), pageObject, null);
		List<Object[]> list = pageObject.getData();
		List<GwDesenServiceInfoView> viewList = new ArrayList<GwDesenServiceInfoView>();
		if(list != null){
			GwDesenServiceInfoView v = null; 
			for (Object[] obj : list) {
				v = new GwDesenServiceInfoView();
				v.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
				v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
				v.setModelId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
				v.setServiceId(obj[4]==null?null:Long.valueOf(String.valueOf(obj[4])));
				v.setServiceCode(obj[5]==null?"":String.valueOf(obj[5]));
				v.setServiceName(obj[6]==null?"":String.valueOf(obj[6]));
				v.setServiceType(obj[7]==null?"":String.valueOf(obj[7]));
				v.setModelCode(obj[8]==null?"":String.valueOf(obj[8]));
				v.setModelCode(obj[9]==null?"":String.valueOf(obj[9]));
				v.setModelCode(obj[10]==null?"":String.valueOf(obj[10]));
				v.setModelName(obj[11]==null?"":String.valueOf(obj[11]));
				v.setInfoDeseId(obj[12]==null?null:Long.valueOf(String.valueOf(obj[12])));
				v.setModelInfo(obj[13]==null?"":String.valueOf(obj[13]));
				v.setServiceInputInfo(obj[14]==null?"":String.valueOf(obj[14]));
				viewList.add(v);
			}
			pageObject.setData(viewList);
		}
		return pageObject;
	}
	
	/**
	 * 根据同一用户、模型下其他服务的信息脱敏列表
	 * @param loginName
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenServiceInfoView> searchModelServiceDesenInfo(Long userId,Long modelId) throws Exception{
		String sql = "select u.user_id,u.login_name,u.user_name,t.service_id,t.service_code,t.service_name,t.service_type,m.model_code,m.model_name,i.info_dese_id,i.model_info,i.service_input_info " +
					 "from gw_user_service us " +
					 "inner join gw_service t on t.service_id=us.service_id and t.status=1 " +
					 "inner join gw_user org on org.user_type=? and org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
					 "inner join gw_user u on u.org_id = org.org_id and u.user_type=? " +
					 "inner join gw_model m on m.model_id=t.model_id and t.status=1 " +
					 "left join gw_desen_service_info i on i.service_id=t.service_id and i.user_id=u.user_id " +
					 "where m.model_id=? and u.user_id=? ";
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(GwUserType.ORG_USER);
		paramList.add(GwUserType.DATA_USER);
		if(modelId==null) modelId = -1l;
		paramList.add(modelId);
		paramList.add(userId);
		List<Object[]> list = super.findListBySql(sql, paramList.toArray(), null);
		List<GwDesenServiceInfoView> viewList = new ArrayList<GwDesenServiceInfoView>();
		if(list != null){
			GwDesenServiceInfoView v = null; 
			for (Object[] obj : list) {
				v = new GwDesenServiceInfoView();
				v.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
				v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
				v.setServiceId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
				v.setServiceCode(obj[4]==null?"":String.valueOf(obj[4]));
				v.setServiceName(obj[5]==null?"":String.valueOf(obj[5]));
				v.setServiceType(obj[6]==null?"":String.valueOf(obj[6]));
				v.setModelCode(obj[7]==null?"":String.valueOf(obj[7]));
				v.setModelName(obj[8]==null?"":String.valueOf(obj[8]));
				v.setInfoDeseId(obj[9]==null?null:Long.valueOf(String.valueOf(obj[9])));
				v.setModelInfo(obj[10]==null?"":String.valueOf(obj[10]));
				v.setServiceInputInfo(obj[11]==null?"":String.valueOf(obj[11]));
				viewList.add(v);
			}
		}
		return viewList;
	}
	
	/**
	 * 根据主键id查询脱敏列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenServiceInfoVO> searchDesenServiceInfoList(String infoDesenIds) throws Exception{
		String hql = "from GwDesenServiceInfoVO where infoDeseId in ("+infoDesenIds+")";	
		return super.findByHql(hql, null);
	}
	
	public List<GwDesenServiceInfoVO> searchDesenServiceInfo(GwDesenServiceInfoVO infoVO) throws Exception{
		String hql = "from GwDesenServiceInfoVO where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(infoVO != null){
			if(infoVO.getServiceId() != null){
				hql += "and serviceId=? ";
				paramList.add(infoVO.getServiceId());
			}
			if(infoVO.getUserId() != null){
				hql += "and userId=? ";
				paramList.add(infoVO.getUserId());
			}
		}
		return super.findByHql(hql, paramList.toArray());
	}
	
	/**
	 * 将userId的除serviceIds外模型的信息权限都删除
	 * @param modelId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int deleteDesenServiceInfoNoServiceIds(String serviceIds,Long userId) throws Exception{
		String sql = "delete Gw_Desen_service_Info where user_Id=? ";
		if(StringUtils.isNotBlank(serviceIds)){
			sql += "and service_Id not in ("+serviceIds+") ";
		}
		return super.executeSql(sql, new Object[]{userId});
	}
}