package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwSysDictVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwSysDict entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwSysDictVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwSysDictDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwSysDictDAO.class);

	// property constants

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwSysDict instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwSysDict as model where model."
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
		log.debug("finding all GwSysDict instances");
		try {
			String queryString = "from GwSysDict";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<GwSysDictVO> searchSysDictList() throws Exception{
		String hql = "from GwSysDictVO order by reorder";
		return super.findByHql(hql, null);
	}
	
	@SuppressWarnings("unchecked")
	public GwSysDictVO searchSysDict(String dictCode,String dictKey) throws Exception{
		String hql = "from GwSysDictVO where dictCode=? and dictKey=?";
		List<GwSysDictVO> list = super.findByHql(hql, new Object[]{dictCode,dictKey});
		return list!=null&&list.size()>0?list.get(0):null;
	}
}