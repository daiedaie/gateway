package com.gztydic.gateway.webservice;

import javax.jws.WebService;

@WebService
public interface IAppService {
	
	public String getDataByServiceId(String params);		//获取实时服务数据
	
	public String getOnlineServiceData(String param);		//数据特区主动推送实时服务数据
	
	public String getDataAreaServiceData(String param);     //外网用户申请服务取数接口
	
	public String getServiceData(String param);//测试接口
}
