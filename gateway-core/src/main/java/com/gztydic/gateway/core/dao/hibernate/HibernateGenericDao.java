package com.gztydic.gateway.core.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.PageObject;

@Repository
@SuppressWarnings("unchecked")
public class HibernateGenericDao extends HibernateDaoSupport{
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@PostConstruct
	public void construct(){
		super.setSessionFactory(sessionFactory);
	}
	
	public Session getCurrentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	/**save对象**/
	public Serializable save(Object data) throws Exception {
		return getHibernateTemplate().save(data);
	}

	/**saveOrUpdate对象**/
	public void saveOrUpdate(Object data) throws Exception {
		getHibernateTemplate().saveOrUpdate(data);
	}

	/** update对象 **/
	public void update(Object data) throws Exception {
		getHibernateTemplate().update(data);
	}
	
	/**删除对象**/
	public void delete(Object data) throws Exception {
		getHibernateTemplate().delete(data);
	}

	/**删除指定ID**/
	protected <T> int deleteById(Class<T> entityClass, Serializable[] ids) throws Exception {
		if(ids!=null&&ids.length>0){
    		return executeByBulk("DELETE "+entityClass.getName()+" WHERE "+getIdName(entityClass)+" IN ("+getPrarmNum(ids)+")", ids);
    	}
		return 0;
	}

	/**批处理对象**/
	public int executeByBulk(String sql, Object[] values) throws Exception {
		return getHibernateTemplate().bulkUpdate(sql, values);
	}
	
	/**清除缓存**/
	public void clear() {
		getHibernateTemplate().clear();
	}
	
	/**刷新缓存**/
	public void flush() {
		getHibernateTemplate().flush();
	}
	
	/**存储过程调用**/
	public Object execNamedQuery(final String name,final Object[] values,final int returnType) throws Exception{
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.getNamedQuery(name);
				if(values!=null&&values.length>0){
					for(int index=0;index<values.length;index++){
						query.setParameter(index, values[index]);
					}
				}
				if(returnType==0){//0 没有返回 
					return query.executeUpdate();
				}
				if(returnType==1){//1返回唯一值
					return query.uniqueResult();
				}
              	return query.list();
			}
		});
	}
	
	/** 查询行数 **/
	protected <T> Integer findByCount(Class<T> entityClass,String hql,Object[] fldValues){
		int index=-1;
		if(hql!=null&&hql.length()>0){
			index=hql.toLowerCase().indexOf("from ");
		}
		if(index!=-1){
			hql="select count(rownum) " + hql.substring(index);
		}else{
			hql="select count(rownum) from " + entityClass.getName();
		}
		List<Object> list=new ArrayList<Object>();
		if(fldValues!=null&&fldValues.length>0){
			for(Object object:fldValues){
				if(object!=null&&object.toString().length()>0){
					list.add(object);
				}
			}
		}
		if(list.size()>0){
			return ((Long)getHibernateTemplate().find(hql,list.toArray()).listIterator().next()).intValue();
		}
		
		return ((Long)getHibernateTemplate().find(hql).listIterator().next()).intValue();
	}
	
	/**
	 * 由主键查询
	 * @param <T> 返回Entity
	 * @param entityClass
	 * @param fetchs 要关联抓取的表
	 * @param id 主键值
	 * @return
	 * @throws Exception
	 */
	protected <T> T findById(final Class<T> entityClass,final String[] fetchs,final Serializable id) throws Exception {
		Object data=getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				Criteria criteria=session.createCriteria(entityClass);
				if(fetchs!=null&&fetchs.length>0){
					for(String fetch:fetchs){
					   criteria.setFetchMode(fetch, FetchMode.JOIN);
					}
				}
				criteria.add(Restrictions.idEq(id));
				return criteria.uniqueResult();
			}
		});
		return (T)data;
	}
	
	/**
	 * 多条件查询
	 * @param <T> 返回Entity
	 * @param entityClass
	 * @param fetchs 要关联抓取的表
	 * @param fldNames 查询的字段
	 * @param fldValues 字段的值
	 * @return
	 * @throws Exception
	 */
	protected <T> T findByObject(final Class<T> entityClass,final String[] fetchs,final String[] fldNames,final Object[] fldValues) throws Exception {
		Object data=getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				Criteria criteria=session.createCriteria(entityClass);
				if(fetchs!=null && fetchs.length>0){
					for(String fetch:fetchs){
					   criteria.setFetchMode(fetch, FetchMode.JOIN);
					}
				}
				if(fldValues!=null && fldValues.length>0){
					for(int i=0; i<fldValues.length; i++){
						if(fldValues[i]==null || "".equals(fldValues[i]))
							continue;
						criteria.add(Restrictions.eq(fldNames[i], fldValues[i]));
					}
				}
				return criteria.uniqueResult();
			}
		});
		return (T)data;
	}
	
	/** 指定hql查询 **/
	public List findByHql(String hql,Object[] fldValues) throws Exception {
		if(fldValues!=null && fldValues.length>0){
			return getHibernateTemplate().find(hql, fldValues);
		}
		return getHibernateTemplate().find(hql);
	}
	
	protected <T> PageObject findByPage(Class<T> entityClass, String hql,PageObject pageObject,String[] fetchs,Object[] fldValues) throws Exception {
    	List data=null;
		int dataCount = findByCount(entityClass,hql,fldValues);
		pageObject.setDataCount(dataCount);
		if (dataCount > 0) {
			data = findByList(hql, pageObject, fetchs,fldValues);
		}
    	pageObject.setData(data);
    	return pageObject;
	}
	
	public List findByList(final String hql, final PageObject pageObject,final String[] fetchs,final Object[] paras) throws Exception{
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				String hqlOrderby = hql;
				if(StringUtils.isNotBlank(pageObject.getSort()) && !"null".equals(pageObject.getSort())){
					hqlOrderby += " order by "+pageObject.getSort()+" "+pageObject.getAsc();
				}else if(StringUtils.isNotBlank(pageObject.getDefaultSort())){
					hqlOrderby += " order by "+pageObject.getDefaultSort();
				}
				Query query = session.createQuery(hqlOrderby);
				if(paras!=null && paras.length>0){
					int index=0;
					for(int i=0;i<paras.length;i++){
						if(paras[i]!=null&&paras[i].toString().length()>0){
							query.setParameter(index, paras[i]);
							index++;
						}
					}
				}
				//分页查询
				if(pageObject.getPageSize()>0){
					query.setFirstResult(pageObject.getBeginPoint());
					query.setMaxResults(pageObject.getPageSize());
				}
				//查询List
				List list= query.list();
				//延时加载
				if((fetchs!=null&&fetchs.length>0)&&(list!=null&&list.size()>0)){
					String methodName = "";
					Class cls;
					Method method;
					try{
						for(Object obj:list){
							for(String fetch:fetchs){
								cls=obj.getClass();
								methodName="get"+fetch.substring(0, 1).toUpperCase()+fetch.substring(1);
								method = cls.getMethod(methodName);
								Hibernate.initialize(method.invoke(obj,new Object[]{}));
							}
						}
					}catch(Exception ex){ex.printStackTrace();}
				}
                return list;
			}
		});
	}
	
	protected <T> Criteria createCriteria(final Class<T> entityClass,final String orderBy,final Criterion... criterions) {
		Criteria criteria=(Criteria)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)throws HibernateException, SQLException {
				Criteria criteria=session.createCriteria(entityClass);
				for (Criterion c : criterions) {
					criteria.add(c);
				}
				if(orderBy!=null&&orderBy.length()>0){
					if (orderBy.toLowerCase().indexOf(" desc")!=-1){
						criteria.addOrder(Order.desc(orderBy));
					}else{
						criteria.addOrder(Order.asc(orderBy));
					}
				}
				return criteria;
			}
		});
		return criteria;
	}
	
	public Integer findCountBySql(final String sql,final Object[] params){
		 Integer count = (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String countSql = "select count(1) from ("+sql+")";
				Query query = session.createSQLQuery(countSql);
				
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						if(params[i]!=null&&params[i].toString().length()>0){
							query.setParameter(index, params[i]);
							index++;
						}
					}
				}
				Object object = query.uniqueResult();
				return Integer.valueOf(String.valueOf(object));
			}
		});
		return count;
	}
	
	public Integer findIntBySql(final String sql,final Object[] params){
		 return (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createSQLQuery(sql);
				
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						if(params[i]!=null&&params[i].toString().length()>0){
							query.setParameter(index, params[i]);
							index++;
						}
					}
				}
				Object object = query.uniqueResult();
				return object==null?0:Integer.valueOf(String.valueOf(object));
			}
		});
	}

	public PageObject findListBySql(final String sql,final Object[] params, final PageObject pageObject, final Class entityClass) throws Exception{
		return (PageObject)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count = findCountBySql(sql,params);
				
				String sqlOrderby = sql;
				if(StringUtils.isNotBlank(pageObject.getSort()) && !"null".equals(pageObject.getSort())){
					sqlOrderby += " order by "+pageObject.getSort()+" "+pageObject.getAsc();
				}else if(StringUtils.isNotBlank(pageObject.getDefaultSort())){
					sqlOrderby += " order by "+pageObject.getDefaultSort();
				}
				
				SQLQuery query = session.createSQLQuery(sqlOrderby);
				
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						if(params[i]!=null&&params[i].toString().length()>0){
							query.setParameter(index, params[i]);
							index++;
						}
					}
				}
				
				//分页查询
				query.setFirstResult((pageObject.getCurPage()-1)*pageObject.getPageSize());
				query.setMaxResults(pageObject.getPageSize());
				
				int pageCount = count / pageObject.getPageSize();
				if(count % pageObject.getPageSize() != 0) pageCount++;
				
		        pageObject.setDataCount(count);
		        if(entityClass != null) pageObject.setData(query.addEntity(entityClass).list());
		        else pageObject.setData(query.list());
		        pageObject.setPageCount(pageCount);
		        return pageObject;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List findListBySql(final String sql,final Object[] params, final Class entityClass) throws Exception{
		return (List)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						if(params[i]!=null&&params[i].toString().length()>0){
							query.setParameter(index, params[i]);
							index++;
						}
					}
				}
				if(entityClass == null) return query.list();
				return query.addEntity(entityClass).list();
			}
		});
	}
	
	public Integer executeSql(final String sql,final Object[] params) throws Exception{
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						query.setParameter(index, params[i]);
						index++;
					}
				}
				return query.executeUpdate();
			}
		});
	}
	
	public Integer executeHql(final String hql,final Object[] params) throws Exception{
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				
				if(params!=null && params.length>0){
					int index=0;
					for(int i=0;i<params.length;i++){
						query.setParameter(index, params[i]);
						index++;
					}
				}
				return query.executeUpdate();
			}
		});
	}
	
	//获取ID字段
	private String getIdName(Class clazz) throws Exception {
		ClassMetadata meta = getSessionFactory().getClassMetadata(clazz);
		return meta.getIdentifierPropertyName();
	}
	
	private String getFromName(String querySel,String className){
		String fromName=" from "+className;
		return querySel!=null?(querySel+fromName):fromName;
	}
	
	public String getPrarmNum(Serializable params[]){
		String param="?";
		for(int i=1;i<params.length;i++){
			param=param+",?";
		}
		return param;
	}
	
	private final String and="and";
	protected String getPrarmNum(String params[],Object[] values,String orderBy){
		String param = "";
		if(params!=null && values!=null && params.length==values.length){
			for(int i=0;i<params.length;i++){
				if(values[i]==null || values[i].toString().length()==0)
					continue;
				
				if(params[i]!=null && params[i].toString().length()>0){
					param += " "+params[i]+(params[i].indexOf("?")>-1 ? "" : "=?")+" "+and;
				}else{
					values[i]=null;
				}
			}
			if(!"".equals(param)){
				param = " where"+param.substring(0, param.length()-and.length());
			}
		}
		
		if(orderBy!=null && orderBy.length()>0){
			param += " order by "+orderBy;
    	}
		return param;
	}
	
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	protected String getPrarmNum(String params[],Object[] values,String[] dateParams,Date[][] dateValues,String[] likeParams,Object[] likeValues,String condition,String orderBy){
		String sqlParam="";
		condition=(condition==null||condition.length()==0)?"and":condition;
		
		if(params!=null&&values!=null&&params.length==values.length){
			String param="";
			for(int i=0;i<params.length;i++){
				if(values[i]!=null&&values[i].toString().length()>0){
					if(params[i]!=null&&params[i].toString().length()>0){
						if(params[i].indexOf("?")!=-1){
							param+=" "+params[i]+" "+condition;
						}else{
							param+=" "+params[i]+"=? "+condition;
						}
					}else{
						values[i]=null;
					}
				}
			}
			if(param!=""){
				sqlParam=param.substring(0,param.length()-condition.length());
			}
		}
		
		if(dateParams!=null&&dateValues!=null&&dateParams.length==dateValues.length){
			String param="";
			for(int i=0;i<dateParams.length;i++){
				if(dateValues[i]!=null&&dateValues[i].length==2){//开始时间和结束时间
					if(dateValues[i][0]!=null){
						param+=" "+dateParams[i]+">=to_date('"+format.format(dateValues[i][0])+"','yyyy-mm-dd') "+condition;
					}
					if(dateValues[i][1]!=null){
						param+=" "+dateParams[i]+"<to_date('"+format.format(dateValues[i][1])+"','yyyy-mm-dd')+1 "+condition;
					}
				}
			}
			if(param!=""){
				param=param.substring(0,param.length()-condition.length());
				if(sqlParam!=""){
					param=condition+param;
				}
				sqlParam+=param;
			}
		}
		
		if(likeParams!=null&&likeValues!=null&&likeParams.length==likeValues.length){
			String param="";
			for(int i=0;i<likeParams.length;i++){
				if(likeValues[i]!=null&&likeValues[i].toString().length()>0){
					param+=" "+likeParams[i]+" like '%"+likeValues[i].toString()+"%' "+condition;
				}
			}
			if(param!=""){
				param=param.substring(0,param.length()-condition.length());
				if(sqlParam!=""){
					param=condition+param;
				}
				sqlParam+=param;
			}
		}
		
		if(sqlParam!=""){
			sqlParam=" where"+sqlParam;
		}
		
		if(orderBy!=null && orderBy.length()>0){
			sqlParam+=" order by "+orderBy;
    	}
		return sqlParam;
	}
	
	public int searchSequenceValue(String sequenceName){
		String sql = "select "+sequenceName+".nextval from dual";
		return this.findIntBySql(sql, null);
	}
}
