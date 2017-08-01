package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceFieldVO entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceFieldVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceFieldDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceFieldDAO.class);
	// property constants
	public static final String GATHER_ID = "gatherId";
	public static final String FIELD_CODE = "fieldCode";
	public static final String FIELD_NAME = "fieldName";
	public static final String FIELD_TYPE = "fieldType";
	public static final String NULLABLE = "nullable";
	public static final String FIELD_DESC = "fieldDesc";

	public GwServiceFieldVO findById(java.lang.Long id) {
		log.debug("getting GwServiceFieldVO instance with id: " + id);
		try {
			GwServiceFieldVO instance = (GwServiceFieldVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwServiceFieldVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwServiceFieldVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwServiceFieldVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByGatherId(Object gatherId) {
		return findByProperty(GATHER_ID, gatherId);
	}

	public List findByFieldCode(Object fieldCode) {
		return findByProperty(FIELD_CODE, fieldCode);
	}

	public List findByFieldName(Object fieldName) {
		return findByProperty(FIELD_NAME, fieldName);
	}

	public List findByFieldType(Object fieldType) {
		return findByProperty(FIELD_TYPE, fieldType);
	}

	public List findByNullable(Object nullable) {
		return findByProperty(NULLABLE, nullable);
	}

	public List findByFieldDesc(Object fieldDesc) {
		return findByProperty(FIELD_DESC, fieldDesc);
	}

	public List findAll() {
		log.debug("finding all GwServiceFieldVO instances");
		try {
			String queryString = "from GwServiceFieldVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public GwServiceFieldVO searchServiceField(Long serviceId,String fieldCode) throws Exception{
		String hql = "from GwServiceFieldVO where serviceId=? and fieldCode=?";
		List<GwServiceFieldVO> fieldList = super.findByHql(hql, new Object[]{serviceId,fieldCode});
		return fieldList.size() > 0 ? fieldList.get(0) : null;
	}
	
	public int deleteServiceField(String fieldCodes,Long serviceId) throws Exception{
		String sql = "delete gw_service_field where service_id=? and field_code not in ("+fieldCodes+")";
		return super.executeSql(sql, new Object[]{serviceId});
	}
	
	public int deleteServiceFieldByreorder(String reorders,Long serviceId,String gatherType) throws Exception{
		String sql = "delete gw_service_field where service_id=? and reorder in ("+reorders+") and gather_type=?";
		return super.executeSql(sql, new Object[]{serviceId,gatherType});
	}
	
	//查询服务输出字段
	public List<GwServiceFieldVO> searchServiceOutField(Long serviceId) throws Exception{
		String hql = "from GwServiceFieldVO where GATHER_TYPE=1 and SERVICE_ID=? order by fieldId";
		return super.findByHql(hql, new Object[]{serviceId});
	}
}