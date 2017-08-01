package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwServiceFieldDictVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceFieldDictVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceFieldDictVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceFieldDictDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceFieldDictDAO.class);

	// property constants

	public GwServiceFieldDictVO findById(GwServiceFieldDictVO id) {
		log.debug("getting GwServiceFieldDictVO instance with id: " + id);
		try {
			GwServiceFieldDictVO instance = (GwServiceFieldDictVO) getSession().get(
					"com.gztydic.gateway.core.vo.GwServiceFieldDictVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwServiceFieldDictVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwServiceFieldDictVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all GwServiceFieldDictVO instances");
		try {
			String queryString = "from GwServiceFieldDictVO";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/**
	 * 查询字段与字典关联表数据
	 * 优先根据模型查询，否则根据服务
	 * @param service
	 * @return
	 * @throws Exception
	 */
	public List searchList(Long userId,GwServiceVO serviceVO) throws Exception{
		String hql = "from GwServiceFieldDictVO where userId=? and fieldId in ";
		Object[] params = null;
		if(serviceVO.getModelId() != null){
			hql += "(select fieldId from GwServiceFieldVO where serviceId in (select serviceId from GwServiceVO where modelId=?))";
			params = new Object[]{userId,serviceVO.getModelId()};
		}else {
			hql += "(select fieldId from GwServiceFieldVO where serviceId=?)";
			params = new Object[]{userId,serviceVO.getServiceId()};
		}
		return super.findByHql(hql, params);
	}
	
	public List searchListByBatch(Long batch) throws Exception{
		String hql = "from GwServiceFieldDictVO f where f.desenId in (select d.fieldDeseId from GwDesenServiceFieldVO d where d.batch=?)";
		return super.findByHql(hql, new Object[]{batch});
	}
	
	public void deleteByServiceId(Long serviceId) throws Exception{
		String sql = "delete Gw_Service_Field_Dict where field_id in " +
				"(select field_Id from Gw_Service_Field where service_Id=?)";
		super.executeSql(sql, new Long[]{serviceId});
	}
}