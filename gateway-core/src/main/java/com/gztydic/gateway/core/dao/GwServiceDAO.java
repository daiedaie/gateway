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
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceVO entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceDAO.class);
	// property constants
	public static final String MODEL_ID = "modelId";
	public static final String SERVICE_CODE = "serviceCode";
	public static final String SERVICE_NAME = "serviceName";
	public static final String SERVICE_TYPE = "serviceType";
	public static final String CYCLE_TYPE = "cycleType";
	public static final String CYCLE_DAY = "cycleDay";
	public static final String STATUS = "status";
	public static final String REMARK = "remark";

	public GwServiceVO findById(Long id) {
		log.debug("getting GwServiceVO instance with id: " + id);
		try {
			GwServiceVO instance = (GwServiceVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwServiceVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwServiceVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwServiceVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByModelId(Object modelId) {
		return findByProperty(MODEL_ID, modelId);
	}

	public List findByServiceCode(Object serviceCode) {
		return findByProperty(SERVICE_CODE, serviceCode);
	}

	public List findByServiceName(Object serviceName) {
		return findByProperty(SERVICE_NAME, serviceName);
	}

	public List findByServiceType(Object serviceType) {
		return findByProperty(SERVICE_TYPE, serviceType);
	}

	public List findByCycleType(Object cycleType) {
		return findByProperty(CYCLE_TYPE, cycleType);
	}

	public List findByCycleDay(Object cycleDay) {
		return findByProperty(CYCLE_DAY, cycleDay);
	}

	public List findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	public List findByRemark(Object remark) {
		return findByProperty(REMARK, remark);
	}

	public List findAll() {
		log.debug("finding all GwServiceVO instances");
		try {
			String queryString = "from GwServiceVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public GwServiceVO searchService(String serviceCode,String serviceSource) throws Exception{
		String hql = "from GwServiceVO where serviceCode=? and serviceSource=?";
		List<GwServiceVO> list = super.findByHql(hql, new Object[]{serviceCode,serviceSource});
		return list!=null&list.size()>0?list.get(0):null;
	}
	
	public int searchServiceCount(String serviceCode,String serviceSource) throws Exception{
		String sql = "select count(1) from Gw_Service where service_Code=? and service_Source=?";
		return super.findIntBySql(sql, new Object[]{serviceCode,serviceSource});
	}
 
	//根据用户ID查找没有被授权的服务列表
	public List<GwServiceVO> searchUnchooseServiceByUserId(Long userId) throws Exception{
		String hql = "from GwServiceVO s where s.status=1 and s.serviceId not in (select serviceId from GwUserServiceVO r where r.userId=?) order by s.serviceId";
		return super.findByHql(hql, new Object[]{userId});
	}
	
	//根据用户ID查找被授权的服务列表
	public List<GwServiceVO> searchchooseServiceByUserId(Long userId) throws Exception{
		String hql = "from GwServiceVO s where s.status=1 and s.serviceId in (select serviceId from GwUserServiceVO r where r.userId=?) order by s.serviceId";
		return super.findByHql(hql, new Object[]{userId});
	}
	
	//根据条件查询未被授权的服务列表
	public List<GwServiceVO> searchchooseServiceByIdAndName(GwServiceVO service) throws Exception{
		String hql="from GwServiceVO s where s.status=1 ";
		List paramList = new ArrayList();
		if(service!=null){
			if(service.getServiceCode()!=null){
				hql+=" and s.serviceCode like ? ";
				paramList.add("%"+service.getServiceCode()+"%");
			}
			if(!StringUtils.isBlank(service.getServiceName())){
				hql+=" and s.serviceName like ? ";
				paramList.add("%"+service.getServiceName()+"%");
			}
				
		}
		return super.findByHql(hql, paramList.toArray());	
	}
	
	//查询服务列表并关联群组编码
	public List searchServiceListByRoleCode(String roleCode,String sort,String asc) throws Exception{
		String sql = "select s.service_id,s.service_code,s.service_name,m.model_name,s.service_type,s.cycle_type,s.model_id,r.role_code,s.cycle_day from gw_service s " +
				"left join gw_model m on m.model_id=s.model_id " +
				"left join gw_role_service r on r.service_id=s.service_id and r.role_code=? " +
				"where s.status=1 ";
		if(StringUtils.isNotBlank(sort)){
			sql += "order by "+sort+" "+asc;
		}
		return super.findListBySql(sql, new String[]{roleCode}, null);
	}

	
	//查询服务列表
	public PageObject searchServiceList(GwServiceView serviceView,PageObject pageObject) throws Exception{
		String sql = "select s.service_code,s.service_name,m.model_name,s.service_id,s.service_type,s.service_source,(select count(1) from gw_model_data_fetch f where f.service_id=s.service_id) fetchCount " +
				"from gw_service s left join gw_model m on m.model_id = s.model_id where s.status=1 ";
		List paramList = new ArrayList();
		if(serviceView != null){
			if(StringUtils.isNotBlank(serviceView.getServiceCode())){
				sql += "and s.service_code = ? ";
				paramList.add(serviceView.getServiceCode());
			}
			if(StringUtils.isNotBlank(serviceView.getServiceName())){
				sql += "and s.service_name like ? ";
				paramList.add("%"+serviceView.getServiceName()+"%");
			}
			if(StringUtils.isNotBlank(serviceView.getModelName())){
				sql += "and m.model_name like ? ";
				paramList.add("%"+serviceView.getModelName()+"%");
			}
		}
		pageObject.setDefaultSort("s.service_id");
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
	//根据用户查询服务
	public PageObject searchServiceListByUser(GwUserVO userVO,GwServiceView serviceView,PageObject pageObject) throws Exception{
		String sql = "select s.service_code,s.service_name,m.model_name,s.service_id,s.service_type,s.service_source,(select count(1) from gw_model_data_fetch f where f.service_id=s.service_id) fetchCount " +
				"from gw_service s " +
				"left join gw_model m on m.model_id = s.model_id " +
				"where s.service_Id in (select us.service_id from gw_user_service us " +
				"where us.user_id in (select user_id from gw_user where status=1 and confirm_status=2 and user_type=? and org_id = (select org_id from gw_user where user_id=?))) and s.status=1 ";
		List paramList = new ArrayList();
		paramList.add(GwUserType.ORG_USER);
		paramList.add(userVO.getUserId());
		if(serviceView != null){
			if(StringUtils.isNotBlank(serviceView.getServiceCode())){
				sql += "and s.service_code = ? ";
				paramList.add(serviceView.getServiceCode());
			}
			if(StringUtils.isNotBlank(serviceView.getServiceName())){
				sql += "and s.service_name like ? ";
				paramList.add("%"+serviceView.getServiceName()+"%");
			}
			if(StringUtils.isNotBlank(serviceView.getModelName())){
				sql += "and m.model_name like ? ";
				paramList.add("%"+serviceView.getModelName()+"%");
			}
		}
		pageObject.setDefaultSort("s.service_id");
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
	public List findServiceModelById(Long serviceId) throws Exception {
		String sql = "select  s.service_code,s.service_name,s.service_type,s.cycle_type,m.model_code,m.model_name,m.model_version,m.model_type,m.alg_type,m.alg_rule,s.service_id,s.cycle_day from gw_service s "				
				+"left join gw_model m on m.model_id = s.model_id "
				+"where s.service_id = "+serviceId;
		return super.findListBySql(sql, null, null);				
	}
	public List searchServicefieldInput(Long serviceId) throws Exception{
		String sql = "select f.field_code,f.field_name,f.field_Desc,f.nullable,f.field_type from gw_service_field f  "
				+"where f.gather_type = '0' and f.service_id = "+serviceId+" order by f.FIELD_ID";
		return super.findListBySql(sql, null, null);
	}
	public List searchServicefieldOutput(Long serviceId)throws Exception{
		String sql = "select f.field_code,f.field_name,f.field_Desc,f.nullable,f.field_type from gw_service_field f  "
				+"where f.gather_type = '1' and f.service_id = "+serviceId+" order by f.FIELD_ID";
		return super.findListBySql(sql, null,null);
	}
	
	public PageObject searchServiceAppList(Long userId,String userType,UserModelServiceAppVO view,PageObject pageObject)throws Exception{
		String sql = "select u.user_id,u.login_name,t.service_id,m.model_id,t.service_code,t.service_name,m.model_code,t.service_type,t.cycle_type,t.cycle_day,d.fetch_type,d.audit_status,d.audit_time,d.fetch_id,t.service_source " +
				 "from gw_user_service us " +
				 "inner join gw_service t on t.service_id=us.service_id and t.status=1 " +
				 "inner join gw_user org on org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
				 "inner join gw_user u on u.org_id = org.org_id and u.user_type=? " +
				 "left join gw_model m on m.model_id=t.model_id " +
				 "left join gw_model_data_fetch d on d.service_id=t.service_id and d.user_id=u.user_id " +
				 "where 1=1 ";
		
		List<Object> paramList = new ArrayList<Object>();
		//paramList.add(GwUserType.ORG_USER);
		paramList.add(GwUserType.DATA_USER);
		if(GwUserType.DATA_USER.equals(userType)){	//查询数据用户userId所属机构用户的服务申请
			sql += "and u.user_id=? ";
			paramList.add(userId);
		}else if(GwUserType.ORG_USER.equals(userType)){	//查询机构用户userId下数据用户的服务申请
			sql += "and org.user_id=? ";
			paramList.add(userId);
		}
		
		if(view!=null){
			if(StringUtils.isNotBlank(view.getServiceCode())){
				sql += "and t.service_code = ? ";
				paramList.add(view.getServiceCode());
			}
			if(StringUtils.isNotBlank(view.getServiceName())){
				sql += "and t.service_name like ? ";
				paramList.add("%"+view.getServiceName()+"%");
			}
			if(StringUtils.isNotBlank(view.getModelCode())){
				sql += "and m.model_code = ? ";
				paramList.add(view.getModelCode());
			}
			if(StringUtils.isNotBlank(view.getLoginName())){
				sql += "and u.login_name = ? ";
				paramList.add(view.getLoginName());
			}
			if(StringUtils.isNotBlank(view.getAuditStatus())){
				sql += "and d.audit_status = ? ";
				paramList.add(view.getAuditStatus());
			}
		}
		pageObject.setDefaultSort("u.user_id");
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
	
	public List<GwServiceVO> searchServiceByModel(Long modelId) throws Exception{
		String hql = "from GwServiceVO where modelId=?";
		return super.findByHql(hql, new Long[]{modelId});
	}
	
	/**
	 * 验证用户是否有该服务的权限
	 * @param serviceId
	 * @param userId
	 * @return
	 */
	public boolean validateServiceAuth(Long serviceId,Long userId){
		String sql = "select count(1) from gw_user_service us " +
					 "inner join gw_user org on org.user_type=? and org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
				 	 "inner join gw_user u on u.org_id = org.org_id and u.user_type=? " +
				 	 "where us.service_id=? and u.user_Id=?";
		return super.findCountBySql(sql, new Object[]{GwUserType.ORG_USER,GwUserType.DATA_USER,serviceId,userId}) > 0;
	}
	
	public void updateServiceStatus(Long serviceId) throws Exception{
		String sql="update gw_service set status=1 where service_id="+serviceId;
		super.executeSql(sql, null);
	}
}