package com.gztydic.gateway.core.common.util;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.SAXReader;

public class PushWebserviceUtil {
	private static final Log logger = LogFactory.getLog(PushWebserviceUtil.class);

    /**
     * 
     * @param dataEndpoint  WebService 部署地址 测试路径
     * @param dataWsdl  接口的包路径
     * @param dataWsdlname  接口名称
     * @param xml
     * @return
     */
    public static String putDataToWebservice(String dataEndpoint,String dataWsdl,String dataWsdlname,String param) {
    	String result = "";
        try {
            Service service = new Service();
            SAXReader sr = new SAXReader();
            long bi = System.currentTimeMillis();
            logger.info("request param:" + param);
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(dataEndpoint));
            call.setOperationName(new QName(dataWsdl, dataWsdlname));
            call.setUseSOAPAction(true);
            call.setTimeout(new Integer(600000));
            call.setEncodingStyle(null);
            //参数名称 arg0
            call.addParameter("arg0", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            result = (String) call.invoke(new Object[]{param});
            long eg = System.currentTimeMillis();
            logger.info("response param : " + result);
            logger.info("use time : " + (eg - bi) + " ms");
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    	return result;
    }
    
    public static void main(String[] args) {
    	PushWebserviceUtil.putDataToWebservice( "http://localhost:8080/gateway-webservice/service/appService?WSDL",
    			"http://webservice.gateway.gztydic.com/",
    			"getOnlineServiceData",
    			"{'authortyGno':'46C4526CEB1D54A53A790CA6E5680B19' ,'dataEntity':{'result':[{'data':'1|1|50'},{'data':'1|3|50'}]},'wsInfo':{'url':'http://localhost:8080/gateway-webservice/service/appService?WSDL','method':'getServiceData','baseWsdl':'http://webservice.gateway.gztydic.com/'}}");

	}
}
