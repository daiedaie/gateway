package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModel entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModelDAO.class);
	// property constants
	public static final String MODEL_CODE = "modelCode";
	public static final String MODEL_NAME = "modelName";
	public static final String MODEL_DESC = "modelDesc";
	public static final String MODEL_INPUT = "modelInput";
	public static final String MODEL_OUTPUT = "modelOutput";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";

	public GwModelVO findById(java.lang.Long id) {
		log.debug("getting GwModel instance with id: " + id);
		try {
			GwModelVO instance = (GwModelVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwModelVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModel instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwModelVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByModelCode(Object modelCode) {
		return findByProperty(MODEL_CODE, modelCode);
	}

	public List findByModelName(Object modelName) {
		return findByProperty(MODEL_NAME, modelName);
	}

	public List findByModelDesc(Object modelDesc) {
		return findByProperty(MODEL_DESC, modelDesc);
	}

	public List findByModelInput(Object modelInput) {
		return findByProperty(MODEL_INPUT, modelInput);
	}

	public List findByModelOutput(Object modelOutput) {
		return findByProperty(MODEL_OUTPUT, modelOutput);
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
		log.debug("finding all GwModel instances");
		try {
			String queryString = "from GwModelVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据用户查询模型
	 * @param userId
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public PageObject searchModelListByUser(GwUserVO userVO,GwModelVO modelVO,PageObject pageObject) throws Exception{
		String modelSql = "select model_id from gw_user_model um " +
				"where um.user_id=? " +
				"union " +
				"select model_id from gw_role_model rm " +
				"where rm.role_code in (select ur.role_code from gw_user_role ur where ur.user_id=?)";
		
		String sql = "select * from Gw_Model m where m.model_Id in ("+modelSql+") ";
		List paramList = new ArrayList();
		paramList.add(userVO.getUserId());
		paramList.add(userVO.getUserId());
		if(modelVO != null){
			if(StringUtils.isNotBlank(modelVO.getModelCode())){
				sql += "and model_code like ? ";
				paramList.add("%"+modelVO.getModelCode()+"%");
			}
			if(StringUtils.isNotBlank(modelVO.getModelName())){
				sql += "and model_name like ? ";
				paramList.add("%"+modelVO.getModelName()+"%");
			}
		}
		return super.findListBySql(sql, paramList.toArray(), pageObject, GwModelVO.class);
	}
	

	public PageObject searchModelList(GwModelVO modelVO,PageObject pageObject) throws Exception{
		String hql = "from GwModelVO where 1=1 ";
		List paramList = new ArrayList();
		if(modelVO != null){
			if(StringUtils.isNotBlank(modelVO.getModelCode())){
				hql += "and modelCode like ? ";
				paramList.add("%"+modelVO.getModelCode()+"%");
			}
			if(StringUtils.isNotBlank(modelVO.getModelName())){
				hql += "and modelName like ? ";
				paramList.add("%"+modelVO.getModelName()+"%");
			}
		}
		return super.findByPage(GwModelVO.class, hql, pageObject, null, paramList.toArray());
	}
	
	//根据用户ID查找没有被授权的模型列表
	public List<GwModelVO> searchUnchoosetModelByUserId(Long userId) throws Exception{
		String hql = "from GwModelVO m where m.modelId not in (select modelId from GwUserModelVO r where r.userId=?)";
		return super.findByHql(hql, new Object[]{userId});
	}
	
	//根据用户ID查找被授权的模型列表
	public List<GwModelVO> searchChooseModelByUserId(Long userId) throws Exception{
		String hql = "from GwModelVO m where m.modelId in (select modelId from GwUserModelVO r where r.userId=?)";
		return super.findByHql(hql, new Object[]{userId});
	}
	
	//根据条件查询未被授权的用户模型
	public List<GwModelVO> searchModelListByModel(GwModelVO modelVO,GwUserVO userVO) throws Exception{
		String hql="from GwModelVO m where m.modelId not in (select modelId from GwUserModelVO r where r.userId="+userVO.getUserId()+")";
		List paramList = new ArrayList();
		if(modelVO!=null){
			if(!StringUtils.isBlank(modelVO.getModelCode())){
				hql+=" and m.modelCode=? ";
				paramList.add(modelVO.getModelCode());
			}
			if(!StringUtils.isBlank(modelVO.getModelName())){
				hql+=" and m.modelName like ? ";
				paramList.add("%"+modelVO.getModelName()+"%");
			}
				
		}
		return super.findByHql(hql, paramList.toArray());	
	}
	
	//查询模型列表并关联群组编码
	public List searchModelListByRoleCode(String roleCode) throws Exception{
		String sql = "select m.model_id,m.model_code,m.model_name,m.model_type,r.role_code from gw_model m " +
				"left join gw_role_model r on r.model_id=m.model_id and r.role_code=? " +
				"where m.status=1 order by m.create_time";	
		return super.findListBySql(sql, new String[]{roleCode}, null);
	}
	
}