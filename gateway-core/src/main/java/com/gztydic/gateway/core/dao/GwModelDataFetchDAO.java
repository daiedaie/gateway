package com.gztydic.gateway.core.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelDataFetch entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelDataFetchVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelDataFetchDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModelDataFetchDAO.class);
	// property constants
	public static final String MODEL_ID = "modelId";
	public static final String USER_ID = "userId";
	public static final String FETCH_TYPE = "fetchType";
	public static final String CYCLE_TYPE = "cycleType";
	public static final String CYCLE_NUM = "cycleNum";
	public static final String AUDIT_STATUS = "auditStatus";
	public static final String CREATE_USER = "createUser";
	public static final String UPDATE_USER = "updateUser";

	public GwModelDataFetchVO findById(java.lang.Long id) {
		log.debug("getting GwModelDataFetchVO instance with id: " + id);
		try {
			GwModelDataFetchVO instance = (GwModelDataFetchVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwModelDataFetchVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelDataFetchVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelDataFetchVO as model where model."
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

	public List findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}

	public List findByFetchType(Object fetchType) {
		return findByProperty(FETCH_TYPE, fetchType);
	}

	public List findByCycleType(Object cycleType) {
		return findByProperty(CYCLE_TYPE, cycleType);
	}

	public List findByCycleNum(Object cycleNum) {
		return findByProperty(CYCLE_NUM, cycleNum);
	}

	public List findByAuditStatus(Object auditStatus) {
		return findByProperty(AUDIT_STATUS, auditStatus);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByUpdateUser(Object updateUser) {
		return findByProperty(UPDATE_USER, updateUser);
	}
	
	public List findAll() {
		log.debug("finding all GwModelDataFetchVO instances");
		try {
			String queryString = "from GwModelDataFetchVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/** 
	 * @Title: searchUserModelAppList 
	 * @Description: TODO(根据用户ID查询用户下所有模型服务) 
	 * @param @param userId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserModelServiceAppList(Long userId) throws Exception{
		
		String sql ="select t.USER_ID as userId,t.SERVICE_ID as serviceId,t.MODEL_ID as modelId,t.SERVICE_CODE as serviceCode,t.SERVICE_NAME as serviceName,FETCH_TYPE as fetchType,CYCLE_TYPE as cycleType,CYCLE_NUM as cycleNum,AUDIT_STATUS as auditStatus from "+
		"(select a.USER_ID,a.SERVICE_ID,b.MODEL_ID,b.SERVICE_CODE,b.SERVICE_NAME from GW_USER_SERVICE a,GW_SERVICE b,GW_MODEL c where a.SERVICE_ID=b.SERVICE_ID and b.MODEL_ID=c.MODEL_ID and USER_ID =?"+
		" union "+
		"select a.USER_ID,b.SERVICE_ID,c.MODEL_ID,c.SERVICE_CODE,c.SERVICE_NAME from GW_USER_ROLE a,GW_ROLE_SERVICE b,GW_SERVICE c " +
		"where a.ROLE_CODE=b.ROLE_CODE and b.SERVICE_ID=c.SERVICE_ID and USER_ID =?) t, "+
		" GW_MODEL_DATA_FETCH s "+
		"where t.user_id=s.user_id(+) and t.model_id=s.model_id(+) and t.service_id=s.service_id(+)";
		 
		List<Object[]> list = findListBySql(sql,new Object[]{userId,userId}, null);
		List<UserModelServiceAppVO> appList = new ArrayList<UserModelServiceAppVO>();
		UserModelServiceAppVO vo = null;
		for(Object[] obj : list){
			vo = new UserModelServiceAppVO();
			vo.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
			vo.setServiceId(obj[1]==null?null:Long.valueOf(String.valueOf(obj[1])));
			vo.setModelId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
			vo.setServiceCode(obj[3]==null?null:String.valueOf(obj[3]));
			vo.setServiceName(obj[4]==null?null:String.valueOf(obj[4]));
			vo.setFetchType(obj[5]==null?null:String.valueOf(obj[5]));
			vo.setCycleType(obj[6]==null?null:String.valueOf(obj[6]));
			vo.setCycleNum(obj[7]==null?null:Long.valueOf(String.valueOf(obj[7])));
			vo.setAuditStatus(obj[8]==null?null:String.valueOf(obj[8]));
			appList.add(vo);
		}
		return appList;
	}
	/** 
	 * @Title: findByServiceIdAndUserId 
	 * @Description: TODO(根据用户Id和服务Id查看服务取数申请信息) 
	 * @param @param userId
	 * @param @param modelId
	 * @param @return    设定文件 
	 * @return List    返回类型 
	 * @throws 
	 */
	public List findByServiceIdAndUserId(Object userId,Object serviceId) {
		log.debug("finding GwModelDataFetchVO instance with property: SERVICE_ID value:"
				+ serviceId +" and USER_ID value:"+ userId);
		try {
			String queryString = "from GwModelDataFetchVO as model where model.serviceId= ? and model.userId= ?  ";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, serviceId);
			queryObject.setParameter(1, userId);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	/** 
	 * @Title: findByServiceIdAndUserId 
	 * @Description: TODO(根据用户Id和服务Id,模型id查看模型服务取数申请信息) 
	 * @param @param userId
	 * @param @param modelId
	 * @param @return    设定文件 
	 * @return List    返回类型 
	 * @throws 
	 */
	public List findByServiceIdAndUserIdAndModelId(Object serviceId,Object userId,Object modelId) {
		log.debug("finding GwModelDataFetchVO instance with property: SERVICE_ID value:"
				+ serviceId +" and USER_ID value:"+ userId+" and MODEL_ID value:"+ modelId);
		try {
			String queryString = "from GwModelDataFetchVO as model where model.serviceId= ? and model.userId= ? and model.modelId=? ";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, serviceId);
			queryObject.setParameter(1, userId);
			queryObject.setParameter(2, modelId);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 
	 * @Title: createOfflineTask 
	 * @Description: TODO(离线服务审核通过创建取数任务) 
	 * @param @param fetchId
	 * @param @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean createOfflineTask(Long fetchId)  {
		//获得连接  
		Connection conn = null;
		try {
			conn = getCurrentSession().connection();
			//创建存储过程的对象  
			CallableStatement call = conn.prepareCall("{call GW_OFFLINE_SERVICE_TASK(?,?)}");
			//给存储过程的第一个参数设置值  
			call.setLong(1,fetchId);   
			//注册存储过程的第二个参数  
			call.registerOutParameter(2,java.sql.Types.INTEGER);
			//执行存储过程  
			call.execute();			
			return call.getInt(2)>0;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("execute the procedures GW_OFFLINE_SERVICE_TASK error:", e);
			return false;
		}  finally{
			try {
				if(conn != null) conn.close();
			} catch (SQLException e) {
				log.error("close connetion error:", e);
			} 
		}
	}
	
	/**
	 * 修改取数申请审核状态
	 * @param userVO
	 * @param serviceId
	 * @param auditStatus
	 * @return
	 * @throws Exception
	 */
	public int updateUserFetch(Long userId,Long serviceId,String auditStatus,String updateUser) throws Exception{
		String sql = "update gw_model_data_fetch t set t.audit_status=?,t.update_time=sysdate,t.update_user=? " +
				"where t.user_id=? and t.service_id=?";
		return executeSql(sql, new Object[]{auditStatus,updateUser,userId,serviceId});
	}
	
	public GwModelDataFetchVO searchModelDataFetch(Long userId,Long serviceId) throws Exception{
		String hql = "from GwModelDataFetchVO where userId=? and serviceId=?";
		List<GwModelDataFetchVO> list = super.findByHql(hql, new Object[]{userId,serviceId});
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public int searchServiceFetchCount(Long serviceId){
		String sql = "select count(1) from gw_model_data_fetch where service_id=?";
		return super.findIntBySql(sql, new Object[]{serviceId});
	}
	
	/** 
	 * @Title: searchUserModelAppList 
	 * @Description: TODO(根据用户ID和审核状态查询用户下所有模型服务) 
	 * @param @param userId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserServiceList(Long userId,String auditStatus) throws Exception{
		
		String sql ="select a.service_id as serviceId,b.service_code as serviceCode,b.service_name as serviceName,a.user_id as userId from " +
				"GW_MODEL_DATA_FETCH a,GW_SERVICE b where a.service_id=b.service_id and user_id=? and AUDIT_STATUS=?";
		 
		List<Object[]> list = findListBySql(sql,new Object[]{userId,auditStatus}, null);
		List<UserModelServiceAppVO> appList = new ArrayList<UserModelServiceAppVO>();
		UserModelServiceAppVO vo = null;
		for(Object[] obj : list){
			vo = new UserModelServiceAppVO();
			vo.setServiceId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
			vo.setServiceCode(obj[1]==null?null:String.valueOf(obj[1]));
			vo.setServiceName(obj[2]==null?null:String.valueOf(obj[2]));
			vo.setUserId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
			appList.add(vo);
		}
		return appList;
	}
	
	/** 
	 * @Title: searchServiceApp 
	 * @Description: TODO(根据用户ID和服务id查询模型服务) 
	 * @param @param userId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public UserModelServiceAppVO searchServiceApp(Long userId,Long serviceId,String auditStatus) throws Exception{
		
		String sql ="select a.user_id as userId,a.FETCH_ID as fetchId ,a.service_id as serviceId,b.service_code as serviceCode,b.service_name as serviceName," +
				"a.audit_time as auditTime,b.cycle_type as cycleType,b.cycle_day as cycleDay,b.service_type as serviceType from " +
				"GW_MODEL_DATA_FETCH a,GW_SERVICE b where a.service_id=b.service_id and b.service_id=? and user_id=? and AUDIT_STATUS=? ";
		 
		List<Object[]> list = findListBySql(sql,new Object[]{serviceId,userId,auditStatus}, null);
		List<UserModelServiceAppVO> appList = new ArrayList<UserModelServiceAppVO>();
		UserModelServiceAppVO vo = null;
		for(Object[] obj : list){
			vo = new UserModelServiceAppVO();
			vo.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
			vo.setFetchId(obj[1]==null?null:Long.valueOf(String.valueOf(obj[1])));
			vo.setServiceId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
			vo.setServiceCode(obj[3]==null?null:String.valueOf(obj[3]));
			vo.setServiceName(obj[4]==null?null:String.valueOf(obj[4]));
			//System.out.println("========="+String.valueOf(obj[5]));
			vo.setAuditTime(obj[5]==null?null:DateUtil.StringTODate5(String.valueOf(obj[5])));
			vo.setCycleType(obj[6]==null?null:String.valueOf(obj[6]));
			vo.setCycleDay(obj[7]==null?null:String.valueOf(obj[7]));
			vo.setServiceType(obj[8]==null?null:String.valueOf(obj[8]));
			appList.add(vo);
		}
		return appList.size() > 0 ? appList.get(0) : null;
	}
	
	public GwModelDataFetchVO searchModelDataFetch(String authorityGno) throws Exception{
		String hql = "from GwModelDataFetchVO where authorityGno=? and auditStatus = 2";
		List<GwModelDataFetchVO> list = super.findByHql(hql, new Object[]{authorityGno});
		return list.size() > 0 ? list.get(0) : null;
	}
}