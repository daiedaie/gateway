package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.loader.custom.Return;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.GwWorkPlanView;
import com.gztydic.gateway.core.view.UserAuditView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwWorkPlan entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwWorkPlanVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwWorkPlanDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwWorkPlanDAO.class);
	// property constants
	public static final String PLAN_TITLE = "planTitle";
	public static final String PLAN_TYPE = "planType";
	public static final String PLAN_CONTENT = "planContent";
	public static final String PLAN_STATE = "planState";
	public static final String PLAN_LEVEL = "planLevel";
	public static final String EXTEN_TABLE_KEY = "extenTableKey";
	public static final String CREATE_USER_ID = "createUserId";
	public static final String DEAL_USER_ID = "dealUserId";

	public GwWorkPlanVO findById(java.lang.Long id) {
		log.debug("getting GwWorkPlan instance with id: " + id);
		try {
			GwWorkPlanVO instance = (GwWorkPlanVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwWorkPlanVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwWorkPlan instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwWorkPlan as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByPlanTitle(Object planTitle) {
		return findByProperty(PLAN_TITLE, planTitle);
	}

	public List findByPlanType(Object planType) {
		return findByProperty(PLAN_TYPE, planType);
	}

	public List findByPlanContent(Object planContent) {
		return findByProperty(PLAN_CONTENT, planContent);
	}

	public List findByPlanState(Object planState) {
		return findByProperty(PLAN_STATE, planState);
	}

	public List findByPlanLevel(Object planLevel) {
		return findByProperty(PLAN_LEVEL, planLevel);
	}

	public List findByExtenTableKey(Object extenTableKey) {
		return findByProperty(EXTEN_TABLE_KEY, extenTableKey);
	}

	public List findByCreateUserId(Object createUserId) {
		return findByProperty(CREATE_USER_ID, createUserId);
	}

	public List findByDealUserId(Object dealUserId) {
		return findByProperty(DEAL_USER_ID, dealUserId);
	}

	public List findAll() {
		log.debug("finding all GwWorkPlan instances");
		try {
			String queryString = "from GwWorkPlan";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List findByDealUserId(Long dealUserId){
		log.debug("finding GwWorkPlan instance with property: DEAL_UEER_ID value:"
				+ dealUserId );
		try {
			String queryString = "from GwWorkPlanVO as plan where plan.dealUserId= ?  and plan.planState='1' order by createTime desc";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, dealUserId);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List findByPlanType(String planTypes){
		log.debug("finding GwWorkPlan instance with property: plan_type value:"
				+ planTypes );
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwWorkPlanVO as plan where plan.planType in (");
			queryString.append(planTypes);
			queryString.append(" )   and plan.planState='1' order by createTime desc");
			
			Query queryObject = getCurrentSession().createQuery(queryString.toString());
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List findByCreateTime(String createTime){
		log.debug("finding GwWorkPlan instance with property: create_time value:"
				+ createTime );
		
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwWorkPlanVO as plan where to_char(plan.createTime,'yyyyMM') in (");
			queryString.append(createTime);
			queryString.append(" ) ");			
			Query queryObject = getCurrentSession().createQuery(queryString.toString());
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	
	public PageObject findByDealUserIdPage(Long dealUserId,PageObject pageObject) throws Exception{
		log.debug("finding GwWorkPlan instance with property: DEAL_UEER_ID value:"
				+ dealUserId );
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(dealUserId);
		try {
			String queryString = "from GwWorkPlanVO as plan where plan.dealUserId= ?  and plan.planState='1' order by createTime desc";
			
			return findByPage(GwWorkPlanVO.class, queryString, pageObject, null, paramsList.toArray());
			
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public PageObject findByParam(Long userId,String  planType,String planTitle,String planContent,PageObject pageObject,String planState) throws Exception{
		log.debug("finding GwWorkPlan instance with property: planType, planTitle,planContent value:"
				+ planType+","+ planTitle+","+planContent);
		try {
			List<Object> paramsList = new ArrayList<Object>();
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwWorkPlanVO as plan where plan.planState in("+planState+") ");
			if(userId != null){
				queryString.append(" and plan.dealUserId=? ");
				paramsList.add(userId);
			}
			if(StringUtils.isNotBlank(planType)){
				queryString.append(" and plan.planType=? ");
				paramsList.add(planType.trim());
			}
			if(StringUtils.isNotBlank(planTitle)){
				queryString.append(" and plan.planTitle like ? ");
				paramsList.add("%"+planTitle.trim()+"%");
			}
			if(StringUtils.isNotBlank(planContent)){
				queryString.append(" and plan.planContent like ? ");
				paramsList.add("%"+planContent.trim()+"%");
			}
			if(CommonState.WAIT_AUDIT.equals(planState)){
				queryString.append(" order by createTime desc");
			}else{
				queryString.append(" order by daelTime desc");
			}
			
			
			return super.findByPage(GwWorkPlanVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	
	public PageObject findByParam(String planTypes,String  planType,String planTitle,String planContent,PageObject pageObject,String planState) throws Exception{
		log.debug("finding GwWorkPlan instance with property: planType, planTitle,planContent value:"
				+ planType+","+ planTitle+","+planContent);
		try {
			List<Object> paramsList = new ArrayList<Object>();
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwWorkPlanVO as plan where plan.planState in("+planState+") ");
			
			if(StringUtils.isNotBlank(planType)){
				if(planTypes.contains(planType)){
					queryString.append(" and plan.planType=? ");
					paramsList.add(planType.trim());
					if(StringUtils.isNotBlank(planTitle)){
						queryString.append(" and plan.planTitle like ? ");
						paramsList.add("%"+planTitle.trim()+"%");
					}
					if(StringUtils.isNotBlank(planContent)){
						queryString.append(" and plan.planContent like ? ");
						paramsList.add("%"+planContent.trim()+"%");
					}
				}else{
					return pageObject;
				}
			}else{
				queryString.append(" and plan.planType in( ");
				queryString.append(planTypes);
				queryString.append(" ) ");
				if(StringUtils.isNotBlank(planTitle)){
					queryString.append(" and plan.planTitle like ? ");
					paramsList.add("%"+planTitle.trim()+"%");
				}
				if(StringUtils.isNotBlank(planContent)){
					queryString.append(" and plan.planContent like ? ");
					paramsList.add("%"+planContent.trim()+"%");
				}
			}
			
			if(CommonState.WAIT_AUDIT.equals(planState)){
				queryString.append(" order by createTime desc");
			}else{
				queryString.append(" order by daelTime desc");
			}
			
			return super.findByPage(GwWorkPlanVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<GwWorkPlanVO> searchWorkPlan(String planType,String planState,String extenTableKey) throws Exception{
		String hql = "from GwWorkPlanVO where 1=1 ";
		List<Object> paramList = new ArrayList<Object>();
		if(StringUtils.isNotBlank(planType)){
			hql += "and planType=? ";
			paramList.add(planType);
		}
		if(StringUtils.isNotBlank(planState)){
			hql += "and planState=? ";
			paramList.add(planState);
		}
		if(StringUtils.isNotBlank(extenTableKey)){
			hql += "and extenTableKey in ("+extenTableKey+") ";
		}
		return super.findByHql(hql, paramList.toArray());
	}
	@SuppressWarnings("unchecked")
	public List<WorkPlanView> searchWorkPlanByUpdate(String planType,String planState,String paramValue) throws Exception{
		String sql = "select a.plan_id,a.plan_type,a.plan_state,b.param_name,b.param_value from gw_work_plan a join gw_work_plan_param b on a.plan_id = b.plan_id where 1=1 and b.param_name='userId' ";
		List<Object> paramList = new ArrayList<Object>();
		if(StringUtils.isNotBlank(planType)){
			sql += "and a.plan_type=? ";
			paramList.add(planType);
		}
		if(StringUtils.isNotBlank(planState)){
			sql += "and a.plan_state=? ";
			paramList.add(planState);
		}
		if(StringUtils.isNotBlank(paramValue)){
			sql += "and b.param_value in ("+paramValue+") ";
		}
		List<Object[]>  list=  super.findListBySql(sql, paramList.toArray(),null);
		List<WorkPlanView> planList= new ArrayList<WorkPlanView>();
		if(list!=null){
			WorkPlanView planView =null;
			for(Object[] obj : list){
				planView = new WorkPlanView();
				planView.setPlanId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				planView.setPlanType(obj[1]==null?null:String.valueOf(obj[1]));
				planView.setPlanState(obj[2]==null?null:String.valueOf(obj[2]));
				planView.setParamName(obj[3]==null?null:String.valueOf(obj[3]));
				planView.setParamValue(obj[4]==null?null:String.valueOf(obj[4]));
				planList.add(planView);
			}
		}
		return planList;
		
	}
	
	public List<WorkPlanView> searchWorkPlanByForService(String[] planType,String planState,String userId,String serviceIds,String planSource) throws Exception{
		String sql="select planId,userId ,serviceId ,planSource from ( "+
				"select userId ,serviceId ,planSource , tmp.plan_id  planId from ( "+
				"select p.*, "+
				"(select a.param_value userId from gw_work_plan_param a where a.param_name='userId' and a.plan_id=p.plan_id) userId, "+
				"(select a.param_value serviceId from gw_work_plan_param a where a.param_name='serviceId' and a.plan_id=p.plan_id) serviceId, " +
				"(select a.param_value planSource from gw_work_plan_param a where a.param_name='planType' and a.plan_id=p.plan_id) planSource "+
				"from "+
				"gw_work_plan p ) tmp "+
				"where userId in ("+userId+") and serviceId in ("+serviceIds+") and planSource =? "+
				"and tmp.plan_type in (?,?,?) and tmp.plan_state=? "+
			")";
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(planSource);
		paramList.add(planType[0]);
		paramList.add(planType[1]);
		paramList.add(planType[2]);
		paramList.add(planState);
		List<Object[]>  list=  super.findListBySql(sql, paramList.toArray(),null);
		List<WorkPlanView> planList= new ArrayList<WorkPlanView>();
		if(list!=null){
			WorkPlanView planView =null;
			for(Object[] obj : list){
				planView = new WorkPlanView();
				planView.setPlanId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				planView.setUserId(obj[1]==null?null:Long.valueOf(String.valueOf(obj[1])));
				planView.setServiceId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
				planView.setPlanSource(obj[3]==null?null:String.valueOf(obj[3]));
				planList.add(planView);
			}
		}
		return planList;
	}
	
	public List<WorkPlanView> searchWorkPlanForDesenService(String[] planType,String planState,String paramValue,String serviceIds) throws Exception{
		String sql="select planId,userId ,serviceId from ( "+
				"select userId ,serviceId ,tmp.plan_id  planId from ( "+
				"select p.*, "+
				"(select a.param_value userId from gw_work_plan_param a where a.param_name='userId' and a.plan_id=p.plan_id) userId, "+
				"(select a.param_value serviceId from gw_work_plan_param a where a.param_name='serviceId' and a.plan_id=p.plan_id) serviceId "+
				"from "+
				"gw_work_plan p ) tmp "+
				"where userId in ("+paramValue+") and serviceId in ("+serviceIds+") "+
				"and tmp.plan_type in (?,?) and tmp.plan_state=? "+
			")";
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(planType[0]);
		paramList.add(planType[1]);
		paramList.add(planState);
		List<Object[]>  list=  super.findListBySql(sql, paramList.toArray(),null);
		List<WorkPlanView> planList= new ArrayList<WorkPlanView>();
		if(list!=null){
			WorkPlanView planView =null;
			for(Object[] obj : list){
				planView = new WorkPlanView();
				planView.setPlanId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				planView.setUserId(obj[1]==null?null:Long.valueOf(String.valueOf(obj[1])));
				planView.setServiceId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
				planList.add(planView);
			}
		}
		return planList;
	}
	public PageObject findUserAuditByParam(String loginName,String  userName,String userType,String orgName,PageObject pageObject,String planTypes) throws Exception{
		log.debug("finding all GwWorkPlanVO instances by loginName、userName、userType、orgHeadName："+
				loginName+","+userName+","+userType+","+orgName);
        try {
                List<Object> paramsList = new ArrayList<Object>();
    			StringBuffer queryString = new StringBuffer();
    			queryString.append("select planId,planType,planSatus,userId, loginName,userName,userType, orgName,extenTableKey from( ");
    			queryString.append("select p.plan_id as planId,p.plan_type as planType,p.plan_state as planSatus,p.create_time as createTime,u.user_id as userId,u.login_name as loginName, ");
    			queryString.append("u.user_name as userName,u.user_type as userType,o.org_name as orgName,p.exten_table_key as extenTableKey from gw_work_plan p,gw_user u,gw_org o ");
    			queryString.append("where p.create_user_id=u.user_id and u.org_id=o.org_id(+) and p.plan_state = '1' and u.status='1' and p.plan_Type in("+planTypes+") )");
    			queryString.append(" where 1=1 ");
    			
    			if(StringUtils.isNotBlank(loginName)){
    				queryString.append(" and loginName like ? ");
    				paramsList.add("%"+loginName.trim()+"%");
    			}
    			if(StringUtils.isNotBlank(userName)){
    				queryString.append(" and userName like ? ");
    				paramsList.add("%"+userName.trim()+"%");
    			}
    			if(StringUtils.isNotBlank(userType)){
    				queryString.append(" and userType  = ? ");
    				paramsList.add(userType.trim());
    			}
    			if(StringUtils.isNotBlank(orgName)){
    				queryString.append(" and orgName like ? ");
    				paramsList.add("%"+orgName.trim()+"%");
    			}
    			pageObject.setDefaultSort("createTime desc");
    			
    			pageObject = super.findListBySql(queryString.toString(), paramsList.toArray(), pageObject, null);
    			if(pageObject != null){
    				List<Object[]> list = pageObject.getData();
    				if(list != null){
    					List<UserAuditView> planList = new ArrayList<UserAuditView>();
    					UserAuditView vo = null;
						for(Object[] obj : list){
							vo = new UserAuditView();
							vo.setPlanId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
							vo.setPlanType(obj[1]==null?null:String.valueOf(obj[1]));
							vo.setPlanState(obj[2]==null?null:String.valueOf(obj[2]));
							vo.setUserId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
							vo.setLoginName(obj[4]==null?null:String.valueOf(obj[4]));
							vo.setUserName(obj[5]==null?null:String.valueOf(obj[5]));
							vo.setUserType(obj[6]==null?null:String.valueOf(obj[6]));
							vo.setOrgName(obj[7]==null?null:String.valueOf(obj[7]));
							vo.setExtenTableKey(obj[8]==null?null:String.valueOf(obj[8]));
							planList.add(vo);
						}
						pageObject.setData(planList);
    				}
    			}
    			return pageObject;
        	} catch (RuntimeException re) {
		        log.error("find all failed", re);
		        throw re;
        	}
	  }
	
	//将未处理的不合规检查审核待办设为失效
	public int updateCheckWorkPlanInvalid(Long userId,Long serviceId) throws Exception{
		String sql = "update gw_work_plan t set t.plan_state=-1,t.DAEL_TIME=sysdate,t.reason='合规检查规则变更' where plan_state=1 and plan_type in (19,21) and plan_id in " +
				"(select plan_id from (select plan_id,count(plan_id) count from ( " +
				"select p.plan_id from gw_work_plan_param p " +
				"where (p.param_name='userId' and p.param_value=?) " +
				"or (p.param_name='serviceId' and p.param_value=?)) " +
				"group by plan_id ) " +
				"where count=2)";
		
		return super.executeSql(sql, new String[]{String.valueOf(userId),String.valueOf(serviceId)});
	}
	
	/**
	 * 
	 * @Title: searchHistoryWorkPlan 
	 * @Description: TODO(查询历史待办信息) 
	 * @param @param planId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws
	 */
	public List<GwWorkPlanView> searchHistoryWorkPlan(Long planId)throws Exception{
		String sql="select u.login_name, u.user_name, b.create_time, b.dael_time, b.plan_state "+
					" from (select * "+
					" from gw_work_plan a "+
					" start with a.plan_Id = ? "+
					" connect by prior a.parent_plan_Id = a.plan_Id) b "+
					" inner join gw_user u "+
					" on u.user_id = b.deal_user_id ";
		
		List<Object[]> list=super.findListBySql(sql, new Object[]{planId}, null);
		List<GwWorkPlanView> workPlanList=new ArrayList<GwWorkPlanView>();
		GwWorkPlanView workPlanView;
		for(Object[] obj:list){
			workPlanView=new GwWorkPlanView();
			workPlanView.setLoginName(obj[0]==null?null:String.valueOf(obj[0]));
			workPlanView.setUserName(obj[1]==null?null:String.valueOf(obj[1]));
			workPlanView.setCreateTime(obj[2]==null?null:DateUtil.StringTODate5(String.valueOf(obj[2])));
			workPlanView.setDaelTime(obj[3]==null?null:DateUtil.StringTODate5(String.valueOf(obj[3])));
			workPlanView.setPlanState(obj[4]==null?null:String.valueOf(obj[4]));
			workPlanList.add(workPlanView);
		}
		return workPlanList;
		
	}
	
	//查看未处理的合规检查待办
	public Long searchCheckWorkPlan(Long taskId)throws Exception{
		String sql=" select * " +
				" from (select t.plan_id,t.plan_type,t.plan_state," +
				" (select p.param_value from gw_work_plan_param p where p.param_name = 'taskId' and p.plan_id = t.plan_id) task_id " +
				" from gw_work_plan t where t.plan_type in('19','21') and t.plan_state = '1') tem" +
				" where tem.task_id =?";
		List<Object[]> list= super.findListBySql(sql, new Object[]{taskId}, null);
		return list.size()==0?0:Long.valueOf(list.get(0)[0].toString());
	}
	//查看所有待办任务
	public PageObject findAllByPage(PageObject pageObject) throws Exception{
		try {
            List<Object> paramsList = new ArrayList<Object>();
			StringBuffer queryString = new StringBuffer();
			queryString.append("from GwWorkPlanVO as plan where 1=1 ");
			return super.findByPage(GwWorkPlanVO.class, queryString.toString(), pageObject, null, paramsList.toArray());
    } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
    }
	}
}