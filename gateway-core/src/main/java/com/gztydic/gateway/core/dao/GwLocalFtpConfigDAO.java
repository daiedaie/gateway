package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.GwModelDataCycleDAO;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwCheckRuleVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
@Repository
public class GwLocalFtpConfigDAO extends HibernateGenericDao{
	private static final Log log = LogFactory.getLog(GwLocalFtpConfigDAO.class);
	
	public List findAll() {
		log.debug("finding all GwLocalFtpConfig instances");
		try {
			String queryString = "from GwSysFtpVo";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	public  List<GwSysFtpVo> findByType(String planState) throws Exception{
		log.debug("getting GwSysFtpVo instance with ftpType: " + planState);
		String sql="select * from GW_SYS_FTP t where t.ftp_type = ? ";
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(planState);
		List<Object[]>  list=  super.findListBySql(sql, paramList.toArray(),null);
		List<GwSysFtpVo> planList= new ArrayList<GwSysFtpVo>();
		if(list!=null){
			GwSysFtpVo gwSysFtpVo = null;
			for (Object[] obj : list){
				gwSysFtpVo = new GwSysFtpVo();
				gwSysFtpVo.setId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				gwSysFtpVo.setFtpIp(obj[0]==null?null:String.valueOf(obj[1]));
				gwSysFtpVo.setFtpType(obj[0]==null?null:String.valueOf(obj[2]));
				gwSysFtpVo.setFtpPort(obj[0]==null?null:String.valueOf(obj[3]));
				planList.add(gwSysFtpVo);
			}
		}
		return planList;
	}

}
