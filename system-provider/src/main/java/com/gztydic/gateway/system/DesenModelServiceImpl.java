package com.gztydic.gateway.system;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.BeanUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwDesenServiceFieldDAO;
import com.gztydic.gateway.core.dao.GwDesenServiceInfoDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDictDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenServiceInfoView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldAuditVO;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/**
 * 模型信息脱敏配置
 * @author wangwei
 *
 */
@Service
public class DesenModelServiceImpl extends GeneralServiceImpl<GwDesenServiceInfoVO> implements DesenModelService {

	private final Log logger = LogFactory.getLog(DesenModelServiceImpl.class);
	
	@Resource
	private GwDesenServiceInfoDAO gwDesenServiceInfoDAO;
	
	@Resource
	private GwDesenServiceFieldDAO gwDesenServiceFieldDAO;
	
	@Resource
	private GwServiceFieldDictDAO gwServiceFieldDictDAO;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	@Resource(name="userServiceImpl")
	private UserService userService;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService serviceService;
	
	@Resource
	private GwModelDataFetchDAO fetchDAO;
	
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	
	@Resource
	private GwServiceCheckRuleDAO checkRuleDAO;
	
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService;    
	
	@Resource
	private GwServiceCheckRuleAuditDAO checkRuleAuditDAO;


	/**
	 * 查询模型服务信息脱敏配置列表
	 * @param loginName
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchDesenServiceInfoList(String loginName, PageObject pageObject) throws Exception {
		return gwDesenServiceInfoDAO.searchDesenServiceInfoList(loginName,pageObject);
	}
	
	public List<GwDesenServiceInfoView> searchModelServiceDesenInfo(Long userId, Long modelId) throws Exception {
		return gwDesenServiceInfoDAO.searchModelServiceDesenInfo(userId, modelId);
	}
	
	private Map<String, GwDesenServiceInfoVO> infoConvert2Map(List<GwDesenServiceInfoVO> list){
		Map<String, GwDesenServiceInfoVO> map = new HashMap<String, GwDesenServiceInfoVO>();
		for (GwDesenServiceInfoVO vo : list) {
			map.put(vo.getInfoDeseId().toString(), vo);
		}
		return map;
	}

	/**
	 * 新增、修改模型信息脱敏配置
	 * @param userId
	 * @param list
	 * @param userVO
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void updateDesenServiceInfoList(List<GwDesenServiceInfoVO> list, GwUserVO loginUser)
			throws Exception {
		if(list == null) throw new Exception("模型服务信息脱敏配置不能为空");
		
		String infoDesenIds = "";
		Long userId = null,serviceId = null;
		for (GwDesenServiceInfoVO v : list) {
			if(userId == null) userId = v.getUserId();
			if(serviceId == null) serviceId = v.getServiceId();
			infoDesenIds += ("".equals(infoDesenIds) ? "" : ",") + String.valueOf(v.getInfoDeseId()); 
		}
		GwUserVO userVO = userService.searchUserDetail(userId);
		GwServiceVO serviceVO = serviceService.search(GwServiceVO.class, serviceId);
		
		List<GwDesenServiceInfoVO> desenInfoList = gwDesenServiceInfoDAO.searchDesenServiceInfoList(infoDesenIds);
		Map<String, GwDesenServiceInfoVO> dbInfoMap = infoConvert2Map(desenInfoList);
		
		String operateContent = "";
		for (GwDesenServiceInfoVO viewVo : list) {
			GwDesenServiceInfoVO dbVO = dbInfoMap.get(String.valueOf(viewVo.getInfoDeseId()));
			if(dbVO == null){	//新增
				viewVo.setInfoDeseId(null);
				viewVo.setCreateUser(loginUser.getLoginName());
				viewVo.setCreateTime(new Date());
				super.save(viewVo);
				
				operateContent += "新增serviceCode="+serviceVO.getServiceCode()+" 模型信息权限="+("1".equals(viewVo.getModelInfo())) + " 输入集信息权限="+("1".equals(viewVo.getServiceInputInfo()))+";";
			}else{	//修改
				dbVO.setModelInfo(viewVo.getModelInfo());
				dbVO.setServiceInputInfo(viewVo.getServiceInputInfo());
				dbVO.setServiceId(viewVo.getServiceId());
				dbVO.setUpdateUser(loginUser.getLoginName());
				dbVO.setUpdateTime(new Date());
				super.saveOrUpdate(dbVO);
				
				operateContent += "修改serviceCode="+serviceVO.getServiceCode()+" 模型信息权限="+("1".equals(dbVO.getModelInfo())) + " 输入集信息权限="+("1".equals(dbVO.getServiceInputInfo()))+";";
			}
		}
		operationLogService.saveOperationLog(loginUser.getLoginName(), userVO.getLoginName(), OperateTypeConstent.INFO_UM_CONF, operateContent);
	}

	/**
	 * 查询模型字段脱敏配置列表
	 */
	public PageObject searchDesenServiceFieldList(String loginName,PageObject pageObject) throws Exception {
		return gwDesenServiceFieldDAO.searchDesenServiceFieldList(loginName,pageObject);
	}

	
	public GwDesenServiceInfoVO searchDesenServiceInfo(Long userId,Long serviceId) throws Exception {
		GwDesenServiceInfoVO infoVO = new GwDesenServiceInfoVO();
		infoVO.setServiceId(serviceId);
		infoVO.setUserId(userId);
		List<GwDesenServiceInfoVO> infoList = gwDesenServiceInfoDAO.searchDesenServiceInfo(infoVO);
		return infoList.size()>0?infoList.get(0):null;
	}
	
	/**
	 * 查询userId的serviceId的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldList(Long userId, Long serviceId) throws Exception {
		return gwDesenServiceFieldDAO.searchDesenRuleServiceFieldList(userId, serviceId);
	}
	
	public List<GwDesenRuleServiceFieldView> search108DesenRuleServiceFieldList(Long userId, Long serviceId) throws Exception {
		return gwDesenServiceFieldDAO.search108DesenRuleServiceFieldList(userId, serviceId);
	}
	
	
	public List<GwDesenRuleServiceFieldView> searchDesenRuleListByBatch(GwModelDataFetchTaskVO taskVO) throws Exception {
		return gwDesenServiceFieldDAO.searchDesenRuleListByBatch(taskVO);
	}
	
	/**
	 * 查询userId的serviceId的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldConfig(Long userId, Long serviceId) throws Exception{
		return gwDesenServiceFieldDAO.searchDesenRuleServiceFieldConfig(userId, serviceId);
	}
	
	//查询userId的serviceId的字段检查规则列表
	public List<GwRuleCheckServiceFieldView> searchCheckRuleServiceFieldConfig(Long userId, Long serviceId) throws Exception{
		return gwDesenServiceFieldDAO.searchCheckRuleServiceFieldConfig(userId, serviceId);
	}
	
	
	/**
	 * 查询服务所属模型下其他服务的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List searchDesenRuleOtherServiceFieldList(Long userId,GwServiceVO service) throws Exception {
		//查询服务所属模型下其他服务的字段脱敏规则列表
		List<GwDesenRuleServiceFieldView> list = gwDesenServiceFieldDAO.searchDesenRuleOtherServiceFieldList(userId, service);
		List ruleList=new ArrayList();
		Map<Long,List<GwDesenRuleServiceFieldView>> map = new HashMap<Long,List<GwDesenRuleServiceFieldView>>();
		Map<Long,String> desenTypeMap=new HashMap<Long,String>();
		List<GwDesenRuleServiceFieldView> viewLsit = null;
		for (GwDesenRuleServiceFieldView v : list) {
			viewLsit = map.get(v.getServiceId());
			if(viewLsit == null) viewLsit = new ArrayList<GwDesenRuleServiceFieldView>();
			viewLsit.add(v);
			//以serviceId为单位分开存放
			map.put(v.getServiceId(), viewLsit);
			desenTypeMap.put(v.getServiceId(), v.getDesenType());
		}
		ruleList.add(map);
		ruleList.add(desenTypeMap);
		return ruleList;
	}
	
	
	
	/**
	 * 新增、修改模型服务字段脱敏配置
	 */
	public void updateDesenServiceFieldList(Long workPlanId,List<GwDesenServiceFieldVO> list, GwUserVO loginUser,GwModelDataFetchVO viewFetchVO, GwUploadFileVO fileVO, File ruleCheckFile,String ruleUpdateType,String processId,String type) throws Exception {
		if("1".equals(viewFetchVO.getDesenType()) && (list == null || list.size() == 0)) throw new Exception("服务字段脱敏配置不能为空");
		int preBatch = 0;//上一个检查规则，用于在审核页面展示
		int desenPreBatch = 0;
		Long ruleBatch = 0l;
		Long desenBatch = 0l;
		
		Map<String, String> paramMap = new HashMap<String, String>();
		GwUserVO userVO = userService.searchUserDetail(viewFetchVO.getUserId());
		GwServiceVO serviceVO = serviceService.search(GwServiceVO.class, viewFetchVO.getServiceId());
		GwModelDataFetchVO dbFetchVO = fetchDAO.searchModelDataFetch(userVO.getUserId(), serviceVO.getServiceId());
		
		String operateContent = "配置userId="+userVO.getUserId()+",serviceCode="+serviceVO.getServiceCode()+"的字段脱敏规则：";
		//if("1".equals(viewFetchVO.getDesenType())){	//脱敏并合规检查
		    
		//}else if("2".equals(viewFetchVO.getDesenType())){	//合规检查
		if(type.equals("2")){
			preBatch = checkRuleDAO.searchMaxBatch(userVO.getUserId(), serviceVO.getServiceId());
		}	
			//ruleCheckFile为空的话，说明使用的是数据用户默认的规则文件,并不需要解析规则文件
			/*if(!CommonState.SERVICE_SOURCE_108.equals(serviceVO.getServiceSource())){
				throw new RuntimeException("错误，合规检查只适用于108的服务");
			}*/
			

			//解析合规检查规则文件
			if(ruleCheckFile != null){
				List<GwServiceCheckRuleVO> ruleList = parseServiceCheckRule(ruleCheckFile, serviceVO, userVO);			
				operateContent = "配置userId="+userVO.getUserId()+",serviceCode="+serviceVO.getServiceCode()+"的合规检查规则：";
				ruleBatch = Long.parseLong(String.valueOf(super.searchSequenceValue("SEQ_SERVICE_CHECK_RULE_BATCH")));
				for(GwServiceCheckRuleVO ruleVO : ruleList){
					ruleVO.setCheckBatch(ruleBatch);
					GwServiceCheckRuleAuditVO ruleAuditVO = new GwServiceCheckRuleAuditVO();
					BeanUtil.copyProperties(ruleAuditVO, ruleVO);
					super.save(ruleAuditVO);
					
					GwSysDictVO dict = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE", ruleVO.getCheckType());
					operateContent += ruleVO.getFieldCode()+"=("+(StringUtils.isNotBlank(dict.getDictValue())?"检查规则类型："+dict.getDictValue()+"; ":"")+
							(StringUtils.isNotEmpty(ruleVO.getCheckRule())?" 检查规则："+ruleVO.getCheckRule()+"、":"");
				}
			}
			operateContent += " 不合规检查数量："+viewFetchVO.getMaxCheckNum()+";合规输出行数："+viewFetchVO.getOutputNum()+";是否需要审核:"+viewFetchVO.getCheckAudit()+";规则文件ID:"+fileVO.getFileId();
		//}
			if(ruleCheckFile == null){
				dbFetchVO.setCheckFileId(fileVO.getFileId());
				ruleBatch = Long.valueOf(checkRuleDAO.searchMaxBatch(userVO.getUserId(), serviceVO.getServiceId()));
			}else{
				uploadFileService.save(fileVO);	//保存上传的文件信息
			}
		//脱敏并合规才有的
		if(type.equals("1")){
			
			desenPreBatch = gwDesenServiceFieldDAO.searchMaxBatch(userVO.getUserId(), serviceVO.getServiceId());
			
			
			desenBatch = Long.parseLong(String.valueOf(super.searchSequenceValue("SEQ_SERVICE_DESEN_BATCH")));
		
			List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(ruleBatch);
			int i=-1;
			for (GwDesenServiceFieldVO viewVo : list) {	
				i++;
				if(viewVo.getFieldId() == null) continue;
				//if(viewVo.getFieldId() == -1){//-1表示勾选的
					if(serviceVO.getServiceSource().equals("1")){//挖掘的脱敏表 是字段ID 是等于 字段表的字段ID
						viewVo.setFieldId(viewVo.getFieldId());	
					}else{//108的脱敏表是字段ID 是 等于 合规表的字段ID
						viewVo.setFieldId(checkRuleAuditList.get(i).getCheckId());	
					}

				//}
				//页面选择的多选框多个值会有空格
				if(StringUtils.isNotBlank(viewVo.getConditionContent())) viewVo.setConditionContent(viewVo.getConditionContent().replaceAll(" ", ""));
				viewVo.setFieldDeseId(null);
				viewVo.setCreateUser(loginUser.getLoginName());
				viewVo.setBatch(desenBatch);
				
				GwDesenServiceFieldAuditVO ruleAuditVO = new GwDesenServiceFieldAuditVO();
				BeanUtil.copyProperties(ruleAuditVO, viewVo);
				ruleAuditVO.setCreateTime(new Date());
				ruleAuditVO.setFieldDeseId(null);
				super.save(ruleAuditVO);
				
				GwSysDictVO dict = SysDictManage.getSysDict("DICT_DESEN_RULE_TYPE", ruleAuditVO.getRuleType());
				operateContent += ruleAuditVO.getFieldCode()+"=("+(dict!=null&&StringUtils.isNotBlank(dict.getDictValue())?"脱敏类型："+dict.getDictValue()+"; ":"")+
						(StringUtils.isNotEmpty(ruleAuditVO.getRuleContent())?" 处理定位："+ruleAuditVO.getRuleContent()+";":"") + 
						(StringUtils.isNotEmpty(ruleAuditVO.getReplaceContent())?" 替换字符："+ruleAuditVO.getReplaceContent()+";":"") +
						(StringUtils.isNotEmpty(ruleAuditVO.getConditionType())?" 条件类型："+getConditionType(viewVo)+";":"") +
						(StringUtils.isNotEmpty(ruleAuditVO.getConditionContent())?" 条件内容："+ruleAuditVO.getConditionContent()+";":"")	;
				//保存字段、字典关联表数据
				if(StringUtils.isNotBlank(ruleAuditVO.getDictCode())){
					GwServiceFieldDictAuditVO dictVO = new GwServiceFieldDictAuditVO();
					dictVO.setFieldId(ruleAuditVO.getFieldId());
					dictVO.setDictCode(ruleAuditVO.getDictCode());
					dictVO.setUserId(userVO.getUserId());
					dictVO.setDesenId(ruleAuditVO.getFieldDeseId());
					dictVO.setBatch(desenBatch);
					gwServiceFieldDictDAO.save(dictVO);
					
					operateContent += "; 关联字典："+ruleAuditVO.getDictCode();
				}
				operateContent += "); ";
			}
		}
		
		operateContent += " 不合规检查数量："+viewFetchVO.getMaxCheckNum()+";合规输出行数："+viewFetchVO.getOutputNum();
		paramMap.put("desenType", dbFetchVO.getDesenType());
		paramMap.put("maxCheckNum", String.valueOf(dbFetchVO.getMaxCheckNum()));
		paramMap.put("checkAudit", dbFetchVO.getCheckAudit());
		paramMap.put("outputNum", String.valueOf(dbFetchVO.getOutputNum()));
		paramMap.put("fileId", String.valueOf(fileVO.getFileId()));
		//dbFetchVO.setDesenType(viewFetchVO.getDesenType());
		
		//dbFetchVO.setMaxCheckNum(viewFetchVO.getMaxCheckNum());
		//dbFetchVO.setCheckAudit(viewFetchVO.getCheckAudit());
		//dbFetchVO.setOutputNum(viewFetchVO.getOutputNum());
		//dbFetchVO.setPushDataWay(viewFetchVO.getPushDataWay());
		//dbFetchVO.setCheckFileId(fileVO.getFileId());

		logger.info(loginUser.getLoginName()+operateContent);
		operationLogService.saveOperationLog(loginUser.getLoginName(), userVO.getLoginName(), OperateTypeConstent.RULE_CONF, operateContent);
		
		GwWorkPlanVO workPlanVO = null;
		if(workPlanId != null){//数据用户申请服务取数才有的待办
			workPlanVO = workPlanService.searchById(workPlanId);
			if(workPlanVO != null){	//字段脱敏配置通过
				workPlanVO.setPlanState(WorkPlanConstent.DEAL_PASS);
				workPlanVO.setDealUserId(loginUser.getUserId());
				workPlanVO.setDaelTime(new Date());
				workPlanService.saveOrUpdate(workPlanVO);
			} 
		}else {//安全管理员主动配置脱敏规则
			//将用户的服务对应的服务申请修改为待审核
			dbFetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
			dbFetchVO.setUpdateUser(loginUser.getLoginName());
			dbFetchVO.setUpdateTime(new Date());
		}
		fetchDAO.update(dbFetchVO);
		String userApply = "";
//		新增字段脱敏审核待办
		String planContent = "";
		if("true".equals(userApply)){//数据用户取数申请
			planContent = GwUserType.getUserTypeName(userVO.getUserType())+"："+userVO.getLoginName()+"申请了服务(服务编码="+serviceVO.getServiceCode()+", 服务名称="+serviceVO.getServiceName()+")取数。";
		}
		planContent += GwUserType.getUserTypeName(loginUser.getUserType())+"："+loginUser.getLoginName()+"配置了用户："+userVO.getLoginName()+"的服务(服务编码="+serviceVO.getServiceCode()+", 服务名称="+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核";	
		String msgContent=SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"配置了用户："+userVO.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核！【数据网关平台】";
		workPlanVO = workPlanService.saveWorkPlan("服务数据脱敏规则审核", WorkPlanConstent.FIELD_DESEN_CONF_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(), 
				null,workPlanVO!=null?workPlanVO.getPlanId():null,msgContent,null);
		paramMap.put("userId",String.valueOf(userVO.getUserId()));
		paramMap.put("serviceId",String.valueOf(serviceVO.getServiceId()));
		if(dbFetchVO.getFetchId() != null){
			paramMap.put("fetchId", dbFetchVO.getFetchId().toString());
		}
		paramMap.put("userApply", userApply);
		paramMap.put("ruleUpdateType", ruleUpdateType);
		paramMap.put("preBatch", String.valueOf(preBatch));
		paramMap.put("ruleBatch", String.valueOf(ruleBatch));
		paramMap.put("desenBatch", String.valueOf(desenBatch));
		paramMap.put("desenPreBatch",String.valueOf(desenPreBatch));
		paramMap.put("type", type);
		workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);
		
		}		

	private String getConditionType(GwDesenServiceFieldVO vo){
		if("in".equals(vo.getConditionType()) && StringUtils.isNotEmpty(vo.getConditionContent())){
			return vo.getDictCode()+"字典数据筛选";
		}
		return vo.getConditionType();
	}
	//************安全管理员保存规则和生成一条待办给审核人员****************
	public void savaRule(GwUserVO loginUser,GwModelDataFetchVO fetchVO, String workPlanId,String serviceId,String fetchId,String userId,File ruleCheckFile,String fileId,String processId  ) throws  Exception{
		GwUserVO userVO = userService.searchUserDetail(Long.valueOf(userId));
		long preBatch = 0l;
		long ruleBatch = 0l;
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(workPlanId));
		ruleBatch = Integer.valueOf(paramMap.get("ruleBatch"));//数据用户上传的规则表ID		
		GwServiceVO serviceVO = serviceService.search(GwServiceVO.class,Long.valueOf(serviceId));
		GwWorkPlanVO workPlanVO = null;
		try {
			GwUploadFileVO fileVo = uploadFileService.findById(Long.valueOf(fileId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		preBatch =  Long.valueOf(ruleBatch);
		String operateContent = "配置userId="+loginUser.getUserId()+",serviceCode="+serviceVO.getServiceCode()+"的合规检查规则：";
		List<GwServiceCheckRuleVO> ruleList;
		try {
			ruleList = parseServiceCheckRule(ruleCheckFile,serviceVO,loginUser);		
			ruleBatch = Long.parseLong(String.valueOf(super.searchSequenceValue("SEQ_SERVICE_CHECK_RULE_BATCH")));
			for(GwServiceCheckRuleVO ruleVO : ruleList){
				ruleVO.setCheckBatch(ruleBatch);
				GwServiceCheckRuleAuditVO ruleAuditVO = new GwServiceCheckRuleAuditVO();
				BeanUtil.copyProperties(ruleAuditVO, ruleVO);
				super.save(ruleAuditVO);				
				GwSysDictVO dict = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE", ruleVO.getCheckType());
				operateContent += ruleVO.getFieldCode()+"=("+(StringUtils.isNotBlank(dict.getDictValue())?"检查规则类型："+dict.getDictValue()+"; ":"")+
						(StringUtils.isNotEmpty(ruleVO.getCheckRule())?" 检查规则："+ruleVO.getCheckRule()+"、":"");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(loginUser.getLoginName()+operateContent);
		operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.RULE_CONF, operateContent);
		
		String planContent = GwUserType.getUserTypeName(loginUser.getUserType())+"："+loginUser.getLoginName()+"配置了用户："+userVO.getLoginName()+"的服务(服务编码="+serviceVO.getServiceCode()+", 服务名称="+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核";	
		String msgContent=SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"配置了用户："+userVO.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核！【数据网关平台】";
		workPlanVO = workPlanService.saveWorkPlan("创建服务并申请审批", WorkPlanConstent.SERVICE_FETCH_AUDIT2, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(), 
				null,workPlanVO!=null?workPlanVO.getPlanId():null,msgContent,processId);
		paramMap.put("userId",String.valueOf(userId));
		paramMap.put("serviceId",String.valueOf(serviceVO.getServiceId()));
		paramMap.put("fetchId", fetchId);
		paramMap.put("preBatch",String.valueOf(preBatch));
		paramMap.put("ruleBatch", String.valueOf(ruleBatch));
		workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);
	
		workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);
		
}
	//************数据用户保存规则和生成一条待办给数据安全用户****************

	public void savaRuleAndWorkPlan(GwModelDataFetchVO fetchVO,GwServiceVO serviceVO, GwUserVO loginUser,GwUploadFileVO fileVO, File ruleCheckFile,String ruleUpdateType,String processId,String processType) throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		//解析合规解析表
		List<GwServiceCheckRuleVO> ruleList = parseServiceCheckRule(ruleCheckFile,serviceVO,loginUser);
		//保存上传的文件信息
		uploadFileService.save(fileVO);	
		//保存合规解析表
		String operateContent = "配置userId="+loginUser.getUserId()+",serviceCode="+serviceVO.getServiceCode()+"的合规检查规则：";
		Long ruleBatch = Long.parseLong(String.valueOf(super.searchSequenceValue("SEQ_SERVICE_CHECK_RULE_BATCH")));
			for(GwServiceCheckRuleVO ruleVO : ruleList){
				ruleVO.setCheckBatch(ruleBatch);
				GwServiceCheckRuleAuditVO ruleAuditVO = new GwServiceCheckRuleAuditVO();
				BeanUtil.copyProperties(ruleAuditVO, ruleVO);
				super.save(ruleAuditVO);				
				GwSysDictVO dict = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE", ruleVO.getCheckType());
				operateContent += ruleVO.getFieldCode()+"=("+(StringUtils.isNotBlank(dict.getDictValue())?"检查规则类型："+dict.getDictValue()+"; ":"")+
						(StringUtils.isNotEmpty(ruleVO.getCheckRule())?" 检查规则："+ruleVO.getCheckRule()+"、":"");
			}
		logger.info(loginUser.getLoginName()+operateContent);
		operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.RULE_CONF, operateContent);
		//生成一条审核待办给数据安全用户
		String extenTableKey = fetchVO.getUserId().toString()+","+ fetchVO.getServiceId()+","+fetchVO.getFetchId();
		String planContent = "数据用户"+loginUser.getUserName()+"创建了服务（服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+"）并申请该服务取数，服务合规检查规则建议如下，请审核";	
		String msgContent=SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"配置了用户："+loginUser.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核！【数据网关平台】";
		logger.info("--------------processId:"+processId);
		GwWorkPlanVO workPlanVO = workPlanService.saveWorkPlan("服务创建并申请审批", WorkPlanConstent.SERVICE_FETCH_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(), 
				null,null,msgContent,processId);
		paramMap.put("userId",String.valueOf(loginUser.getUserId()));
		paramMap.put("serviceId",String.valueOf(serviceVO.getServiceId()));
		paramMap.put("ruleUpdateType", ruleUpdateType);
		paramMap.put("ruleBatch", String.valueOf(ruleBatch));
		paramMap.put("isServiceFetch","isServiceFetch");
		paramMap.put("fileId", String.valueOf(fileVO.getFileId()));
		paramMap.put("fetchId", String.valueOf(fetchVO.getFetchId()));
		workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);		
		
		/***写流程表-start***/
		/***写流程表-end***/
		/***写流程操作记录表-start***/
		if(processType.equals(DataTypeConstent.SHENQINGQUSHU)){
			//新增申请取数流程
			processService.saveProcess(Long.valueOf(processId), DataTypeConstent.SHENQINGQUSHU, new Date(), null, DataTypeConstent.EXCUTING,DataTypeConstent.APPFETCH,loginUser.getUserId(),DataTypeConstent.EXCUTING);
			//新增修改改规则步骤
			processOperationService.saveProcessOperation(Long.valueOf(processId), loginUser.getUserId(), "完成申请取数", new Date(), workPlanVO.getPlanId(),DataTypeConstent.COMPLETED,loginUser.getUserType(),DataTypeConstent.S_FIRST);
			//更新流程状态
			processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.SHENQINGQUSHU, DataTypeConstent.COMPLETED,null,null);
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,GwUserType.SAFE_USER,DataTypeConstent.S_SECOND);
		}
		if(processType.equals(DataTypeConstent.XIUGAIGUIZHESHENPI)){
			//新增修改改规则流程
			processService.saveProcess(Long.valueOf(processId), DataTypeConstent.XIUGAIGUIZHESHENPI, new Date(), null, DataTypeConstent.EXCUTING,DataTypeConstent.APPFETCH,loginUser.getUserId(),DataTypeConstent.EXCUTING);
			//新增修改改规则步骤
			processOperationService.saveProcessOperation(Long.valueOf(processId), loginUser.getUserId(), "完成修改规则", new Date(), workPlanVO.getPlanId(),DataTypeConstent.COMPLETED,loginUser.getUserType(),DataTypeConstent.X_FIRST);
			//更新流程状态
			processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.SHENQINGQUSHU, DataTypeConstent.COMPLETED,null,null);
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,GwUserType.SAFE_USER,DataTypeConstent.X_SECOND);

		}
		/***写流程操作记录表-end***/
		
		
	}
	//解析合规检查规则文件
	public List<GwServiceCheckRuleVO> parseServiceCheckRule(File ruleCheckFile, GwServiceVO serviceVO, GwUserVO userVO) throws Exception{
		try {
            Workbook workbook = null;
	  		try {
	  			workbook = new HSSFWorkbook(new FileInputStream(ruleCheckFile));	//2003
	  		} catch (Exception e) {
	  			try {
	  				workbook = new XSSFWorkbook(new FileInputStream(ruleCheckFile)); //2007
	  			} catch (Exception e1) {
	  				e1.printStackTrace();
	  				throw e1;
	  			}
	  		}
	  		int rowCount = 0;
            Sheet sheet = workbook.getSheetAt(0);
            rowCount = sheet.getLastRowNum();
            if(rowCount < 1){
            	throw new RuntimeException("合规检查规则文件数据必须大于1行");
            }
            
            GwServiceCheckRuleVO ruleVO = null;
            List<GwServiceCheckRuleVO> desenList = new ArrayList<GwServiceCheckRuleVO>();
          	for(int i=1;i<=rowCount;i++){//行
          		String reorder = getCellValue(sheet.getRow(i).getCell(0));//序号, 也是字段编码
          		String fieldName = getCellValue(sheet.getRow(i).getCell(1)); //变量定义，字段中文名称
          		String fieldType = getCellValue(sheet.getRow(i).getCell(2)); //字段类型
          		String checkType = getRuleCheckType(getCellValue(sheet.getRow(i).getCell(3)));//检查规则类型
          		String checkRule = getCellValue(sheet.getRow(i).getCell(4));//检查规则
          		if(StringUtils.isBlank(fieldType)){
          			throw new Exception("规则文件中的第三列字段类型不能为空");
          		}
          		
          		if(StringUtils.isBlank(checkType) || StringUtils.isBlank(checkRule)){
          			throw new Exception("第"+(i)+"行的检查规则类型或检查规则不能为空");
          		}
          		
          		ruleVO = new GwServiceCheckRuleVO();
          		if(serviceVO != null){
              		ruleVO.setServiceId(serviceVO.getServiceId());
          		}
          		if(userVO != null){
              		ruleVO.setUserId(userVO.getUserId());
          		}
          		ruleVO.setCheckType(checkType);
          		ruleVO.setCheckRule(checkRule);
//          		ruleVO.setReorder(Long.parseLong(reorder));
          		ruleVO.setReorder(Long.parseLong(String.valueOf(i)));	//行号(-1)作为输出文件的字段顺序
          		ruleVO.setFieldCode("COLUMN_"+reorder);
          		ruleVO.setFieldName(fieldName);
          		ruleVO.setFieldType(fieldType);
          		desenList.add(ruleVO);
          	}
          	return desenList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析合规检查规则文件失败:"+e.getMessage());
		}
	}
	
	public String getCellValue(Cell cell){
		if(cell==null) return "";
		if(cell.getCellType() != HSSFCell.CELL_TYPE_STRING){
			cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
        } 
		String value = cell.getStringCellValue();
		return value != null ? value.trim() : "";
	}
	
	private String getRuleCheckType(String ruleCheckType) throws Exception{
		if(StringUtils.isBlank(ruleCheckType)) return null;
		Map<String, GwSysDictVO> dictMap = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE");
		Iterator<String> it = dictMap.keySet().iterator();
		String checkType = null;
		while (it.hasNext()) {
			String key = it.next();
			if(ruleCheckType.equals(dictMap.get(key).getDictValue())){
				checkType = dictMap.get(key).getDictKey();
				break;
			}
		}
		if(checkType == null) {
			logger.error("检查规则类型值不正确："+ruleCheckType);
			throw new Exception("检查规则类型不正确："+ruleCheckType);
		}
		return checkType;
	}
	
	public void appViewService(GwUserVO loginUser,GwServiceView serviceView) throws Exception{
		String userType = GwUserType.DATA_USER.equals(loginUser.getUserType())?"数据":GwUserType.ORG_USER.equals(loginUser.getUserType())?"机构":"";
		String planContent = userType+"用户："+loginUser.getLoginName()+"申请查看服务(服务编码="+serviceView.getServiceCode()+"，服务名称="+serviceView.getServiceName()+")的输入集信息，请审核！";
		//待办入库
		String msgContent=userType+":"+loginUser.getLoginName()+"申请查看服务("+serviceView.getServiceName()+")的输入集信息，请审核！【数据网关平台】";
		GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("服务输入集信息脱敏配置",WorkPlanConstent.INFO_DESEN_CONF_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(),null,null,msgContent,null);
		Map map=new HashMap();
		map.put("userId",String.valueOf(loginUser.getUserId()));
		map.put("serviceId", String.valueOf(serviceView.getServiceId()));
		map.put("planType", "viewService");
		workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
	}
	
	public void appViewModel(GwUserVO loginUser,GwServiceView serviceView) throws Exception{
		String userType = GwUserType.DATA_USER.equals(loginUser.getUserType())?"数据":GwUserType.ORG_USER.equals(loginUser.getUserType())?"机构":"";
		String planContent = userType+"用户："+loginUser.getLoginName()+"申请查看服务(服务编码="+serviceView.getServiceCode()+"，服务名称="+serviceView.getServiceName()+")的模型基本信息，请审核！";
		//待办入库
		String msgContent=userType+":"+loginUser.getLoginName()+"申请查看服务(潜在单卡转合约用户发现服务（离线月）)的模型基本信息，请审核！【数据网关平台】";
		GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("模型基本信息脱敏配置",WorkPlanConstent.INFO_DESEN_CONF_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(),null,null,msgContent,null);
		Map map=new HashMap();
		map.put("userId",loginUser.getUserId().toString());
		map.put("serviceId", String.valueOf(serviceView.getServiceId()));
		map.put("planType", "viewModel");
		workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
	}
	
	public Map<String, Integer> searchTaskCheckCount(Long serviceId,Long userId) throws Exception{
		Integer checkCount = gwDesenServiceFieldDAO.searchCheckingTaskCount(serviceId, userId);
		Integer auditCount = gwDesenServiceFieldDAO.searchAuditingTaskCount(serviceId, userId);
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("checkCount", checkCount);
		map.put("auditCount", auditCount);
		return map;
	}

	public void updateServiceFetchFieldList(Long workPlanId,
			List<GwDesenServiceFieldVO> list, GwUserVO loginUser,
			GwModelDataFetchVO viewFetchVO, GwUploadFileVO fileVO,
			File ruleCheckFile, String ruleUpdateType) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	public PageObject searchServiceCheckRule(Long serviceId,
			Long userId,PageObject pageObject) throws Exception {
		return checkRuleDAO.searchLastCheckRule(userId, serviceId,pageObject);
		
    }
	
	/**
	 * 
	 * @Title: searchLastCheckRuleList 
	 * @Description: TODO(查询服务最新的合规检查规则) 
	 * @param @param userId
	 * @param @param serviceId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwServiceCheckRuleVO>    返回类型 
	 * @throws
	 */
	public List<GwServiceCheckRuleVO> searchLastCheckRuleList(Long userId, Long serviceId) throws Exception {
		return checkRuleDAO.searchLastCheckRuleList(userId, serviceId);
	}
}
