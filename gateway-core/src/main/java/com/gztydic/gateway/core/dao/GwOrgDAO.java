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
import com.gztydic.gateway.core.view.OrgView;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * A data access object (DAO) providing persistence and search support for GwOrg
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwOrgVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwOrgDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwOrgDAO.class);
	// property constants
	public static final String ORG_NAME = "orgName";
	public static final String ORG_HEAD_NAME = "orgHeadName";
	public static final String CERT_TYPE = "certType";
	public static final String CERT_NO = "certNo";
	public static final String REG_CODE = "regCode";
	public static final String ORG_ADDR = "orgAddr";
	public static final String ORG_TEL = "orgTel";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";
	public static final String RECORD_CODE = "recordCode";

	public GwOrgVO findById(java.lang.Long id) {
		log.debug("getting GwOrgVO instance with id: " + id);
		try {
			GwOrgVO instance = (GwOrgVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwOrgVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwOrgVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwOrgVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOrgName(Object orgName) {
		return findByProperty(ORG_NAME, orgName);
	}

	public List findByOrgHeadName(Object orgHeadName) {
		return findByProperty(ORG_HEAD_NAME, orgHeadName);
	}

	public List findByCertType(Object certType) {
		return findByProperty(CERT_TYPE, certType);
	}

	public List findByCertNo(Object certNo) {
		return findByProperty(CERT_NO, certNo);
	}

	public List findByRegCode(Object regCode) {
		return findByProperty(REG_CODE, regCode);
	}

	public List findByOrgAddr(Object orgAddr) {
		return findByProperty(ORG_ADDR, orgAddr);
	}

	public List findByOrgTel(Object orgTel) {
		return findByProperty(ORG_TEL, orgTel);
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

	public List findByRecordCode(Object recordCode) {
		return findByProperty(RECORD_CODE, recordCode);
	}

	public List findAll() {
		log.debug("finding all GwOrgVO instances");
		try {
			String queryString = "from GwOrgVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List findByRepetOrgName(String orgName, Long orgId) {
        log.debug("finding GwOrgVO instance with orgName: " + orgName
                        + ", not in orgId: " + orgId);
        try {
                String queryString = "from GwOrgVO as org where org.orgName = ? and orgId != ?";
                                
                Query queryObject = getCurrentSession().createQuery(queryString);
                queryObject.setParameter(0, orgName);
                queryObject.setParameter(1, orgId);
                return queryObject.list();
        } catch (RuntimeException re) {
                log.error("find by property name failed", re);
                throw re;
        }
	}
	public PageObject searchOrgList(GwOrgVO vo,PageObject pageObject,GwUserVO userVO)throws Exception{
		String sql="select o.org_id,o.org_name,o.org_head_name,o.cert_type,o.cert_no,u.user_id,u.login_name,u.CONFIRM_STATUS from gw_org o left join gw_user u on o.org_id=u.org_id where u.user_type=? and u.status=1 ";
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(userVO.getUserType());
		if(vo!=null){
			if ((!StringUtils.isBlank(vo.getOrgName()))) {
		        sql+=" and o.org_name like ? ";
		        paramList.add("%"+vo.getOrgName()+"%");
		    }
			/*if ((!StringUtils.isBlank(vo.getCertType()))) {
				 sql+=" and o.cert_type=? ";
		        paramList.add(vo.getCertType());
		    }*/
			if ((!StringUtils.isBlank(vo.getCertNo()))) {
				 sql+=" and o.cert_no like ? ";
		        paramList.add("%"+vo.getCertNo()+"%");
		    }
			if ((!StringUtils.isBlank(vo.getOrgHeadName()))) {
				 sql+=" and o.org_head_name like ? ";
		        paramList.add("%"+vo.getOrgHeadName()+"%");
		    }
		}
		return super.findListBySql(sql, paramList.toArray(), pageObject, null);
	}
}