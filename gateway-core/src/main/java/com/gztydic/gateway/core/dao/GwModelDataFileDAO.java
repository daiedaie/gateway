package com.gztydic.gateway.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.ModelFileView;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelDataFile entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelDataFileVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelDataFileDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModelDataFileDAO.class);
	// property constants
	public static final String USER_ID = "userId";
	public static final String FILE_TYPE = "fileType";
	public static final String FILE_STATUS = "fileStatus";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_NAME = "fileName";
	public static final String TASK_ID = "taskId";

	public GwModelDataFileVO findById(java.lang.Long id) {
		log.debug("getting GwModelDataFile instance with id: " + id);
		try {
			GwModelDataFileVO instance = (GwModelDataFileVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwModelDataFileVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelDataFileVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelDataFileVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}
	
	public List<GwModelDataFileVO> findByTaskAndType(Long taskId, String fileType) {
		log.debug("finding GwModelDataFile instance with property: "
				+"taskId: " + taskId + ", FileTypevalue: " + fileType);
		try {
			String queryString = "from GwModelDataFileVO  where taskId=? and fileType=?";
			
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, taskId);
			queryObject.setParameter(1, fileType);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}
	
	public List findByTaskId(Object taskId) {
		return findByProperty(TASK_ID, taskId);
	}
	
	public List findByFileType(Object fileType) {
		return findByProperty(FILE_TYPE, fileType);
	}

	public List findByFileStatus(Object fileStatus) {
		return findByProperty(FILE_STATUS, fileStatus);
	}

	public List findByFilePath(Object filePath) {
		return findByProperty(FILE_PATH, filePath);
	}

	public List findByFileName(Object fileName) {
		return findByProperty(FILE_NAME, fileName);
	}

	public List findAll() {
		log.debug("finding all GwModelDataFile instances");
		try {
			String queryString = "from GwModelDataFile";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * 根据userId,serviceId查找出相对应产生任务的模型结果集文件记录
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwModelDataFileVO> searchModelDataFileByTaskId(Long userId,Long serviceId) throws Exception {
		String sql=" select * " +
				" from gw_model_data_file where task_id in(" +
				" select task_id from gw_model_data_fetch_task t " +
    			" where t.data_progress_status in (5,6,7,8,9,10) and t.task_status=1 and data_status=1 " +
    			" and t.user_id=? and t.service_id=? "+
				")"+
				" and file_type=2 and file_status=1";
		return super.findListBySql(sql,new Object[]{userId,serviceId},GwModelDataFileVO.class);
	}
	
	/**
	 * 根据taskId查找出过时的模型文件记录
	 * @param sourceDays
	 * @param resultDays
	 * @return
	 * @throws Exception
	 */
	public List<ModelFileView> searchModelDataFile(int resultDays,String fileType) throws Exception {
		String sql = "select f.file_id,f.task_id,f.file_path,f.file_name,s.service_source,f.unzip_name,s.service_type,s.service_id,t.field_code,t.field_value " +
				"from gw_model_data_file f " +
				"inner join gw_model_data_fetch_task t on t.task_id = f.task_id " +
				"inner join gw_service s on s.service_id = t.service_id " +
				"where t.data_status=1 " +
				"and f.file_status=1 and (( f.file_type=? and f.create_time+? < sysdate ))";
		
		List<Object[]> list = super.findListBySql(sql, new Object[]{fileType,resultDays}, null);
		List<ModelFileView> fileList = new ArrayList<ModelFileView>();
		ModelFileView file = null;
		for (Object[] obj : list) {
			file = new ModelFileView();
			file.setFileId(obj[0]==null?null:Long.parseLong(obj[0].toString()));
			file.setTaskId(obj[1]==null?null:Long.parseLong(obj[1].toString()));
			file.setFilePath(obj[2]==null?null:String.valueOf(obj[2]));
			file.setFileName(obj[3]==null?null:String.valueOf(obj[3]));
			file.setServiceSource(obj[4]==null?null:String.valueOf(obj[4]));
			file.setUnzipName(obj[5]==null?null:String.valueOf(obj[5]));
			file.setServiceType(obj[6]==null?null:String.valueOf(obj[6]));
			file.setServiceId(obj[7]==null?0:Long.parseLong(obj[7].toString()));
			file.setFieldCode(obj[8]==null?null:String.valueOf(obj[8]));
			file.setFieldValue(obj[9]==null?null:String.valueOf(obj[9]));
			fileList.add(file);
		}
		return fileList;
	}
	//修改模型数据状态
	public int updateModelDataFileStatus(String files,String fileStatus) throws Exception{
		String sql = "update gw_model_data_file f set f.file_status=?,f.delete_time=sysdate " +
    			"where f.file_id in ("+files+")";
    	return super.executeSql(sql, new Object[]{fileStatus});
	}
	
	/**
	 * 查询已过期的原始文件记录
	 * @param sourceDays
	 * @return
	 * @throws Exception
	 */
	public List<ModelFileView> searchModelSourceFile(int sourceDays) throws Exception{
		String sql="select f.file_id,f.file_name,t.task_id,t.field_code,t.field_value,t.service_id,s.service_type,f.unzip_name " +
				" from gw_model_data_file f inner join gw_model_data_fetch_task t  " +
				" on f.task_id=t.task_id " +
				" inner join gw_service s " +
				" on s.service_id=t.service_id " +
				" where t.data_progress_status>3 and f.file_status=1 and (( f.file_type=1 and f.create_time+? < sysdate )) ";
		List<ModelFileView> modelFileList=new ArrayList<ModelFileView>();
		ModelFileView modelFileView;
		List<Object[]> list=super.findListBySql(sql, new Object[]{sourceDays}, null);
		for(Object[] obj:list){
			modelFileView=new ModelFileView();
			modelFileView.setFileId(obj[0]==null?null:Long.parseLong(obj[0].toString()));
			modelFileView.setFileName(obj[1]==null?null:String.valueOf(obj[1]));
			modelFileView.setTaskId(obj[2]==null?null:Long.parseLong(obj[2].toString()));
			modelFileView.setFieldCode(obj[3]==null?null:String.valueOf(obj[3]));
			modelFileView.setFieldValue(obj[4]==null?null:String.valueOf(obj[4]));
			modelFileView.setServiceId(obj[5]==null?null:Long.parseLong(obj[5].toString()));
			modelFileView.setServiceType(obj[6]==null?null:String.valueOf(obj[6]));
			modelFileView.setUnzipName(obj[7]==null?null:String.valueOf(obj[7]));
			modelFileList.add(modelFileView);
		}
		return modelFileList;
	}
	
	//清除缓存表的数据
	public void deleteCacheData(ModelFileView modelFileView)throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = super.getCurrentSession().connection();
			
			String checkExists = "select 1 from user_tables where table_name=?";
			if(findCountBySql(checkExists, new String[]{"GW_SERVICE_"+modelFileView.getServiceId()}) > 0){
				String sql="";
				if(CommonState.SERVICE_TYPE_IMMEDIATELY.equals(modelFileView.getServiceType())){
					sql=" delete GW_SERVICE_"+modelFileView.getServiceId()+" where gateway_task_id = "+modelFileView.getTaskId();
				}else if(CommonState.SERVICE_TYPE_TIMING.equals(modelFileView.getServiceType())) {
					sql=" delete GW_SERVICE_"+modelFileView.getServiceId()+" where gateway_"+modelFileView.getFieldCode()+" = "+modelFileView.getFieldValue();
				}
				log.info("清理缓存的原始数据sql:"+sql);
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//清除脱敏后的缓存表的数据
	public void deleteDesenCacheData(ModelFileView modelFileView)throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = super.getCurrentSession().connection();
			
			String checkExists = "select 1 from user_tables where table_name=?";
			if(findCountBySql(checkExists, new String[]{"GW_SERVICE_DESEN_"+modelFileView.getServiceId()}) > 0){
				String sql="";
				if(CommonState.SERVICE_TYPE_IMMEDIATELY.equals(modelFileView.getServiceType())){
					sql=" delete GW_SERVICE_DESEN_"+modelFileView.getServiceId()+" where gateway_task_id = "+modelFileView.getTaskId();
				}else if(CommonState.SERVICE_TYPE_TIMING.equals(modelFileView.getServiceType())) {
					sql=" delete GW_SERVICE_DESEN_"+modelFileView.getServiceId()+" where gateway_"+modelFileView.getFieldCode()+" = "+modelFileView.getFieldValue();
				}
				log.info("清理缓存脱敏后数据sql："+sql);
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//清除脱敏后的缓存表的数据
	public void deleteCheckResultData(ModelFileView modelFileView)throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = super.getCurrentSession().connection();
			String delCheckSql="delete gw_service_check_"+modelFileView.getServiceId()+" t where gateway_task_id="+modelFileView.getTaskId();
			String delRecordSql = "delete gw_service_check_record t where task_id="+modelFileView.getTaskId();
			
			String checkExists = "select 1 from user_tables where table_name=?";
			if(findCountBySql(checkExists, new String[]{"GW_SERVICE_CHECK_"+modelFileView.getServiceId()}) > 0){
				log.info("清理不合规检查行数据sql："+delCheckSql);
				ps = con.prepareStatement(delCheckSql);
				ps.execute();
				
				log.info("清理不合规检查记录sql："+delRecordSql);
				ps = con.prepareStatement(delRecordSql);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	} 
	
	public GwModelDataFileVO searchDataFile(Long taskId,String fileType) throws Exception{
		String hql = "from GwModelDataFileVO where taskId=? and fileType=?";
		List<GwModelDataFileVO> list = super.findByHql(hql, new Object[]{taskId,fileType});
		return list!=null&&list.size()>0?list.get(0):null;
	}
	
	public List<ModelFileView> searchModelDataFiles(String fileIds)throws Exception{
		String sql="select f.file_id,f.file_name,f.file_path,f.create_time,u.login_name "+
					" from gw_model_data_file f left join gw_user u on f.user_id=u.user_id "+
					" where f.file_id in("+fileIds+")";
		List<ModelFileView> fileList=new ArrayList<ModelFileView>();
		List<Object[]> list=super.findListBySql(sql, null, null);
		ModelFileView modelFileView;
		for(Object[] obj:list){
			modelFileView=new ModelFileView();
			modelFileView.setFileId(obj[0]==null?null:Long.parseLong(obj[0].toString()));
			modelFileView.setFileName(obj[1]==null?null:String.valueOf(obj[1]));
			modelFileView.setFilePath(obj[2]==null?null:String.valueOf(obj[2]));
			modelFileView.setCreateTime(obj[3]==null?null:DateUtil.StringTODate5(String.valueOf(obj[3])));
			modelFileView.setCreateUser(obj[4]==null?null:String.valueOf(obj[4]));
			fileList.add(modelFileView);
		}
		return fileList;
	}
}