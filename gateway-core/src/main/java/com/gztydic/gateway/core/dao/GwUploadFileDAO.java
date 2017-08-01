package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwUploadFileVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUploadFileVO entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUploadFileVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUploadFileDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUploadFileDAO.class);
	// property constants
	public static final String REAL_NAME = "realName";
	public static final String FILE_PATH = "filePath";
	public static final String CREATE_USER = "createUser";
	public static final String FILE_TYPE = "fileType";

	public GwUploadFileVO findById(java.lang.Long id) {
		log.debug("getting GwUploadFileVO instance with id: " + id);
		try {
			GwUploadFileVO instance = (GwUploadFileVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwUploadFileVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUploadFileVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwUploadFileVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByRealName(Object realName) {
		return findByProperty(REAL_NAME, realName);
	}

	public List findByFilePath(Object filePath) {
		return findByProperty(FILE_PATH, filePath);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByFileType(Object fileType) {
		return findByProperty(FILE_TYPE, fileType);
	}

	public List findAll() {
		log.debug("finding all GwUploadFileVO instances");
		try {
			String queryString = "from GwUploadFileVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}


}