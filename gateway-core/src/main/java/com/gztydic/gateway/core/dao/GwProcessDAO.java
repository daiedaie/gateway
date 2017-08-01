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
import com.gztydic.gateway.core.vo.GwProcessVO;


@Repository
public class GwProcessDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwProcessDAO.class);
	// property constants
	public static final String PROCESS_ID = "processId";
	public static final String PROCESS_TYPE = "processType";
	public static final String STATUS = "status";
	//public static final Date CREATE_TIME = "createTime";
	//public static final Date END_TIME = "endTime";

	public GwProcessVO findById(java.lang.Long id) {
		log.debug("getting GwProcessVO instance with id: " + id);
		try {
			GwProcessVO instance = (GwProcessVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwProcessVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

		
	public PageObject findAllByPage(String processType,String status,PageObject pageObject) throws Exception{
        log.debug("finding all GwProcessVO instances by processType、status："+
        		processType+","+status);
        try {
                List<Object> paramsList = new ArrayList<Object>();
    			StringBuffer queryString = new StringBuffer();
    			queryString.append("from GwProcessVO as process where 1=1 ");
                if(StringUtils.isNotBlank(processType)){
    				queryString.append(" and process.processType = ? ");
    				paramsList.add(processType.trim());
    			}
    			if(StringUtils.isNotBlank(status)){
    				queryString.append(" and process.status = ? ");
    				paramsList.add(status.trim());
    			}
    			//目前只对申请取数、修改规则审批、服务任务流程进度提供查看
    			queryString.append("and process.processType in ('1','2','3') ");
    			queryString.append(" order by createTime desc");
    			return super.findByPage(GwProcessVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
        } catch (RuntimeException re) {
                log.error("find all failed", re);
                throw re;
        }
	}
	
	@SuppressWarnings("unchecked")
	public List<GwProcessVO> findUserProcessByPage(Long processId,String processType,String status,PageObject pageObject) throws Exception{
		
		log.debug("finding all GwProcessVO instances by processType、status："+
        		processType+","+status);
        try {
                List<Object> paramsList = new ArrayList<Object>();
    			StringBuffer queryString = new StringBuffer();
    			queryString.append("from GwProcessVO as process where 1=1 ");
                if(StringUtils.isNotBlank(processType)){
    				queryString.append(" and process.processType = ? ");
    				paramsList.add(processType.trim());
    			}
    			if(StringUtils.isNotBlank(status)){
    				queryString.append(" and process.status = ? ");
    				paramsList.add(status.trim());
    			}
    			if(processId != null){
    				queryString.append(" and process.processId = ? ");
    				paramsList.add(processId);
    			}
    			//目前只对申请取数、修改规则审批、服务任务流程进度提供查看
    			queryString.append("and process.processType in ('1','2','3') ");
    			queryString.append(" order by createTime desc");
    			return super.findByList(queryString.toString(), pageObject, null, paramsList.toArray());
        } catch (RuntimeException re) {
                log.error("find all failed", re);
                throw re;
        }
	}

	public PageObject searchProcessByUserId(Long ownId, PageObject pageObject,String processType,String status) throws Exception{

        try {
                List<Object> paramsList = new ArrayList<Object>();
    			StringBuffer queryString = new StringBuffer();	
    			queryString.append("from GwProcessVO as process where 1=1 ");            
    			
    			if(processType!=""&&processType!=null){
    				paramsList.add(processType);
        			queryString.append(" and process.processType = ? ");
    			}
    			if(status!=""&&status!=null){
    				paramsList.add(status);
        			queryString.append(" and process.status = ? ");
    			}
    			
    			queryString.append(" and process.ownId = ? ");
    			paramsList.add(ownId);
    			queryString.append(" ORDER BY process.processId DESC");
    			return super.findByPage(GwProcessVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
        } catch (RuntimeException re) {
                log.error("find all failed", re);
                throw re;
        }
	}

	
	public List<GwProcessVO> findByCreateTime(String createTime){
		log.debug("finding GwProcess instance with property: create_time value:"
				+ createTime );
		
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwProcessVO as process where to_char(process.createTime,'yyyyMM') in (");
			queryString.append(createTime);
			queryString.append(" ) ");			
			Query queryObject = getCurrentSession().createQuery(queryString.toString());
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

}


