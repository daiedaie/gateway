package com.gztydic.gateway.core.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.ShellUtil;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.view.ModelTaskView;
import com.gztydic.gateway.core.view.ServiceCycleAppView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * A data access object (DAO) providing persistence and search support for
 * GwModelDataFetchTask entities. Transaction control of the save(), update()
 * and delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwModelDataFetchTaskVOVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwModelDataFetchTaskDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwModelDataFetchTaskDAO.class);
	// property constants
	public static final String FETCH_ID = "fetchId";
	public static final String USER_ID = "userId";
	public static final String MODEL_ID = "modelId";
	public static final String DATA_PROGRESS_STATUS = "dataProgressStatus";
	public static final String DATA_STATUS = "dataStatus";
	public static final String DATA_NUM = "dataNum";
	public static final String CREATE_USER = "createUser";
	public static final String DATA_SOURCE = "dataSource";
	public static final String DATA_TYPE = "dataType";

	public GwModelDataFetchTaskVO findById(java.lang.Long id) {
		log.debug("getting GwModelDataFetchTaskVO instance with id: " + id);
		try {
			GwModelDataFetchTaskVO instance = (GwModelDataFetchTaskVO) getCurrentSession()
					.get("com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwModelDataFetchTaskVO instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from GwModelDataFetchTaskVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFetchId(Object fetchId) {
		return findByProperty(FETCH_ID, fetchId);
	}

	public List findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}

	public List findByModelId(Object modelId) {
		return findByProperty(MODEL_ID, modelId);
	}

	public List findByDataProgressStatus(Object dataProgressStatus) {
		return findByProperty(DATA_PROGRESS_STATUS, dataProgressStatus);
	}

	public List findByDataStatus(Object dataStatus) {
		return findByProperty(DATA_STATUS, dataStatus);
	}

	public List findByDataNum(Object dataNum) {
		return findByProperty(DATA_NUM, dataNum);
	}

	public List findByCreateUser(Object createUser) {
		return findByProperty(CREATE_USER, createUser);
	}

	public List findByDataSource(Object dataSource) {
		return findByProperty(DATA_SOURCE, dataSource);
	}

	public List findByDataType(Object dataType) {
		return findByProperty(DATA_TYPE, dataType);
	}

	public List findAll() {
		log.debug("finding all GwModelDataFetchTaskVO instances");
		try {
			String queryString = "from GwModelDataFetchTaskVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public PageObject findAllByPage(String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception{
        log.debug("finding all GwModelDataFetchTaskVO instances by modelCode、modelName、loginName、startDate、endDate："+
        		modelCode+","+modelName+","+loginName+","+startDate+","+endDate);
        try {
            List<Object> paramsList = new ArrayList<Object>();
			String sql = "select a.task_id,e.CYCLE_TYPE,e.CYCLE_DAY,c.login_name,d.model_code," +
					"d.model_name,a.data_progress_status ,a.data_status ,a.data_num ,a.task_status ," +
					"a.create_time ,a.end_time ,a.download_start_time ,a.download_end_time ,a.data_source ," +
					"e.service_code ,e.service_name ,e.SERVICE_TYPE ,a.audit_time ,f.file_status ,b.desen_type ,b.service_id ,b.user_id ,(select e.file_status from GW_MODEL_DATA_FILE e  where e.file_type='1' and e.task_id=a.task_id) originalFileStatus," +
					"e.service_source,a.check_num,a.check_result,a.field_value,a.REDO_TAG,v.field_num,a.pre_data_progress_status " +
					"from GW_MODEL_DATA_FETCH_TASK a " +
					"left join GW_MODEL_DATA_FETCH b on a.fetch_id=b.fetch_id " +
					"left join GW_USER c on a.user_id=c.user_id " +
					"left join GW_MODEL d on a.model_id=d.model_id " +
					"left join gw_service e on a.service_id=e.service_id " +
					"left join GW_MODEL_DATA_FILE f on a.task_id=f.task_id and f.file_type=2 " +
					"left join v_service_field_count v on v.task_id=a.task_id " +
					"where 1=1 ";
			
            if(StringUtils.isNotBlank(modelCode)){
				sql += "and d.model_Code = ? ";
				paramsList.add(modelCode.trim());
			}
			if(StringUtils.isNotBlank(modelName)){
				sql += "and d.model_Name like ? ";
				paramsList.add("%"+modelName.trim()+"%");
			}
			if(StringUtils.isNotBlank(loginName)){
				sql += "and c.login_Name = ? ";
				paramsList.add(loginName.trim());
			}

			// startDate,Date endDate
			if(startDate != null){
				sql += "and a.download_start_time >= ? ";
				paramsList.add(startDate);
			}
			if(endDate != null){
				sql += "and a.download_end_time <= ? ";
				paramsList.add(endDate);
			}
			if(StringUtils.isNotBlank(serviceCode)){
 				sql += "and e.service_Code = ? ";
 				paramsList.add(serviceCode.trim());
 			}
			if(StringUtils.isNotBlank(dataProgressStatus)){
 				sql += "and a.data_progress_status = ? ";
 				paramsList.add(dataProgressStatus);
 			}
			pageObject.setDefaultSort("a.task_Id desc");
			
			//List<Object[]> list = findListBySql(sql,new Object[]{userId,userId}, null);
			if(StringUtils.isNotBlank(pageObject.getSort())){
				pageObject.setSort(pageObject.getSort()+" "+ pageObject.getAsc()+",task_id");
			}
			pageObject = super.findListBySql(sql, paramsList.toArray(), pageObject, null);
			if(pageObject != null){
				List<Object[]> list = pageObject.getData();
				if(list != null){
					List<ModelTaskView> appList = new ArrayList<ModelTaskView>();
					ModelTaskView vo = null;
					for(Object[] obj : list){
						vo = new ModelTaskView();
						vo.setTaskId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
						vo.setCycleType(obj[1]==null?null:String.valueOf(obj[1]));
						vo.setCycleNum(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
						vo.setUserName(obj[3]==null?null:String.valueOf(obj[3]));
						vo.setModelCode(obj[4]==null?null:String.valueOf(obj[4]));
						vo.setModelName(obj[5]==null?null:String.valueOf(obj[5]));
						vo.setDataProgressStatus(obj[6]==null?null:String.valueOf(obj[6]));
						vo.setDataStatus(obj[7]==null?null:String.valueOf(obj[7]));
						vo.setDataNum(obj[8]==null?null:Long.valueOf(String.valueOf(obj[8])));
						vo.setTaskStatus(obj[9]==null?null:String.valueOf(obj[9]));
						vo.setCreateTime(obj[10]==null?null:DateUtil.StringTODate5(String.valueOf(obj[10])));
						vo.setEndTime(obj[11]==null?null:DateUtil.StringTODate5(String.valueOf(obj[11])));
						vo.setDownloadStartTime(obj[12]==null?null:DateUtil.StringTODate5(String.valueOf(obj[12])));
						vo.setDownloadEndTime(obj[13]==null?null:DateUtil.StringTODate5(String.valueOf(obj[13])));
						vo.setDataSource(obj[14]==null?null:String.valueOf(obj[14]));
						vo.setServiceCode(obj[15]==null?null:String.valueOf(obj[15]));
						vo.setServiceName(obj[16]==null?null:String.valueOf(obj[16]));
						vo.setServiceType(obj[17]==null?null:String.valueOf(obj[17]));
						vo.setAuditTime(obj[18]==null?null:DateUtil.StringTODate5(String.valueOf(obj[18])));
						vo.setFileStatus(obj[19]==null?null:String.valueOf(String.valueOf(obj[19])));
						vo.setDesenType(obj[20]==null?null:String.valueOf(String.valueOf(obj[20])));
						vo.setServiceId(obj[21]==null?null:Long.valueOf(String.valueOf(obj[21])));
						vo.setUserId(obj[22]==null?null:Long.valueOf(String.valueOf(obj[22])));
						vo.setOriginalFileStatus(obj[23]==null?null:String.valueOf(obj[23]));
						vo.setServiceSource(obj[24]==null?null:String.valueOf(String.valueOf(obj[24])));
						vo.setCheckNum(obj[25]==null?null:Long.valueOf(String.valueOf(obj[25])));
						vo.setCheckResult(obj[26]==null?"":String.valueOf(obj[26]));
						vo.setFieldValue(obj[27]==null?"":String.valueOf(obj[27]));
						vo.setRedoTag(obj[28]==null?"":String.valueOf(obj[28]));
						vo.setFieldNum(obj[29]==null?null:Long.valueOf(String.valueOf(obj[29])));
						vo.setPreDataProgressStatus(obj[30]==null?null:String.valueOf(obj[30]));
						appList.add(vo);
					}
					pageObject.setData(appList);
				}
			}
			return pageObject;
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
	}
        
    
    public PageObject findAllByPageAndUserId(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception{
        log.debug("finding all GwModelDataFetchTaskVO instances by modelCode、modelName、loginName、serviceCode、startDate、endDate："+
        		modelCode+","+modelName+","+loginName+","+serviceCode+","+startDate+","+endDate);
        try {
            List<Object> paramsList = new ArrayList<Object>();
    		String sql = "select a.task_id ,e.CYCLE_TYPE ,e.CYCLE_DAY ,c.login_name ,d.model_code ," +
    				"d.model_name ,a.data_progress_status ,a.data_status ,a.data_num ,a.task_status ," +
    				"a.create_time ,a.end_time ,a.download_start_time ,a.download_end_time ,a.data_source ," +
    				"e.service_code ,e.service_name ,e.SERVICE_TYPE ,a.audit_time ,f.file_status ,b.desen_type ,b.service_id ,b.user_id ,(select e.file_status from GW_MODEL_DATA_FILE e  where e.file_type='1' and e.task_id=a.task_id) originalFileStatus," +
    				"e.service_source,a.check_num,a.check_result,a.field_value,a.REDO_TAG,v.field_num,a.pre_data_progress_status " +
    				"from GW_MODEL_DATA_FETCH_TASK a " +
    				"left join GW_MODEL_DATA_FETCH b on a.fetch_id=b.fetch_id " +
    				"left join GW_USER c on a.user_id=c.user_id " +
    				"left join GW_MODEL d on a.model_id=d.model_id " +
    				"left join gw_service e on a.service_id=e.service_id " +
    				"left join GW_MODEL_DATA_FILE f on a.task_id=f.task_id and f.file_type=2 " +
    				"left join v_service_field_count v on v.task_id=a.task_id " +
    				"where a.user_id in ("+userIds+") ";
			
			if(StringUtils.isNotBlank(modelCode)){
				sql += "and d.model_Code = ? ";
				paramsList.add(modelCode.trim());
			}
			if(StringUtils.isNotBlank(modelName)){
				sql += "and d.model_Name like ? ";
				paramsList.add("%"+modelName.trim()+"%");
			}
			if(StringUtils.isNotBlank(loginName)){
				sql += "and c.login_Name = ? ";
				paramsList.add(loginName.trim());
			}
			// startDate,Date endDate
			if(startDate != null){
				sql += "and a.download_start_time >= ? ";
				paramsList.add(startDate);
			}
			if(endDate != null){
				sql += "and a.download_end_time <= ? ";
				paramsList.add(endDate);
			}
			if(StringUtils.isNotBlank(serviceCode)){
 				sql += "and e.service_Code = ? ";
 				paramsList.add(serviceCode.trim());
 			}
			if(StringUtils.isNotBlank(dataProgressStatus)){
 				sql += "and a.data_progress_status = ? ";
 				paramsList.add(dataProgressStatus);
 			}
			pageObject.setDefaultSort("a.task_Id desc");
			
			//List<Object[]> list = findListBySql(sql,new Object[]{userId,userId}, null);
			pageObject = super.findListBySql(sql, paramsList.toArray(), pageObject, null);
			if(pageObject != null){
				List<Object[]> list = pageObject.getData();
				if(list != null){
					List<ModelTaskView> appList = new ArrayList<ModelTaskView>();
					ModelTaskView vo = null;
					for(Object[] obj : list){
						vo = new ModelTaskView();
						vo.setTaskId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
						vo.setCycleType(obj[1]==null?null:String.valueOf(obj[1]));
						vo.setCycleNum(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
						vo.setUserName(obj[3]==null?null:String.valueOf(obj[3]));
						vo.setModelCode(obj[4]==null?null:String.valueOf(obj[4]));
						vo.setModelName(obj[5]==null?null:String.valueOf(obj[5]));
						vo.setDataProgressStatus(obj[6]==null?null:String.valueOf(obj[6]));
						vo.setDataStatus(obj[7]==null?null:String.valueOf(obj[7]));
						vo.setDataNum(obj[8]==null?null:Long.valueOf(String.valueOf(obj[8])));
						vo.setTaskStatus(obj[9]==null?null:String.valueOf(obj[9]));
						vo.setCreateTime(obj[10]==null?null:DateUtil.StringTODate5(String.valueOf(obj[10])));
						vo.setEndTime(obj[11]==null?null:DateUtil.StringTODate5(String.valueOf(obj[11])));
						vo.setDownloadStartTime(obj[12]==null?null:DateUtil.StringTODate5(String.valueOf(obj[12])));
						vo.setDownloadEndTime(obj[13]==null?null:DateUtil.StringTODate5(String.valueOf(obj[13])));
						vo.setDataSource(obj[14]==null?null:String.valueOf(obj[14]));
						vo.setServiceCode(obj[15]==null?null:String.valueOf(obj[15]));
						vo.setServiceName(obj[16]==null?null:String.valueOf(obj[16]));
						vo.setServiceType(obj[17]==null?null:String.valueOf(obj[17]));
						vo.setAuditTime(obj[18]==null?null:DateUtil.StringTODate5(String.valueOf(obj[18])));
						vo.setFileStatus(obj[19]==null?null:String.valueOf(String.valueOf(obj[19])));
						vo.setDesenType(obj[20]==null?null:String.valueOf(String.valueOf(obj[20])));
						vo.setServiceId(obj[21]==null?null:Long.valueOf(String.valueOf(obj[21])));
						vo.setUserId(obj[22]==null?null:Long.valueOf(String.valueOf(obj[22])));
						vo.setOriginalFileStatus(obj[23]==null?null:String.valueOf(String.valueOf(obj[23])));
						vo.setServiceSource(obj[24]==null?null:String.valueOf(String.valueOf(obj[24])));
						vo.setCheckNum(obj[25]==null?null:Long.valueOf(String.valueOf(obj[25])));
						vo.setCheckResult(obj[26]==null?"":String.valueOf(obj[26]));
						vo.setFieldValue(obj[27]==null?"":String.valueOf(obj[27]));
						vo.setRedoTag(obj[28]==null?"":String.valueOf(obj[28]));
						vo.setFieldNum(obj[29]==null?null:Long.valueOf(String.valueOf(obj[29])));
						vo.setPreDataProgressStatus(obj[30]==null?null:String.valueOf(obj[30]));
						appList.add(vo);
					}
					pageObject.setData(appList);
				}
			}
			return pageObject;
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public List<ServiceCycleAppView> findServiceTaskList() throws Exception{
    	String sql = "select t.service_id,t.field_code,t.field_value,s.service_code,min(t.task_id) " +
    				 "from GW_MODEL_DATA_FETCH_TASK t " +
    				 "inner join Gw_Service s on s.service_id=t.service_id " +
    				 "where t.data_progress_status is null and t.task_status='1' and s.service_source='1' and s.status='1' and s.service_type=1 " +
    				 "group by t.service_id,t.field_code,t.field_value,s.service_code ";
    	List<Object[]>  list=  super.findListBySql(sql,null,null);
    	List<ServiceCycleAppView> serviceAppList= new ArrayList<ServiceCycleAppView>();
    	if(list!=null){
    		ServiceCycleAppView serviceApp =null;
			for(Object[] obj : list){
				serviceApp = new ServiceCycleAppView();
				serviceApp.setServiceid(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
				serviceApp.setFieldCode(obj[1]==null?null:String.valueOf(obj[1]));
				serviceApp.setFieldValue(obj[2]==null?null:String.valueOf(obj[2]));
				serviceApp.setServiceCode(obj[3]==null?null:String.valueOf(obj[3]));
				serviceApp.setTaskId(obj[4]==null?null:Long.valueOf(String.valueOf(obj[4])));
				serviceAppList.add(serviceApp);
			}
		}
		return serviceAppList;
    }
    
    public List findTaskList(Long servceId,String fieldCode,String fieldValue) throws Exception{
    	log.debug("finding all GwModelDataFetchTaskVO instances by ");
		try {
			String queryString = "from GwModelDataFetchTaskVO where dataProgressStatus is null and serviceId =? and fieldCode = ? and fieldValue =?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, servceId);
			queryObject.setParameter(1, fieldCode);
			queryObject.setParameter(2, fieldValue);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
    }
    
    /**
     * 接收挖掘平台数据生成通知后，将相同类型的服务统一修改为dataProgressStatus=2
     * @param taskId
     * @return
     * @throws Exception
     */
    public int batchUpdateModelDataFetchTask(Long taskId,String dataProgressStatus,int dataNum) throws Exception{
    	//其他任务的数据来源为cache
    	String cacheSql = "update gw_model_data_fetch_task task set task.data_progress_status=?,task.data_num=?,task.DATA_SOURCE=1 " +
    			"where task.task_id in ( " +
    			"select t2.task_id from gw_model_data_fetch_task t " +
    			"inner join gw_model_data_fetch_task t2 " +
    			"on t2.service_id=t.service_id and t2.field_code=t.field_code and t2.field_value=t.field_value " +
    			"where t.task_id=? and (t2.data_progress_status=1 or t2.data_progress_status is null)) and task.task_id <> ?";
    	
    	//当前任务的数据来源为dataCenter
    	String sql = "update gw_model_data_fetch_task task set task.data_progress_status=?,task.data_num=?,task.DATA_SOURCE=2 " +
    		  "where task.task_id = ? and (task.data_progress_status=1 or task.data_progress_status is null)";
    	
    	//dataProgressStatus = 1发起申请时设置发起时间
    	if(DataProgressStatus.REQUEST_DATA.equals(dataProgressStatus)){
    		String dataProgressStatusSql = "update gw_model_data_fetch_task task set task.download_start_time=sysdate " +
    				"where task.task_id in (select t2.task_id from gw_model_data_fetch_task t " +
    				"inner join gw_model_data_fetch_task t2 " +
    				"on t2.service_id=t.service_id and t2.field_code=t.field_code and t2.field_value=t.field_value " +
    				"where t.task_id=?) and (task.data_progress_status=1 or task.data_progress_status is null)";
    		//发起申请时，缓存数据的数据状态设为1
    		super.executeSql(cacheSql, new Object[]{DataProgressStatus.REQUEST_DATA,dataNum,taskId,taskId});
    		super.executeSql(dataProgressStatusSql, new Object[]{taskId});
		}else if(DataProgressStatus.DATA_READY.equals(dataProgressStatus)){
			//挖掘平台数据准备好后，缓存数据的数据状态设为4
			super.executeSql(cacheSql, new Object[]{DataProgressStatus.DOWNLOAD_FINISH,dataNum,taskId,taskId});
		}
    	return super.executeSql(sql, new Object[]{dataProgressStatus,dataNum,taskId});
    }
    
    /**
     * 修改字段脱敏配置后，将原来（5、6）的任务设为失效，并生成新的任务
     * 然后重新脱敏
     * @param serviceId
     * @return
     * @throws Exception 
     */
    public int redesenTask(Long userId,GwServiceVO serviceVo) throws Exception{
		String hql = "from GwModelDataFetchTaskVO where dataProgressStatus in (5,6,7,8,10) and taskStatus=1 and dataStatus=1 and userId=? and serviceId=? ";
		List<GwModelDataFetchTaskVO> taskList = super.findByHql(hql, new Object[]{userId,serviceVo.getServiceId()});
    		
		for (GwModelDataFetchTaskVO dbTaskVO : taskList) {
			//挖掘平台的服务需要重新脱敏,108直接做检查
			GwModelDataFetchTaskVO newTaskVO = new GwModelDataFetchTaskVO();
			PropertyUtils.copyProperties(newTaskVO, dbTaskVO);
			
			newTaskVO.setTaskId(null);
			newTaskVO.setTaskStatus(CommonState.VALID);
			newTaskVO.setDataStatus(CommonState.VALID);
			newTaskVO.setCreateTime(new Date());
			newTaskVO.setDataNum(null);
			newTaskVO.setDownloadStartTime(new Date());
			newTaskVO.setDownloadEndTime(null);
			newTaskVO.setDownloadTime(null);
			newTaskVO.setDataProgressStatus(DataProgressStatus.DOWNLOAD_FINISH);	//修改脱敏规则后，重新生成任务
			save(newTaskVO);
//    		if(CommonState.SERVICE_SOURCE_108.equals(serviceVo.getServiceSource())) {
			//关联新的文件记录
			List<GwModelDataFileVO> fileList = super.findByHql("from GwModelDataFileVO where taskId=? and fileType=1 and fileStatus=1", new Object[]{dbTaskVO.getTaskId()});
			for (GwModelDataFileVO file : fileList) {
				GwModelDataFileVO newFileVO = new GwModelDataFileVO();
				PropertyUtils.copyProperties(newFileVO, file);
				newFileVO.setTaskId(newTaskVO.getTaskId());
				newFileVO.setCreateTime(new Date());
				newFileVO.setDeleteTime(null);
				super.save(newFileVO);
			}
//    		}
    		
			dbTaskVO.setTaskStatus(CommonState.INVALID);
			if(dbTaskVO.getDownloadEndTime() == null)
			dbTaskVO.setDownloadEndTime(new Date());
			update(dbTaskVO);
		}
    	return 1;
    }
    
    /**
     * 修改模型取数任务记录表的数据状态为无效
     * @param sourceDays
     * @param resultDays
     * @return
     * @throws Exception
     */
    public int updateTaskDataStatus(String tasks) throws Exception{
    	String sql = "update gw_model_data_fetch_task t set t.data_status=0 " +
    			"where t.task_id in ("+tasks+")";
    	return super.executeSql(sql, null);
    }
    
    public int updateDataProgressStatus(Long taskId,String passTag)throws Exception{
    	String sql="update gw_model_data_fetch_task t set t.data_progress_status=? where t.task_id="+taskId;
    	return super.executeSql(sql, new String[]{passTag});
    }
    
    public int searchRuleCheckCount(GwModelDataFetchTaskVO taskVO) throws Exception{
    	String sql = "select count(1) from gw_service_check_"+taskVO.getServiceId()+" where gateway_task_id="+taskVO.getTaskId();
    	return super.findIntBySql(sql, null);
    }
    
    //查询不合规数据，并导出到excel
    public HSSFWorkbook exportRuleCheckData(GwModelDataFetchTaskVO taskVO,List<GwServiceFieldVO> fieldList,int exportNum) throws Exception{
    	Connection con = super.getCurrentSession().connection();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		int count = searchRuleCheckCount(taskVO);
    		int pageSize = 5000;
    		int maxExportData = exportNum;				//根据页面条件导出行数
    		if(maxExportData > 65535) maxExportData = 65535;	//最多导出65536行
    		if(count > maxExportData) count = maxExportData;
        	int pageCount = count / pageSize;
        	if(count % pageSize != 0) pageCount++;
        	
        	log.info("taskId="+taskVO.getTaskId()+"不合规数据导出，数据组装开始。。。maxExportData="+maxExportData+",exportNum="+exportNum+",pageCount="+pageCount);
        	
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("合规检查结果");
        	//第一行表头，字段名称
        	HSSFRow row = sheet.createRow(0); 
        	
        	/*row.createCell(0).setCellValue("行号");
        	for (int j = 0;j < fieldList.size(); j++) {
        		GwServiceFieldVO fieldVO = fieldList.get(j);
        		row.createCell(j+1).setCellValue(fieldVO.getFieldCode());
        	}*/
            
        	//查询行数据
        	String sql = "select t.* from (select tmp.*,rownum rnum from (" +
	    			 "select c.gateway_row_id,c.row_data,r.fieldSorts from gw_service_check_"+taskVO.getServiceId()+" c " +
	    			 "left join (select task_id,row_id,wm_concat(field_sort) fieldSorts from gw_service_check_record rc " +
	    			 "where rc.task_id=? group by task_id,row_id) r " +
	    			 "on r.task_id=c.gateway_task_id and r.row_id=c.gateway_row_id " +
	    			 "where r.task_id=? order by gateway_row_id " +
	    			 ") tmp ) t where rnum>? and rnum<=? ";
        	
        	HSSFCell cell = null;
        	HSSFCellStyle style = wb.createCellStyle();  
        	HSSFFont font=wb.createFont();
        	font.setColor(HSSFFont.COLOR_RED);
        	style.setFont(font);
        	int rowIndex = 0;
        	for (int i = 1; i <= pageCount; i++) {
        		int start = (i-1)*pageSize;
        		int end = i*pageSize;
        		if(end>maxExportData) end = maxExportData;
        		log.info("分页查询：start="+start+",end="+end);
        		
    			ps = con.prepareStatement(sql);
    			ps.setLong(1, taskVO.getTaskId());
    			ps.setLong(2, taskVO.getTaskId());
    			ps.setInt(3, start);
    			ps.setInt(4, end);
    			log.info("导出分页查询开始"+i+":"+DateUtil.DateToString5(new Date()));
    			rs = ps.executeQuery();
    			log.info("导出分页查询结束"+i+":"+DateUtil.DateToString5(new Date()));
    			while (rs.next()) {
    				String[] fieldArray=rs.getObject("ROW_DATA").toString().split("\\|");
    				if(rowIndex==0){
    					//组装表头
    		        	row.createCell(0).setCellValue("行号");
    		        	for (int j = 0;j < fieldArray.length; j++) {
    		        		row.createCell(j+1).setCellValue("COLUMN"+(j+1));
    		        	}
    				}
    				rowIndex++;
    				row = sheet.createRow(rowIndex);
    				int cellIndex = 0;
    				//读取行数据，写入excel
    				cell = row.createCell(cellIndex);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(rs.getObject("GATEWAY_ROW_ID").toString());
					for(int j=0;j<fieldArray.length;j++){
						cellIndex++;
    					cell = row.createCell(cellIndex);
    					cell.setCellType(Cell.CELL_TYPE_STRING);
    					cell.setCellValue(fieldArray[j]);
    					int redFlag=j+1;
    					String[] redFieldCodeArray=rs.getObject("FIELDSORTS").toString().split(",");
    					for(String fieldCode:redFieldCodeArray){
    						if(fieldCode.equals(Integer.valueOf(redFlag).toString()))
    						//不合规的字段值，设为红色
							cell.setCellStyle(style);
    					}
					}
				}
    			rs = null;
    			ps = null;
    		}
        	log.info("taskId="+taskVO.getTaskId()+"不合规数据导出，数据组装结束。。。");
        	return wb;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(con != null) con.close();
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				log.error("关闭数据库连接失败："+e2.getMessage(),e2);
			}
		}
    }
    
    //查询不合规数据，并导出到txt
    public String searchRuleCheckDataForTxt(GwModelDataFetchTaskVO taskVO,List<GwServiceFieldVO> fieldList) throws Exception{
    	Connection con = super.getCurrentSession().connection();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		int count = searchRuleCheckCount(taskVO);
    		int pageSize = 5000;	
        	int pageCount = count / pageSize;			
        	if(count % pageSize != 0) pageCount++;
        	log.info("taskId="+taskVO.getTaskId()+"不合规数据生成txt开始!一共有"+pageCount+"页，每页有"+pageSize+"条数据----");
        	
        	String sql="select t.* from (select tmp.*,rownum rnum from ("+
                       " select c.gateway_row_id,c.row_data,r.fieldSort from gw_service_check_"+taskVO.getServiceId()+" c "+
                       " left join (select task_id,row_id,wm_concat(field_sort) fieldSort from gw_service_check_record rc "+
                       " where rc.task_id="+taskVO.getTaskId()+" group by task_id,row_id ) r "+
                       " on r.task_id=c.gateway_task_id and r.row_id=c.gateway_row_id "+
                       " where c.gateway_task_id="+taskVO.getTaskId()+" order by gateway_row_id "+
                        " ) tmp ) t where rnum>? and rnum<=?";
        	
        	String filePath = ConfigConstants.BASE_UPLOAD_FILE_PATH+"/tmp/";
        	File file = new File(filePath+new Date().getTime()+".txt");
        	
        	if(!new File(filePath).exists())
				new File(filePath).mkdirs();
        	
        	if(!file.exists()){
        		file.createNewFile();
        	}
        	Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "GBK");
        	StringBuilder builder = new StringBuilder();
        	String fieldListStr="";
        	
        	//第一行表头，字段名称
        	/*builder.append("序号,");
        	builder.append("行号,");
        	String fieldListStr="";
        	for (GwServiceFieldVO fieldVO : fieldList) {
        		fieldListStr += ("".equals(fieldListStr) ? "" : "|") +fieldVO.getFieldCode(); 
    		}
        	builder.append(fieldListStr);
        	builder.append(",字段顺序");
        	builder.append("\r\n");
        	writer.write(builder.toString());*/
        	
        	int index=0;//自定义序号
        	for (int i = 1; i <= pageCount; i++) {
        		log.info("正在导出start="+(i-1)*pageSize+", end="+i*pageSize);
    			ps = con.prepareStatement(sql);
    			ps.setInt(1, (i-1)*pageSize);
    			ps.setInt(2, i*pageSize);
    			rs = ps.executeQuery();
    			while (rs.next()) {
    				index++;
    				builder = new StringBuilder();
    				
    				//组装表头
    				String[] fieldArray=rs.getObject("ROW_DATA").toString().split("\\|");
    				if("".equals(fieldListStr)){
    					for (int j = 0;j < fieldArray.length; j++) {
    						fieldListStr += ("".equals(fieldListStr) ? "" : "|") +"COLUMN"+(j+1);
    		        	}
    					builder.append("序号,");
    		        	builder.append("行号,");
    					builder.append(fieldListStr);
    		        	builder.append(",字段顺序");
    		        	builder.append("\r\n");
    				}
    				
    				//行数据
    				builder.append(index+",");
    				builder.append(rs.getObject("GATEWAY_ROW_ID")+",");
    				builder.append(rs.getObject("ROW_DATA")+",");
    				String fieldSort = toString(rs.getObject("fieldSort"));
    				fieldSort=fieldSort.replace(",", "|");
    				builder.append(fieldSort);
    				builder.append("\r\n");
    				writer.write(builder.toString());
				}
    		}
        	writer.close();
        	log.info("taskId="+taskVO.getTaskId()+"不合规数据生成txt完毕！----");
        	return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(con != null) con.close();
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				log.error("关闭数据库连接失败："+e2.getMessage(),e2);
			}
		}
    }
    
    /**
     * 
     * @Title: redesenTask 
     * @Description: TODO(重新生成服务检查) 
     * @param @param palanId
     * @param @return
     * @param @throws Exception    设定文件 
     * @return int    返回类型 
     * @throws
     */
    public int redesenTask(Long taskId) throws Exception{
		GwModelDataFetchTaskVO dbTaskVO = findById(taskId);
    		
		//挖掘平台的服务需要重新脱敏,108直接做检查
		GwModelDataFetchTaskVO newTaskVO = new GwModelDataFetchTaskVO();
		PropertyUtils.copyProperties(newTaskVO, dbTaskVO);
		
		newTaskVO.setTaskId(null);
		newTaskVO.setTaskStatus(CommonState.VALID);
		newTaskVO.setDataStatus(CommonState.VALID);
		newTaskVO.setCreateTime(new Date());
		newTaskVO.setDataNum(null);
		newTaskVO.setDownloadStartTime(new Date());
		newTaskVO.setDownloadEndTime(null);
		newTaskVO.setDownloadTime(null);
		newTaskVO.setDataProgressStatus(DataProgressStatus.DOWNLOAD_FINISH);	//修改脱敏规则后，重新生成任务
		save(newTaskVO);
		
		//关联新的文件记录
		List<GwModelDataFileVO> fileList = super.findByHql("from GwModelDataFileVO where taskId=? and fileType=1 and fileStatus=1", new Object[]{dbTaskVO.getTaskId()});
		for (GwModelDataFileVO file : fileList) {
			GwModelDataFileVO newFileVO = new GwModelDataFileVO();
			PropertyUtils.copyProperties(newFileVO, file);
			newFileVO.setTaskId(newTaskVO.getTaskId());
			newFileVO.setCreateTime(new Date());
			newFileVO.setDeleteTime(null);
			super.save(newFileVO);
		}
		
		dbTaskVO.setRedoTag(CommonState.REDO_TAG);
		update(dbTaskVO);
			
    	return 1;
    }
    
    private String toString(Object value){
    	if(value==null) return "";
    	return value.toString();
    }
    
    /** 查询所有文件合规检查行数的总数 
     * @throws Exception */
    public String searchRuleCheckRowNum(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate) throws Exception{
    	String sql = "select sum(check_num) from gw_model_data_fetch_task t " +
    			"left join gw_service s on s.service_id=t.service_id " +
    			"left join gw_model m on m.model_id=s.model_id " +
    			"left join gw_user u on u.user_id=t.user_id " +
    			"where 1=1 ";
    	List paramsList = new ArrayList();
    	if(StringUtils.isNotBlank(userIds)){
    		sql += "and t.user_id in ("+userIds+") ";
    	}
    	if(StringUtils.isNotBlank(modelCode)){
			sql += "and m.model_Code like ? ";
			paramsList.add("%"+modelCode.trim()+"%");
		}
		if(StringUtils.isNotBlank(modelName)){
			sql += "and m.model_Name like ? ";
			paramsList.add("%"+modelName.trim()+"%");
		}
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_Name  like ? ";
			paramsList.add("%"+loginName.trim()+"%");
		}
		if(startDate != null){
			sql += "and t.download_start_time >= ? ";
			paramsList.add(startDate);
		}
		if(endDate != null){
			sql += "and t.download_end_time <= ? ";
			paramsList.add(endDate);
		}
		if(StringUtils.isNotBlank(serviceCode)){
			sql += "and s.service_Code like ? ";
			paramsList.add("%"+serviceCode.trim()+"%");
		}
		List list = super.findListBySql(sql, paramsList.toArray(), null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
    }
    
    /** 查询所有检查合规的文件数 
     * @throws Exception */
    public String searchRuleCheckPassCount(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate) throws Exception{
    	String sql = "select count(1) from gw_model_data_fetch_task t " +
    			"left join gw_service s on s.service_id=t.service_id " +
    			"left join gw_model m on m.model_id=s.model_id " +
    			"left join gw_user u on u.user_id=t.user_id " +
    			"where t.check_result=0 ";
    	List paramsList = new ArrayList();
    	if(StringUtils.isNotBlank(userIds)){
    		sql += "and t.user_id in ("+userIds+") ";
    	}
    	if(StringUtils.isNotBlank(modelCode)){
			sql += "and m.model_Code like ? ";
			paramsList.add("%"+modelCode.trim()+"%");
		}
		if(StringUtils.isNotBlank(modelName)){
			sql += "and m.model_Name like ? ";
			paramsList.add("%"+modelName.trim()+"%");
		}
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_Name  like ? ";
			paramsList.add("%"+loginName.trim()+"%");
		}
		if(startDate != null){
			sql += "and t.download_start_time >= ? ";
			paramsList.add(startDate);
		}
		if(endDate != null){
			sql += "and t.download_end_time <= ? ";
			paramsList.add(endDate);
		}
		if(StringUtils.isNotBlank(serviceCode)){
			sql += "and s.service_Code like ? ";
			paramsList.add("%"+serviceCode.trim()+"%");
		}
		List list = super.findListBySql(sql, paramsList.toArray(), null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
    }
    
    /** 查询所有合规文件的总行数 
     * @throws Exception */
    public String searchRuleCheckPassDataNum(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate) throws Exception{
    	String sql = "select sum(data_num) from gw_model_data_fetch_task t " +
    			"left join gw_service s on s.service_id=t.service_id " +
    			"left join gw_model m on m.model_id=s.model_id " +
    			"left join gw_user u on u.user_id=t.user_id " +
    			"where t.check_result=0 and t.data_progress_status='12' ";
    	List paramsList = new ArrayList();
    	if(StringUtils.isNotBlank(userIds)){
    		sql += "and t.user_id in ("+userIds+") ";
    	}
    	if(StringUtils.isNotBlank(modelCode)){
			sql += "and m.model_Code like ? ";
			paramsList.add("%"+modelCode.trim()+"%");
		}
		if(StringUtils.isNotBlank(modelName)){
			sql += "and m.model_Name like ? ";
			paramsList.add("%"+modelName.trim()+"%");
		}
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_Name  like ? ";
			paramsList.add("%"+loginName.trim()+"%");
		}
		if(startDate != null){
			sql += "and t.download_start_time >= ? ";
			paramsList.add(startDate);
		}
		if(endDate != null){
			sql += "and t.download_end_time <= ? ";
			paramsList.add(endDate);
		}
		if(StringUtils.isNotBlank(serviceCode)){
			sql += "and s.service_Code like ? ";
			paramsList.add("%"+serviceCode.trim()+"%");
		}
		List list = super.findListBySql(sql, paramsList.toArray(), null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
    }
    
    /** 查询总数据量 
     * @throws Exception */
    public String searchRuleCheckOutputDataNum(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate) throws Exception{
    	String sql = "select sum(data_num * field_num) from ("+
    			"select data_num ,v.field_num " +
    			"from gw_model_data_fetch_task t " +
    			"left join gw_service s on s.service_id=t.service_id " +
    			"left join gw_model m on m.model_id=s.model_id " +
    			"left join gw_user u on u.user_id=t.user_id " +
    			"left join v_service_field_count v on v.task_id=t.task_id " +
    			"where t.check_result=0 and t.data_progress_status='12' ";
    	List paramsList = new ArrayList();
    	if(StringUtils.isNotBlank(userIds)){
    		sql += "and t.user_id in ("+userIds+") ";
    	}
    	if(StringUtils.isNotBlank(modelCode)){
			sql += "and m.model_Code like ? ";
			paramsList.add("%"+modelCode.trim()+"%");
		}
		if(StringUtils.isNotBlank(modelName)){
			sql += "and m.model_Name like ? ";
			paramsList.add("%"+modelName.trim()+"%");
		}
		if(StringUtils.isNotBlank(loginName)){
			sql += "and u.login_Name  like ? ";
			paramsList.add("%"+loginName.trim()+"%");
		}
		if(startDate != null){
			sql += "and t.download_start_time >= ? ";
			paramsList.add(startDate);
		}
		if(endDate != null){
			sql += "and t.download_end_time <= ? ";
			paramsList.add(endDate);
		}
		if(StringUtils.isNotBlank(serviceCode)){
			sql += "and s.service_Code like ? ";
			paramsList.add("%"+serviceCode.trim()+"%");
		}
		sql += ") ";
		List list = super.findListBySql(sql, paramsList.toArray(), null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
		
    }
    
    public List<GwModelDataFetchTaskVO> searchWaitPushTask(int pushCount) throws Exception{
    	String hql = "from GwModelDataFetchTaskVO where (dataProgressStatus=? or dataProgressStatus=?) and taskStatus=1 and dataStatus=1 and (pushCount<? or pushCount is null)";
    	/*String status = "+DataProgressStatus.RULE_CHECK_REPUSH+","+DataProgressStatus.RULE_CHECK_PUSHING";*/
    	return super.findByHql(hql, new Object[]{DataProgressStatus.RULE_CHECK_REPUSH,DataProgressStatus.RULE_CHECK_PUSHING,pushCount});
    }
    
    public List<GwModelDataFileVO> searchSameFile(Long userId,String path,String fileName) throws Exception{
    	String sql = "select FILE_ID,MODEL_ID,TASK_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,DELETE_TIME,FTP_IP,FTP_PORT,FTP_User,FTP_PASSWORD,UNZIP_NAME from "+
    			"GW_MODEL_DATA_FILE where 1=1 ";
    	List paramsList = new ArrayList();
    	if(userId != null){
    		sql += "and USER_ID = ? ";
    		paramsList.add(userId);
    	}
    	sql += "and FILE_TYPE = 1 and FILE_STATUS = 1 ";
    	
    	if(StringUtils.isNotBlank(path)){
    		sql += "and FILE_PATH = ?";
    		paramsList.add(path);
    	}
    	
    	if(StringUtils.isNotBlank(fileName)){
    		sql += "and FILE_NAME = ?";
    		paramsList.add(fileName);
    	}
    	return  (List<GwModelDataFileVO>)super.findListBySql(sql, paramsList.toArray(), GwModelDataFileVO.class);
		
    }
    
    public int onlineCheck(GwUserVO acceptUser,GwServiceVO serviceVO,Long fetchId,Date auditTime,String fileNameList,GwUserVO loginUser,GwUserVO orgUser) throws Exception{
    	String[] list=fileNameList.split(";");
    	for(String fileName:list){
    		String path="/home/gateway/vuser/"+orgUser.getLoginName()+"/"+acceptUser.getLoginName();
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
			String dateStr = df.format(new Date());
    		//检查历史任务是否有同名文件
    		List<GwModelDataFileVO> fileList = searchSameFile(acceptUser.getUserId(), path, fileName);
    		String newFileName = fileName.substring(0, fileName.indexOf("."))+"_"+dateStr+fileName.substring(fileName.indexOf("."),fileName.length());
    		 
    		if(fileList !=null && !fileList.isEmpty()){
    			String UzipFileName = "";
    			String newUzipFileName = "";
    			for(GwModelDataFileVO filetmp:fileList){
    				if(filetmp.getUnzipName()!=null){
    					UzipFileName = filetmp.getUnzipName();
    				}
    				else{
    					UzipFileName = filetmp.getFileName().substring(0,fileName.lastIndexOf("."));
    					filetmp.setUnzipName(UzipFileName);
    				}
    				newUzipFileName = filetmp.getUnzipName().substring(0, filetmp.getUnzipName().indexOf("."))+"_"+dateStr+filetmp.getUnzipName().substring(filetmp.getUnzipName().indexOf("."),filetmp.getUnzipName().length());
    				filetmp.setFileName(newFileName);
    				filetmp.setUnzipName(newUzipFileName);
    				super.update(filetmp);
    			}
    			
    			//文件重命名
        		String command = ConfigConstants.RENAME_FILE_COMMAND + " "+path+"/"+fileName+" "+path+"/"+newFileName;
        		ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
        		
        		if(StringUtils.isNotBlank(newUzipFileName)  && StringUtils.isNotBlank(UzipFileName)){
        			command = ConfigConstants.RENAME_FILE_COMMAND + " "+path+"/"+UzipFileName+" "+path+"/"+newUzipFileName;
        			ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
        			
        			command = ConfigConstants.RENAME_FILE_COMMAND + " "+path+"/"+"Done-"+UzipFileName+" "+path+"/"+"Done-"+newUzipFileName;
        			ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
        			 
        		}
        		
        		
        		
    		}

    		
    		
    		
    		GwModelDataFetchTaskVO newTaskVO = new GwModelDataFetchTaskVO();
			newTaskVO.setTaskId(null);
			newTaskVO.setUserId(acceptUser.getUserId());
			newTaskVO.setServiceId(serviceVO.getServiceId());
			newTaskVO.setModelId(serviceVO.getModelId());
			newTaskVO.setTaskStatus(CommonState.VALID);
			newTaskVO.setDataStatus(CommonState.VALID);
			newTaskVO.setCreateTime(new Date());
			newTaskVO.setCreateUser(loginUser.getLoginName());
			newTaskVO.setDataNum(null);
			newTaskVO.setDownloadStartTime(new Date());
			newTaskVO.setDownloadEndTime(null);
			newTaskVO.setDownloadTime(null);
			newTaskVO.setAuditTime(auditTime);
			newTaskVO.setFieldValue("实时");
			newTaskVO.setFetchId(fetchId);
			newTaskVO.setDataProgressStatus(DataProgressStatus.READY_DATA);	
			save(newTaskVO);
			
			GwModelDataFileVO newFileVO = new GwModelDataFileVO();
			newFileVO.setTaskId(newTaskVO.getTaskId());
			newFileVO.setModelId(serviceVO.getModelId());
			newFileVO.setUserId(acceptUser.getUserId());
			newFileVO.setFileType("1");
			newFileVO.setFileStatus(CommonState.VALID);
			newFileVO.setFileName(fileName);
			newFileVO.setCreateTime(new Date());
			
			newFileVO.setFilePath(path);
			newFileVO.setFtpIp(acceptUser.getFtpIp());
			newFileVO.setFtpPort(acceptUser.getFtpPort());
			newFileVO.setFtpUser(acceptUser.getFtpUsername());
			newFileVO.setFtpPassword(acceptUser.getFtpPassword());
			super.save(newFileVO);
    	}
    	return 1;
    }
    
    public String searchServiceCheckRecord(Long taskId)throws Exception{
    	String sql = "select count(*) from gw_service_check_record where task_id = " +taskId;
    	List list = super.findListBySql(sql, null, null);
		return list.size()>0 && list.get(0)!=null?list.get(0).toString():"0";
    }
}