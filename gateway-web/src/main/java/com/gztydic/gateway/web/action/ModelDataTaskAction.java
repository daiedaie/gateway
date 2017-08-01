package com.gztydic.gateway.web.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.ShellUtil;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.ModelTaskView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModelLiabilityLogVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.gather.DataGatherService;
import com.gztydic.gateway.model.ModelDataAppService;
import com.gztydic.gateway.model.ModelDataTaskService;
import com.gztydic.gateway.model.ModelInfoService;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.LiabilityService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.OrgService;
import com.gztydic.gateway.system.ServiceDictService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

/** 
 * @ClassName: ModelDataTaskAction 
 * @Description: TODO(模型取数任务页面控制类) 
 * @author davis
 * @date 2014-11-19 下午03:20:16 
 *  
 */
@Controller
@Scope("prototype")
public class ModelDataTaskAction extends BaseAction{
	
	private static Log logger = LogFactory.getLog(ModelDataTaskAction.class);

	private static final long serialVersionUID = 1L;
	
    private String modelCode;
	
	private String modelName;
	
	private String userName;
	
	private String loginName;
	
	private String startDate;
	
	private String endDate;
	
	private String serviceCode;
	
	private String dataProgressStatus;
	
	private ModelTaskView modelTaskView;
	
	@Resource(name="modelDataTaskServiceImpl")
	private ModelDataTaskService modelDataTaskService;
	
	@Resource(name="modelInfoServiceImpl")
	private ModelInfoService modelInfoService;
	
	@Resource(name="gwUserServiceImpl")
	private GwUserService gwUserService;
	
	@Resource(name="modelDataAppServiceImpl")
	private ModelDataAppService modelDataAppService;
	
	@Resource(name="liabilityServiceImpl")
	private LiabilityService liabilityService;
	
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService serviceService;
	
	@Resource(name="serviceDictServiceImpl")
	private ServiceDictService dictService;
	
	@Resource(name="userServiceImpl")
	private UserService userService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="gwServiceFieldDAO")
	private GwServiceFieldDAO gwServiceFieldDAO;
	@Resource(name="dataGatherServiceImpl")
	private DataGatherService dataGatherService;
	@Resource
	private GwServiceCheckRuleDAO checkRuleDAO;
	@Resource(name = "orgServiceImpl")
	private OrgService orgService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	//查询任务列表
	public String searchTaskList() throws Exception{
		GwUserVO loginUser = getLoginUser();
		if(loginUser != null){
			request.setAttribute("loginUser", loginUser);
			String userType = getLoginUser().getUserType();
			String endDateStr = endDate;
			if( !StringUtils.isBlank(endDateStr) ){
				endDateStr =  endDateStr+" 23:59:59";
			}
			if(pageObject == null) {
				pageObject = new PageObject();
			}
			String userIds = "";
			//只有管理用户可以查询模型任务信息，其他用户只能查看自己的或者机构下用户
			if(GwUserType.SAFE_USER.equals(userType) || GwUserType.SUPER_USER.equals(userType) || GwUserType.AUDIT_USER.equals(userType) || GwUserType.MAINATE_USER.equals(userType)){
				pageObject = modelDataTaskService.searchAllPage(modelCode,modelName,loginName,serviceCode,DateUtil.StringTODate(startDate),DateUtil.StringTODate5(endDateStr),dataProgressStatus,pageObject);
			}else	{
				if(GwUserType.DATA_USER.equals(userType)){
					userIds = loginUser.getUserId().toString();
				}else if(GwUserType.ORG_USER.equals(userType)){
					List<GwUserVO> userList =gwUserService.searchUserByOrgAndStatus(getLoginUser().getOrgId(), null);
					for(int i = 0;i<userList.size();i++){
						GwUserVO userVo = new GwUserVO();
						userVo = userList.get(i);
						if( i == 0){
							userIds = userVo.getUserId().toString();
						}else{
							userIds = userIds +","+userVo.getUserId().toString();
						}
					}
					if("".equals(userIds)) userIds = "-1";
				}
				pageObject = modelDataTaskService.searchAllPage(userIds, modelCode,modelName,loginName,serviceCode,DateUtil.StringTODate(startDate),DateUtil.StringTODate5(endDateStr),dataProgressStatus,pageObject);
			}
			Map taskCountMap = modelDataTaskService.searchTaskCount(userIds, modelCode,modelName,loginName,serviceCode,DateUtil.StringTODate(startDate),DateUtil.StringTODate5(endDateStr));
			request.setAttribute("taskCountMap", taskCountMap);
		}
		
		return  "taskList";
	}
	
	//终止任务
	public String stopTask() throws Exception{
		String taskIdStr = request.getParameter("taskId");
		if(taskIdStr != null){
			Long taskId = Long.valueOf(taskIdStr);
			GwUserVO loginUserVO=getLoginUser();
			modelDataTaskService.updateTaskStatusByTaskId(taskId, CommonState.INVALID,loginUserVO);
			//写操作日志
			GwUserVO loginUser = getLoginUser();
			operationLogService.saveOperationLog(loginUser.getLoginName(),loginUser.getLoginName(), OperateTypeConstent.STOP_DATA_TASK, loginUser.getLoginName()+"终止任务："+taskId+"！");
		}
		return searchTaskList();
	}
	
	//实时服务数据获取
	public String onlineGetServiceData() throws Exception{
		String serviceIdStr = request.getParameter("serviceId");
		String userIdStr =  request.getParameter("userId");
		Long serviceId = serviceIdStr == null?null:Long.valueOf(serviceIdStr);
		Long appUserId = serviceIdStr == null?null:Long.valueOf(userIdStr);
		GwModelDataFetchVO fetchVO = modelDataAppService.searchUserServiceAppInfo(appUserId, serviceId);
		//实时取数脱敏
		Map columnMap =  modelDataTaskService.searchServiceName(Long.valueOf(serviceId));
		Map result = modelDataTaskService.searchOnlineServiceData(fetchVO,getLoginUser());
		request.setAttribute("columnMap", columnMap);
		request.setAttribute("dataMap", result);
		return "onlineDataView";
	}
	
	//实时服务数据抽样
	public String onlinePreviewServiceData() throws Exception{
		Long taskId = Long.valueOf(request.getParameter("taskId"));
		GwModelDataFetchTaskVO taskVO = modelDataTaskService.searchById(taskId);
		
		//查询脱敏规则
    	GwServiceVO serviceVO = serviceService.searchService(taskVO.getServiceId());
    	List<GwDesenRuleServiceFieldView> ruleViewList = desenModelService.searchDesenRuleListByBatch(taskVO);
		request.setAttribute("fieldDict", JSONObject.fromObject(dictService.searchFieldDictMap(taskVO.getUserId(),serviceVO)));	//字段、字典关联数据
		request.setAttribute("groupDict", dictService.searchGroupDict());	//字典组
		request.setAttribute("dictMap", JSONObject.fromObject(dictService.searchDictMap()));		//字典
		request.setAttribute("service", serviceVO);
		request.setAttribute("user", userService.searchUserDetail(taskVO.getUserId()));
		request.setAttribute("ruleViewList", ruleViewList);
		
		//实时取数脱敏
		Map columnMap =  modelDataTaskService.searchServiceName(Long.valueOf(taskVO.getServiceId()));
		Map result = modelDataTaskService.searchOnlinePreviewServiceData(taskVO);
		request.setAttribute("columnMap", columnMap);
		request.setAttribute("dataMap", result);
		return "onlineDataPreview";
	}
	
	//离线服务数据抽样
	public String offlinePreviewServiceData() throws Exception{
		String taskId = request.getParameter("taskId");
		GwModelDataFetchTaskVO taskVO = modelDataTaskService.searchById(Long.valueOf(taskId));
	    if(taskVO != null){
	    	if(pageObject == null) {
	    		pageObject = new PageObject();
	    		pageObject.setPageSize(20);
	    	}
	    	
	    	//查询脱敏规则
	    	GwServiceVO serviceVO = serviceService.searchService(taskVO.getServiceId());
	    	List<GwDesenRuleServiceFieldView> ruleViewList = desenModelService.searchDesenRuleListByBatch(taskVO);
			request.setAttribute("fieldDict", JSONObject.fromObject(dictService.searchFieldDictMap(taskVO.getUserId(),serviceVO)));	//字段、字典关联数据
			request.setAttribute("groupDict", dictService.searchGroupDict());	//字典组
			request.setAttribute("dictMap", JSONObject.fromObject(dictService.searchDictMap()));		//字典
			request.setAttribute("service", serviceVO);
			request.setAttribute("user", userService.searchUserDetail(taskVO.getUserId()));
			request.setAttribute("ruleViewList", ruleViewList);
	    	
			Map columnMap =  modelDataTaskService.searchServiceName(taskVO.getServiceId());
			Map dataMap = modelDataTaskService.searchOfflinePreviewServiceData(taskVO,pageObject);
			request.setAttribute("columnMap", columnMap);
			request.setAttribute("dataMap", dataMap);
	    }
	    request.setAttribute("taskId", taskId);
		return "offlineDataPreview";
	}
	
	//不合规数据抽样
	public String previewRuleCheck()throws Exception{
		try {
			String taskId=request.getParameter("taskId");
			String serviceId=request.getParameter("serviceId");
			String userId=request.getParameter("userId");
			
			GwServiceVO service = serviceService.searchService(Long.parseLong(serviceId));
			if(service.getModelId()!=null){
				GwModelVO model = modelInfoService.searchModelById(service.getModelId());
				request.setAttribute("model", model);
			}
			GwModelDataFetchTaskVO taskVO = modelDataTaskService.searchById(Long.parseLong(taskId));
			GwUserVO dataUserVO=  gwUserService.searchById(Long.valueOf(userId));
			GwUserVO orgUserVO=orgService.searchOrgUser(dataUserVO.getOrgId());
			request.setAttribute("dataUserVO", dataUserVO);
			request.setAttribute("orgUserVO", orgUserVO);
			request.setAttribute("taskVO", taskVO);
			GwModelDataFileVO fileVO=modelInfoService.searchModelDataFile(Long.valueOf(taskId), "1");
			request.setAttribute("fileVO", fileVO);
			request.setAttribute("service", service);
			request.setAttribute("taskId", taskVO.getTaskId());
			request.setAttribute("serviceId", taskVO.getServiceId());
			request.setAttribute("userId", taskVO.getUserId());
			String irregularCount=modelDataTaskService.searchServiceCheckRecord(taskVO.getTaskId());
			request.setAttribute("irregularCount", irregularCount);
			if("3".equals(taskVO.getCheckResult())){
				//根据check_batch，查询行数
				int rowCount=dataGatherService.searchRowCount(taskVO);
				request.setAttribute("rowCount", rowCount);
				//查询warnRow,rowData
				Map map=dataGatherService.searchServiceCheckWarn(taskVO, 1);
				String[] rowData=((String)map.get("rowData")).split("\\|");
				request.setAttribute("warnRow", map.get("warnRow"));
				request.setAttribute("rowData", rowData);
				request.setAttribute("rowDataLength", rowData.length);
			}else if("4".equals(taskVO.getCheckResult())){
				//根据check_batch，查询行数
				int rowCount=dataGatherService.searchRowCount(taskVO);
				request.setAttribute("rowCount", rowCount);
				//查询warnRow,rowData
				Map map=dataGatherService.searchServiceCheckWarn(taskVO, 2);
				request.setAttribute("warnRow", map.get("warnRow"));
				request.setAttribute("rowData", map.get("rowData"));
				String[] rowData=((String)map.get("rowData")).split("\\|");
				request.setAttribute("rowDataLength", rowData.length);
			}
			
			//查询服务输出字段
//			List<GwServiceFieldVO> fieldCodeList=gwServiceFieldDAO.searchServiceOutField(taskVO.getServiceId());
			/*//将服务输出字段组装成map
			Map<String,String> fieldCodeMap=new LinkedHashMap<String,String>();
			for(GwServiceFieldVO gwServiceFieldVO:fieldCodeList){
				fieldCodeMap.put(gwServiceFieldVO.getFieldCode(),gwServiceFieldVO.getFieldName());
			}*/
			
			if(pageObject==null) pageObject=new PageObject();
			pageObject=dataGatherService.searchRuleCheckAuditList(service,taskVO.getTaskId(), pageObject);
			
			//组装表头
			if(CommonState.SERVICE_SOURCE_108.equals(service.getServiceSource())){
				List<GwServiceCheckRuleVO> serviceCodeList=dataGatherService.searchServiceFieldCode(taskVO);
				request.setAttribute("serviceCodeList", serviceCodeList);
			}else if(CommonState.SERVICE_SOURCE_DATA.equals(service.getServiceSource())){
				List<String> titleList = dataGatherService.searchFieldTitleList(service, taskVO.getTaskId());
				request.setAttribute("titleList", titleList);
			}
			//根据taskId,行号查询需要标红的字段
			Map recordMap = dataGatherService.searchRuleCheckAuditField(taskVO,pageObject);
			request.setAttribute("recordMap", recordMap);
			
			GwModelDataFetchVO fetchVO=modelDataAppService.searchUserServiceAppInfo(Long.valueOf(userId), Long.valueOf(serviceId));
			if(fetchVO.getDesenType().equals("2")){//合规检查，108的才有
				List<GwServiceCheckRuleVO> checkRuleList = checkRuleDAO.searchCheckRuleList(taskVO.getCheckBatch());
				request.setAttribute("checkRuleList", checkRuleList);
			}else if(fetchVO.getDesenType().equals("1")){
				//查询脱敏配置信息
				request.setAttribute("fieldDict", JSONObject.fromObject(dictService.searchFieldDictMap(taskVO.getUserId(),service)));	//字段、字典关联数据
				request.setAttribute("groupDict", dictService.searchGroupDict());	//字典组
				request.setAttribute("dictMap", JSONObject.fromObject(dictService.searchDictMap()));		//字典
				
				List<GwDesenRuleServiceFieldView> ruleViewList = desenModelService.searchDesenRuleListByBatch(taskVO);
				request.setAttribute("ruleViewList", ruleViewList);
			}
			request.setAttribute("fetchVO", fetchVO);
			return "ruleCheckPreview";
		} catch (Exception e) {
			logger.error("检查结果查询异常:"+e.getMessage(),e);
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看检查结果),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看检查结果时发生异常！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看检查结果");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
	}
	
	//服务输出文件下载
	public String downloadFtpFile()throws Exception{
		String taskIdStr = request.getParameter("taskId");
		String fileTypeStr=request.getParameter("fileType");  //1:原件下载、2：结果文件下载
		Long taskId = taskIdStr != null?Long.parseLong(taskIdStr.trim()):null;
		if(taskId != null){
			GwUserVO loginUser = getLoginUser();
			GwModelDataFetchTaskVO taskVo = modelDataTaskService.searchById(taskId);
			/*if(!"1".equals(fileTypeStr)){
				if(!((DataProgressStatus.RULE_CHECK_SUCCESS.equals(taskVo.getDataProgressStatus())
					||DataProgressStatus.RULE_CHECK_AUDIT.equals(taskVo.getDataProgressStatus())
					||DataProgressStatus.RULE_CHECK_INVALID.equals(taskVo.getDataProgressStatus()))
					&& "0".equals(taskVo.getCheckResult()))){
					throw new Exception("任务状态非法，不能下载！");
				}
			}*/
			/*if(!(loginUser.getUserType().equals(GwUserType.SAFE_USER) || loginUser.getUserType().equals(GwUserType.AUDIT_USER)) 
					&& !loginUser.getUserId().equals(taskVo.getUserId())){
				logger.error("用户："+loginUser.getUserName()+"被拒绝下载taskId="+taskVo.getTaskId()+"的文件");
				throw new Exception("无权限，不能下载该文件！");
			}*/
			if(taskVo != null){
				GwModelDataFileVO fileVo = modelDataTaskService.searchTaskFileInfo(taskId,fileTypeStr);
				logger.info("文件全路径："+fileVo.getFilePath()+"/"+fileVo.getFileName());
				GwUserVO userInfo = getLoginUser();
				if(userInfo != null){
					GwModelLiabilityLogVO logVO=liabilityService.saveLiabilityLog(taskVo, userInfo);
					request.setAttribute("logId", logVO.getLogId());
					logger.info("历史ID："+logVO.getLogId());
				}
				taskVo.setDownloadTime(new Date());
				modelDataTaskService.saveOrUpdate(taskVo);
				GwUserVO userVo = gwUserService.searchById(taskVo.getUserId());
				request.setAttribute("fileName", fileVo.getFileName());
				request.setAttribute("filePath", fileVo.getFilePath());
				request.setAttribute("userVo", userVo);
			}
		}
		return "download";
	}
	
	//查询文件总行数
	public void searchFileRows()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			long taskId=Long.valueOf(request.getParameter("taskId"));
			String fileType=request.getParameter("fileType");
			GwModelDataFileVO modelDataFileVO= modelDataTaskService.searchTaskFileInfo(taskId,fileType);
			if (modelDataFileVO.getUnzipName() != null) {
				String filePath=modelDataFileVO.getFilePath()+"/"+modelDataFileVO.getUnzipName();
				//查询.txt文件行数
				String command = ConfigConstants.GET_FILE_ROWS_COMMAND + " "+filePath+"|awk '{print $1}'";
				String rows = ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
				logger.info("文件全路径："+filePath+",文件总行数："+rows);
				logger.info("命令："+command);
				ajaxResult = AjaxResult.SUCCESS(null,rows);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询文件总行数),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看文件总行数时发生异常！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询文件总行数");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"查询文件总行数出错！");
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//服务输出文件抽样下载
	public String sampleDownloadFtpFile()throws Exception{
		String startRow=request.getParameter("startRow");
		String endRow=request.getParameter("endRow");
		String taskIdStr = request.getParameter("taskId");
		String fileTypeStr=request.getParameter("fileType");  //1:原件下载、2：结果文件下载
		Long taskId = taskIdStr != null?Long.parseLong(taskIdStr.trim()):null;
		if(taskId != null){
			GwUserVO loginUser = getLoginUser();
			GwModelDataFetchTaskVO taskVo = modelDataTaskService.searchById(taskId);
			logger.info("taskId:"+taskId+",fileTypeStr:"+fileTypeStr+(taskVo==null));
			/*if(!(loginUser.getUserType().equals(GwUserType.SAFE_USER) || loginUser.getUserType().equals(GwUserType.AUDIT_USER)) 
					&& !loginUser.getUserId().equals(taskVo.getUserId())){
				logger.error("用户："+loginUser.getUserName()+"被拒绝下载taskId="+taskVo.getTaskId()+"的文件");
				throw new Exception("无权限，不能下载该文件！");
			}*/
			if(taskVo != null){
				GwModelDataFileVO fileVo = modelDataTaskService.searchTaskFileInfo(taskId,fileTypeStr);
				GwUserVO userInfo = getLoginUser();
				long dataNum=Long.valueOf(endRow)-Long.valueOf(startRow)+1;
				if(userInfo != null){
					GwModelDataFetchTaskVO logTaskVO=new GwModelDataFetchTaskVO();
					logTaskVO.setDataNum(dataNum);
					logTaskVO.setServiceId(taskVo.getServiceId());
					logTaskVO.setCheckBatch(taskVo.getCheckBatch());
					logTaskVO.setUserId(taskVo.getUserId());
					logTaskVO.setTaskId(taskVo.getTaskId());
					GwModelLiabilityLogVO logVO=liabilityService.saveLiabilityLog(logTaskVO, userInfo);
					request.setAttribute("logId", logVO.getLogId());
				}
				taskVo.setDownloadTime(new Date());
				modelDataTaskService.saveOrUpdate(taskVo);
				GwUserVO userVo = gwUserService.searchById(taskVo.getUserId());
				//源文件全路径
				String filePath=fileVo.getFilePath()+"/"+fileVo.getUnzipName();
				//抽样文件路径
				String sampleFileName=fileVo.getUnzipName().substring(0, fileVo.getUnzipName().indexOf(".txt"))+"_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".txt";
				String sampleFilePath=fileVo.getFilePath()+"/"+sampleFileName;
				logger.info("源文件全路径："+filePath+",抽样文件路径:"+sampleFilePath);
				/*String command = "sed -n '" + startRow+","+endRow+"p' "+filePath +">"+sampleFilePath;//sed -n '1,10p' /home/gateway/10002-3-201501.gz >/home/gateway/10002-3-201501-20150615150456.txt*/
				String command = "awk 'NR>=" + startRow+";NR=="+endRow+"{exit}' "+filePath +">"+sampleFilePath;
				logger.info("命令："+command);
				ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
				request.setAttribute("fileName", sampleFileName);
				request.setAttribute("filePath", fileVo.getFilePath());
				request.setAttribute("userVo", userVo);
			}
		}
		return "download";
	}

	//服务输出文件下载
	public String downloadFtpCheckFile()throws Exception{
		Long taskId = Long.parseLong(request.getParameter("taskId"));
		if(taskId != null){
			GwModelDataFetchTaskVO taskVo = modelDataTaskService.searchById(taskId);
			GwUserVO loginUser = getLoginUser();
			if(!(loginUser.getUserType().equals(GwUserType.SAFE_USER) || loginUser.getUserType().equals(GwUserType.AUDIT_USER)) 
					&& !loginUser.getUserId().equals(taskVo.getUserId())){
				logger.error("用户："+loginUser.getUserName()+"被拒绝下载taskId="+taskVo.getTaskId()+"的文件");
				throw new Exception("无权限，不能下载该文件！");
			}
			if(taskVo != null){
				GwModelDataFileVO fileVo = modelDataTaskService.searchTaskFileInfo(taskId,"3");
				GwUserVO userVo = gwUserService.searchById(taskVo.getUserId());
				request.setAttribute("fileName", fileVo.getFileName());
				request.setAttribute("filePath", fileVo.getFilePath());
				request.setAttribute("userVo", userVo);
			}
		}
		return "download";
	}
	
	public void reCheckRule()throws Exception{
		AjaxResult ajaxResult = null;
		try{
			Long taskId = Long.parseLong(request.getParameter("taskId"));
			if(taskId != null){
				String userId = request.getParameter("userId");
				String serviceId=request.getParameter("serviceId");
				String fieldValue=request.getParameter("fieldValue");
				GwServiceVO serviceVO = serviceService.searchService(Long.valueOf(serviceId));
				GwOperationLogVO logVO=new GwOperationLogVO();
				GwUserVO loginUser=getLoginUser();
				logVO.setOperationUser(loginUser.getLoginName());
				GwUserVO acceptUser=userService.searchUserDetail(Long.valueOf(userId));
				logVO.setAcceptUser(acceptUser.getLoginName());
				String operationContent=loginUser.getLoginName()+"重新检查服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+fieldValue+",数据用户="+acceptUser.getLoginName()+")";
				logVO.setOperationContent(operationContent);
				logVO.setOperationType(OperateTypeConstent.REDO_SERVICE);
				logVO.setOperationTime(new Date());
				modelDataTaskService.redoTask(Long.valueOf(taskId), logVO);
				ajaxResult = AjaxResult.SUCCESS("任务已经成功创建！");
			}
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除帮助文档),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "重新检查服务发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "重新检查服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"任务创建失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
		
	}
	
	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public ModelTaskView getModelTaskView() {
		return modelTaskView;
	}

	public void setModelTaskView(ModelTaskView modelTaskView) {
		this.modelTaskView = modelTaskView;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getDataProgressStatus() {
		return dataProgressStatus;
	}

	public void setDataProgressStatus(String dataProgressStatus) {
		this.dataProgressStatus = dataProgressStatus;
	}
	
}
