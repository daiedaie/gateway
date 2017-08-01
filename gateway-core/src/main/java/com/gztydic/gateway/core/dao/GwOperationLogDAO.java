package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwOperationLogVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwOperationLog entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwOperationLogVOVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwOperationLogDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwOperationLogDAO.class);
	// property constants
	public static final String OPERATION_USER = "operationUser";
	public static final String ACCEPT_USER = "acceptUser";
	public static final String OPERATION_TYPE = "operationType";
	public static final String OPERATION_CONTENT = "operationContent";

	public GwOperationLogVO findById(java.lang.Long id) {
		log.debug("getting GwOperationLog instance with id: " + id);
		try {
			GwOperationLogVO instance = (GwOperationLogVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwOperationLogVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwOperationLog instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwOperationLogVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOperationUser(Object operationUser) {
		return findByProperty(OPERATION_USER, operationUser);
	}

	public List findByAcceptUser(Object acceptUser) {
		return findByProperty(ACCEPT_USER, acceptUser);
	}

	public List findByOperationType(Object operationType) {
		return findByProperty(OPERATION_TYPE, operationType);
	}

	public List findByOperationContent(Object operationContent) {
		return findByProperty(OPERATION_CONTENT, operationContent);
	}

	public List findAll() {
		log.debug("finding all GwOperationLog instances");
		try {
			String queryString = "from GwOperationLogVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public PageObject findAllByPage(PageObject pageObject) throws Exception{
	        log.debug("finding all GwOperationLog instances");
	        try {
	                String queryString = "from GwOperationLogVO  order by operationTime desc";
	                return findByPage(GwOperationLogVO.class, queryString, pageObject, null, null);
	        } catch (RuntimeException re) {
	                log.error("find all failed", re);
	                throw re;
	        }
	}
	
	public PageObject findAllByPage(String operCode,String acceptCode,String operType,Date startDate,Date endDate,PageObject pageObject) throws Exception{
        log.debug("finding all GwOperationLogVO instances by operationUser、acceptUser、operType、startDate、endDate："+
        		operCode+","+acceptCode+","+operType+","+startDate+","+endDate);
        try {
                List<Object> paramsList = new ArrayList<Object>();
    			StringBuffer queryString = new StringBuffer();
    			queryString.append("from GwOperationLogVO as log where 1=1 ");
                if(StringUtils.isNotBlank(operCode)){
    				queryString.append(" and log.operationUser like ? ");
    				paramsList.add("%"+operCode.trim()+"%");
    			}
    			if(StringUtils.isNotBlank(acceptCode)){
    				queryString.append(" and log.acceptUser like ? ");
    				paramsList.add("%"+acceptCode.trim()+"%");
    			}
    			if(StringUtils.isNotBlank(operType)){
    				queryString.append(" and log.operationType = ? ");
    				paramsList.add(operType.trim());
    			}
    			// startDate,Date endDate
    			if(startDate != null){
    				queryString.append(" and log.operationTime >= ? ");
    				paramsList.add(startDate);
    			}
    			if(endDate != null){
    				queryString.append(" and log.operationTime <= ? ");
    				paramsList.add(endDate);
    			}
    			pageObject.setDefaultSort("operationTime desc");
    			return super.findByPage(GwOperationLogVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
        } catch (RuntimeException re) {
                log.error("find all failed", re);
                throw re;
        }
	}
}