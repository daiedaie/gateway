package com.gztydic.gateway.gather.webservice.impl;

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.gather.webservice.IDataService;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.gather.webservice.client.data.IDataServiceLocator;
import com.gztydic.gateway.gather.webservice.client.data.IDataServicePortType;
import com.gztydic.gateway.gather.webservice.client.data.ServiceRequestParam;

@Service
public class DataServiceImpl implements IDataService {
	
	private static final Log logger = LogFactory.getLog(DataServiceImpl.class);
	
	//获取服务数据
	public DataResponse getServiceData(GwServiceVO serviceVO,GwModelDataFetchTaskVO taskVO){
		DataResponse dataResponse = new DataResponse();
		try {
			//有效的服务才能发起调用
			if(CommonState.SERVICE_STATUS_VALID.equals(serviceVO.getStatus())){
				ServiceRequestParam param = new ServiceRequestParam();
				//param.setServiceId(Long.parseLong(serviceVO.getServiceCode()));
				param.setServiceId(serviceVO.getServiceId());
				param.setSystemId(ConfigConstants.DATA_SYSTEM_ID);
				param.setUserName(ConfigConstants.DATA_USERNAME);
				param.setUserPassword(ConfigConstants.DATA_PASSWORD);
				//离线服务
				if(CommonState.SERVICE_TYPE_TIMING.equals(serviceVO.getServiceType()) && taskVO != null){
					param.setTaskId(String.valueOf(taskVO.getTaskId()));
					String condition = "month_id='"+getTime(taskVO)+"'";	//获取帐期
					param.setCondition(condition);
				}
				
				//调用接口
				String result = callDataService("getDataByServiceId",param);
				dataResponse = parseDataResponse(result);
			}else {
				dataResponse.setResult(false);
				dataResponse.setInfo("服务未启用，不能请求服务");
			}
		} catch (Exception e) {
			e.printStackTrace();
			dataResponse.setResult(false);
			dataResponse.setInfo("获取服务数据失败："+e.getMessage());
			logger.error(dataResponse.getInfo(),e);
		}
		return dataResponse;
	}
	
	//调用挖掘平台接口
	private String callDataService(String callType,ServiceRequestParam param) throws Exception{
		try {
			IDataServiceLocator dataServiceLocator = new IDataServiceLocator();
			IDataServicePortType dataService = dataServiceLocator.getIDataServiceHttpPort(new URL(ConfigConstants.DATA_WEBSERVICE_URL));
			String result = null;
			logger.info(callType+"接口请求报文："+JSONObject.fromObject(param).toString());
			if("getDataByServiceId".equals(callType))
				result = dataService.getDataByServiceId(param);
			logger.info(callType+"接口返回报文："+result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("调用挖掘平台接口失败",e);
			throw e;
		}
	}
	
	//解析挖掘平台返回的报文
	private DataResponse parseDataResponse(String result){
		JsonConfig jsonConfig = new JsonConfig();
	    jsonConfig.setRootClass(DataResponse.class);
	    Map<String, Class> classMap = new HashMap<String, Class>();
	    classMap.put("data", Map.class); 	// 指定DataResponse的data字段的内部类型
	    jsonConfig.setClassMap(classMap);
		DataResponse dataResponse = (DataResponse)JSONObject.toBean(JSONObject.fromObject(result), jsonConfig);
		return dataResponse;
	}
	
	//帐期
	private String getTime(GwModelDataFetchTaskVO taskVO){
		String year = taskVO.getFieldValue().substring(0,4);
		String month = taskVO.getFieldValue().substring(4,6);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
//		if(taskVO.getFieldCode().equals("month_id")){	//月，取上一月数据
//			c.set(Calendar.MONTH, (Integer.parseInt(month)-2));
//		}else {
			c.set(Calendar.MONTH, (Integer.parseInt(month)-1));
//		}
		return DateUtil.DateToString(c.getTime(),"yyyyMM");
	}
	
}
