package com.gztydic.gateway.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.BeanUtil;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.SFTPRemoteUtil;
import com.gztydic.gateway.core.dao.GwDesenServiceFieldAuditDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDictAuditDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldAuditVO;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.commons.Md5Util;

/** 
 * @ClassName: ModelDataAppServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author davis
 * @date 2014-11-20 上午10:22:28 
 *  
 */
@Service
public class ModelDataAppServiceImpl extends GeneralServiceImpl<GwModelDataFetchVO> implements ModelDataAppService {

	private static final Log log = LogFactory.getLog(ModelDataAppServiceImpl.class);
	
	@Resource
	private GwModelDataFetchDAO gwModelDataFetchDAO;
	@Resource
	private OperationLogService operationLogService;
	@Resource
	private GwServiceDAO gwServiceDAO;
	@Resource(name="userServiceImpl")
	private UserService userService;
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	@Resource
	private GwModelDataFetchDAO fetchDAO;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService ;
	@Resource
	private GwModelDataFetchTaskDAO taskDAO;
	@Resource
	private GwModelDataFileDAO gwModelDataFileDAO;
	@Resource
	private GwServiceFieldDAO fieldDAO;
	@Resource
	private GwServiceFieldDAO gwServiceFieldDAO;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	@Resource
	private GwDesenServiceFieldAuditDAO fieldDesenAuditDao;
	@Resource
	private GwServiceCheckRuleAuditDAO checkRuleAuditDAO;
	@Resource
	private GwServiceFieldDictAuditDAO fieldDictAuditDAO;
	@Resource(name="processServiceImpl")
	private ProcessService processService;	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService; 
	/** 
	 * @Title: searchUserModelAppList 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserModelServiceAppList(Long userId) throws Exception{
		//写操作日志
//		operationLogService.saveOperationLog(userId, null, "401", "查看自己拥有模型的模型取数申请列表");
		
		return gwModelDataFetchDAO.searchUserModelServiceAppList(userId);
	}
	
	public PageObject searchServiceAppList(Long userId,String userType,UserModelServiceAppVO view,PageObject pageObject)throws Exception{
		pageObject=gwServiceDAO.searchServiceAppList(userId, userType,view, pageObject);
		List<Object[]> list = pageObject.getData();
		List appList = new ArrayList();
		UserModelServiceAppVO vo = null;
		for (Object[] obj : list) {
			vo = new UserModelServiceAppVO();
			vo.setUserId(obj[0]==null?null:Long.valueOf(String.valueOf(obj[0])));
			vo.setLoginName(obj[1]==null?null:String.valueOf(obj[1]));
			vo.setServiceId(obj[2]==null?null:Long.valueOf(String.valueOf(obj[2])));
			vo.setModelId(obj[3]==null?null:Long.valueOf(String.valueOf(obj[3])));
			vo.setServiceCode(obj[4]==null?null:String.valueOf(obj[4]));
			vo.setServiceName(obj[5]==null?null:String.valueOf(obj[5]));
			vo.setModelCode(obj[6]==null?null:String.valueOf(obj[6]));
			vo.setServiceType(obj[7]==null?null:String.valueOf(obj[7]));
			vo.setCycleType(obj[8]==null?null:String.valueOf(obj[8]));
			vo.setCycleDay(obj[9]==null?null:String.valueOf(obj[9]));
			vo.setFetchType(obj[10]==null?null:String.valueOf(obj[10]));
			vo.setAuditStatus(obj[11]==null?null:String.valueOf(obj[11]));
			vo.setAuditTime(obj[12]==null?null:DateUtil.StringTODate5(String.valueOf(obj[12])));
			vo.setFetchId(obj[13]==null?null:Long.valueOf(String.valueOf(obj[13])));
			vo.setServiceSource((obj[14]==null?"":String.valueOf(obj[14])));
			appList.add(vo);
		}
		pageObject.setData(appList);
		return pageObject;
	}
	
	/** 
	 * @Title: searchUserModelServiceAppInfo 
	 * @Description: TODO(查询用户服务申请记录信息) 
	 * @param @param userId 用户Id
	 * @param @param modeId 服务Id
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelDataFetchVO    返回类型 
	 * @throws 
	 */
	public GwModelDataFetchVO searchUserServiceAppInfo(Long userId,Long serviceId)throws Exception{
		List result = gwModelDataFetchDAO.findByServiceIdAndUserId(userId, serviceId);
		if(result!=null&& result.size()>0){
			return (GwModelDataFetchVO)result.get(0);
		}
		return null;
	}
	
	
	/** 
	 * @Title: updateModelAppState 
	 * @Description: TODO(更新模型取数申请审核状态) 
	 * @param @param userId 用户ID
	 * @param @param modelId 模型ID
	 * @param @param auditStatus 审核状态
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateModelAppState(Long userId,Long modelId,String auditStatus)throws Exception{
		GwModelDataFetchVO gwModelDataFetchVO = searchUserServiceAppInfo(userId, modelId);
		if(gwModelDataFetchVO != null){
			gwModelDataFetchVO.setAuditStatus(auditStatus);
			gwModelDataFetchVO.setUpdateTime(new Date());
			gwModelDataFetchVO.setAuditTime(new Date());
			gwModelDataFetchDAO.update(gwModelDataFetchVO);
		}
	}
	
	/** 
	 * @Title: updateModelAppVo 
	 * @Description: TODO(更新模型提数申请对象) 
	 * @param @param modelDataFetchVO
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateModelAppVo(GwModelDataFetchVO modelDataFetchVO)throws Exception{
		gwModelDataFetchDAO.update(modelDataFetchVO);
	}
	
	/** 
	 * @Title: searchById 
	 * @Description: TODO(根据表主键查询模型取数对象) 
	 * @param @param fetchId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelDataFetchVO    返回类型 
	 * @throws 
	 */
	public GwModelDataFetchVO searchById(Long fetchId)throws Exception{
		
		return gwModelDataFetchDAO.findById(fetchId);
	}
	
	/**
	 * @Title: createOfflineTask 
	 * @Description: TODO(离线服务审核通过创建取数任务) 
	 * @param @param fetchId
	 * @param @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean createOfflineTask(Long fetchId) {
		return gwModelDataFetchDAO.createOfflineTask(fetchId);
	}

	public GwModelDataFetchVO doVerifyApp(String passTag, String planId, String suggestion,GwUserVO loginUser) throws Exception {
		GwModelDataFetchVO appFetchVO = null;
		Long workPlanId = planId == null?null:Long.valueOf(planId);
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		Long userId = Long.valueOf(paramMap.get("userId"));
		Long serviceId = Long.valueOf(paramMap.get("serviceId"));
		Long maxCheckNum = Long.valueOf(paramMap.get("serviceId"));
		String checkAudit = paramMap.get("checkAudit");
		Long outputNum = Long.valueOf(paramMap.get("outputNum"));
		String pushDataWay = paramMap.get("pushDataWay");
		String fileId = paramMap.get("fileId");

		String fetchId = paramMap.get("fetchId");
		String ruleUpdateType = paramMap.get("ruleUpdateType");
		String userApply = paramMap.get("userApply");
		String ruleBatch = paramMap.get("ruleBatch");
		String desenBatch = paramMap.get("desenBatch");
		String type = paramMap.get("type");
		//修改待办状态
		workPlanService.updateWorkPlanState(workPlanId,suggestion, passTag,loginUser.getUserId());
		GwUserVO userVO = userService.searchUserDetail(userId);
		GwServiceVO serviceVO = gwServiceService.searchService(serviceId);
		//更新审核结果
		GwModelDataFetchVO fetchVO = null;
		
		if(fetchId != null){	//用户发起的服务取数申请
			fetchVO = fetchDAO.findById(Long.valueOf(fetchId));
			fetchVO.setAuditStatus(passTag);
			fetchVO.setAuditTime(new Date());
			fetchVO.setUpdateTime(new Date());
			fetchVO.setUpdateUser(loginUser.getLoginName());
			
		}
		
		if(CommonState.PASS.equals(passTag)){	//不通过的需要让数据安全管理员重新配置再来审核，不通过不修改
			
			List<GwServiceFieldVO> fieldList = fieldDAO.searchServiceOutField(serviceVO.getServiceId());
			Map<String, GwServiceFieldVO> fieldMap = new HashMap<String, GwServiceFieldVO>();
			for (GwServiceFieldVO fieldVO : fieldList) {
				fieldMap.put(String.valueOf(fieldVO.getReorder()), fieldVO);
			}
			GwModelDataFetchVO fetch108VO = fetchDAO.searchModelDataFetch(userId, serviceId);
			GwUploadFileVO fileVO = uploadFileService.findById(fetch108VO.getCheckFileId());
			File ruleCheckFile = new File(ConfigConstants.BASE_UPLOAD_FILE_PATH+"/"+fileVO.getFilePath());
				
//			if(serviceVO.getServiceSource().equals("1")){//挖掘平台的才需要保存字段进gw_service_field表
//			//解析合规检查规则文件
//			List<GwServiceCheckRuleVO> ruleList = desenModelService.parseServiceCheckRule(ruleCheckFile, serviceVO, userVO);
//			//更新108服务输出字段
//			List<GwDesenServiceFieldAuditVO> desenRuleAuditList = fieldDesenAuditDao.searchDesenRuleList(Long.valueOf(desenBatch));
//			int i=0;
//			for (GwServiceCheckRuleVO ruleVO : ruleList) {
//				GwServiceFieldVO fieldVO = fieldMap.get(ruleVO.getReorder());	//根据序号查找服务输出字段，存在则更新，否则新增
//		        if(fieldVO == null) fieldVO = new GwServiceFieldVO();
//		        	fieldVO.setServiceId(serviceVO.getServiceId());
//		        	fieldVO.setFieldCode(ruleVO.getFieldCode());
//		        	fieldVO.setFieldName(ruleVO.getFieldName());
//		      		fieldVO.setFieldType(ruleVO.getFieldType());
//		      		fieldVO.setGatherType("1");
//		      		fieldVO.setReorder(ruleVO.getReorder());
//		      		fieldVO.setCreateTime(new Date());
//		      		fieldVO.setCreateUser(loginUser.getLoginName());
//		      		GwDesenServiceFieldAuditVO gw = new GwDesenServiceFieldAuditVO();
//		      		gw = desenRuleAuditList.get(i);
//		      		fieldVO.setFieldId(gw.getFieldId());
//		      		super.saveOrUpdate(fieldVO);	
//		      		fieldMap.remove(ruleVO.getReorder());
//		      		i++;
//				}
//			}
			//挖掘平台 
			if(serviceVO.getServiceSource().equals("1")){
			
				if(type.equals("2")){// 合规检查
					List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
					for (GwServiceCheckRuleAuditVO auditVO : checkRuleAuditList) {
						GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();
						BeanUtil.copyProperties(ruleVO, auditVO);
						super.saveOrUpdate(ruleVO);
					}
				}
				if(type.equals("1")){//脱敏并合规 
					List<GwDesenServiceFieldAuditVO> desenRuleAuditList = fieldDesenAuditDao.searchDesenRuleList(Long.valueOf(desenBatch));
					List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
					int i=0;
					for (GwServiceCheckRuleAuditVO auditVO : checkRuleAuditList) {							
						GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();							
						BeanUtil.copyProperties(ruleVO, auditVO);
						ruleVO.setCheckId(desenRuleAuditList.get(i).getFieldId());
						i++;
						super.saveOrUpdate(ruleVO);							
					}						
						
					Map<Long, Long> desenMap = new HashMap<Long, Long>();
						//审核通过，从待审核中保存到脱敏规则表中						
					for (GwDesenServiceFieldAuditVO auditVO : desenRuleAuditList) {
						GwDesenServiceFieldVO desenVO = new GwDesenServiceFieldVO();
						Date createTime = auditVO.getCreateTime();
						auditVO.setCreateTime(null);
						BeanUtil.copyProperties(desenVO, auditVO);
						auditVO.setCreateTime(createTime);
						desenVO.setCreateTime(createTime);
						super.save(desenVO);
						desenMap.put(desenVO.getFieldId(), desenVO.getFieldDeseId());
					}
					List<GwServiceFieldDictAuditVO> fieldDictAuditList = fieldDictAuditDAO.searchListByBatch(Long.valueOf(desenBatch));
						for (GwServiceFieldDictAuditVO dictAuditVO : fieldDictAuditList) {
							GwServiceFieldDictVO dictVO = new GwServiceFieldDictVO();
							BeanUtil.copyProperties(dictVO, dictAuditVO);
							dictVO.setDesenId(desenMap.get(dictVO.getFieldId()));
							super.save(dictVO);
						}
		          	}										
			}
			//108服务平台
			if(serviceVO.getServiceSource().equals("2")){
					
				if(type.equals("2")){// 合规检查
					List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
					for (GwServiceCheckRuleAuditVO auditVO : checkRuleAuditList) {
						GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();
						BeanUtil.copyProperties(ruleVO, auditVO);
						super.saveOrUpdate(ruleVO);
					}
				}
				if(type.equals("1")){//脱敏并合规 
					Map<Long, Long> desenMap = new HashMap<Long, Long>();
					List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
					for (GwServiceCheckRuleAuditVO auditVO : checkRuleAuditList) {
						GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();
						BeanUtil.copyProperties(ruleVO, auditVO);
						super.saveOrUpdate(ruleVO);
					}						
					//审核通过，从待审核中保存到脱敏规则表中
					List<GwDesenServiceFieldAuditVO> desenRuleAuditList = fieldDesenAuditDao.searchDesenRuleList(Long.valueOf(desenBatch));
					for (GwDesenServiceFieldAuditVO auditVO : desenRuleAuditList) {
						GwDesenServiceFieldVO desenVO = new GwDesenServiceFieldVO();
						Date createTime = auditVO.getCreateTime();
						auditVO.setCreateTime(null);
						BeanUtil.copyProperties(desenVO, auditVO);
						auditVO.setCreateTime(createTime);
						desenVO.setCreateTime(createTime);
						//desenVO.setFieldDeseId(null);
						super.save(desenVO);
						desenMap.put(desenVO.getFieldId(), desenVO.getFieldDeseId());
					}
		         }
			}								
				//根据序号删除不在规则文件中的服务输出字段
//	          	if(!fieldMap.isEmpty()){
//	          		Iterator<String> it = fieldMap.keySet().iterator();
//	          		String reorders = "";
//	          		while (it.hasNext()) {
//						String reorder = (String) it.next();
//						reorders += ("".equals(reorders)?"":",") + reorder;
//					}
//	          		if(!"".equals(reorders))
//	          		gwServiceFieldDAO.deleteServiceFieldByreorder(reorders, serviceVO.getServiceId(),"1");
//	          	}
	          	
	         //安全管理员发起的字段脱敏配置
			if(!"true".equals(userApply)){	
				if(CommonState.SERVICE_SOURCE_DATA.equals(serviceVO.getServiceSource())){
					//字段脱敏配置审核后，需要重新先删除已脱敏文件，然后重新脱敏
					//挖掘平台的才要删除脱敏文件
					List<GwModelDataFileVO> modelDataFileList=gwModelDataFileDAO.searchModelDataFileByTaskId(userId, serviceId);
					if(modelDataFileList!=null && modelDataFileList.size()>0){
						List<String> fileNameList=new ArrayList<String>();
						for(GwModelDataFileVO vo:modelDataFileList){
							fileNameList.add(vo.getFilePath()+"/"+vo.getFileName());
						}
						SFTPRemoteUtil.deleteFile(ConfigConstants.FTP_SERVER_IP, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, Integer.parseInt(ConfigConstants.FTP_SERVER_PORT), fileNameList);
						for(GwModelDataFileVO vo:modelDataFileList){
							gwModelDataFileDAO.delete(vo);
						}
					}
				}
				
				fetchDAO.updateUserFetch(userId, serviceId, passTag, loginUser.getLoginName());
				
				//修改立即生效
				if("1".equals(ruleUpdateType)){
					log.info("配置规则审核通过，立即生效");
					//修改字段脱敏配置审核通过后，将原来（5、6）的任务设为失效，并生成新的任务
					taskDAO.redesenTask(userId, serviceVO);
					
					//立即生效，将还未处理的不合规检查审核待办设为失效
					workPlanService.updateCheckWorkPlanInvalid(userId, serviceId);
				}
			}
			
			//判断流程类型,不同流程类型对应不同的流程进度
			String processId = workPlanService.searchById(Long.valueOf(planId)).getProcessId();
			if(processId !=null && processId != ""){
			/***更新流程表-start***/
			processService.updateProcess(Long.valueOf(processId), "二次审批", new Date());
			/***更新流程表-end***/
			/***更新流程操作记录表-start***/	
			GwProcessOperationVO gwVO = processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.SECONDAUDIT);
			gwVO.setOperateContent("已完成二次审核");
			gwVO.setOperateTime(new Date());
			gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
			gwVO.setDealType(GwUserType.AUDIT_USER);
			gwVO.setPlanId(Long.valueOf(workPlanId));
			processOperationService.save(gwVO);
			/***更新流程操作记录表-end***/
			}
			if(maxCheckNum != null){
				fetchVO.setMaxCheckNum(maxCheckNum);
			}
			if(checkAudit != null){
				fetchVO.setCheckAudit(checkAudit);
			}
			if(outputNum != null){
				fetchVO.setOutputNum(outputNum);
			}
			if(pushDataWay != null){
				fetchVO.setPushDataWay(Long.valueOf(pushDataWay));
			}
			if(fileId != null){
				fetchVO.setCheckFileId(Long.valueOf(fileId));
			}
			if(type != null){
				fetchVO.setDesenType(type);	
			}
			
			fetchDAO.update(fetchVO);	
		}
		
		//审核不通过生产退回待办
		if(CommonState.NO_PASS.equals(passTag)){
			String planContentStr = "您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"操作不通过，请重新申请。";
			//待办关联表主键
			String extenTableKey = userId+","+ serviceId+","+fetchId;
			String msgContent="您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"审核不通过,请知晓。【数据网关平台】";
			GwWorkPlanVO workPlan = workPlanService.saveWorkPlan("服务数据脱敏规则退回",WorkPlanConstent.GET_DATA_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(),userVO.getUserId(),workPlanId,msgContent,null);
			workPlanParamService.saveParamMap(workPlan.getPlanId(), paramMap);
			fetchDAO.update(fetchVO);	
		}else if(CommonState.REMODIFYRULE.equals(passTag)){
			String planContentStr = "您配置的脱敏规则,服务编码="+serviceVO.getServiceCode()+"用户编码="+userVO.getLoginName()+"审核不通过，请重新配置。";
			//待办关联表主键
			String extenTableKey = userId+","+ serviceId+","+fetchId;
			String msgContent="您配置的脱敏规则,服务编码="+serviceVO.getServiceCode()+"用户编码="+userVO.getLoginName()+"审核不通过，请重新配置。【数据网关平台】";
			GwWorkPlanVO workPlan = workPlanService.saveWorkPlan("服务数据脱敏规则退回",WorkPlanConstent.FIELD_DESEN_CONF_AUDIT_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(),null,workPlanId,msgContent,null);
			workPlanParamService.saveParamMap(workPlan.getPlanId(), paramMap);
		}else{
		    //生成离线服务取数任务，实时的不创建任务让用户页面实时取
			if("true".equals(userApply)){	//数据用户发起的服务取数申请
				if(serviceVO != null){
					if(DataTypeConstent.OFFLINE.equals(serviceVO.getServiceType())){
						appFetchVO = fetchVO;	//放在action中调用，事物没提交在存储过程中查询不到数据
					}
				}
			}
		}
		
		GwWorkPlanVO workPlanVO = workPlanService.searchById(workPlanId);
		GwUserVO planCreateUser = userService.searchUserDetail(workPlanVO.getCreateUserId());
		//查询待办信息
		if(loginUser != null) {
			//写操作日志
			GwSysDictVO dictVO = SysDictManage.getSysDict("DICT_AUDIT_STATE", passTag);
			passTag = dictVO != null ? dictVO.getDictValue() : passTag;
			String operateContent = GwUserType.getUserTypeName(planCreateUser.getUserType())+"："+planCreateUser.getLoginName()+"配置了用户："+userVO.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，审核结果："+passTag+"。";
			if("true".equals(userApply)){	//数据用户发起的服务
				operateContent += "并"+passTag+userVO.getLoginName()+"的服务取数申请，fetchId="+fetchVO.getFetchId();
			}
			operationLogService.saveOperationLog(loginUser.getLoginName(), userVO.getLoginName(), OperateTypeConstent.DATA_AUDIT, operateContent);
		}
		return appFetchVO;
	}
	//****************审核处理*********************
	public GwModelDataFetchVO serviceFetchAuditDeal(String passTag, String planId, String suggestion,GwUserVO loginUser) throws Exception {
		GwModelDataFetchVO appFetchVO = null;
		Long workPlanId = planId == null?null:Long.valueOf(planId);
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		Long userId = Long.valueOf(paramMap.get("userId"));
		Long serviceId = Long.valueOf(paramMap.get("serviceId"));
		String fetchId = paramMap.get("fetchId");
		String ruleUpdateType = paramMap.get("ruleUpdateType");
		String userApply = paramMap.get("userApply");
		String ruleBatch = paramMap.get("ruleBatch");
		String processId = workPlanService.searchById(workPlanId).getProcessId();
		//修改待办状态
		workPlanService.updateWorkPlanState(workPlanId,suggestion, passTag,loginUser.getUserId());
		GwUserVO userVO = userService.searchUserDetail(userId);
		GwServiceVO serviceVO = gwServiceService.searchService(serviceId);
		//更新审核结果
		GwModelDataFetchVO fetchVO = null;
		
		if(fetchId != null){	//用户发起的服务取数申请
			fetchVO = fetchDAO.findById(Long.valueOf(fetchId));
			fetchVO.setAuditStatus(passTag);
			fetchVO.setAuditTime(new Date());
			fetchVO.setUpdateTime(new Date());
			fetchVO.setUpdateUser(loginUser.getLoginName());
			fetchDAO.update(fetchVO);
		}
		//更新流程状态为第二次审核的执行中
      	processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.SECONDAUDIT,DataTypeConstent.EXCUTING ,null,"1");

		if(CommonState.PASS.equals(passTag)){	//不通过的需要让数据安全管理员重新配置再来审核，不通过不修改
				List<GwServiceFieldVO> fieldList = fieldDAO.searchServiceOutField(serviceVO.getServiceId());
				Map<String, GwServiceFieldVO> fieldMap = new HashMap<String, GwServiceFieldVO>();
				for (GwServiceFieldVO fieldVO : fieldList) {
					fieldMap.put(String.valueOf(fieldVO.getReorder()), fieldVO);
				}
				GwModelDataFetchVO fetch108VO = fetchDAO.searchModelDataFetch(userId, serviceId);
				GwUploadFileVO fileVO = uploadFileService.findById(fetch108VO.getCheckFileId());
				File ruleCheckFile = new File(ConfigConstants.BASE_UPLOAD_FILE_PATH+"/"+fileVO.getFilePath());

//				//解析合规检查规则文件
//				List<GwServiceCheckRuleVO> ruleList = desenModelService.parseServiceCheckRule(ruleCheckFile, serviceVO, userVO);
//				//更新108服务输出字段
//				for (GwServiceCheckRuleVO ruleVO : ruleList) {
//					GwServiceFieldVO fieldVO = fieldMap.get(ruleVO.getReorder());	//根据序号查找服务输出字段，存在则更新，否则新增
//	          		if(fieldVO == null) fieldVO = new GwServiceFieldVO();
//	          		fieldVO.setServiceId(serviceVO.getServiceId());
//	          		fieldVO.setFieldCode(ruleVO.getFieldCode());
//	      			fieldVO.setFieldName(ruleVO.getFieldName());
//	      			fieldVO.setFieldType(ruleVO.getFieldType());
//	      			fieldVO.setGatherType("1");
//	      			fieldVO.setReorder(ruleVO.getReorder());
//	      			fieldVO.setCreateTime(new Date());
//	      			fieldVO.setCreateUser(loginUser.getLoginName());
//	      			super.saveOrUpdate(fieldVO);	
//	      			fieldMap.remove(ruleVO.getReorder());
//				}
				
				//审核通过，从待审核中保存到检查规则表中
				List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
				for (GwServiceCheckRuleAuditVO auditVO : checkRuleAuditList) {
					GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();
					BeanUtil.copyProperties(ruleVO, auditVO);
					super.saveOrUpdate(ruleVO);
				}
				
				//根据序号删除不在规则文件中的服务输出字段
	          	if(!fieldMap.isEmpty()){
	          		Iterator<String> it = fieldMap.keySet().iterator();
	          		String reorders = "";
	          		while (it.hasNext()) {
						String reorder = (String) it.next();
						reorders += ("".equals(reorders)?"":",") + reorder;
					}
	          		if(!"".equals(reorders))
	          		gwServiceFieldDAO.deleteServiceFieldByreorder(reorders, serviceVO.getServiceId(),"1");
	          	}
	          	//--start--更新流程进度及记录流程操作过程
				GwProcessOperationVO gwVO = processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.S_THIRD);
				if(gwVO==null){
					gwVO=processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.X_THIRD);
					gwVO.setOperateContent("已完成二次审核");
					gwVO.setOperateTime(new Date());
					gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
					gwVO.setDealType(GwUserType.AUDIT_USER);
					gwVO.setPlanId(Long.valueOf(workPlanId));
					processOperationService.update(gwVO);
				}else{
					gwVO.setOperateContent("已完成二次审核");
					gwVO.setOperateTime(new Date());
					gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
					gwVO.setDealType(GwUserType.AUDIT_USER);
					gwVO.setPlanId(Long.valueOf(workPlanId));
					processOperationService.update(gwVO);
				}
				//更新流程状态为第二次审核的结束
		      	processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.SECONDAUDIT,DataTypeConstent.COMPLETED ,new Date(),"0");
				/***更新流程操作记录表-end***/		
	          	//--end--
	          	//--start--发送G 鉴权号 给数据用户和短信通知  并保存在fetch表authority_gno字段
				Endecrypt endecrypt = new Endecrypt();
				String loginPwd = endecrypt.get3DESDecrypt(userVO.getLoginPwd(), SessionConstant.SPKEY_PASSWORD);
				String G_Message = userVO.getLoginName()+"||"+loginPwd+"||"+serviceVO.getServiceCode();			
				String G_Num = Md5Util.encrypt(G_Message);
				GwModelDataFetchVO fetchVO2 = fetchDAO.findById(Long.valueOf(fetchId));
				fetchVO2.setAuthorityGno(G_Num);
				fetchDAO.update(fetchVO);
				String planContentStr = "您申请的服务（服务编码："+serviceVO.getServiceCode()+"，服务名称："+serviceVO.getServiceName()+"）取数已审批通过，G鉴权号为："+G_Num;
				String msgContent="您申请的服务（服务编码："+serviceVO.getServiceCode()+"，服务名称："+serviceVO.getServiceName()+"）取数已审批通过，G鉴权号为："+G_Num;
				workPlanService.saveWorkPlan("服务申请成功",WorkPlanConstent.FETCH_APPLY_SUCCESS, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(),userId,Long.valueOf(workPlanId),msgContent,null);
				//--end--
			}						
		//审核不通过生产退回待办
		if(CommonState.NO_PASS.equals(passTag)){
			//--start--结束流程
			processService.updateProcess(Long.valueOf(processId), DataTypeConstent.COMPLETED, new Date());
			//--end--
			String planContentStr = "您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"操作不通过，请重新申请。";
			//待办关联表主键
			String extenTableKey = userId+","+ serviceId+","+fetchId;
			String msgContent="您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"审核不通过,请知晓。【数据网关平台】";
			GwWorkPlanVO workPlan = workPlanService.saveWorkPlan("创建服务并取数退回",WorkPlanConstent.GET_DATA_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(),userVO.getUserId(),workPlanId,msgContent,processId);
			workPlanParamService.saveParamMap(workPlan.getPlanId(), paramMap);
		}else if(CommonState.REMODIFYRULE.equals(passTag)){
			String planContentStr = "您配置的合规规则,服务编码="+serviceVO.getServiceCode()+"用户编码="+userVO.getLoginName()+"审核不通过，请重新配置。";
			//待办关联表主键
			String extenTableKey = userId+","+ serviceId+","+fetchId;
			String msgContent="您配置的合规规则,服务编码="+serviceVO.getServiceCode()+"用户编码="+userVO.getLoginName()+"审核不通过，请重新配置。【数据网关平台】";
			GwWorkPlanVO workPlan = workPlanService.saveWorkPlan("创建服务并取数规则退回",WorkPlanConstent.SERVICE_FETCH_AUDIT_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(),null,workPlanId,msgContent,processId);
			workPlanParamService.saveParamMap(workPlan.getPlanId(), paramMap);
		}else{
		    //生成离线服务取数任务，实时的不创建任务让用户页面实时取
			if("true".equals(userApply)){	//数据用户发起的服务取数申请
				if(serviceVO != null){
					if(DataTypeConstent.OFFLINE.equals(serviceVO.getServiceType())){
						appFetchVO = fetchVO;	//放在action中调用，事物没提交在存储过程中查询不到数据
					}
				}
			}
		}
		
		GwWorkPlanVO workPlanVO = workPlanService.searchById(workPlanId);
		GwUserVO planCreateUser = userService.searchUserDetail(workPlanVO.getCreateUserId());
		//查询待办信息
		if(loginUser != null) {
			//写操作日志
			GwSysDictVO dictVO = SysDictManage.getSysDict("DICT_AUDIT_STATE", passTag);
			passTag = dictVO != null ? dictVO.getDictValue() : passTag;
			String operateContent = GwUserType.getUserTypeName(planCreateUser.getUserType())+"："+planCreateUser.getLoginName()+"配置了用户："+userVO.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，审核结果："+passTag+"。";
			if("true".equals(userApply)){	//数据用户发起的服务
				operateContent += "并"+passTag+userVO.getLoginName()+"的服务取数申请，fetchId="+fetchVO.getFetchId();
			}
			operationLogService.saveOperationLog(loginUser.getLoginName(), userVO.getLoginName(), OperateTypeConstent.DATA_AUDIT, operateContent);
		}
		return appFetchVO;
	}
	//实时检查
	public void onlineCheck(GwUserVO acceptUser, GwServiceVO serviceVO,Long fetchId,Date auditTime,String fileList, GwOperationLogVO logVO,GwUserVO loginUser,GwUserVO orgUser) throws Exception{

		taskDAO.onlineCheck(acceptUser,serviceVO,fetchId,auditTime,fileList,loginUser,orgUser);
		//写操作日志
		try {
			operationLogService.save(logVO);
		} catch (Exception e) {
			log.error("保存操作日志出错："+e.getMessage()+"。userCode="+logVO.getOperationUser()+",acceptUserCode="+logVO.getAcceptUser()+",operateType="+logVO.getOperationType()+",OperateContent="+logVO.getOperationContent(),e);
			e.printStackTrace();
		}

	}

	
	public GwModelDataFetchVO serviceFetchVerify(String passTag, String planId,
			String suggestion, GwUserVO loginUser) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 创建新的取数任务
	 * 
	 */
	public void createNewServiceTask(GwUserVO acceptUser, GwServiceVO serviceVO,Long fetchId,Date auditTime,String fileName, GwOperationLogVO logVO,GwUserVO loginUser,GwUserVO orgUser,String processId) throws Exception{

		//源数据文件目录
		String path=ConfigConstants.FTP_USER_PATH+orgUser.getLoginName()+"/"+acceptUser.getLoginName();
		
		//创建服务任务
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
		newTaskVO.setDataProgressStatus(DataProgressStatus.DOWNLOAD_FINISH);	
		newTaskVO.setProcessId(Long.valueOf(processId));
		taskDAO.save(newTaskVO);
		
		//保存服务任务文件
		GwModelDataFileVO newFileVO = new GwModelDataFileVO();
		newFileVO.setTaskId(newTaskVO.getTaskId());
		newFileVO.setModelId(serviceVO.getModelId());
		newFileVO.setUserId(acceptUser.getUserId());
		newFileVO.setFileType("1");
		newFileVO.setFileStatus(CommonState.VALID);
		newFileVO.setFileName(fileName);
		newFileVO.setCreateTime(new Date());
		if(!StringUtils.isEmpty(fileName)){
			String unzipName = fileName.substring(0, fileName.indexOf(".gz"));
			newFileVO.setUnzipName(unzipName);
		}
		newFileVO.setFilePath(path);
		newFileVO.setFtpIp(acceptUser.getFtpIp());
		newFileVO.setFtpPort(acceptUser.getFtpPort());
		newFileVO.setFtpUser(acceptUser.getFtpUsername());
		newFileVO.setFtpPassword(acceptUser.getFtpPassword());
		gwServiceFieldDAO.save(newFileVO);
//		taskDAO.onlineCheck(acceptUser,serviceVO,fetchId,auditTime,fileList,loginUser,orgUser);
		//写操作日志
		try {
			operationLogService.save(logVO);
		} catch (Exception e) {
			log.error("保存操作日志出错："+e.getMessage()+"。userCode="+logVO.getOperationUser()+",acceptUserCode="+logVO.getAcceptUser()+",operateType="+logVO.getOperationType()+",OperateContent="+logVO.getOperationContent(),e);
			e.printStackTrace();
		}


		/***写流程表-start***/
		processService.saveProcess(Long.valueOf(processId), DataTypeConstent.FUWURENWUCHULI, new Date(), null, DataTypeConstent.EXCUTING, DataTypeConstent.CREATE,loginUser.getUserId(),DataTypeConstent.EXCUTING);
		/***写流程表-end***/
		/***写流程操作记录表-start***/
		processOperationService.saveProcessOperation(Long.valueOf(processId), loginUser.getUserId(), "系统创建服务", new Date(), null,DataTypeConstent.COMPLETED,GwUserType.DATA_USER,DataTypeConstent.F_FIRST);
		processOperationService.saveProcessOperation(Long.valueOf(processId), loginUser.getUserId(), "完成服务取数", new Date(), null,DataTypeConstent.COMPLETED,"System",DataTypeConstent.F_SECOND);
		if(serviceVO.getServiceSource().equals("1")){
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,"system",DataTypeConstent.F_THIRD);
		}else{
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,"system",DataTypeConstent.F_FOURTH);
		}
		/***写流程操作记录表-end***/
		processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.FETCH, DataTypeConstent.COMPLETED, null,"1");
	}
	
	/** 
	 * @Title: searchUserlServiceList 
	 * @Description: TODO(根据用户ID和审核状态查询用户下所有模型服务) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserServiceList(Long userId, String auditStatus) throws Exception{
		//写操作日志
//		operationLogService.saveOperationLog(userId, null, "401", "查看自己拥有模型的模型取数申请列表");
		
		return gwModelDataFetchDAO.searchUserServiceList(userId,auditStatus);
	}
	
	/** 
	 * @Title: searchUserlServiceList 
	 * @Description: TODO(根据用户ID和服务id查询模型服务) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public UserModelServiceAppVO searchServiceApp(Long userId,Long serviceId,String auditStatus)  throws Exception{
		//写操作日志
//		operationLogService.saveOperationLog(userId, null, "401", "查看自己拥有模型的模型取数申请列表");
		
		return gwModelDataFetchDAO.searchServiceApp(userId,serviceId,auditStatus);
	}
	
	public static void main(String[] args) {
		String fileName = "sdyuwy733.txt.gz";
		
		System.out.print("======="+fileName.substring(0, fileName.indexOf(".gz")));
	}
	
}
