package com.gztydic.gateway.gather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.PushWebserviceUtil;
import com.gztydic.gateway.core.dao.DataGatherDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.gather.webservice.IDataService;
import com.gztydic.gateway.gather.webservice.client.data.DataAreaResponse;
import com.gztydic.gateway.gather.webservice.client.data.DataEntity;
import com.gztydic.gateway.gather.webservice.client.data.ResponseVO;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.gather.webservice.client.data.ServiceRequestVO;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.LiabilityService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.WorkPlanService;

/**
 * 数据处理
 *
 */
@Service
public class DataGatherServiceImpl implements DataGatherService{

	private static final Log log = LogFactory.getLog(DataGatherServiceImpl.class);
	
	@Resource(name="dataDesenServiceImpl")
	private DataDesenService dataDesenService;
	
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	
	@Resource(name="dataServiceImpl")
	private IDataService dataService;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService logService;
	
	@Resource(name="liabilityServiceImpl")
	private LiabilityService liabilityService;
	
	@Resource
	private GwServiceDAO serviceDAO;
	
	@Resource
	private GwServiceFieldDAO serviceFieldDAO;
	
	@Resource
	private DataGatherDAO dataGatherDAO;
	
	@Resource
	private GwServiceCheckRuleDAO gwServiceCheckRuleDAO;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	public Map getOnlineServiceData(Long serviceId, GwModelDataFetchTaskVO taskVO, GwUserVO userVO) throws Exception{
		GwServiceVO serviceVO = serviceDAO.findById(serviceId);
		DataResponse dataResponse = this.doGetServiceData(serviceVO, taskVO, userVO);
		Map resultMap = new HashMap();
		if(!dataResponse.isResult()){
			throw new Exception("获取服务数据异常，原因："+dataResponse.getInfo());
		}
		
		if(dataResponse.isResult() && dataResponse.getData().size() > 0){
			Iterator<String> it = null;
			List<String> dataList = null;
			List resultList = new ArrayList();
			Map<String, String> fieldMap = new LinkedHashMap<String, String>();
			
			//服务字段脱敏规则
			if(dataResponse.getData().size() > 0){
				//列表头字段
				Map<String, String> dataMap = dataResponse.getData().get(0);
				it = dataMap.keySet().iterator();
				while(it.hasNext()){
					String fieldCode = it.next();
					fieldMap.put(fieldCode, fieldCode);
				}
			}
			
			//列表数据
			for (Map<String, String> dataMap : dataResponse.getData()) {	//服务行数据
				it = dataMap.keySet().iterator();
				dataList = new ArrayList<String>();
				while(it.hasNext()){
					String fieldCode = it.next();
					dataList.add(dataMap.get(fieldCode));	//服务字段字段
				}
				if(dataList.size()>0) resultList.add(dataList);	//行数据
			}
			resultMap.put("fieldCodeMap", fieldMap);
			resultMap.put("fieldValueList", resultList);
		}
		return resultMap;
	}
	
	/**
	 * 实时服务预览脱敏数据
	 * @param serviceId
	 * @param taskId
	 * @param userVO
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public Map getOnlinePreviewServiceData(GwModelDataFetchTaskVO taskVO) throws Exception{
		try {
			Map<String, String> ignoreMap = new HashMap<String, String>();
			ignoreMap.put("GATEWAY_TASK_ID", "GATEWAY_TASK_ID");
			List<Map<String, String>> originalDataList = dataGatherDAO.searchOnlineServiceCacheData(taskVO,ignoreMap);
			Map resultMap = new HashMap();
		
			List<GwServiceFieldVO> fieldList = serviceFieldDAO.searchServiceOutField(taskVO.getServiceId());
			//列表头的字段名
			Map<String, String> dataCodeMap = new LinkedHashMap<String, String>();
			for (GwServiceFieldVO fieldVO : fieldList) {
				dataCodeMap.put(fieldVO.getFieldCode(), fieldVO.getFieldCode());
			}
			
			//服务字段脱敏规则
			List<GwDesenRuleServiceFieldView> desenRuleList = desenModelService.searchDesenRuleServiceFieldConfig(taskVO.getUserId(), taskVO.getServiceId());
			//脱敏后的数据结果集
			List<Map<String, String>> desenDataList = dataGatherDAO.searchOnlineServiceDesenData(taskVO, ignoreMap);
			
			//列表中的原始数据
			Map<String, Object> originalDataMap = new LinkedHashMap<String, Object>();
			for (Map<String, String> dataMap : originalDataList) {
				String previewROWID = dataMap.get("GATEWAY_ROW_ID");
				dataMap.remove("GATEWAY_ROW_ID");
				originalDataMap.put(previewROWID, dataMap);
			}
			
			//列表中的脱敏数据
			Map<String, Object> desenDataMap = new LinkedHashMap<String, Object>();
			if(desenDataList != null){
				for (Map<String, String> dataMap : desenDataList) {
					String previewROWID = dataMap.get("GATEWAY_ROW_ID");
					dataMap.remove("GATEWAY_ROW_ID");
					desenDataMap.put(previewROWID, dataMap);
				}
			}
			resultMap.put("desenFieldCount", desenRuleList.size());
			resultMap.put("dataFieldMap", dataCodeMap);
			resultMap.put("originalDataMap", originalDataMap);
			resultMap.put("desenDataMap", desenDataMap);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("实时服务预览脱敏数据出错", e);
		}
	}
	
	/**
	 * 获取服务数据
	 * @param serviceId
	 * @param taskId
	 * @param userId
	 * @throws Exception
	 */
	public DataResponse doGetServiceData(GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO) throws Exception{
		DataResponse dataResponse = new DataResponse();
		try {
			log.info("serviceId="+serviceVO.getServiceId()+";serviceCode="+serviceVO.getServiceCode()+(taskVO.getTaskId()!=null?";taskId="+taskVO.getTaskId():"")+"请求服务开始");
			dataResponse = dataService.getServiceData(serviceVO, taskVO);
			log.info("serviceId="+serviceVO.getServiceId()+";serviceCode="+serviceVO.getServiceCode()+(taskVO.getTaskId()!=null?";taskId="+taskVO.getTaskId():"")+"请求服务结束");
			log.info("返回信息："+JSONObject.fromObject(dataResponse).toString());
			
			if(dataResponse.isResult()){	//接口返回成功
				//实时服务
				if(CommonState.SERVICE_TYPE_IMMEDIATELY.equals(serviceVO.getServiceType())){
					//实时服务，保存缓存数据
					dataGatherDAO.saveOnlineServiceData(serviceVO, taskVO, dataResponse.getData());
					
					//服务字段脱敏规则
					List<GwDesenRuleServiceFieldView> desenRuleList = desenModelService.searchDesenRuleServiceFieldConfig(userVO.getUserId(), serviceVO.getServiceId());
					
					//脱敏时需要忽略的字段，脱敏后需要保存到数据库
					Map<String, String> ignoreFieldMap = new HashMap<String, String>();
					ignoreFieldMap.put("GATEWAY_TASK_ID", "GATEWAY_TASK_ID");
					ignoreFieldMap.put("GATEWAY_ROW_ID", "GATEWAY_ROW_ID");
					//根据taskId,serviceId获取原始数据，主要查出自定义的gatewayRowId，脱敏后需要保存到脱敏结果表
					List<Map<String, String>> originalList=dataGatherDAO.searchOnlineServiceCacheData(taskVO,null);
					//数据脱敏
					List<Map<String, String>> resultList = dataDesenService.dataDesen(originalList,desenRuleList, serviceVO.getServiceId(), userVO.getUserId(), ignoreFieldMap);
					//保存脱敏后的数据
					dataGatherDAO.saveOnlineServiceDesenData(serviceVO,resultList);
					
					//去掉自定义字段
					for (Map<String, String> map : resultList) {
						map.remove("GATEWAY_TASK_ID");
						map.remove("GATEWAY_ROW_ID");
					}
					dataResponse.setData(resultList);
					
					//免责日志
					if(taskVO != null){
						taskVO.setDataNum(Long.valueOf(resultList.size()));
						liabilityService.saveLiabilityLog(taskVO, userVO);
					}
					
					String operateType = "用户："+userVO.getLoginName()+"请求了服务编码="+serviceVO.getServiceCode()+"的数据";
					logService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.DOWNLOAD_DATA, operateType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			dataResponse.setResult(false);
			dataResponse.setInfo("获取服务数据异常，原因："+e.getMessage());
			log.error("获取服务数据异常"+e.getMessage(),e);
		}
		return dataResponse;
	}
	
	/**
	 * 数据特区主动推送实时服务数据
	 */
	public ResponseVO doPutServiceData(String authortyGno,String desenType,List<GwServiceFieldVO> columnList,GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO,List<Map<String, String>> dataList,String webserviceUrl,String webserviceMethod,String baseWsdl) throws Exception{
		ResponseVO response = new ResponseVO();
		List<Map<String, String>> resultList = dataList;
		try {
			
			//合规检查
			//服务字段检查规则
			List<GwRuleCheckServiceFieldView> checkRuleList = desenModelService.searchCheckRuleServiceFieldConfig(userVO.getUserId(), serviceVO.getServiceId());
			//数据检查
			String result = dataDesenService.dataCheck(dataList, checkRuleList, serviceVO.getServiceId(), taskVO.getTaskId());
			
			if(StringUtils.isBlank(result)){//合规检查通过
				log.info("合规检查通过！");
				//数据脱敏
				if("1".equals(desenType)){//需要脱敏
					//实时服务，保存缓存数据
					List<Map<String, String>> dataListMap = new ArrayList<Map<String, String>>();
					Map<String, String> rowMap;
					for(Map<String, String> rowData:dataList){
						rowMap = new HashMap<String, String>();
						String data= rowData.get("data");
						String[] dataValues;
						if(data.indexOf("|") != -1){
							dataValues = data.split("\\|");
						}else{
							dataValues= new String[]{data};
						}
						
						for(int i= 0;i<dataValues.length;i++){
							rowMap.put(columnList.get(i).getFieldCode(), dataValues[i]);
						}
						dataListMap.add(rowMap);
					}
					dataGatherDAO.saveOnlineServiceData(serviceVO, taskVO,dataListMap);
					
					//服务字段脱敏规则
					List<GwDesenRuleServiceFieldView> desenRuleList = desenModelService.searchDesenRuleServiceFieldConfig(userVO.getUserId(), serviceVO.getServiceId());
					
					//脱敏时需要忽略的字段，脱敏后需要保存到数据库
					Map<String, String> ignoreFieldMap = new HashMap<String, String>();
					ignoreFieldMap.put("GATEWAY_TASK_ID", "GATEWAY_TASK_ID");
					ignoreFieldMap.put("GATEWAY_ROW_ID", "GATEWAY_ROW_ID");
					//根据taskId,serviceId获取原始数据，主要查出自定义的gatewayRowId，脱敏后需要保存到脱敏结果表
					List<Map<String, String>> originalList=dataGatherDAO.searchOnlineServiceCacheData(taskVO,null);
					//数据脱敏
					resultList = dataDesenService.dataDesen(originalList,desenRuleList, serviceVO.getServiceId(), userVO.getUserId(), ignoreFieldMap);
					//保存脱敏后的数据
					dataGatherDAO.saveOnlineServiceDesenData(serviceVO,resultList);
					//去掉自定义字段
					for (Map<String, String> map : resultList) {
						map.remove("GATEWAY_TASK_ID");
						map.remove("GATEWAY_ROW_ID");
					}
				}
				
				List<Map<String, String>> dataListMap= new ArrayList<Map<String, String>>();
				Map<String, String> dataMap;
				//组装数据
				if("1".equals(desenType)){//需要脱敏
					for(Map<String, String> map:resultList){
						dataMap = new HashMap<String, String>();
						String data="";
						Iterator<String> it = map.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							String value = map.get(key);
							data += StringUtils.isBlank(data)?value:"|"+value;
						}
						dataMap.put("data", data);
						dataListMap.add(dataMap);
					}
				}else{
					dataListMap = dataList;
				}
				
				//推送数据到数据用户的webservice
				DataEntity param = new DataEntity();
				param.setServiceCode(serviceVO.getServiceCode());
				param.setServiceName(serviceVO.getServiceName());
				param.setResult(dataList);
				PushWebserviceUtil.putDataToWebservice(webserviceUrl, baseWsdl, webserviceMethod, JSONObject.fromObject(param).toString());
				
			}else{//存在不合规的数据，则发送待办信息给数据用户
				String sms="webservice接口合规检查中存在不合规的数据！【数据服务网关】";
				workPlanService.saveWorkPlan("webservice接口合规检查", WorkPlanConstent.WEBSERVICE_RULE_CHECK_FAILURE, "webservice接口合规检查中存在不合规的数据！", WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, userVO.getUserId(), null,sms,null);
				log.error("存在不合规数据");
				response.setResultFlag("0");
				response.setMessage("合规检查不通过："+result);
			}
			
			//免责日志
			if(taskVO != null){
				taskVO.setDataNum(Long.valueOf(resultList.size()));
				liabilityService.saveLiabilityLog(taskVO, userVO);
			}
			
			String operateType = "[数据特区主动推送服务数据]用户："+userVO.getLoginName()+"请求了服务编码="+serviceVO.getServiceCode()+"的数据";
			logService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.DOWNLOAD_DATA, operateType);
			response.setResultFlag("1");
			response.setMessage("受理成功");
		} catch (Exception e) {
			e.printStackTrace();
			response.setResultFlag("0");
			response.setMessage("[数据特区主动推送服务数据]处理服务数据异常，原因："+e.getMessage());
			log.error("[数据特区主动推送服务数据]处理服务数据异常"+e.getMessage(),e);
		}
		return response;
	}
	
	//解析数据特区返回的数据
	private DataAreaResponse parseDataAreaResponse(String result){
		JSONObject obj = JSONObject.fromObject(result);  
		Object resultFlag = obj.get("resultFlag"); 
		Object message = obj.get("message");
		Object dataEntity = obj.get("dataEntity"); 
		JSONObject dataEntityObj = JSONObject.fromObject(dataEntity);  
		List<Map<String, String>> dataList = (List<Map<String, String>>)JSONArray.toList(JSONArray.fromObject(dataEntityObj.get("result")), Map.class);
		DataAreaResponse response = new DataAreaResponse();
		response.setResultFlag(resultFlag.toString());
		response.setMessage(message.toString());
		response.setDataEntity(dataList);
		return response;
	}
	/**
	 * 从数据特区获取服务数据
	 * @param authortyTno
	 * @param period
	 * @return
	 * @throws Exception
	 */
	public ResponseVO doGetDataAreaServiceData(String desenType,List<GwServiceFieldVO> columnList,GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO,String authortyTno,String period,String webserviceUrl,String webserviceMethod,String baseWsdl) throws Exception{
		ResponseVO response = new ResponseVO();
		try {
			log.info("authortyTno="+authortyTno+";period="+period+"请求服务开始");
			ServiceRequestVO param = new ServiceRequestVO();
			param.setAuthortyTno(authortyTno);
			param.setAuthortyTno(period);
			String returnParam = PushWebserviceUtil.putDataToWebservice(ConfigConstants.DATA_AREA_WEBSERVICE_URL, ConfigConstants.DATA_AREA_BASE_WSDL, ConfigConstants.DATA_AREA_METHOD, JSONObject.fromObject(param).toString());
			DataAreaResponse dataResponse = parseDataAreaResponse(returnParam);
			log.info("authortyTno="+authortyTno+";period="+period+"请求服务结束");
			log.info("返回信息："+JSONObject.fromObject(dataResponse).toString());
			
			if("1".equals(dataResponse.getResultFlag())){	//接口返回成功
				//合规检查
				List<Map<String, String>> resultList = dataResponse.getDataEntity();
				//服务字段检查规则
				List<GwRuleCheckServiceFieldView> checkRuleList = desenModelService.searchCheckRuleServiceFieldConfig(userVO.getUserId(), serviceVO.getServiceId());
				//数据检查
				String result = dataDesenService.dataCheck(dataResponse.getDataEntity(), checkRuleList, serviceVO.getServiceId(), taskVO.getTaskId());
				
				if(StringUtils.isBlank(result)){//合规检查通过
					log.info("合规检查通过！");
					if("1".equals(desenType)){//需要脱敏
						List<Map<String, String>> dataListMap = new ArrayList<Map<String, String>>();
						Map<String, String> rowMap;
						for(Map<String, String> rowData:dataResponse.getDataEntity()){
							rowMap = new HashMap<String, String>();
							String data= rowData.get("data");
							String[] dataValues;
							if(data.indexOf("|") != -1){
								dataValues = data.split("\\|");
							}else{
								dataValues= new String[]{data};
							}
							for(int i= 0;i<dataValues.length;i++){
								rowMap.put(columnList.get(i).getFieldCode(), dataValues[i]);
							}
							dataListMap.add(rowMap);
						}
						//实时服务，保存缓存数据
						dataGatherDAO.saveOnlineServiceData(serviceVO, taskVO,dataListMap);
						
						//服务字段脱敏规则
						List<GwDesenRuleServiceFieldView> desenRuleList = desenModelService.searchDesenRuleServiceFieldConfig(userVO.getUserId(), serviceVO.getServiceId());
						
						//脱敏时需要忽略的字段，脱敏后需要保存到数据库
						Map<String, String> ignoreFieldMap = new HashMap<String, String>();
						ignoreFieldMap.put("GATEWAY_TASK_ID", "GATEWAY_TASK_ID");
						ignoreFieldMap.put("GATEWAY_ROW_ID", "GATEWAY_ROW_ID");
						//根据taskId,serviceId获取原始数据，主要查出自定义的gatewayRowId，脱敏后需要保存到脱敏结果表
						List<Map<String, String>> originalList=dataGatherDAO.searchOnlineServiceCacheData(taskVO,null);
						//数据脱敏
						resultList = dataDesenService.dataDesen(originalList,desenRuleList, serviceVO.getServiceId(), userVO.getUserId(), ignoreFieldMap);
						//保存脱敏后的数据
						dataGatherDAO.saveOnlineServiceDesenData(serviceVO,resultList);
						
						//去掉自定义字段
						for (Map<String, String> map : resultList) {
							map.remove("GATEWAY_TASK_ID");
							map.remove("GATEWAY_ROW_ID");
						}
					}
					//组装数据
					List<Map<String, String>> dataListMap= new ArrayList<Map<String, String>>();
					Map<String, String> dataMap;
					if("1".equals(desenType)){//需要脱敏
						for(Map<String, String> map:resultList){
							dataMap = new HashMap<String, String>();
							String data="";
							Iterator<String> it = map.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next();
								String value = map.get(key);
								data += StringUtils.isBlank(data)?value:"|"+value;
							}
							dataMap.put("data", data);
							dataListMap.add(dataMap);
						}
					}else{
						dataListMap = dataResponse.getDataEntity();
					}
					taskVO.setDataNum(Long.valueOf(dataListMap.size()));
					
					//推送数据到数据用户的webservice
					DataEntity dataEntity = new DataEntity();
					dataEntity.setServiceCode(serviceVO.getServiceCode());
					dataEntity.setServiceName(serviceVO.getServiceName());
					dataEntity.setResult(dataListMap);
					PushWebserviceUtil.putDataToWebservice(webserviceUrl, baseWsdl, webserviceMethod, JSONObject.fromObject(dataEntity).toString());
					
				}else{//存在不合规的数据，则发送待办信息给数据用户
					String sms="webservice接口合规检查中存在不合规的数据！【数据服务网关】";
					workPlanService.saveWorkPlan("webservice接口合规检查", WorkPlanConstent.WEBSERVICE_RULE_CHECK_FAILURE, "webservice接口合规检查中存在不合规的数据！", WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, userVO.getUserId(), null,sms,null);
					log.error("存在不合规数据");
					response.setResultFlag("0");
					response.setMessage("合规检查不通过："+result);
				}
				
				//免责日志
				if(taskVO != null){
					taskVO.setDataNum(Long.valueOf(resultList.size()));
					liabilityService.saveLiabilityLog(taskVO, userVO);
				}
				
				String operateType = "[外网用户申请服务取数接口]用户："+userVO.getLoginName()+"请求了服务编码="+serviceVO.getServiceCode()+"的数据";
				logService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.DOWNLOAD_DATA, operateType);
				response.setResultFlag("1");
				response.setMessage("受理成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setResultFlag("0");
			response.setMessage("[外网用户申请服务取数接口]获取服务数据异常，原因："+e.getMessage());
			log.error("[外网用户申请服务取数接口]获取服务数据异常"+e.getMessage(),e);
		}
		return response;
	}
	
	
	/**
	 * 查询合规检查不通过的数据
	 * @param serviceId
	 * @param taskId
	 * @param pageObject
	 * @param ignoreFieldMap
	 * @return
	 * @throws Exception
	 */
	public PageObject searchRuleCheckAuditList(GwServiceVO serviceVO,Long taskId,PageObject pageObject)throws Exception{
		return dataGatherDAO.searchRuleCheckAuditList(serviceVO, taskId, pageObject);
	}
	
	/**
	 * 根据taskId,行号查询出所有不合规的字段，并且组装成Map
	 * @param taskId
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public Map searchRuleCheckAuditField(GwModelDataFetchTaskVO taskVO,PageObject pageObject) throws Exception{
		List<Map<String,String>> dataList =pageObject.getData();
		String rowId="";
		for(Map<String,String> map:dataList){
			rowId += ("".equals(rowId) ? "" : ",") + "'"+String.valueOf(map.get("ROW_ID"))+"'";
		}
		if(StringUtils.isBlank(rowId)) return null;
		return dataGatherDAO.searchRuleCheckAuditField(taskVO,rowId);
	}
	
	/**
	 * 根据check_batch，查询行数
	 * @param taskVO
	 * @return
	 * @throws Exception
	 */
	public int searchRowCount(GwModelDataFetchTaskVO taskVO) throws Exception{
		return gwServiceCheckRuleDAO.searchCheckRuleListCount(taskVO.getCheckBatch());
	}
	
	/**
	 * 查询warnRow，rowData
	 * @param taskVO
	 * @param warnType
	 * @return
	 * @throws Exception
	 */
	public Map searchServiceCheckWarn(GwModelDataFetchTaskVO taskVO,long warnType) throws Exception{
		return gwServiceCheckRuleDAO.searchServiceCheckWarn(taskVO, warnType);
	}
	
	/**
	 * 根据checkBatch查询字段，组装表头
	 * @param taskVO
	 * @return
	 * @throws Exception
	 */
	public List<GwServiceCheckRuleVO> searchServiceFieldCode(GwModelDataFetchTaskVO taskVO)throws Exception{
		return gwServiceCheckRuleDAO.searchServiceFieldCode(taskVO);
	}
	
	public List<String> searchFieldTitleList(GwServiceVO serviceVO,Long taskId)throws Exception{
		return dataGatherDAO.searchFieldTitleList(serviceVO, taskId);
	}
}
