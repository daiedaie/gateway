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
import com.gztydic.gateway.core.vo.GwSmsVO;

@Repository
public class GwSmsDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwSmsDAO.class);

	public GwSmsVO findById(Long id) {
		log.debug("getting GwSmsVO instance with id: " + id);
		try {
			GwSmsVO instance = (GwSmsVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwSmsVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwSmsVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwSmsVO as config where config."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	public PageObject searchSmsList(GwSmsVO vo,PageObject obj) throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("from GwSmsVO s where 1=1 ");
		List<Object> params = new ArrayList<Object>(0);
		if(vo!=null){
			if ((!StringUtils.isBlank(vo.getSmsMobile()))) {
		        hql.append(" and s.smsMobile like ? ");
		        params.add("%"+vo.getSmsMobile()+"%");
		    }
			if ((!StringUtils.isBlank(vo.getSendStatus()))) {
				if("-1".equals(vo.getSendStatus())){
					hql.append(" and s.sendStatus = null ");
				}else{
					hql.append(" and s.sendStatus = ? ");
					 params.add(vo.getSendStatus());
				}
		    }
		}
		obj.setDefaultSort("s.createTime");
		return super.findByPage(vo.getClass(), hql.toString(), obj,null,params.toArray());
	}

	//查询待发送短信、发送失败且发送次数少于sendCount
	public List<GwSmsVO> searchWaitSendSms(int maxSendCount) throws Exception{
		String hql = "from GwSmsVO where sendStatus is null or (sendStatus=0 and sendCount<?)";
		return super.findByHql(hql, new Object[]{maxSendCount});
	}
}