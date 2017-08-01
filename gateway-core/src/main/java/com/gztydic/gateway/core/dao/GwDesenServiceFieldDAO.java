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
import com.gztydic.gateway.core.view.DesenServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenServiceFieldView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;
import com.gztydic.gateway.core.view.GwServiceCheckRecordView;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwDesenServiceFieldVO entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.gztydic.gateway.core.vo.GwDesenServiceFieldVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwDesenServiceFieldDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwDesenServiceFieldDAO.class);
	// property constants
	public static final String MODEL_ID = "modelId";
	public static final String USER_ID = "userId";
	public static final String FIELD_ID = "fieldId";
	public static final String RULE_TYPE = "ruleType";
	public static final String RULE_CONTENT = "ruleContent";
	public static final String REPLACE_CONTENT = "replaceContent";
	public static final String CONDITION_TYPE = "conditionType";
	public static final String CONDITION_CONTENT = "conditionContent";
	public static final String CREATE_USER = "createUser";
	public static final String UPDATE_USER = "updateUser";

	public GwDesenServiceFieldVO findById(Long id) {
		log
				.debug("getting GwDesenServiceFieldVO instance with id: "
						+ id);
		try {
			GwDesenServiceFieldVO instance = (GwDesenServiceFieldVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwDesenServiceFieldVO",id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log
				.debug("finding GwDesenServiceFieldVO instance with property: "
						+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwDesenServiceFieldVO as model where model."
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

	public List findByFieldId(Object fieldId) {
		return findByProperty(FIELD_ID, fieldId);
	}

	public List findByRuleType(Object ruleType) {
		return findByProperty(RULE_TYPE, ruleType);
	}

	public List findByRuleContent(Object ruleContent) {
		return findByProperty(RULE_CONTENT, ruleContent);
	}

	public List findByReplaceContent(Object replaceContent) {
		return findByProperty(REPLACE_CONTENT, replaceContent);
	}

	public List findByConditionType(Object conditionType) {
		return findByProperty(CONDITION_TYPE, conditionType);
	}

	public List findByConditionContent(Object conditionContent) {
		return findByProperty(CONDITION_CONTENT, conditionContent);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByUpdateUser(Object updateUser) {
		return findByProperty(UPDATE_USER, updateUser);
	}

	public List findAll() {
		log.debug("finding all GwDesenServiceFieldVO instances");
		try {
			String queryString = "from GwDesenServiceFieldVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据数据用户查询模型服务字段脱敏列表
	 * @param loginName
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchDesenServiceFieldList(String loginName,PageObject pageObject) throws Exception{
		String sql = "select u.user_id,u.login_name,u.user_name,m.model_id,t.service_id,t.service_code,t.service_name,t.service_type,t.cycle_type,t.cycle_day,m.model_code,m.model_name," +
				"(select count(1) from gw_desen_service_field i where i.service_id=t.service_id and i.user_id=u.user_id) desenCount " +
				 "from gw_user_service us " +
				 "inner join gw_service t on t.service_id=us.service_id and t.status=1 " +
				 "inner join gw_user org on  org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
				 "inner join gw_user u on u.org_id = org.org_id and u.user_type=? " +
				 "inner join gw_model_data_fetch f on f.service_id=t.service_id and f.user_id=u.user_id " +
				 "left join gw_model m on m.model_id=t.model_id and m.status=1 " +
				 "where 1=1 ";	
		List<String> params = new ArrayList<String>();
		//params.add(GwUserType.ORG_USER);
		params.add(GwUserType.DATA_USER);
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_name = ? ";
			params.add(loginName); 
		}
		pageObject.setDefaultSort("u.user_id,t.service_id");
		pageObject = super.findListBySql(sql, params.toArray(), pageObject, null);
		List<Object[]> list = pageObject.getData();
		List<GwDesenServiceFieldView> viewList = new ArrayList<GwDesenServiceFieldView>();
		if(list != null){
			GwDesenServiceFieldView v = null; 
			for (Object[] obj : list) {
				v = new GwDesenServiceFieldView();
				v.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
				v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
				v.setModelId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
				v.setServiceId(obj[4]==null?null:Long.valueOf(String.valueOf(obj[4])));
				v.setServiceCode(obj[5]==null?"":String.valueOf(obj[5]));
				v.setServiceName(obj[6]==null?"":String.valueOf(obj[6]));
				v.setServiceType(obj[7]==null?"":String.valueOf(obj[7]));
				v.setCycleType(obj[8]==null?"":String.valueOf(obj[8]));
				v.setCycleDay(obj[9]==null?null:Long.parseLong(String.valueOf(obj[9])));
				v.setModelCode(obj[10]==null?"":String.valueOf(obj[10]));
				v.setModelName(obj[11]==null?"":String.valueOf(obj[11]));
				v.setDesenCount(obj[12]==null?null:Long.valueOf(String.valueOf(obj[12])));
				viewList.add(v);
			}
			pageObject.setData(viewList);
		}
		return pageObject;
	}
	/**
	 *查询108脱敏规则列表 
	 * 
	 */
	public List<GwDesenRuleServiceFieldView> search108DesenRuleServiceFieldList(Long userId,Long serviceId) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select dmf.field_id,f.service_id,f.field_code,f.field_name,f.field_type,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule " +
				"from Gw_Service_Check_Rule f " +
				"left join (select * from gw_desen_service_field dmf where dmf.batch=(select max(batch) from gw_desen_service_field where user_Id=? and service_Id=?)) dmf " +
				"on dmf.field_id=f.check_id and dmf.user_id=? " +
				"where f.service_id=?"+
				" and f.check_batch = (select max(check_batch) from Gw_Service_Check_Rule   where user_Id = ?  and service_Id = ?)";
		List<Object[]> list = super.findListBySql(sql, new Object[]{userId,serviceId,userId,serviceId,userId,serviceId}, null);
		GwDesenRuleServiceFieldView view = null;
		List<GwDesenRuleServiceFieldView> viewList = new ArrayList<GwDesenRuleServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwDesenRuleServiceFieldView();
			view.setUserId(userId);
			view.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setServiceId(obj[1]==null?null:Long.parseLong(String.valueOf(obj[1])));
			view.setFieldCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setFieldName(obj[3]==null?"":String.valueOf(obj[3]));
			view.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			view.setFieldDesc(obj[5]==null?"":String.valueOf(obj[5]));
			view.setRuleType(obj[6]==null?"":String.valueOf(obj[6]));
			view.setRuleContent(obj[7]==null?"":String.valueOf(obj[7]));
			view.setReplaceContent(obj[8]==null?"":String.valueOf(obj[8]));
			view.setConditionType(obj[9]==null?"":String.valueOf(obj[9]));
			view.setConditionContent(obj[10]==null?"":String.valueOf(obj[10]));
			view.setCheckType(obj[11]==null?"":String.valueOf(obj[11]));
			view.setCheckRule(obj[12]==null?"":String.valueOf(obj[12]));
			viewList.add(view);
		}
		return viewList;
	}
	
	/**
	 * 查询字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldList(Long userId,Long serviceId) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.field_id,f.service_id,f.field_code,f.field_name,f.field_type,f.field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule " +
				"from gw_service_field f " +
				"left join (select * from gw_desen_service_field dmf where dmf.batch=(select max(batch) from gw_desen_service_field where user_Id=? and service_Id=?)) dmf " +
				"on dmf.field_id=f.field_id and dmf.user_id=? " +
				"where f.service_id=? and gather_type=1 " +	//只查输出
				"order by f.field_id ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{userId,serviceId,userId,serviceId}, null);
		GwDesenRuleServiceFieldView view = null;
		List<GwDesenRuleServiceFieldView> viewList = new ArrayList<GwDesenRuleServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwDesenRuleServiceFieldView();
			view.setUserId(userId);
			view.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setServiceId(obj[1]==null?null:Long.parseLong(String.valueOf(obj[1])));
			view.setFieldCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setFieldName(obj[3]==null?"":String.valueOf(obj[3]));
			view.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			view.setFieldDesc(obj[5]==null?"":String.valueOf(obj[5]));
			view.setFieldDeseId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setRuleType(obj[7]==null?"":String.valueOf(obj[7]));
			view.setRuleContent(obj[8]==null?"":String.valueOf(obj[8]));
			view.setReplaceContent(obj[9]==null?"":String.valueOf(obj[9]));
			view.setConditionType(obj[10]==null?"":String.valueOf(obj[10]));
			view.setConditionContent(obj[11]==null?"":String.valueOf(obj[11]));
			view.setCheckType(obj[12]==null?"":String.valueOf(obj[12]));
			view.setCheckRule(obj[13]==null?"":String.valueOf(obj[13]));
			viewList.add(view);
		}
		return viewList;
	}
	
	/**
	 * 查询字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleListByBatch(GwModelDataFetchTaskVO taskVO) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.field_id,f.service_id,f.field_code,f.field_name,f.field_type,f.field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule " +
				"from gw_service_field f " +
				"left join gw_desen_service_field dmf on dmf.field_id=f.field_id and dmf.batch=? and dmf.user_id=? " +
				"where f.service_id=? and gather_type=1 " +	//只查输出
				"order by f.field_id ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{taskVO.getCheckBatch(),taskVO.getUserId(),taskVO.getServiceId()}, null);
		GwDesenRuleServiceFieldView view = null;
		List<GwDesenRuleServiceFieldView> viewList = new ArrayList<GwDesenRuleServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwDesenRuleServiceFieldView();
			view.setUserId(taskVO.getUserId());
			view.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setServiceId(obj[1]==null?null:Long.parseLong(String.valueOf(obj[1])));
			view.setFieldCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setFieldName(obj[3]==null?"":String.valueOf(obj[3]));
			view.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			view.setFieldDesc(obj[5]==null?"":String.valueOf(obj[5]));
			view.setFieldDeseId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setRuleType(obj[7]==null?"":String.valueOf(obj[7]));
			view.setRuleContent(obj[8]==null?"":String.valueOf(obj[8]));
			view.setReplaceContent(obj[9]==null?"":String.valueOf(obj[9]));
			view.setConditionType(obj[10]==null?"":String.valueOf(obj[10]));
			view.setConditionContent(obj[11]==null?"":String.valueOf(obj[11]));
			view.setCheckType(obj[12]==null?"":String.valueOf(obj[12]));
			view.setCheckRule(obj[13]==null?"":String.valueOf(obj[13]));
			viewList.add(view);
		}
		return viewList;
	}
	
	/**
	 * 查询字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldConfig(Long userId,Long serviceId) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.field_id,f.service_id,f.field_code,f.field_name,f.field_type,f.field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content " +
				"from gw_service_field f " +
				"inner join (select * from gw_desen_service_field dmf where dmf.batch=(select max(batch) from gw_desen_service_field where user_Id=? and service_Id=?)) dmf " +
				"on dmf.field_id=f.field_id and dmf.user_id=? " +
				"where f.service_id=? and gather_type=1 " +	//只查输出
				"order by f.field_id ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{userId,serviceId,userId,serviceId}, null);
		GwDesenRuleServiceFieldView view = null;
		List<GwDesenRuleServiceFieldView> viewList = new ArrayList<GwDesenRuleServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwDesenRuleServiceFieldView();
			view.setUserId(userId);
			view.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setServiceId(obj[1]==null?null:Long.parseLong(String.valueOf(obj[1])));
			view.setFieldCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setFieldName(obj[3]==null?"":String.valueOf(obj[3]));
			view.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			view.setFieldDesc(obj[5]==null?"":String.valueOf(obj[5]));
			view.setFieldDeseId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setRuleType(obj[7]==null?"":String.valueOf(obj[7]));
			view.setRuleContent(obj[8]==null?"":String.valueOf(obj[8]));
			view.setReplaceContent(obj[9]==null?"":String.valueOf(obj[9]));
			view.setConditionType(obj[10]==null?"":String.valueOf(obj[10]));
			view.setConditionContent(obj[11]==null?"":String.valueOf(obj[11]));
			viewList.add(view);
		}
		return viewList;
	}
	
	/**
	 * 查询字段规则检查列表
	 * @return
	 * @throws Exception
	 */
	public List<GwRuleCheckServiceFieldView> searchCheckRuleServiceFieldConfig(Long userId,Long serviceId) throws Exception{
		//根据userId、modelId查出用户配置的模型字段检查规则
		String sql = "select t.reorder,t.check_type,d.dict_value,t.check_rule,t.field_code,t.field_name from gw_service_check_rule t " +
				" left join gw_sys_dict d on d.dict_code = 'DICT_CHECK_RULE_TYPE' and d.dict_key = t.check_type " +
				" where t.user_id = ? " +
				" and t.service_id = ? " +
				" and t.check_batch = (select max(check_batch) from gw_service_check_rule where user_id = ? and service_id = ?)" +
				" order by t.reorder ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{userId,serviceId,userId,serviceId}, null);
		GwRuleCheckServiceFieldView view = null;
		List<GwRuleCheckServiceFieldView> viewList = new ArrayList<GwRuleCheckServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwRuleCheckServiceFieldView();
			view.setRecorder(obj[0]==null?null:String.valueOf(obj[0]));
			view.setCheckType(obj[1]==null?null:String.valueOf(obj[1]));
			view.setDictValue(obj[2]==null?null:String.valueOf(obj[2]));
			view.setCheckRule(obj[3]==null?null:String.valueOf(obj[3]));
			view.setFieldCode(obj[4]==null?null:String.valueOf(obj[4]));
			view.setFieldName(obj[5]==null?null:String.valueOf(obj[5]));
			viewList.add(view);
		}
		return viewList;
	}
	
	//保存不合规数据记录
	public void saveServiceCheckRecode(GwServiceCheckRecordView gwServiceCheckRecordView)throws Exception{
		String sql = "insert into gw_service_check_record(record_id,row_id,task_id,service_id,check_type,check_rule,FIELD_SORT) " +
				"values(seq_gw_service_check_record.nextval,?,?,?,?,?,?)";
		super.executeSql(sql, new Object[]{gwServiceCheckRecordView.getRowId(),gwServiceCheckRecordView.getTaskId(),gwServiceCheckRecordView.getServiceId(),
				gwServiceCheckRecordView.getCheckType(),gwServiceCheckRecordView.getCheckRule(),gwServiceCheckRecordView.getFieldSort()});
	}
	
	//清除该任务警告信息
	public void deleteServiceCheckWarn(Long taskId)throws Exception{
		String sql = "delete gw_service_check_warn where task_id=? ";
		super.executeSql(sql, new Object[]{taskId});
	}
	
	//插入多列警告信息
	public void insertServiceCheckWarn(int warnType,Long rowNum,Long taskId,String str)throws Exception{
		String sql = "insert into gw_service_check_warn(warn_id,warn_type,warn_row,task_id,row_data) " +
				"values(seq_gw_service_check_warn.nextval,?,?,?,?)";
		super.executeSql(sql, new Object[]{warnType,rowNum,taskId,str});
	}
	
	/**
	 * 查询服务所属模型下其他服务的字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleOtherServiceFieldList(Long userId,GwServiceVO service) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.field_id,f.service_id,f.field_code,f.field_name,f.field_type,f.field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule,h.desen_type " +
				"from gw_service_field f " +
				"inner join gw_model_data_fetch h on h.service_id=f.service_id and h.user_id=? and h.audit_status=2 " +
				"inner join gw_service s on s.service_id=f.service_id and s.model_id=? and s.status=1 " +
				"left join (select * from gw_desen_service_field dmf where dmf.batch = (select max(batch) from gw_desen_service_field where user_Id = "+userId+" and service_Id = "+service.getServiceId()+")) dmf " +
				"on dmf.field_id=f.field_id and dmf.user_id=h.user_id " +
				"where gather_type=1 " + //只查输出
				"and f.service_id in " +
				"(select us.service_id from gw_user_service us " +	
				"inner join gw_user org on org.user_type=? and org.user_id=us.user_id and org.status=1 and org.confirm_status=2 " + //查询机构用户拥有的service
				"inner join gw_user u on u.org_id = org.org_id and u.user_type=? "+
				"where u.user_id=h.user_id and us.service_id<>?) " +	
				"order by s.service_id,f.field_code ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{userId,service.getModelId(),GwUserType.ORG_USER,GwUserType.DATA_USER,service.getServiceId()}, null);
		GwDesenRuleServiceFieldView view = null;
		List<GwDesenRuleServiceFieldView> viewList = new ArrayList<GwDesenRuleServiceFieldView>();
		for (Object[] obj : list) {
			view = new GwDesenRuleServiceFieldView();
			view.setUserId(userId);
			view.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setServiceId(obj[1]==null?null:Long.parseLong(String.valueOf(obj[1])));
			view.setFieldCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setFieldName(obj[3]==null?"":String.valueOf(obj[3]));
			view.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			view.setFieldDesc(obj[5]==null?"":String.valueOf(obj[5]));
			view.setFieldDeseId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setRuleType(obj[7]==null?"":String.valueOf(obj[7]));
			view.setRuleContent(obj[8]==null?"":String.valueOf(obj[8]));
			view.setReplaceContent(obj[9]==null?"":String.valueOf(obj[9]));
			view.setConditionType(obj[10]==null?"":String.valueOf(obj[10]));
			view.setConditionContent(obj[11]==null?"":String.valueOf(obj[11]));
			view.setCheckType(obj[12]==null?"":String.valueOf(obj[12]));
			view.setCheckRule(obj[13]==null?"":String.valueOf(obj[13]));
			view.setDesenType(obj[14]==null?"":String.valueOf(obj[14]));
			viewList.add(view);
		}
		return viewList;
	}
	
	/**
	 * 根据主键id查询脱敏列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenServiceFieldVO> searchDesenServiceFieldList(String fieldDesenIds) throws Exception{
		String hql = "from GwDesenServiceFieldVO where fieldDeseId in ("+fieldDesenIds+")";	
		return super.findByHql(hql, null);
	}
	
	/**
	 * 将userId、serviceId的服务字段脱敏配置都删除
	 * @param modelId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int deleteDesenServiceField(Long serviceId,Long userId) throws Exception{
		String sql = "delete Gw_Desen_service_Field where user_Id=? and service_Id=? ";
		return super.executeSql(sql, new Object[]{userId,serviceId});
	}
	
	public List<DesenServiceFieldView> searchListByServiceIdAndUserId(Long serviceId,Long userId) throws Exception{
		log.debug("finding GwDesenServiceFieldVO instance with property: "
				+"serviceId: " + serviceId + ", userId: " + userId);
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("select t.field_dese_id as fieldDeseId,t.service_id as serviceId,t.user_id as userId,t.field_id as fieldId,t.rule_type as ruleType, d.dict_value as ruleName,")
			.append("t.rule_content as ruleContent,t.replace_content as replaceContent,t.condition_type as conditionType,t.condition_content as conditionContent,f.field_code as fieldCode,f.field_name as fieldName,")
			.append("t.check_type,r.dict_value,t.check_rule ")
			.append("from gw_desen_service_field t ")
			.append("inner join gw_service_field f on t.field_id=f.field_id ")
			.append("left join GW_SYS_DICT d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ")
			.append("left join GW_SYS_DICT r on r.dict_code='DICT_CHECK_RULE_TYPE' and r.dict_key=t.check_type ")
			.append("where t.service_id=? and t.user_id=? and t.batch=(select max(batch) from gw_desen_service_field where user_Id=? and service_Id=?)");
			
			List<Object[]> list = super.findListBySql(queryString.toString(),new Object[]{serviceId,userId,userId,serviceId},null);
			List<DesenServiceFieldView> planList = new ArrayList<DesenServiceFieldView>();
			if(list != null){
				DesenServiceFieldView vo = null;
				for(Object[] obj : list){
					vo = new DesenServiceFieldView();
					vo.setFieldDeseId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
					vo.setServiceId(obj[1]==null?null:Long.valueOf(String.valueOf(obj[1])));
					vo.setUserId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
					vo.setFieldId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
					vo.setRuleType(obj[4]==null?null:String.valueOf(obj[4]));
					vo.setRuleName(obj[5]==null?null:String.valueOf(obj[5]));
					vo.setRuleContent(obj[6]==null?null:String.valueOf(obj[6]));
					vo.setReplaceContent(obj[7]==null?null:String.valueOf(obj[7]));
					vo.setConditionType(obj[8]==null?null:String.valueOf(obj[8]));
					vo.setConditionContent(obj[9]==null?null:String.valueOf(obj[9]));
					vo.setFieldCode(obj[10]==null?null:String.valueOf(obj[10]));
					vo.setFieldName(obj[11]==null?null:String.valueOf(obj[11]));
					vo.setCheckType(obj[12]==null?null:String.valueOf(obj[12]));
					vo.setCheckTypeName(obj[13]==null?null:String.valueOf(obj[13]));
					vo.setCheckRule(obj[14]==null?null:String.valueOf(obj[14]));
					planList.add(vo);
				}
			}
			return planList;
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public Integer searchCheckingTaskCount(Long serviceId,Long userId){
		String sql = "select count(1) from gw_model_data_fetch_task where data_progress_status=7 and task_status='1' and service_id=? and user_id=?";
		return super.findIntBySql(sql, new Object[]{serviceId,userId});
	}
	
	public Integer searchAuditingTaskCount(Long serviceId,Long userId){
		String sql = "select count(1) from gw_model_data_fetch_task where data_progress_status=8 and task_status='1' and service_id=? and user_id=?";
		return super.findIntBySql(sql, new Object[]{serviceId,userId});
	}
	
	public int searchMaxBatch(Long userId,Long serviceId){
		String sql = "select max(batch) from Gw_Desen_Service_Field where user_Id=? and service_Id=?";
		return super.findIntBySql(sql, new Object[]{userId,serviceId});
	}
}