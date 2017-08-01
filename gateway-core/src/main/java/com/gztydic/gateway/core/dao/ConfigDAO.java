package com.gztydic.gateway.core.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;

@Repository
public class ConfigDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(ConfigDAO.class);
	
	public List searchNextDate(Long jobNo) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list=new ArrayList();
		try {
			con = super.getCurrentSession().connection();
			String sql="select next_date,interval from user_jobs where job="+jobNo;
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			String result=null;
			while(rs.next()){
				list.add(rs.getString(1));
				list.add(rs.getString(2));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @Title: updateJobTimer 
	 * @Description: TODO(修改定时器每天几点) 
	 * @param @param jobNo
	 * @param @param proName
	 * @param @param times
	 * @throws
	 */
	public void updateJobTimer(Long jobNo,String proName,String times){
		//获得连接  
		Connection conn = null;
		try {
			conn = getCurrentSession().connection();
			//创建存储过程的对象  
			CallableStatement call;
			call = conn.prepareCall("{call GW_UPDATE_JOB_TIME(?,?,?)}");
			//给存储过程的第一个参数设置值  
			call.setLong(1,jobNo);   
			call.setString(2, proName);
			call.setString(3,times);
			//执行存储过程  
			call.execute();			
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("execute the procedures error:", e);
		}  finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("close connetion error:", e);
			} 
		}
	}
	
	public void callCreateDataTask(){
		//获得连接  
		Connection conn = null;
		try {
			conn = getCurrentSession().connection();
			//创建存储过程的对象  
			CallableStatement call;
			call = conn.prepareCall("{call GW_CREATE_DATA_TASK()}");
			//执行存储过程  
			call.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("execute the procedures error:", e);
		}  finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("close connetion error:", e);
			} 
		}
	}
}
