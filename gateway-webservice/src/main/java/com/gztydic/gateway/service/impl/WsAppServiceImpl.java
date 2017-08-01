package com.gztydic.gateway.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.dao.GwModelDataFetchDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.gather.DataGatherService;
import com.gztydic.gateway.gather.webservice.client.data.DataAreaResponse;
import com.gztydic.gateway.gather.webservice.client.data.ResponseVO;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.service.IWsAppService;
import com.gztydic.gateway.webservice.client.app.ServiceRequestParam;

@Service
public class WsAppServiceImpl implements IWsAppService {
	
	private static Log logger = LogFactory.getLog(WsAppServiceImpl.class);

	@Resource
	private GwModelDataFetchDAO fetchDAO;
	
	@Resource
	private GwServiceDAO serviceDAO;
	
	@Resource
	private GwServiceFieldDAO serviceFieldDAO;
	
	@Resource
	private GwModelDataFetchTaskDAO taskDAO;
	
	@Resource
	private GwUserDAO userDAO;
	
	@Resource(name="dataGatherServiceImpl")
	private DataGatherService dataGatherService;
	
	/**
	 * 第三方接口获取实时服务数据
	 */
	public DataResponse doGetDataByServiceId(String param) throws Exception {
		DataResponse dataResponse = new DataResponse();
		ServiceRequestParam requestParam = (ServiceRequestParam)JSONObject.toBean(JSONObject.fromObject(param), ServiceRequestParam.class);
		Endecrypt endecrypt = new Endecrypt();
		String password = endecrypt.get3DESEncrypt(requestParam.getPassword(), SessionConstant.SPKEY_PASSWORD);
		
		logger.info("第三方接口获取实时服务数据，请求报文："+param);
		GwUserVO userVO = userDAO.searchUser(requestParam.getUserName(),password);
		if(userVO == null){
			dataResponse.setResult(false);
			dataResponse.setInfo("错误：用户名或密码不正确");
		}else if(!CommonState.PASS.equals(userVO.getConfirmStatus())){
			dataResponse.setResult(false);
			dataResponse.setInfo("错误：用户状态不合法，请联系管理员处理。");
		}else if(!CommonState.VALID.equals(userVO.getStatus())){
			dataResponse.setResult(false);
			dataResponse.setInfo("错误：用户已注销，请联系管理员处理。");
		}else{
			GwServiceVO serviceVO = serviceDAO.searchService(requestParam.getServiceCode(),CommonState.SERVICE_SOURCE_DATA);
			if(serviceVO==null){
				dataResponse.setResult(false);
				dataResponse.setInfo("错误：不存在服务编码为"+requestParam.getServiceCode()+"的服务。");
			}else if(!serviceDAO.validateServiceAuth(serviceVO.getServiceId(), userVO.getUserId())){
				dataResponse.setResult(false);
				dataResponse.setInfo("错误：你没有权限访问服务编码为"+requestParam.getServiceCode()+"的服务。");
			}else if(!CommonState.SERVICE_TYPE_IMMEDIATELY.equals(serviceVO.getServiceType())){
				dataResponse.setResult(false);
				dataResponse.setInfo("错误：服务编码为"+requestParam.getServiceCode()+"的服务不是实时服务，不能访问。");
			}else {
				GwModelDataFetchVO fetchVO = fetchDAO.searchModelDataFetch(userVO.getUserId(), serviceVO.getServiceId());
				if(fetchVO == null){
					dataResponse.setResult(false);
					dataResponse.setInfo("错误：服务取数需要申请，请先申请后再获取数据。");
				}else if(!CommonState.PASS.equals(fetchVO.getAuditStatus())){
					dataResponse.setResult(false);
					GwSysDictVO dictVO = SysDictManage.getSysDict("DICT_AUDIT_STATE", fetchVO.getAuditStatus());
					String auditStatus = dictVO != null ? dictVO.getDictValue() : fetchVO.getAuditStatus();
					dataResponse.setInfo("错误：服务取数申请状态为["+auditStatus+"]，审核通过后才能获取数据。");
				}else {
					GwModelDataFetchTaskVO taskVO = new GwModelDataFetchTaskVO();
					taskVO.setFetchId(fetchVO.getFetchId());
					taskVO.setServiceId(fetchVO.getServiceId());
					taskVO.setUserId(fetchVO.getUserId());
					taskVO.setModelId(fetchVO.getModelId());
					taskVO.setDataProgressStatus(DataProgressStatus.REQUEST_DATA);
					taskVO.setDataStatus(CommonState.NORMAL);
					taskVO.setDataSource(DataTypeConstent.DATACENTER);
					taskVO.setAuditTime(fetchVO.getAuditTime());
					taskVO.setCreateTime(new Date());
					taskVO.setCreateUser(userVO.getLoginName());
					taskVO.setDownloadStartTime(new Date());
					taskVO.setDataNum(0l);
					taskVO.setDataType(DataTypeConstent.TABLE);
					taskVO.setTaskStatus(CommonState.VALID);
					taskDAO.save(taskVO);
					
					dataResponse = dataGatherService.doGetServiceData(serviceVO, taskVO, userVO);
					//实时取数接口并脱敏
					taskVO.setDataProgressStatus(DataProgressStatus.RESULT_FINISH);
					taskVO.setCheckResult("0");
					if(dataResponse.getData() != null)
					taskVO.setDataNum(Long.valueOf(dataResponse.getData().size()));
					taskVO.setDownloadEndTime(new Date());
					taskVO.setDownloadTime(new Date());
					taskDAO.update(taskVO);
				}
			}
		}
		logger.info("第三方接口获取实时服务数据，返回报文："+JSONObject.fromObject(dataResponse).toString());
		return dataResponse;
	}
	
	/**
	 * 数据特区主动推送实时服务数据接口
	 */
	public ResponseVO doGetOnlineServiceData(String param) throws Exception {
		
		ResponseVO response = new ResponseVO();
		try{
			JSONObject obj = JSONObject.fromObject(param);  
			String authortyGno = (String)obj.get("authortyGno"); //G鉴权号
			if(authortyGno == null){
				response.setResultFlag("0");
				response.setMessage("错误：请求参数【G鉴权号】为空！");
				return response;
			}
			
			GwModelDataFetchVO fetchVO = fetchDAO.searchModelDataFetch(authortyGno.toString());
			if(fetchVO == null){
				response.setResultFlag("0");
				response.setMessage("错误：G鉴权号不正确！");
				return response;
			}
			
			Object dataEntity = obj.get("dataEntity"); //服务数据JASON对象
			if(dataEntity == null){
				response.setResultFlag("0");
				response.setMessage("错误：请求参数【服务数据】为空！");
				return response;
			}
			
			JSONObject wsInfo = JSONObject.fromObject(obj.get("wsInfo"));//结果推送的webservice接口信息JASON对象
			String url = wsInfo.getString("url");
			String method = wsInfo.getString("method");
			String baseWsdl = wsInfo.getString("baseWsdl");
			
			JSONObject dataEntityObj = JSONObject.fromObject(dataEntity);  
			List<Map<String, String>> dataList = (List<Map<String, String>>)JSONArray.toList(JSONArray.fromObject(dataEntityObj.get("result")), Map.class);
			List<GwServiceFieldVO> columnList = serviceFieldDAO.searchServiceOutField(fetchVO.getServiceId());//字段列表
			
			GwModelDataFetchTaskVO taskVO = new GwModelDataFetchTaskVO();
			taskVO.setFetchId(fetchVO.getFetchId());
			taskVO.setServiceId(fetchVO.getServiceId());
			taskVO.setUserId(fetchVO.getUserId());
			taskVO.setModelId(fetchVO.getModelId());
			taskVO.setDataProgressStatus(DataProgressStatus.REQUEST_DATA);
			taskVO.setDataStatus(CommonState.NORMAL);
			taskVO.setDataSource(DataTypeConstent.DATACENTER);
			taskVO.setAuditTime(fetchVO.getAuditTime());
			taskVO.setCreateTime(new Date());
			taskVO.setCreateUser(fetchVO.getUserId().toString());
			taskVO.setDownloadStartTime(new Date());
			taskVO.setDataNum(0l);
			taskVO.setDataType(DataTypeConstent.TABLE);
			taskVO.setTaskStatus(CommonState.VALID);
			taskDAO.save(taskVO);
			
			GwUserVO userVO = userDAO.findById(fetchVO.getUserId());
			GwServiceVO serviceVO = serviceDAO.findById(fetchVO.getServiceId());
			
			response = dataGatherService.doPutServiceData(authortyGno,fetchVO.getDesenType(),columnList,serviceVO, taskVO, userVO,dataList,url,method,baseWsdl);
			//实时取数接口并脱敏
			taskVO.setDataProgressStatus(DataProgressStatus.RESULT_FINISH);
			taskVO.setCheckResult("0");
			taskVO.setDataNum(Long.valueOf(dataList.size()));
			taskVO.setDownloadEndTime(new Date());
			taskVO.setDownloadTime(new Date());
			taskDAO.update(taskVO);
		}catch (Exception e) {
			response.setResultFlag("0");
			response.setMessage("数据特区主动推送实时服务数据接口发生错误："+e.getMessage());
		}
		
		return response;
		
	}
	
	/**
	 * 外网用户申请服务取数接口
	 */
	public ResponseVO doGetDataAreaServiceData(String param) throws Exception {
		
		ResponseVO response = new ResponseVO();
		try{
			JSONObject obj = JSONObject.fromObject(param);  
			String authortyGno = (String)obj.get("authortyGno"); //G鉴权号
			String authortyTno = (String)obj.get("authortyTno"); //T鉴权号
			String period = (String)obj.get("period"); //账期
			JSONObject wsInfo = JSONObject.fromObject(obj.get("wsInfo"));//结果推送的webservice接口信息JASON对象
			String url = wsInfo.getString("url");
			String method = wsInfo.getString("method");
			String baseWsdl = wsInfo.getString("baseWsdl");
			
			//检验是否是否为空
			if(StringUtils.isBlank(authortyGno) || StringUtils.isBlank(authortyTno) || StringUtils.isBlank(url) || StringUtils.isBlank(method)){
				response.setResultFlag("0");
				response.setMessage("数据为空！请确认参数是否正确");
				return response;
			}
			
			//检验G鉴权号是否正确
			GwModelDataFetchVO fetchVO = fetchDAO.searchModelDataFetch(authortyGno);
			if(fetchVO == null){
				response.setResultFlag("0");
				response.setMessage("G鉴权号不正确！");
				return response;
			}
			
			GwUserVO userVO = userDAO.findById(fetchVO.getUserId());
			GwServiceVO serviceVO = serviceDAO.findById(fetchVO.getServiceId());
			
			GwModelDataFetchTaskVO taskVO = new GwModelDataFetchTaskVO();
			taskVO.setFetchId(fetchVO.getFetchId());
			taskVO.setServiceId(fetchVO.getServiceId());
			taskVO.setUserId(fetchVO.getUserId());
			taskVO.setModelId(fetchVO.getModelId());
			taskVO.setDataProgressStatus(DataProgressStatus.REQUEST_DATA);
			taskVO.setDataStatus(CommonState.NORMAL);
			taskVO.setDataSource(DataTypeConstent.DATACENTER);
			taskVO.setAuditTime(fetchVO.getAuditTime());
			taskVO.setCreateTime(new Date());
			taskVO.setCreateUser(userVO.getLoginName());
			taskVO.setDownloadStartTime(new Date());
			taskVO.setDataNum(0l);
			taskVO.setDataType(DataTypeConstent.TABLE);
			taskVO.setTaskStatus(CommonState.VALID);
			taskDAO.save(taskVO);
			
			List<GwServiceFieldVO> columnList = serviceFieldDAO.searchServiceOutField(fetchVO.getServiceId());//字段列表
			response = dataGatherService.doGetDataAreaServiceData(fetchVO.getDesenType(),columnList,serviceVO, taskVO, userVO,authortyTno,period,url,method,baseWsdl);
			//实时取数接口并脱敏
			taskVO.setDataProgressStatus(DataProgressStatus.RESULT_FINISH);
			taskVO.setCheckResult("0");
			taskVO.setDownloadEndTime(new Date());
			taskVO.setDownloadTime(new Date());
			taskDAO.update(taskVO);
			response.setResultFlag("1");
			response.setMessage("");
		}catch (Exception e) {
			response.setResultFlag("0");
			response.setMessage("外网用户申请服务取数接口发生错误："+e.getMessage());
		}
		return response;
		
	}
	
	
}
