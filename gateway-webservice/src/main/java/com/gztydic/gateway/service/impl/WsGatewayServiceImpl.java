package com.gztydic.gateway.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.dao.GwModelDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.service.IWsGatewayService;
import com.gztydic.gateway.webservice.IGatewayService;
import com.gztydic.gateway.webservice.client.gateway.ResultResponse;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam.ModelInfo;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam.ServiceField;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam.ServiceInfo;
import com.gztydic.gateway.webservice.client.gateway.ServiceNoticeParam;

@Service
public class WsGatewayServiceImpl implements IWsGatewayService {
	
	private static Log logger = LogFactory.getLog(WsGatewayServiceImpl.class);

	@Resource
	private GwModelDataFetchTaskDAO taskDAO;
	
	@Resource
	private GwModelDataFileDAO fileDAO;
	
	@Resource
	private GwServiceDAO serviceDAO;
	
	@Resource
	private GwServiceFieldDAO fieldDAO;
	
	@Resource
	private GwModelDAO modelDAO;
	
	/**
	 * 服务通知
	 * @param param
	 * @throws Exception 
	 */
	public ResultResponse doServiceNotice(ServiceNoticeParam param) throws Exception{
		logger.info("服务通知开始；taskId="+param.getTaskId()+"; dataStatus="+param.getDataStatus());
		if(param.getTaskId()==null) throw new Exception("taskId不能为空");
		
		GwModelDataFetchTaskVO taskVO = taskDAO.findById(Long.parseLong(param.getTaskId()));
		if(taskVO == null) throw new Exception("不存在taskId="+param.getTaskId()+"的服务取数任务");
		
		logger.info("taskId="+param.getTaskId()+"服务数据生成通知，Gateway任务状态="+taskVO.getDataProgressStatus()+";接口数据状态dataStatus="+param.getDataStatus());
		ResultResponse response = new ResultResponse();
//		1发起请求、2平台数据生成、3开始取数、4完成取数、5脱敏处理、6结果生成
		if(DataProgressStatus.REQUEST_DATA.equals(taskVO.getDataProgressStatus()) || StringUtils.isEmpty(taskVO.getDataProgressStatus())){
			//数据状态：1，已生成；2，未生成
			if("1".equals(param.getDataStatus())){
				//接收挖掘平台数据生成通知后，将相同类型的服务统一修改为dataProgressStatus=2
				taskDAO.batchUpdateModelDataFetchTask(Long.parseLong(param.getTaskId()),DataProgressStatus.DATA_READY, param.getDataCount());
				
				//保存ftp文件目录信息
				GwModelDataFileVO fileVO = new GwModelDataFileVO();
				fileVO.setFtpPort(param.getPort());
				fileVO.setFtpIp(param.getIp());
				fileVO.setFtpUser(param.getUserName());
				fileVO.setFtpPassword(param.getPassword());
				fileVO.setFilePath(param.getFilePath());
				fileVO.setFileName(param.getTaskId()+".txt");
				fileVO.setFileType("1");	//1原始文件、2脱敏后的文件
				fileVO.setFileStatus("1");	//1有效、2失效
				fileVO.setCreateTime(new Date());
				fileVO.setTaskId(taskVO.getTaskId());
				fileVO.setUserId(taskVO.getUserId());
				fileDAO.save(fileVO);
				
				response.setRespCode(IGatewayService.STATUS_SUCCESS);
			}
		}else if(DataProgressStatus.DATA_READY.equals(taskVO.getDataProgressStatus())){//状态相同，可能重复调用，直接返回成功
			response.setRespCode(IGatewayService.STATUS_SUCCESS);
		}else {
			response.setRespCode(IGatewayService.STATUS_FAILURE);
			response.setRespMsg("taskId="+param.getTaskId()+"任务在Gateway中处于["+getProgressStatus(taskVO.getDataProgressStatus())+"]状态，接收服务数据生成通知失败");
		}
		logger.info("服务通知结束；taskId="+param.getTaskId()+"; respCode="+response.getRespCode()+"; respMsg="+response.getRespMsg());
		return response;
	}
	
	private String getProgressStatus(String status){
		if("1".equals(status))
			return "发起取数请求";
		else if("2".equals(status))
			return "挖掘平台数据已生成";
		else if("3".equals(status))
			return "开始取数";
		else if("4".equals(status))
			return "完成取数";
		else if("5".equals(status))
			return "脱敏处理";
		else if("6".equals(status))
			return "结果生成";
		
		return "未知状态";
	}

	/**
	 * 服务信息同步
	 */
	@SuppressWarnings("unchecked")
	public ResultResponse doServiceInfoSync(ServiceInfoSyncParam param) throws Exception {
		ResultResponse response = new ResultResponse();
		ServiceInfo serviceInfo = param.getServiceInfo();
		logger.info("服务信息同步开始：operType="+param.getOperType()+"; serviceCode="+serviceInfo.getServiceCode());
		if(!"1".equals(param.getOperType()) && !"2".equals(param.getOperType())) throw new Exception("操作类型operType="+param.getOperType()+"值不正确");
		
		ModelInfo modelInfo = serviceInfo.getModelInfo();
		List<GwModelVO> modelList = modelDAO.findByModelCode(modelInfo.getModelCode()); 
		GwModelVO modelVO = new GwModelVO();
		//创建模型信息
		if(modelInfo != null){
			modelVO = modelList.size() > 0 ? modelList.get(0) : new GwModelVO();
			modelVO.setModelCode(modelInfo.getModelCode());
			modelVO.setModelName(modelInfo.getModelName());
			modelVO.setModelVersion(modelInfo.getModelVersion());
			modelVO.setStartTime(modelInfo.getOnlineTime());
			modelVO.setModelType(modelInfo.getModelType());
			modelVO.setModelDesc(modelInfo.getModelDesc());
			modelVO.setAlgType(modelInfo.getAlgType());
			modelVO.setAlgRule(modelInfo.getAlgRule());
			if(!CommonState.MODEL_STATUS_VALID.equals(modelInfo.getStatus())){
				modelInfo.setStatus(CommonState.MODEL_STATUS_INVALID);
			}
			modelVO.setStatus(modelInfo.getStatus());
			modelVO.setRemark("");
			if(modelVO.getModelId() == null){
				modelVO.setCreateTime(new Date());
				modelVO.setCreator("gatewayService");
			}else{
				modelVO.setModifier("gatewayService");
				modelVO.setModifyTime(new Date());
			}
			modelDAO.saveOrUpdate(modelVO);
		}
		
		//服务信息
		GwServiceVO serviceVO = serviceDAO.searchService(serviceInfo.getServiceCode(),CommonState.SERVICE_SOURCE_DATA);
		
//		1，新增；2，修改
		/*if("1".equals(param.getOperType()) && serviceVO != null){
			logger.error("已存在serviceCode="+serviceInfo.getServiceCode()+"的服务，不能新增");
			throw new Exception("已存在serviceCode="+serviceInfo.getServiceCode()+"的服务，不能新增");
		}else if("2".equals(param.getOperType()) && serviceVO.getServiceId() == null){
			logger.error("不存在serviceCode="+serviceInfo.getServiceCode()+"的服务，无法修改");
			throw new Exception("不存在serviceCode="+serviceInfo.getServiceCode()+"的服务，无法修改");
		}*/
		if(serviceVO==null) serviceVO = new GwServiceVO();
		serviceVO.setServiceCode(serviceInfo.getServiceCode());
		serviceVO.setModelId(modelVO.getModelId());
		serviceVO.setServiceSource(CommonState.SERVICE_SOURCE_DATA);
		serviceVO.setServiceName(serviceInfo.getServiceName());
		serviceVO.setServiceType(serviceInfo.getServiceType());
		serviceVO.setCycleType(serviceInfo.getCycleType());
		serviceVO.setCycleDay(serviceInfo.getCycleNum());
		if(!CommonState.SERVICE_STATUS_VALID.equals(serviceInfo.getStatus()) && !CommonState.SERVICE_STATUS_INVALID.equals(serviceInfo.getStatus()))
			throw new Exception("服务状态status="+serviceInfo.getStatus()+"值不正确");
		serviceVO.setStatus(serviceInfo.getStatus());
		serviceVO.setInputName(serviceInfo.getInputName());
		serviceVO.setInputDesc(serviceInfo.getInputDesc());
		serviceVO.setOutName(serviceInfo.getOutName());
		serviceVO.setOutDesc(serviceInfo.getOutDesc());
		if(serviceVO.getServiceId()==null){
			serviceVO.setCreateUser("gatewayService");
			serviceVO.setCreateTime(new Date());
		}else {
			serviceVO.setUpdateUser("gatewayService");
			serviceVO.setUpdateTime(new Date());
		}
		serviceDAO.saveOrUpdate(serviceVO);
		
		//服务输入、输出字段
		List<ServiceField> fieldList = serviceInfo.getServiceFieldList();
		GwServiceFieldVO fieldVO = null;
		String fieldCodes = "";
		for (ServiceField field : fieldList) {
			fieldVO = fieldDAO.searchServiceField(serviceVO.getServiceId(), field.getFieldCode());
			if(fieldVO==null) fieldVO = new GwServiceFieldVO();
			fieldVO.setServiceId(serviceVO.getServiceId());
			fieldVO.setFieldCode(field.getFieldCode());
			fieldVO.setFieldName(field.getFieldName());
			fieldVO.setFieldType(field.getFieldType());
			fieldVO.setNullable(field.getNullable());
			fieldVO.setFieldDesc(field.getFieldDesc());
			fieldVO.setReorder(field.getReorder());
			if(!"1".equals(field.getGatherType()) && !"0".equals(field.getGatherType()))
				throw new Exception("服务字段数据集类型gatherType="+field.getGatherType()+"值不正确");
			fieldVO.setGatherType(field.getGatherType());
			if(fieldVO.getFieldId()==null){
				fieldVO.setCreateTime(new Date());
				fieldVO.setCreateUser("root");
			}else {
				fieldVO.setUpdateTime(new Date());
				fieldVO.setUpdateUser("root");
			}
			fieldDAO.saveOrUpdate(fieldVO);
			
			fieldCodes += ("".equals(fieldCodes)?"":",") + "'"+fieldVO.getFieldCode()+"'";
		}
//		if(StringUtils.isNotBlank(fieldCodes)){	//将服务的其他字段删除
//			fieldDAO.deleteServiceField(fieldCodes, serviceVO.getServiceId());
//		}
		response.setRespCode(IGatewayService.STATUS_SUCCESS);
		
		logger.info("服务信息同步结束：operType="+param.getOperType()+"; serviceCode="+serviceInfo.getServiceCode()+";");
		logger.info("respCode="+response.getRespCode()+"; respMsg="+response.getRespMsg());
		return response;
	}

}
