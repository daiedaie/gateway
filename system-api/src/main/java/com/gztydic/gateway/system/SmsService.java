package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwSmsVO;

/**
 * 短信记录管理
 * @author 杰辉
 *
 */
public interface SmsService extends GeneralService<GwSmsVO>{
	
	public PageObject searchSMSList(GwSmsVO vo,PageObject obj)throws Exception;
	
	//查询待发送短信、发送失败且发送次数少于sendCount
	public List<GwSmsVO> searchWaitSendSms(int maxSendCount) throws Exception;
}
