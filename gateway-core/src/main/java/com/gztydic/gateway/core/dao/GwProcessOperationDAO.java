package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;


@Repository
public class GwProcessOperationDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwProcessOperationDAO.class);
	// property constants
	public static final String OPERATION_USER = "operationUser";
	public static final String OPERATION_CONTENT = "operationContent";

	public GwProcessOperationVO findById(java.lang.Long id) {
		log.debug("getting GwProcessOperationVO instance with id: " + id);
		try {
			GwProcessOperationVO instance = (GwProcessOperationVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwProcessOperationVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public PageObject findByProcessId(java.lang.Long processId,PageObject pageObject) throws Exception {
		log.debug("getting GwProcessOperationVO instance with id: " + processId);
		try {			
			List<Object> paramsList = new ArrayList<Object>();
 			StringBuffer queryString = new StringBuffer();
 			queryString.append("from GwProcessOperationVO as process where 1=1 ");
             if(StringUtils.isNotBlank(processId.toString())){
 				queryString.append(" and process.processId = ? ");
 				paramsList.add(processId);
 			}
 			pageObject.setDefaultSort("step asc");
 			return super.findByPage(GwProcessOperationVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
		} catch (RuntimeException re) {
			log.error("get process operation failed", re);
			throw re;
		}
	}
	
	public List findByUserId(java.lang.Long userId) throws Exception {		
		String sql="select distinct t.process_id from GW_PROCESS_OPERATION t where t.user_id=? ORDER BY PROCESS_ID DESC";
		return super.findListBySql(sql, new String[]{userId.toString()}, null);
	}

	public GwProcessOperationVO findByProcessIdStep(Long processId, String step) throws Exception {
		String sql="select * from GW_PROCESS_OPERATION t where t.process_id=? and  t.step=?";
		List<GwProcessOperationVO> list = super.findListBySql(sql, new Object[]{processId,step}, GwProcessOperationVO.class);
		return list.size() > 0 ? list.get(0) : null;

	}
	
}