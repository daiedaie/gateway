package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwServiceDictView;
import com.gztydic.gateway.core.vo.GwServiceDictVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceDictVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceDictVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceDictDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceDictDAO.class);

	// property constants

	public GwServiceDictVO findById(
			com.gztydic.gateway.core.vo.GwServiceDictVO id) {
		log.debug("getting GwServiceDictVO instance with id: " + id);
		try {
			GwServiceDictVO instance = (GwServiceDictVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwServiceDictVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwServiceDictVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwServiceDictVO as model where model."
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
		log.debug("finding all GwServiceDictVO instances");
		try {
			String queryString = "from GwServiceDictVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	//根据serviceId查询service_field关联的的字典
	public Map<Long, List<GwServiceDictView>> searchByServiceId(Long serviceId) throws Exception {
		String sql = "select f.field_id,d.dict_code,d.dict_name,d.dict_key,d.dict_value from Gw_Service_Dict d " +
				"inner join Gw_Service_Field_Dict f on f.dict_Code = d.dict_Code " +
				"where f.field_id in (select field_id from gw_service_field where service_id=?) " +
				"order by d.reorder";
		List<Object[]> list = super.findListBySql(sql, new Object[]{serviceId}, null);
		GwServiceDictView v = null;
		Map<Long, List<GwServiceDictView>> map = new HashMap<Long, List<GwServiceDictView>>();
		for (Object[] obj : list) {
			v = new GwServiceDictView();
			v.setFieldId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			v.setDictCode(obj[1]==null?"":String.valueOf(obj[1]));
			v.setDictName(obj[2]==null?"":String.valueOf(obj[2]));
			v.setDictKey(obj[3]==null?"":String.valueOf(obj[3]));
			v.setDictValue(obj[4]==null?"":String.valueOf(obj[4]));
			
			List viewList = map.get(v.getFieldId());
			if(viewList == null) viewList = new ArrayList();
			viewList.add(v);
			map.put(v.getFieldId(), viewList);
		}
		return map;
	}
	
	//查询字典组
	public List<GwServiceDictVO> searchGroupDict() throws Exception{
		String sql = "select distinct dict_code,dict_name from gw_service_dict";
		List<Object[]> list = super.findListBySql(sql, null, null);
		List<GwServiceDictVO> dictList = new ArrayList<GwServiceDictVO>();
		for (Object[] obj : list) {
			GwServiceDictVO v = new GwServiceDictVO();
			if(obj[0] == null || obj[1]==null) continue;
			v.setDictCode(obj[0].toString());
			v.setDictName(obj[1].toString());
			dictList.add(v);
		}
		return dictList;
	}
	
	public List<GwServiceDictVO> searchDictList() throws Exception{
		String hql = "from GwServiceDictVO order by reorder";
		return super.findByHql(hql, null);
	}
}