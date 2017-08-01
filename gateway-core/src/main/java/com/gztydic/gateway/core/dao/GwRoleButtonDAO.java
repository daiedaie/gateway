package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwButtonView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwRoleButtonVO;
import com.gztydic.gateway.core.vo.GwRoleServiceVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwRoleButton entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwRoleButtonVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwRoleButtonDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwRoleButtonDAO.class);

	// property constants
	public static final String ROLE_CODE = "roleCode";
	
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwRoleButtonVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwRoleButtonVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List findByRoleCode(Object roleCode) {
		return findByProperty(ROLE_CODE, roleCode);
	}
	
	public List findAll() {
		log.debug("finding all GwRoleButtonVO instances");
		try {
			String queryString = "from GwRoleButtonVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public void deleteByRoleCode(String roleCode) throws Exception{
		String sql = "delete gw_role_button where role_code=?";
		super.executeSql(sql, new String[]{roleCode});
	}
	
	public List<GwButtonView> searchRoleBtnList(String roleCode,String funcCode)throws Exception{
		String sql=" select r.button_code,b.func_code,r.role_code " +
				"from gw_role_button r left join gw_button b " +
				"on r.button_code =b.button_code " +
				"where r.role_code=? and b.func_code=?";
		List<Object[]> list=super.findListBySql(sql, new String[]{roleCode,funcCode}, null);
		List<GwButtonView> roleBtnList=new ArrayList<GwButtonView>();
		GwButtonView buttonView;
		for(Object[] obj : list){
			buttonView=new GwButtonView();
			buttonView.setButtonCode(obj[0]==null?null:String.valueOf(obj[0]));
			buttonView.setFuncCode(obj[1]==null?null:String.valueOf(obj[1]));
			buttonView.setRoleCode(obj[2]==null?null:String.valueOf(obj[2]));
			roleBtnList.add(buttonView);
		}
		return roleBtnList;
	}
}