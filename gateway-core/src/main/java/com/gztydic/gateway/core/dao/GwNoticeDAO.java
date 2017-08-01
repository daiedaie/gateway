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
import com.gztydic.gateway.core.vo.GwNoticeVO;

/**
 * A data access object (DAO) providing persistence and search support for GwOrg
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwNoticeVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwNoticeDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwNoticeDAO.class);
	// property constants
	public static final String NOTICE_ID = "noticeId";
	public static final String NOTICE_TITLE = "noticeTitle";
	public static final String NOTICE_CONTENT = "noticeContent";
	public static final String CREATE_USER = "createUser";
	public static final String CREATE_TIME = "createTime";

	public GwNoticeVO findById(java.lang.Long id) {
		log.debug("getting GwNoticeVO instance with id: " + id);
		try {
			GwNoticeVO instance = (GwNoticeVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwNoticeVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwNoticeVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwNoticeVO as model where model."
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
		log.debug("finding all GwNoticeVO instances");
		try {
			String queryString = "from GwNoticeVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List<GwNoticeVO> searchNoticeList(int maxRow) throws Exception{
		String sql = "select * from (select * from Gw_Notice order by create_Time desc) where rownum<=? ";
		return super.findListBySql(sql, new Object[]{maxRow}, GwNoticeVO.class);
	}
	
	public PageObject searchNoticeList(GwNoticeVO noticeVO, PageObject pageObject) throws Exception{
		String hql = "from GwNoticeVO where 1=1 ";
		List paramList = new ArrayList();
		if(noticeVO != null){
			if(StringUtils.isNotBlank(noticeVO.getNoticeTitle())){
				hql += "and noticeTitle like ?";
				paramList.add("%"+noticeVO.getNoticeTitle()+"%");
			}
		}
		return super.findByPage(GwNoticeVO.class, hql, pageObject, null , paramList.toArray());
	}
}