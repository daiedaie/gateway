package com.gztydic.gateway.core.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceFieldVO entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceCheckRuleAuditDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceCheckRuleAuditDAO.class);
	
	/**查询指定的检查规则*/
	public List<GwServiceCheckRuleAuditVO> searchCheckRuleList(Long checkBatch) throws Exception{
		String hql = "from GwServiceCheckRuleAuditVO where checkBatch=? order by reorder";
		return super.findByHql(hql, new Object[]{checkBatch});
	}
	
}