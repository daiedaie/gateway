package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldAuditVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwDesenServiceFieldAuditVO entities. Transaction control of the save(),
 * update() and delete() operations can directly support Spring
 * container-managed transactions or they can be augmented to handle
 * user-managed Spring transactions. Each of these methods provides additional
 * information for how to configure it for the desired type of transaction
 * control.
 * 
 * @see com.gztydic.gateway.core.vo.GwDesenServiceFieldAuditVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwDesenServiceFieldAuditDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwDesenServiceFieldAuditDAO.class);
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

	public GwDesenServiceFieldAuditVO findById(Long id) {
		log
				.debug("getting GwDesenServiceFieldAuditVO instance with id: "
						+ id);
		try {
			GwDesenServiceFieldAuditVO instance = (GwDesenServiceFieldAuditVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwDesenServiceFieldAuditVO",id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log
				.debug("finding GwDesenServiceFieldAuditVO instance with property: "
						+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwDesenServiceFieldAuditVO as model where model."
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
		log.debug("finding all GwDesenServiceFieldAuditVO instances");
		try {
			String queryString = "from GwDesenServiceFieldAuditVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 查询字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldList(Long userId,Long serviceId,Long batch) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.field_id,f.service_id,f.field_code,f.field_name,f.field_type,f.field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule " +
				"from gw_service_field f " +
				"left join (select * from gw_desen_service_field_audit dmf where dmf.batch=?) dmf " +
				"on dmf.field_id=f.field_id and dmf.user_id=? " +
				"where f.service_id=? and gather_type=1 " +	//只查输出
				"order by f.field_id ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{batch,userId,serviceId}, null);
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
	 * 查询108服务字段脱敏规则列表
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> search108DesenRuleServiceFieldList(Long userId,Long serviceId,Long batch,Long checkBatch) throws Exception{
		//根据userId、modelId查出用户配置的模型字段脱敏配置
		String sql = "select f.check_id,f.service_id,f.field_code,f.field_name,f.field_type,'' as field_desc,dmf.field_dese_id,dmf.rule_type,dmf.rule_content,dmf.replace_content," +
				"dmf.condition_type,dmf.condition_content,dmf.check_type,dmf.check_rule " +
				"from gw_service_check_rule_audit f " +
				"left join (select * from gw_desen_service_field_audit dmf where dmf.batch=?) dmf " +
				"on dmf.field_id=f.check_id and dmf.user_id=? " +
				"where f.service_id=? " +	//只查输出
				"and f.check_batch=?"+
				"order by f.check_id ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{batch,userId,serviceId,checkBatch}, null);
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
	
	
	public List<GwDesenServiceFieldAuditVO> searchDesenRuleList(Long batch) throws Exception{
		String hql = "from GwDesenServiceFieldAuditVO where batch=?";
		return super.findByHql(hql, new Object[]{batch});
	}
}