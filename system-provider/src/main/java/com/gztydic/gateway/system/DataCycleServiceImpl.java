package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.SFTPRemoteUtil;
import com.gztydic.gateway.core.dao.GwModelDataCycleDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.ModelFileView;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/**
 * 模型数据结果集存放周期配置
 * @author wangwei
 *
 */
@Service
@SuppressWarnings("unchecked")
public class DataCycleServiceImpl extends GeneralServiceImpl<GwModelDataCycleVO> implements DataCycleService{

	private final Log log = LogFactory.getLog(DataCycleServiceImpl.class);
	
	@Resource
	private GwModelDataCycleDAO gwModelDataCycleDAO;
	@Resource
	private GwModelDataFileDAO gwModelDataFileDAO;
	@Resource
	private GwModelDataFetchTaskDAO gwModelDataFetchTaskDAO;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService ;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public List<GwModelDataCycleVO> searchList() throws Exception{
		return gwModelDataCycleDAO.findAll();
	}

	private Map<Long, GwModelDataCycleVO> convert2Map(List<GwModelDataCycleVO> list){
		Map<Long, GwModelDataCycleVO> map = new HashMap<Long, GwModelDataCycleVO>();
		for (GwModelDataCycleVO vo : list) {
			map.put(vo.getCycleId(), vo);
		}
		return map;
	}
	
	/**
	 * 新增、修改周期配置
	 */
	public void updateDataCycleList(List<GwModelDataCycleVO> list,GwUserVO userVO) throws Exception {
		if(list.size() == 0) throw new Exception("数据存放周期配置不能为空");
		try {
			Map<Long, GwModelDataCycleVO> cycleMap = convert2Map(searchList());
			for (GwModelDataCycleVO vo : list) {
				GwModelDataCycleVO dbVO = cycleMap.get(vo.getCycleId());
				if(dbVO == null){	//新增
					vo.setCycleId(null);
					vo.setCreateUser(userVO.getLoginName());
					vo.setCreateTime(new Date());
					super.save(vo);
				}else {	//修改
					dbVO.setCycleType(vo.getCycleType());
					dbVO.setDataType(vo.getDataType());
					dbVO.setCycleNum(vo.getCycleNum());
					dbVO.setUpdateUser(userVO.getLoginName());
					dbVO.setUpdateTime(new Date());
					super.saveOrUpdate(dbVO);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("新增或修改周期配置出错:"+e.getMessage(),e);
		}
	}
	
	/**
	 * 根据数据存放周期配置查询过期缓存数据
	 */
	public void searchCleanCacheData() throws Exception{
		List<GwModelDataCycleVO> dataCycleList = gwModelDataCycleDAO.findAll();
		if(dataCycleList.size() == 0) {
			log.info("未配置数据存放周期");
			return;
		}
		
		log.info("清除过期数据开始："+JSONArray.fromObject(dataCycleList).toString());
		Date date = new Date();
		//获得原始数据、脱敏文件过期天数
		int sourceDays=0, resultDays=0;
		for (GwModelDataCycleVO vo : dataCycleList) {
			Calendar c = Calendar.getInstance();
			if("1".equals(vo.getCycleType())){
				c.set(Calendar.YEAR,Integer.parseInt(DateUtil.DateToYear(date)) + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("2".equals(vo.getCycleType())){
				c.set(Calendar.MONTH,3 * Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("3".equals(vo.getCycleType())){
				c.set(Calendar.MONTH, Integer.parseInt(DateUtil.DateToMonth(date)) - 1 + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("4".equals(vo.getCycleType())){
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DateUtil.DateToDay(date)) + 7 * Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("5".equals(vo.getCycleType())){
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DateUtil.DateToDay(date)) + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}
			
			if("1".equals(vo.getDataType())){	//原始文件
				sourceDays=DateUtil.getDays(date, c.getTime());
			}else if("2".equals(vo.getDataType())){ //脱敏文件
				resultDays=DateUtil.getDays(date, c.getTime());
			}
		}
		
		String files = "", tasks = "";
		
		//查询已过期的脱敏后的文件记录
		List<ModelFileView> modelDataFileList=gwModelDataFileDAO.searchModelDataFile(resultDays,"2");
		if(modelDataFileList.size() != 0) {
			for(ModelFileView modelFileView:modelDataFileList){
				files += ("".equals(files) ? "" : ",") + "'"+modelFileView.getFileId()+"'"; 
				tasks += ("".equals(tasks) ? "" : ",") + "'"+modelFileView.getTaskId()+"'"; 
			}
		}
		
		//查询已过期的原始文件记录
		List<ModelFileView> modelSourceDataFileList=gwModelDataFileDAO.searchModelSourceFile(sourceDays);
		if(modelSourceDataFileList.size() != 0) {
			for(ModelFileView modelFileView:modelSourceDataFileList){
				files += ("".equals(files) ? "" : ",") + "'"+modelFileView.getFileId()+"'"; 
				tasks += ("".equals(tasks) ? "" : ",") + "'"+modelFileView.getTaskId()+"'"; 
			}
			
		}
		//将文件状态设置为已失效
		if(!"".equals(files)) gwModelDataFileDAO.updateModelDataFileStatus(files,CommonState.INVALID);
		if(!"".equals(tasks)) gwModelDataFetchTaskDAO.updateTaskDataStatus(tasks);
		if(!"".equals(files)){
			//生成待办
			String planContentStr = "数据文件已过期，请及时清理!";
			String msgContent="数据文件已过期，请您及时清理!【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("过期数据清理",WorkPlanConstent.DATA_CLEAN , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,null,null, null,msgContent,null);
			Map map=new HashMap();
			map.put("fileIds",files);
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
		}
		
	}
	
	/**
	 * 根据数据存放周期配置清理过期缓存数据
	 */
	public void cleanCacheData() throws Exception{
		List<GwModelDataCycleVO> dataCycleList = gwModelDataCycleDAO.findAll();
		if(dataCycleList.size() == 0) {
			log.info("未配置数据存放周期");
			return;
		}
		
		log.info("清除过期数据开始："+JSONArray.fromObject(dataCycleList).toString());
		Date date = new Date();
		//获得原始数据、脱敏文件过期天数
		int sourceDays=0, resultDays=0;
		for (GwModelDataCycleVO vo : dataCycleList) {
			Calendar c = Calendar.getInstance();
			if("1".equals(vo.getCycleType())){
				c.set(Calendar.YEAR,Integer.parseInt(DateUtil.DateToYear(date)) + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("2".equals(vo.getCycleType())){
				c.set(Calendar.MONTH,Integer.parseInt(DateUtil.DateToMonth(date)) - 1 +3 * Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("3".equals(vo.getCycleType())){
				c.set(Calendar.MONTH, Integer.parseInt(DateUtil.DateToMonth(date)) - 1 + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("4".equals(vo.getCycleType())){
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DateUtil.DateToDay(date)) + 7 * Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}else if("5".equals(vo.getCycleType())){
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DateUtil.DateToDay(date)) + Integer.parseInt(String.valueOf(vo.getCycleNum())));
			}
			
			if("1".equals(vo.getDataType())){	//原始文件
				sourceDays=DateUtil.getDays(date, c.getTime());
			}else if("2".equals(vo.getDataType())){ //脱敏文件
				resultDays=DateUtil.getDays(date, c.getTime());
			}
		}
		log.info("清理过期数据：sourceDays="+sourceDays+", resultDays="+resultDays);
		
		String files = "", tasks = "";
		//查询已过期的不合规的文件记录
		List<ModelFileView> modelDataFileList=gwModelDataFileDAO.searchModelDataFile(resultDays,"3");
		if(modelDataFileList.size() != 0) {
			log.info("清除不合规记录文件："+JSONArray.fromObject(modelDataFileList).toString());
			List<String> fileNameList=new ArrayList<String>();
			Map<String, String> fileMap = new HashMap<String, String>();
			for(ModelFileView modelFileView:modelDataFileList){
				fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getFileName(), "");
				
				if(StringUtils.isNotBlank(modelFileView.getUnzipName())){
					fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getUnzipName(), "");
				}
			}
			
			Iterator<String> it = fileMap.keySet().iterator();
			while (it.hasNext()) {
				fileNameList.add(it.next());
			}
			//删除ftp脱敏后的结果文件
			SFTPRemoteUtil.deleteFile(ConfigConstants.FTP_SERVER_IP, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, Integer.parseInt(ConfigConstants.FTP_SERVER_PORT), fileNameList);
		}
		//查询已过期的脱敏后的文件记录
		modelDataFileList=gwModelDataFileDAO.searchModelDataFile(resultDays,"2");
		if(modelDataFileList.size() != 0) {
			log.info("清除结果文件："+JSONArray.fromObject(modelDataFileList).toString());
			List<String> fileNameList=new ArrayList<String>();
			Map<String, String> fileMap = new HashMap<String, String>();
			for(ModelFileView modelFileView:modelDataFileList){
				files += ("".equals(files) ? "" : ",") + "'"+modelFileView.getFileId()+"'"; 
				tasks += ("".equals(tasks) ? "" : ",") + "'"+modelFileView.getTaskId()+"'"; 
				
				fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getFileName(), "");
				if(StringUtils.isNotBlank(modelFileView.getUnzipName())){
					fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getUnzipName(), "");
				}
				
				//清除缓存的脱敏数据
				gwModelDataFileDAO.deleteDesenCacheData(modelFileView);
				
				//清理检查结果数据
				gwModelDataFileDAO.deleteCheckResultData(modelFileView);
			}
			
			Iterator<String> it = fileMap.keySet().iterator();
			while (it.hasNext()) {
				fileNameList.add(it.next());
			}
			//删除ftp脱敏后的结果文件
			SFTPRemoteUtil.deleteFile(ConfigConstants.FTP_SERVER_IP, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, Integer.parseInt(ConfigConstants.FTP_SERVER_PORT), fileNameList);
		}
		//查询已过期的原始文件记录
		List<ModelFileView> modelSourceDataFileList=gwModelDataFileDAO.searchModelSourceFile(sourceDays);
		if(modelSourceDataFileList.size() != 0) {
			log.info("清除原始文件："+JSONArray.fromObject(modelSourceDataFileList).toString());
			List<String> sourceFileNameList=new ArrayList<String>();
			Map<String, String> fileMap = new HashMap<String, String>();
			for(ModelFileView modelFileView:modelSourceDataFileList){
				files += ("".equals(files) ? "" : ",") + "'"+modelFileView.getFileId()+"'"; 
				tasks += ("".equals(tasks) ? "" : ",") + "'"+modelFileView.getTaskId()+"'"; 
				
				//用map去除重复的文件路径
				if(CommonState.SERVICE_SOURCE_108.equals(modelFileView.getServiceSource())){
					//108输出文件增加上传完成的标识文件：Done_10002-3-201503.txt
					String txtName = modelFileView.getFileName().substring(0,modelFileView.getFileName().indexOf("."))+".txt";
					fileMap.put(modelFileView.getFilePath()+"/Done_"+txtName, "");
				}
				//用map去除重复的文件路径
				fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getFileName(), "");
				if(StringUtils.isNotBlank(modelFileView.getUnzipName())){
					fileMap.put(modelFileView.getFilePath()+"/"+modelFileView.getUnzipName(), "");
				}
				
				//清除缓存表的数据
				gwModelDataFileDAO.deleteCacheData(modelFileView);
			}
			
			Iterator<String> it = fileMap.keySet().iterator();
			while (it.hasNext()) {
				sourceFileNameList.add(it.next());
			}
			//删除ftp原始文件
			SFTPRemoteUtil.deleteFile(ConfigConstants.FTP_SERVER_IP, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, Integer.parseInt(ConfigConstants.FTP_SERVER_PORT), sourceFileNameList);
		}
		log.info("清理过期数据后，将文件状态设为已删除。fileIds=("+files+"),taskIds=("+tasks+")");
		//将文件状态设置为已删除
		if(!"".equals(files)) gwModelDataFileDAO.updateModelDataFileStatus(files,CommonState.DELETE);
		
		log.info("清除过期数据结束。。。");
	}
}
