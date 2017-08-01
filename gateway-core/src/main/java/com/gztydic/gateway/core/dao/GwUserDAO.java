package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.UserView;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.sun.java_cup.internal.runtime.virtual_parse_stack;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwUser entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwUserVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwUserDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwUserDAO.class);
	// property constants
	public static final String ORG_ID = "orgId";
	public static final String USER_TYPE = "userType";
	public static final String USER_NAME = "userName";
	public static final String LOGIN_NAME = "loginName";
	public static final String LOGIN_PWD = "loginPwd";
	public static final String TOKEN = "token";
	public static final String CERT_TYPE = "certType";
	public static final String CERT_NO = "certNo";
	public static final String EMAIL = "email";
	public static final String MOBLIE = "moblie";
	public static final String ADDR = "addr";
	public static final String CONFIRM_STATUS = "confirmStatus";
	public static final String ONLINE_STATUS = "onlineStatus";
	public static final String RUN_LEVEL = "runLevel";
	public static final String STATUS = "status";
	public static final String CREATOR = "creator";
	public static final String MODIFIER = "modifier";
	public static final String REMARK = "remark";
	public static final String RECORD_CODE = "recordCode";

	public GwUserVO findById(java.lang.Long id) {
		log.debug("getting GwUser instance with id: " + id);
		try {
			GwUserVO instance = (GwUserVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwUserVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwUser instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwUserVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByOrgId(Object orgId) {
		return findByProperty(ORG_ID, orgId);
	}

	public List findByUserType(Object userType) {
		return findByProperty(USER_TYPE, userType);
	}

	public List findByUserName(Object userName) {
		return findByProperty(USER_NAME, userName);
	}

	public List findByLoginName(Object loginName) {
		return findByProperty(LOGIN_NAME, loginName);
	}

	public List findByLoginPwd(Object loginPwd) {
		return findByProperty(LOGIN_PWD, loginPwd);
	}

	public List findByToken(Object token) {
		return findByProperty(TOKEN, token);
	}

	public List findByCertType(Object certType) {
		return findByProperty(CERT_TYPE, certType);
	}

	public List findByCertNo(Object certNo) {
		return findByProperty(CERT_NO, certNo);
	}

	public List findByEmail(Object email) {
		return findByProperty(EMAIL, email);
	}

	public List findByMoblie(Object moblie) {
		return findByProperty(MOBLIE, moblie);
	}

	public List findByAddr(Object addr) {
		return findByProperty(ADDR, addr);
	}

	public List findByConfirmStatus(Object confirmStatus) {
		return findByProperty(CONFIRM_STATUS, confirmStatus);
	}

	public List findByOnlineStatus(Object onlineStatus) {
		return findByProperty(ONLINE_STATUS, onlineStatus);
	}

	public List findByRunLevel(Object runLevel) {
		return findByProperty(RUN_LEVEL, runLevel);
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
		log.debug("finding all GwUser instances");
		try {
			String queryString = "from GwUser";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List searchOrgUser(Long orgId) throws Exception{
		String hql="from GwUserVO where 1=1 and orgId=? and userType=? ";
		Object[] obj=new Object[2];
		obj[0]=new Long(orgId);
		obj[1]=GwUserType.ORG_USER;
		return super.findByHql(hql, obj);
	}
	public List<GwUserVO> searchDataUser(Long orgId) throws Exception{
		String hql="from GwUserVO where 1=1 and orgId=? and userType=? ";
		Object[] obj=new Object[2];
		obj[0]=new Long(orgId);
		obj[1]=GwUserType.DATA_USER;
		return super.findByHql(hql, obj);
	}
	public List searchUserList(GwUserVO user,GwOrgVO org,PageObject pageObject) throws Exception{
		String sql =" select u.login_name,u.user_name,u.confirm_status,u.user_type,o.org_name,u.online_status,u.user_id, u.org_id from gw_user u left join gw_org o on o.org_id = u.org_id where u.status=? ";
		List<Object> params = new ArrayList<Object>(0);
		params.add(CommonState.VALID);
		if(user != null){
			if (!StringUtils.isBlank(user.getLoginName())) {
				sql+=" and u.login_name like ? ";
				params.add("%"+user.getLoginName()+"%");
			}
			if (!StringUtils.isBlank(user.getUserName())) {
				sql+=" and u.user_name like ? ";
				params.add("%"+user.getUserName()+"%");
			}
			if (!StringUtils.isBlank(user.getUserType())) {
				sql+=" and u.user_type = ? ";
				params.add(user.getUserType());
			}
			if (!StringUtils.isBlank(user.getConfirmStatus())) {
				sql+=" and u.confirm_status= ? ";
				params.add(user.getConfirmStatus());
			}
		}
		if(org != null){
			if (!StringUtils.isBlank(org.getOrgName())) {
				sql+=" and o.org_name like ?  ";
				params.add("%"+org.getOrgName()+"%");
			}
		}
		pageObject.setDefaultSort("u.org_id");
		return super.findListBySql(sql, params.toArray(), pageObject, null).getData();

	}
	public List searchUserListByOrg(GwUserVO user,GwOrgVO org,PageObject pageObject,GwUserVO user1) throws Exception{
		String sql =" select u.login_name,u.user_name,u.confirm_status,u.user_type,o.org_name,u.online_status,u.user_id,u.org_id from gw_user u left join gw_org o on o.org_id = u.org_id where u.status=? and u.org_id = " +user1.getOrgId();
		List<Object> params = new ArrayList<Object>(0);
		params.add(CommonState.VALID);
		if(user != null){
			if (!StringUtils.isBlank(user.getLoginName())) {
				sql+=" and u.login_name like ? ";
				params.add("%"+user.getLoginName()+"%");
			}
			if (!StringUtils.isBlank(user.getUserName())) {
				sql+=" and u.user_name like ? ";
				params.add("%"+user.getUserName()+"%");
			}
			if (!StringUtils.isBlank(user.getUserType())) {
				sql+=" and u.user_type = ? ";
				params.add(user.getUserType());
			}
			if (!StringUtils.isBlank(user.getConfirmStatus())) {
				sql+=" and u.confirm_status= ? ";
				params.add(user.getConfirmStatus());
			}
		}
	
		return super.findListBySql(sql, params.toArray(), pageObject, null).getData();

	}
	
	public List findByOrgId(Long orgId,String status,String userType) throws Exception{
		log.debug("finding GwUserVO instance with property: orgId,status value:"
				+ orgId+","+ status);
		try {
			List<Object> paramsList = new ArrayList<Object>();
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwUserVO  where 1=1 ");
			if(orgId != null){
				queryString.append(" and orgId=? ");
				paramsList.add(orgId);
			}
			if(status != null){
				queryString.append(" and status=? ");
				paramsList.add(status);
			}
			if(userType != null){
				queryString.append(" and userType=? ");
				paramsList.add(userType);
			}
			return super.findByHql(queryString.toString(), paramsList.toArray());
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	/**
	 * 机构用户注销，将机构用户及机构下所有用户都注销
	 * @param cancelUserVO
	 * @throws Exception 
	 */
	public int cancelUserByOrg(GwUserVO loginUserVO,GwUserVO cancelUserVO) throws Exception{
		String sql = "update gw_user t set t.status=?,t.modifier=?,t.modify_time=?  where t.org_id=?";
		return super.executeSql(sql, new Object[]{CommonState.INVALID,loginUserVO.getLoginName(),new Date(),cancelUserVO.getOrgId()});
	}
	
	public GwUserVO searchUser(String loginName,String password) throws Exception{
		String hql = "from GwUserVO where loginName=? and loginPwd=?";
		List<GwUserVO> list = super.findByHql(hql, new String[]{loginName,password});
		return list.size()>0?list.get(0):null;
	}
	
	//根据用户类型查询值班人员列表
	public int searchOtherOnlineUser(Long userId,String userType)throws Exception{
		String sql = "select 1 from gw_user where online_status=1 and status=1 and user_id<>? and user_type=?";
		return super.findCountBySql(sql, new Object[]{userId,userType});
	}
	//根据用户类型查询值班人员列表
	public List<GwUserVO> searchOnlineUser(String userType)throws Exception{
		String sql = "select * from gw_user where online_status=1 and status=1  and user_type=?";
		return super.findListBySql(sql,  new String[]{userType}, GwUserVO.class);
	}
	
	//查询所有机构用户
	public List<UserView> searchOrgUserList()throws Exception{
		String sql="select u.user_id, u.login_name,u.user_name,o.org_name " +
				"from gw_user u inner join gw_org o " +
				"on u.org_id=o.org_id " +
				"where u.status=1 and u.confirm_status=2 and u.user_type=? " +
				"order by u.run_level,u.user_id ";
		List<Object[]> list=super.findListBySql(sql, new String[]{GwUserType.ORG_USER}, null);
		List<UserView> userViewList=new ArrayList<UserView>();
		UserView userView;
		for(Object[] obj:list){
			userView=new UserView();
			userView.setUserId(obj[0]==null?null:Long.parseLong(obj[0].toString()));
			userView.setLoginName(obj[1]==null?null:String.valueOf(obj[1]));
			userView.setUserName(obj[2]==null?null:String.valueOf(obj[2]));
			userView.setOrgName(obj[3]==null?null:String.valueOf(obj[3]));
			userViewList.add(userView);
		}
		return userViewList;
	}
	
	//查询某个机构下的所有数据用户
	public List<UserView> searchDataUserList(Long orgId)throws Exception{
		String sql="select u.user_id, u.login_name,u.user_name,o.org_name " +
				"from gw_user u inner join gw_org o " +
				"on u.org_id=o.org_id " +
				"where u.status=1 and u.confirm_status=2 and u.user_type=? and u.org_id=? " +
				"order by u.run_level,u.user_id ";
		List<Object[]> list=super.findListBySql(sql, new Object[]{GwUserType.DATA_USER,orgId}, null);
		List<UserView> userViewList=new ArrayList<UserView>();
		UserView userView;
		for(Object[] obj:list){
			userView=new UserView();
			userView.setUserId(obj[0]==null?null:Long.parseLong(obj[0].toString()));
			userView.setLoginName(obj[1]==null?null:String.valueOf(obj[1]));
			userView.setUserName(obj[2]==null?null:String.valueOf(obj[2]));
			userView.setOrgName(obj[3]==null?null:String.valueOf(obj[3]));
			userViewList.add(userView);
		}
		return userViewList;
	}
	
	//修改用户列表的取数优先级
	public void updateUserListLevel(String loginName,Integer runLevel)throws Exception{
		String sql="update gw_user set run_level=? where login_name=? ";
		super.executeSql(sql, new Object[]{runLevel,loginName});
	}
}