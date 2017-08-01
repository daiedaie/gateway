package com.gztydic.gateway.gather.webservice;

import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.gather.webservice.client.sms.SmsResponse;



public interface IWsSmsService {
	
	public final static String SMS_SUCCESS = "1";
	public final static String SMS_FAILURE = "0";

	public SmsResponse sendSms(GwSmsVO smsVO) throws Exception;
}
