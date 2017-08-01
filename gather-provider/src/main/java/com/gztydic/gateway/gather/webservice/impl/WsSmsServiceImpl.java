package com.gztydic.gateway.gather.webservice.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.gather.webservice.IWsSmsService;
import com.gztydic.gateway.gather.webservice.client.sms.SmsResponse;
import com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServiceLocator;
import com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServicePortType;

@Service
public class WsSmsServiceImpl implements IWsSmsService {
	
	private static final Log logger = LogFactory.getLog(WsSmsServiceImpl.class);
	
	//发送短信
	public SmsResponse sendSms(GwSmsVO smsVO) throws Exception{
		List<String> xmlList = createRequestXml(smsVO);
		SmsResponse smsResponse = null;
		for (String requestXml : xmlList) {
			smsResponse = callService(requestXml);
		}
		return smsResponse;
	}
	
	private List<String> createRequestXml(GwSmsVO smsVO){
		List<String> xmlList = new ArrayList<String>();
		String[] mobiles = smsVO.getSmsMobile().split("\\|");
		for (int i = 0; i < mobiles.length; i++) {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("request");
			root.addElement("userId");
			root.addElement("customerId").addText(ConfigConstants.SMS_CUSTOMER_ID);
			root.addElement("customerPassport").addText(ConfigConstants.SMS_CUSTOMER_PASSPORT);
			root.addElement("senderName").addText("gateway数据服务网关");
			root.addElement("strmobileNumber").addText(mobiles[i]);
			root.addElement("strContent").addText(smsVO.getSmsContent());
			
			xmlList.add(root.asXML());
		}
		return xmlList;
	}
	
	private static SmsResponse parseSmsResponse(String responseXml) throws DocumentException{
		Document document = DocumentHelper.parseText(responseXml);
		
		Element root = document.getRootElement();
		Element valueEle = root.element("returnvalue");
		Element operatingEle = root.element("operatingreturn");
		
		SmsResponse smsResponse = new SmsResponse();
		smsResponse.setReturnvalue(valueEle.getText());
		smsResponse.setOperatingreturn(operatingEle.getText());
		return smsResponse;
	}
	
	private SmsResponse callService(String requestXml){
		try {
			UserAppSmsSendWebServiceLocator locator = new UserAppSmsSendWebServiceLocator();
			UserAppSmsSendWebServicePortType smsService = locator.getUserAppSmsSendWebServiceHttpPort(new URL(ConfigConstants.SMS_WEBSERVICE_URL));
			
			logger.info("短信发送接口请求报文："+requestXml);
			String returnText = smsService.userAppSmsSend(requestXml);
			logger.info("短信发送接口返回报文："+returnText);
			return parseSmsResponse(returnText);
		} catch (Exception e) {
			logger.error("调用短信发送接口失败："+e.getMessage(), e);
			e.printStackTrace();
			SmsResponse smsResponse = new SmsResponse();
			smsResponse.setReturnvalue(IWsSmsService.SMS_FAILURE);
			smsResponse.setOperatingreturn("调用短信发送接口失败："+e.getMessage());
			return smsResponse;
		}
	}
}
