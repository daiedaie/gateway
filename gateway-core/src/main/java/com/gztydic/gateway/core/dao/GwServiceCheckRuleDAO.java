package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwServiceFieldVO entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwServiceCheckRuleVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwServiceCheckRuleDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwServiceCheckRuleDAO.class);
	
	/**查询最新的检查规则*/
	public List<GwServiceCheckRuleVO> searchLastCheckRuleList(Long userId,Long serviceId) throws Exception{
		String hql = "from GwServiceCheckRuleVO where checkBatch=(select max(checkBatch) from GwServiceCheckRuleVO where userId=? and serviceId=?)  order by reorder";
		return super.findByHql(hql, new Object[]{userId,serviceId});
	}
	/**查询最新的待审核检查规则*/
	public List<GwServiceCheckRuleAuditVO> searchLastCheckRuleAuditList(Long userId,Long serviceId) throws Exception{
		String hql = "from GwServiceCheckRuleAuditVO where checkBatch=(select max(checkBatch) from GwServiceCheckRuleAuditVO where userId=? and serviceId=?)  order by reorder";
		return super.findByHql(hql, new Object[]{userId,serviceId});
	}
	/**查询最新的检查规则返回page*/
	public PageObject searchLastCheckRule(Long userId,Long serviceId,PageObject pageObject) throws Exception{
		log.debug("finding all GwServiceCheckRuleVO instances by userId、serviceId："+
				userId+","+serviceId);
		try {
            List<Object> paramsList = new ArrayList<Object>();
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwServiceCheckRuleVO as serviceRule where 1=1 ");
            if(StringUtils.isNotBlank(userId.toString())){
				queryString.append(" and serviceRule.userId = ? ");
				paramsList.add(userId);
			}
			if(StringUtils.isNotBlank(serviceId.toString())){
				queryString.append(" and serviceRule.serviceId = ? ");
				paramsList.add(serviceId);
			}
			return super.findByPage(GwProcessVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
	    } catch (RuntimeException re) {
	            log.error("find all failed", re);
	            throw re;
	    }
	}
	
	public List<GwServiceCheckRuleVO> searchCheckRuleList(Long checkBath) throws Exception{
		String hql = "from GwServiceCheckRuleVO where checkBatch=? order by reorder";
		return super.findByHql(hql, new Object[]{checkBath});
	}
	
	public int searchCheckRuleListCount(long checkBath) throws Exception{
		String sql="select count(1) from gw_service_check_rule where check_batch="+checkBath;
		return super.findIntBySql(sql, null);
	}
	
	//查询warnRow，rowData
	public Map searchServiceCheckWarn(GwModelDataFetchTaskVO taskVO,long warnType) throws Exception{
		Map map=new HashMap();
		String sql="select warn_row,row_data  from gw_service_check_warn where task_id ="+taskVO.getTaskId()+" and warn_type="+warnType;
		List<Object[]> rowDataList=super.findListBySql(sql, null, null);
		if(rowDataList.size()==0){
			map.put("warnRow", "");
			map.put("rowData", "");
		}else{
			Object[] serviceCheckWarn=rowDataList.get(0);
			map.put("warnRow", serviceCheckWarn[0]);
			map.put("rowData", serviceCheckWarn[1]);
		}
		return map;
	}
	
	//根据checkBatch查询字段，组装表头
	public List<GwServiceCheckRuleVO> searchServiceFieldCode(GwModelDataFetchTaskVO taskVO)throws Exception{
		String sql="select * from gw_service_check_rule where check_batch="+taskVO.getCheckBatch()+" order by reorder";
		return super.findListBySql(sql, null, GwServiceCheckRuleVO.class);
	}
	
	public int searchMaxBatch(Long userId,Long serviceId){
		String sql = "select max(check_Batch) from Gw_Service_Check_Rule where user_Id=? and service_Id=?";
		return super.findIntBySql(sql, new Object[]{userId,serviceId});
	}
}