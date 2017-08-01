package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwModelLiabilityLogView;
import com.gztydic.gateway.core.vo.GwModelLiabilityLogVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelLiabilityLogVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelLiabilityLogVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelLiabilityLogDAO extends HibernateGenericDao {
	private GwUserDAO gwUserDAO;
	private static final Log log = LogFactory
			.getLog(GwModelLiabilityLogDAO.class);
	// property constants
	public static final String USER_ID = "userId";
	public static final String MODEL_FIELDS = "modelFields";
	public static final String MODEL_DATA_NUM = "modelDataNum";
	public static final String CREATE_USER = "createUser";

	public GwModelLiabilityLogVO findById(java.lang.Long id) {
		log.debug("getting GwModelLiabilityLog instance with id: " + id);
		try {
			GwModelLiabilityLogVO instance = (GwModelLiabilityLogVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwModelLiabilityLogVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelLiabilityLogVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelLiabilityLogVO as model where model."
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

	public List findByModelFields(Object modelFields) {
		return findByProperty(MODEL_FIELDS, modelFields);
	}

	public List findByModelDataNum(Object modelDataNum) {
		return findByProperty(MODEL_DATA_NUM, modelDataNum);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findAll() {
		log.debug("finding all  instances");
		try {
			String queryString = "from ";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 查询免责日志列表
	 * @param view
	 * @param object
	 * @return
	 * @throws Exception 
	 */
	public PageObject searchLiabilityList(GwModelLiabilityLogView view,PageObject pageObject) throws Exception{
		String sql = "select log.log_id,u.login_name,u.user_name,o.org_name,m.model_code,m.model_name,log.model_data_num,log.create_time,s.service_name,s.service_code,org.login_name orgLoginName,v.field_num " +
				"from gw_model_liability_log log " +
				"left join gw_user u on u.user_id=log.user_id " +
				"left join gw_org o on o.org_id=u.org_id " +
				"left join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' " +
				"left join gw_service s on s.service_id = log.service_id " +
				"left join gw_model m on m.model_id=s.model_id " +
				"left join v_service_field_count v on v.task_id=log.task_id " +
				"where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(view != null){
			if(view.getLogId() != null){
				sql += "and log.log_id = ? ";
				paramList.add(view.getLogId());
			}
			if(StringUtils.isNotBlank(view.getLoginName())){
				sql += "and u.login_name like ? ";
				paramList.add("%"+view.getLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getUserName())){
				sql += "and u.user_name like ? ";
				paramList.add("%"+view.getUserName()+"%");
			}
			if(StringUtils.isNotBlank(view.getModelName())){
				sql += "and m.model_name like ? ";
				paramList.add("%"+view.getModelName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgLoginName())){
				sql += "and org.login_name like ? ";
				paramList.add("%"+view.getOrgLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgName())){
				sql += "and o.org_name like ? ";
				paramList.add("%"+view.getOrgName()+"%");
			}
			if(StringUtils.isNotBlank(view.getServiceCode())){
				sql += "and s.service_code = ? ";
				paramList.add(view.getServiceCode());
			}
			if(StringUtils.isNotBlank(view.getDownloadStartTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') >= ? ";
				paramList.add(view.getDownloadStartTime());
			}
			if(StringUtils.isNotBlank(view.getDownloadEndTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') <= ? ";
				paramList.add(view.getDownloadEndTime());
			}
		}
		pageObject.setDefaultSort("log.create_time desc");
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
	
	public List searchLiabilityList(GwModelLiabilityLogView view) throws Exception{
		String sql = "select log.log_id,u.login_name,u.user_name,o.org_name,m.model_code,m.model_name,log.model_data_num,log.create_time,s.service_name,m.model_version,s.service_type,s.cycle_type,s.cycle_day,s.remark,m.model_type,m.start_time,m.alg_type,m.model_desc,s.service_code,log.model_fields,log.desen_rule_content,m.model_id,m.alg_rule " +
				"from gw_model_liability_log log " +
				"left join gw_user u on u.user_id=log.user_id " +
				"left join gw_org o on o.org_id=u.org_id " +
				"left join gw_service s on s.service_id = log.service_id " +
				"left join gw_model m on m.model_id=s.model_id " +
				"where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(view != null){
			if(view.getLogId() != null){
				sql += "and log.log_id = ? ";
				paramList.add(view.getLogId());
			}
		}
		return super.findListBySql(sql, paramList.toArray(),null);
	}
	
	/**
	 * 敏感信息追溯列表
	 * @param view
	 * @param object
	 * @return
	 * @throws Exception 
	 */
	public PageObject searchDesenList(GwModelLiabilityLogView view,PageObject pageObject,String searchBy) throws Exception{
		String sql = "select log.log_id,u.login_name,u.user_name,o.org_id,o.org_name,m.model_code,m.model_name,s.service_name,s.service_code,log.model_fields,log.model_data_num,log.create_time,org.login_name orgLoginName " +
				"from gw_model_liability_log log " +
				"left join gw_user u on u.user_id=log.user_id " +
				"left join gw_org o on o.org_id=u.org_id " +
				"left join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' " +
				"left join gw_service s on s.service_id=log.service_id " +
				"left join gw_model m on m.model_id=s.model_id " +
				"where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(view != null){
			if(StringUtils.isNotBlank(view.getModelFields())){
				String[] modelFieldsArray=view.getModelFields().split(",");
				sql += "and (";
				for(int i=0;i<modelFieldsArray.length;i++){
					sql += (i!=0?"or ":"")+"log.model_fields like ? ";
					paramList.add("%"+modelFieldsArray[i]+"%");
				}
				sql += ") ";
				for(int i=0;i<modelFieldsArray.length;i++){
					sql += "and exists (select 1 from gw_model_liability_log where model_fields like ? )";
					paramList.add("%"+modelFieldsArray[i]+"%");
				}
			}
			if(StringUtils.isNotBlank(view.getLoginName())){
				if("org".equals(searchBy)){
					sql +=" and o.org_id = (select org_id from gw_user where login_name=? ) ";
					paramList.add(view.getLoginName());
				}
				if("loginName".equals(searchBy)){
					sql +=" and u.login_name = ? ";
					paramList.add(view.getLoginName());
				}
			}
		}
		pageObject.setDefaultSort("log.create_time desc");
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
	
	/**查询已下载文件的输出行数
	 * @throws Exception */
	public String searchLiabilityDataNum(GwModelLiabilityLogView view) throws Exception{
		String sql = "select sum(dataNumSum) from (" +
				"select sum(model_data_num) dataNumSum " +
				"from gw_model_liability_log log " +
				"left join gw_user u on u.user_id=log.user_id " +
				"left join gw_org o on o.org_id=u.org_id " +
				"left join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' " +
				"left join gw_service s on s.service_id = log.service_id " +
				"left join gw_model m on m.model_id=s.model_id " +
				"where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(view != null){
			if(view.getLogId() != null){
				sql += "and log.log_id = ? ";
				paramList.add(view.getLogId());
			}
			if(StringUtils.isNotBlank(view.getLoginName())){
				sql += "and u.login_name like ? ";
				paramList.add("%"+view.getLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getUserName())){
				sql += "and u.user_name like ? ";
				paramList.add("%"+view.getUserName()+"%");
			}
			if(StringUtils.isNotBlank(view.getModelName())){
				sql += "and m.model_name like ? ";
				paramList.add("%"+view.getModelName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgLoginName())){
				sql += "and org.login_name like ? ";
				paramList.add("%"+view.getOrgLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgName())){
				sql += "and o.org_name like ? ";
				paramList.add("%"+view.getOrgName()+"%");
			}
			if(StringUtils.isNotBlank(view.getServiceCode())){
				sql += "and s.service_code = ? ";
				paramList.add(view.getServiceCode());
			}
			if(StringUtils.isNotBlank(view.getDownloadStartTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') >= ? ";
				paramList.add(view.getDownloadStartTime());
			}
			if(StringUtils.isNotBlank(view.getDownloadEndTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') <= ? ";
				paramList.add(view.getDownloadEndTime());
			}
		}
		sql += "group by log.task_id) tmp";
		List resultList=super.findListBySql(sql, paramList.toArray(), null);
		return resultList.size()>0 && resultList.get(0)!=null?resultList.get(0).toString():"0";
	}
	
	public String searchLiabilityOutputDataNum(GwModelLiabilityLogView view) throws Exception{
		String sql = "select sum(dataNumSum*field_Num) from (" +
				"select dataNumSum ,v.field_Num " +
				"from(select log.service_id,log.task_id,sum(model_data_num) dataNumSum " +
				"from gw_model_liability_log log " +
				"left join gw_user u on u.user_id=log.user_id " +
				"left join gw_org o on o.org_id=u.org_id " +
				"left join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' " +
				"left join gw_service s on s.service_id = log.service_id " +
				"left join gw_model m on m.model_id=s.model_id " +
				"where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(view != null){
			if(view.getLogId() != null){
				sql += "and log.log_id = ? ";
				paramList.add(view.getLogId());
			}
			if(StringUtils.isNotBlank(view.getLoginName())){
				sql += "and u.login_name like ? ";
				paramList.add("%"+view.getLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getUserName())){
				sql += "and u.user_name like ? ";
				paramList.add("%"+view.getUserName()+"%");
			}
			if(StringUtils.isNotBlank(view.getModelName())){
				sql += "and m.model_name like ? ";
				paramList.add("%"+view.getModelName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgLoginName())){
				sql += "and org.login_name like ? ";
				paramList.add("%"+view.getOrgLoginName()+"%");
			}
			if(StringUtils.isNotBlank(view.getOrgName())){
				sql += "and o.org_name like ? ";
				paramList.add("%"+view.getOrgName()+"%");
			}
			if(StringUtils.isNotBlank(view.getServiceCode())){
				sql += "and s.service_code = ? ";
				paramList.add(view.getServiceCode());
			}
			if(StringUtils.isNotBlank(view.getDownloadStartTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') >= ? ";
				paramList.add(view.getDownloadStartTime());
			}
			if(StringUtils.isNotBlank(view.getDownloadEndTime())){
				sql += "and to_char(log.create_time,'yyyy-MM-dd') <= ? ";
				paramList.add(view.getDownloadEndTime());
			}
		}
		sql += "group by log.task_id,log.service_id)tmp " +
				"left join v_service_field_count v on v.task_id =tmp.task_id)";
		
		List list=super.findListBySql(sql, paramList.toArray(), null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
		
	}
	
	//下载文件，点击“取消”，数据量置0
	public void updateDataNum(Long logId)throws Exception{
		String sql="update gw_model_liability_log set model_data_num=0 where log_id="+logId;
		super.executeSql(sql, null);
	}
}