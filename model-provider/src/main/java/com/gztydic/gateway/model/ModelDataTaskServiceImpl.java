package com.gztydic.gateway.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.PushFtpUtil;
import com.gztydic.gateway.core.common.util.PushWebserviceUtil;
import com.gztydic.gateway.core.common.util.ShellUtil;
import com.gztydic.gateway.core.dao.DataGatherDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.dao.GwOperationLogDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.dao.GwSmsDAO;
import com.gztydic.gateway.core.dao.GwUserButtonDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.DataEntity;
import com.gztydic.gateway.core.view.ServiceCycleAppView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.gather.DataGatherService;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.system.ConfigService;
import com.gztydic.gateway.system.LiabilityService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;

/** 
 * @ClassName: ModelDataTaskServiceImpl 
 * @Description: TODO(模型取数任务接口实现类) 
 * @author davis
 * @date 2014-11-19 下午03:11:51 
 *  
 */
@Service
public class ModelDataTaskServiceImpl extends GeneralServiceImpl<GwModelDataFetchTaskVO> implements ModelDataTaskService {

	private static final Log log = LogFactory.getLog(ModelDataTaskServiceImpl.class);
	
	@Resource
	private GwModelDataFetchTaskDAO gwModelDataFetchTaskDAO;
	@Resource
	private GwModelDataFetchDAO gwModelDataFetchDAO;
	@Resource
	private DataGatherDAO dataDesenDAO;
	
	@Resource
	private GwServiceDAO serviceDAO;
	
	@Resource(name="dataGatherServiceImpl")
	private DataGatherService dataGatherService;
	
	@Resource
	private GwModelDataFileDAO gwModelDataFileDAO;
	
	@Resource
	private GwServiceFieldDAO gwServiceFieldDAO;
	
	@Resource
	private GwOperationLogDAO gwOperationLogDAO;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	@Resource(name="userServiceImpl")
	private UserService userService;
	
	@Resource(name="configServiceImpl")
	private ConfigService configService;
	
	@Resource(name="liabilityServiceImpl")
	private LiabilityService liabilityService;
	
	@Resource
	private GwUserButtonDAO gwUserButtonDAO;
	
	@Resource
	private GwSmsDAO gwSmsDAO;
	
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService;
	
	public List<GwModelDataFetchTaskDAO> searchList() throws Exception{
		return gwModelDataFetchTaskDAO.findAll();
	}
	
	/* 
	 * <p>Title: updateTaskStatusByTaskId</p> 
	 * <p>Description: 根据模型取数任务ID修改任务状态</p> 
	 * @param taskId 任务ID
	 * @param taskStatus 目标任务状态
	 * @throws Exception  
	 */
	public void updateTaskStatusByTaskId(Long taskId,String taskStatus,GwUserVO loginUserVO) throws Exception{
		GwModelDataFetchTaskVO taskVo = gwModelDataFetchTaskDAO.findById(taskId);
		String preDataProgressStatus=taskVo.getDataProgressStatus();
		if(taskVo != null){
			taskVo.setPreDataProgressStatus(preDataProgressStatus);
			taskVo.setDataProgressStatus(DataProgressStatus.MANUAL_STOP);
			gwModelDataFetchTaskDAO.update(taskVo);
		}
		
		Long planId=workPlanService.searchCheckWorkPlan(taskId);
		
		if(planId!=0){
			List<String> userMobilesList=gwUserButtonDAO.searchUserByPlanType("19");
			String moblieStr="";
			for(String moblie : userMobilesList){
				moblieStr+=(moblieStr==""?"":"|");
				moblieStr+=moblie;
			}
			Map<String, String> paramMap=workPlanParamService.searchParamMap(planId);
			String userId=paramMap.get("userId");
			String serviceId=paramMap.get("serviceId");
			GwUserVO userVO=userService.searchUserDetail(Long.valueOf(userId));
			GwServiceVO serviceVO=serviceDAO.findById(Long.valueOf(serviceId));
			String smsContent="用户："+loginUserVO.getLoginName()+"手工终止服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+("0".equals(serviceVO.getServiceType())?"实时":"离线")+")，该服务的待办不需要处理，请知晓。【数据网关平台】";
			GwSmsVO gwSmsVO=new GwSmsVO();
			gwSmsVO.setSendStatus(null);
			gwSmsVO.setSendResult(null);
			gwSmsVO.setSendCount(0);
			gwSmsVO.setCreateTime(new Date());
			gwSmsVO.setSendTime(null);
			gwSmsVO.setSmsContent(smsContent);
			gwSmsVO.setSmsMobile(moblieStr);
			gwSmsDAO.saveOrUpdate(gwSmsVO);
			
			workPlanService.updateWorkPlanState(planId,gwSmsVO.getSmsId());
			
		}
	}
	
	/** 
	 * @Title: searchAllPage 
	 * @Description: TODO(全查询模型取数任务列表信息) 
	 * @param @param modelCode
	 * @param @param modelName
	 * @param @param userName
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllPage(String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception{
		return gwModelDataFetchTaskDAO.findAllByPage(modelCode, modelName, loginName, serviceCode,startDate, endDate,dataProgressStatus, pageObject);
	} 
	
	/** 
	 * @Title: searchAllPage 
	 * @Description: TODO(查询某些用户模型取数任务列表信息) 
	 * @param @param userIds
	 * @param @param modelCode
	 * @param @param modelName
	 * @param @param userName
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllPage(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception{
		return gwModelDataFetchTaskDAO.findAllByPageAndUserId(userIds,modelCode, modelName, loginName, serviceCode,startDate, endDate,dataProgressStatus, pageObject);
	} 
	
	/**
	 * @Title: searchServiceTaskNoLocal 
	 * @Description: TODO(查询相同服务ID相同周期申请任务列表) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List    返回类型 
	 * @throws
	 */
	public List<ServiceCycleAppView> searchServiceTaskNoLocal()throws Exception{
		return gwModelDataFetchTaskDAO.findServiceTaskList();
	}

	public Map<String, String> searchServiceName(Long serviceId)throws Exception{
		return dataDesenDAO.searchServiceName(serviceId);
	}

	/**
	 * 
	 * @Title: getServiceDataAppAndUpdate 
	 * @Description: TODO(某一服务申请并修改不同用户对同服务同周期任务状态修改) 
	 * @param @param appView
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void doServiceDataAppAndUpdate(ServiceCycleAppView appView) throws Exception{
		GwModelDataFetchTaskVO taskVO = gwModelDataFetchTaskDAO.findById(appView.getTaskId());
		//向数据中心发起离线服务请求
		DataResponse dataResponse = dataGatherService.doGetServiceData(serviceDAO.findById(appView.getServiceid()), taskVO, null);
		//申请成功修改任务信息
		if(dataResponse.isResult()){ 
			gwModelDataFetchTaskDAO.batchUpdateModelDataFetchTask(appView.getTaskId(), DataProgressStatus.REQUEST_DATA, 0);
		}else {
			log.error("taskId="+appView.getTaskId()+"请求离线服务失败，原因："+dataResponse.getInfo());
		}
	}
	
	/**
	 * @Title: searchOnlineServiceData 
	 * @Description: TODO(实时取数并脱敏方法) 
	 * @param @param fetchVO
	 * @param @return    设定文件 
	 * @return Map    返回类型 
	 * @throws
	 */
	public Map searchOnlineServiceData(GwModelDataFetchVO fetchVO,GwUserVO loginUser)throws Exception{
		GwModelDataFetchTaskVO taskVO = new GwModelDataFetchTaskVO();
		taskVO.setFetchId(fetchVO.getFetchId());
		taskVO.setServiceId(fetchVO.getServiceId());
		taskVO.setUserId(fetchVO.getUserId());
		taskVO.setModelId(fetchVO.getModelId());
		taskVO.setDataProgressStatus(DataProgressStatus.DATA_DESEN);
		taskVO.setDataStatus(CommonState.NORMAL);
		taskVO.setDataSource(DataTypeConstent.DATACENTER);
		taskVO.setAuditTime(fetchVO.getAuditTime());
		taskVO.setCreateTime(new Date());
		taskVO.setCreateUser(loginUser.getLoginName());
		taskVO.setDownloadStartTime(new Date());
		taskVO.setDataNum(0l);
		taskVO.setDataType(DataTypeConstent.TABLE);
		taskVO.setTaskStatus(CommonState.VALID);		
		gwModelDataFetchTaskDAO.save(taskVO);
		
		//实时取数接口并脱敏
		Map resultMap = dataGatherService.getOnlineServiceData(fetchVO.getServiceId(), taskVO, loginUser);
		List fieldValueList = (List)resultMap.get("fieldValueList");
		taskVO.setDataProgressStatus(DataProgressStatus.RESULT_FINISH);
		taskVO.setCheckResult("0");
		taskVO.setDataNum(fieldValueList!=null?fieldValueList.size():0l);
		taskVO.setDownloadEndTime(new Date());
		taskVO.setDownloadTime(new Date());
		gwModelDataFetchTaskDAO.update(taskVO);
		return resultMap;
	}

	/**
	 * 查询实时服务预览数据
	 * @param serviceId
	 * @param userId
	 * @param fieldCode
	 * @param fieldValue
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> searchOnlinePreviewServiceData(GwModelDataFetchTaskVO taskVO) throws Exception{
		return dataGatherService.getOnlinePreviewServiceData(taskVO);
	}
	
	/**
	 * 查询离线预览数据
	 * @param serviceId
	 * @param userId
	 * @param fieldCode
	 * @param fieldValue
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> searchOfflinePreviewServiceData(GwModelDataFetchTaskVO taskVO,PageObject pageObject) throws Exception{
		return dataDesenDAO.searchOfflinePreviewServiceData(taskVO,pageObject);
	}
	
	public GwModelDataFetchTaskVO searchById(Long taskId)throws Exception{
		return gwModelDataFetchTaskDAO.findById(taskId);
	}

	public GwModelDataFileVO searchTaskFileInfo(Long taskId,String fileType)throws Exception{
		List<GwModelDataFileVO> fileList = gwModelDataFileDAO.findByTaskAndType(taskId, fileType);
		GwModelDataFileVO fileVo = null;
		if(fileList.size() > 0){
			fileVo = fileList.get(0);
		}
		return fileVo;
	}
	
	public GwServiceVO searchServiceByTaskId(String taskId) throws Exception{
		GwModelDataFetchTaskVO taskVO = searchById(Long.parseLong(taskId));
		return serviceDAO.findById(taskVO.getServiceId());
	}

	//导出不合规数据
	public HSSFWorkbook exportCheckResult(String taskId,String exportNum) throws Exception {
		GwModelDataFetchTaskVO taskVO = searchById(Long.parseLong(taskId));
		List<GwServiceFieldVO> fieldList = gwServiceFieldDAO.searchServiceOutField(taskVO.getServiceId());
		
		return gwModelDataFetchTaskDAO.exportRuleCheckData(taskVO, fieldList, Integer.parseInt(exportNum));
	}
	
	//查询不合规数据，并导出到txt
	public String searchRuleCheckDataForTxt(GwModelDataFetchTaskVO taskVO,List<GwServiceFieldVO> fieldList) throws Exception{
		return gwModelDataFetchTaskDAO.searchRuleCheckDataForTxt(taskVO, fieldList);
	}

	public Map searchTaskCount(String userIds, String modelCode,String modelName, String loginName, String serviceCode,Date startDate, Date endDate) throws Exception {
		Map map = new HashMap();
		
		String checkRowNum = gwModelDataFetchTaskDAO.searchRuleCheckRowNum(userIds, modelCode, modelName, loginName, serviceCode, startDate, endDate);
		String checkPassCount = gwModelDataFetchTaskDAO.searchRuleCheckPassCount(userIds, modelCode, modelName, loginName, serviceCode, startDate, endDate);
		String passDataNum = gwModelDataFetchTaskDAO.searchRuleCheckPassDataNum(userIds, modelCode, modelName, loginName, serviceCode, startDate, endDate);
		String outputDataNum=gwModelDataFetchTaskDAO.searchRuleCheckOutputDataNum(userIds, modelCode, modelName, loginName, serviceCode, startDate, endDate);
		map.put("checkRowNum", checkRowNum);
		map.put("checkPassCount", checkPassCount);
		map.put("passDataNum", passDataNum);
		map.put("outputDataNum", outputDataNum);
		return map;
	}
	
	/**
	 * 108文件发送到ftp失败后重新发送
	 * @throws Exception 
	 */
	public void doRepush108Ftp() throws Exception{
		GwSysCnfigVO configVO=configService.findByConfigType(CommonState.CONFIG_REPUSH_COUNT);
		int maxPushCount = configVO==null||configVO.getConfigValue()==null?10:Integer.parseInt(String.valueOf(configVO.getConfigValue()));
		List<GwModelDataFetchTaskVO> taskList = gwModelDataFetchTaskDAO.searchWaitPushTask(maxPushCount);
		for (GwModelDataFetchTaskVO taskVO : taskList) {
			int pushCount = taskVO.getPushCount()==null?0:taskVO.getPushCount();
			pushCount++;
			log.info("taskId="+taskVO.getTaskId()+"输出文件推送开始,最大推送次数："+maxPushCount+",当前次数："+pushCount);
			GwModelDataFileVO dataFileVO = gwModelDataFileDAO.searchDataFile(taskVO.getTaskId(), "2");
			GwModelDataFileVO dataFileVO2 = gwModelDataFileDAO.searchDataFile(taskVO.getTaskId(), "5");//未加密的
			if(dataFileVO==null){
				taskVO.setDataProgressStatus(DataProgressStatus.TASK_ERROR);
				log.error("taskId="+taskVO.getTaskId()+"不存在fileType=2的结果文件，repush失败");
			}else {
				GwUserVO userVO = userService.searchUserDetail(taskVO.getUserId());
				GwServiceVO serviceVO = serviceDAO.findById(taskVO.getServiceId());
				dataFileVO.setFtpIp(userVO.getFtpIp());
				dataFileVO.setFtpUser(userVO.getFtpUsername());
				dataFileVO.setFtpPassword(userVO.getFtpPassword());
				
				GwModelDataFetchVO gwModelDataFetchVO = gwModelDataFetchDAO.findById(taskVO.getFetchId());
				
				String filePath=dataFileVO2.getFilePath()+"/"+dataFileVO2.getUnzipName();
				String command = ConfigConstants.GET_FILE_ROWS_COMMAND + " "+filePath+"|awk '{print $1}'";
				String rows = ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
				if("2".equals(gwModelDataFetchVO.getPushDataWay()) && Long.valueOf(rows) < 10000 ){ //推送方式为webservice
					List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
					Map<String,String> dataMap = new HashMap<String,String>();
					FileInputStream fis = null;
					InputStreamReader isr = null;
					BufferedReader br = null; 
					try {
						String str = "";
						fis = new FileInputStream(filePath);
						isr = new InputStreamReader(fis); 
						br = new BufferedReader(isr);
						while ((str = br.readLine()) != null) {
							dataMap.put("data", str);
							dataList.add(dataMap);
						}
					} catch (FileNotFoundException e) {
						log.error("找不到指定文件");
					} catch (IOException e) {
						log.error("读取文件失败");
					} finally {
						try {
							br.close();
							isr.close();
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					DataEntity param = new DataEntity();
					param.setServiceCode(serviceVO.getServiceCode());
					param.setServiceName(serviceVO.getServiceName());
					param.setResult(dataList);
					String result = PushWebserviceUtil.putDataToWebservice(userVO.getWebserviceUrl(), userVO.getBaseWsdl(), userVO.getWebserviceMethod(), param.toString());
					JSONObject obj = JSONObject.fromObject(result);
					if("1".equals(obj.get("resultFlag"))){//推送成功
						log.error("taskId="+dataFileVO.getTaskId()+",fileId="+dataFileVO.getFileId()+" push文件到webservice("+userVO.getWebserviceUrl()+")成功。");
						taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_PUSHED);
						taskVO.setDownloadEndTime(new Date());
						String msgContent=userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件推送到用户 webservice("+userVO.getWebserviceUrl()+")成功，请知晓！【数据网关平台】";
						String planContentStr = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件已重新推送到用户 webservice("+userVO.getWebserviceUrl()+")服务器";
						workPlanService.saveWorkPlan("文件推送成功通知",WorkPlanConstent.DATA_REPUSH_SUCCESS , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
								null, null,null,null, null,msgContent,null);
						String planContent ="您请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件已推送到您的webservice("+userVO.getWebserviceUrl()+")服务器";
						workPlanService.saveWorkPlan("文件推送成功通知",WorkPlanConstent.DATA_REPUSH_SUCCESS_FOR_DATA_USER , planContent, WorkPlanConstent.WAIT_FOR_DEAL,
								null, null,null,userVO.getUserId(), null,msgContent,null);
						
						liabilityService.saveLiabilityLog(taskVO, userVO);		
					}else{
						log.error("taskId="+taskVO.getTaskId()+",push文件("+dataFileVO.getFilePath()+"/"+dataFileVO.getFileName()+")到webservice("+userVO.getWebserviceUrl()+")失败");
						taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_REPUSH);
						if(pushCount >= maxPushCount){
							log.info("taskId="+taskVO.getTaskId()+"推送次数"+pushCount+"已达到最大推送次数"+maxPushCount+",生成审核待办");
							Map<String, String> paramMap = new HashMap<String,String>();
							paramMap.put("taskId", String.valueOf(taskVO.getTaskId()));
							String msgContent=userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件重复推送到用户 webservice("+userVO.getWebserviceUrl()+")失败次数已达"+maxPushCount+"次，请处理！【数据网关平台】";
							String planContentStr = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")" +
									"输出文件重复推送到用户 webservice("+userVO.getWebserviceUrl()+")失败次数已达"+pushCount+"次，请处理";
							workPlanService.saveWorkPlan("文件推送失败通知",WorkPlanConstent.DATA_REPUSH_FAILURE , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
									null, null,1l,null, null,paramMap,msgContent);
						}
						
						if(pushCount == 1){
							log.info("taskId="+taskVO.getTaskId()+"首次推送失败"+maxPushCount+",生成审核待办");
							Map<String, String> paramMap = new HashMap<String,String>();
							paramMap.put("taskId", String.valueOf(taskVO.getTaskId()));
							
							String planContent = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")" +
									"输出文件首次推送到用户 webservice("+userVO.getWebserviceUrl()+")失败，请处理";
							workPlanService.saveWorkPlan("文件首次推送失败通知",WorkPlanConstent.DATA_FIRST_PUSH_FAILURE , planContent, WorkPlanConstent.WAIT_FOR_DEAL,
									null, null,1l,null, null,paramMap,null);
						}
					}
					
				}else{//推送方式为ftp
					if(PushFtpUtil.pushFileToFtp(dataFileVO)){
						log.error("taskId="+dataFileVO.getTaskId()+",fileId="+dataFileVO.getFileId()+" push文件到FTP("+userVO.getFtpIp()+")成功。");
						taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_PUSHED);
						taskVO.setDownloadEndTime(new Date());
						String msgContent=userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件推送到用户 FTP("+userVO.getFtpIp()+")成功，请知晓！【数据网关平台】";
						String planContentStr = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件已重新推送到用户 FTP("+userVO.getFtpIp()+")服务器";
						workPlanService.saveWorkPlan("文件推送成功通知",WorkPlanConstent.DATA_REPUSH_SUCCESS , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
								null, null,null,null, null,msgContent,null);
						String planContent ="您请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件已推送到您的 FTP("+userVO.getFtpIp()+")服务器";
						workPlanService.saveWorkPlan("文件推送成功通知",WorkPlanConstent.DATA_REPUSH_SUCCESS_FOR_DATA_USER , planContent, WorkPlanConstent.WAIT_FOR_DEAL,
								null, null,null,userVO.getUserId(), null,msgContent,null);
						
						liabilityService.saveLiabilityLog(taskVO, userVO);					
						
					}else {
						log.error("taskId="+taskVO.getTaskId()+",push文件("+dataFileVO.getFilePath()+"/"+dataFileVO.getFileName()+")到ftp("+userVO.getFtpIp()+")失败");
						taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_REPUSH);
						if(pushCount >= maxPushCount){
							log.info("taskId="+taskVO.getTaskId()+"推送次数"+pushCount+"已达到最大推送次数"+maxPushCount+",生成审核待办");
							Map<String, String> paramMap = new HashMap<String,String>();
							paramMap.put("taskId", String.valueOf(taskVO.getTaskId()));
							String msgContent=userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件重复推送到用户 FTP("+userVO.getFtpIp()+")失败次数已达"+maxPushCount+"次，请处理！【数据网关平台】";
							String planContentStr = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")" +
									"输出文件重复推送到用户 FTP("+userVO.getFtpIp()+")失败次数已达"+pushCount+"次，请处理";
							workPlanService.saveWorkPlan("文件推送失败通知",WorkPlanConstent.DATA_REPUSH_FAILURE , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
									null, null,1l,null, null,paramMap,msgContent);
						}
						
						if(pushCount == 1){
							log.info("taskId="+taskVO.getTaskId()+"首次推送失败"+maxPushCount+",生成审核待办");
							Map<String, String> paramMap = new HashMap<String,String>();
							paramMap.put("taskId", String.valueOf(taskVO.getTaskId()));
							
							String planContent = userVO.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")" +
									"输出文件首次推送到用户 FTP("+userVO.getFtpIp()+")失败，请处理";
							workPlanService.saveWorkPlan("文件首次推送失败通知",WorkPlanConstent.DATA_FIRST_PUSH_FAILURE , planContent, WorkPlanConstent.WAIT_FOR_DEAL,
									null, null,1l,null, null,paramMap,null);
							
						}
					}
				}
				taskVO.setPushCount(pushCount);
			}
			gwModelDataFetchTaskDAO.update(taskVO);
			log.info("taskId="+taskVO.getTaskId()+"输出文件推送结束");
		}
	}

	/**
	 * 重新做任务检查
	 * @throws Exception 
	 */
	public void redoTask(Long taskId, GwOperationLogVO logVO) throws Exception{
		
		gwModelDataFetchTaskDAO.redesenTask(taskId);
		//写操作日志
		try {
			gwOperationLogDAO.save(logVO);
		} catch (Exception e) {
			log.error("保存操作日志出错："+e.getMessage()+"。userCode="+logVO.getOperationUser()+",acceptUserCode="+logVO.getAcceptUser()+",operateType="+logVO.getOperationType()+",OperateContent="+logVO.getOperationContent(),e);
			e.printStackTrace();
		}

	}
	
	//查询不合规数据量
	public String searchServiceCheckRecord(Long taskId)throws Exception{
		return gwModelDataFetchTaskDAO.searchServiceCheckRecord(taskId);
	}

	
}
