package com.gztydic.gateway.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwSmsDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwSmsVO;

/**
 * 短信记录管理
 * @author 杰辉
 *
 */
@Service
public class SmsServiceImpl extends GeneralServiceImpl<GwSmsVO> implements SmsService {
	
	@Resource
    private GwSmsDAO gwSmsDAO;
	
	public PageObject searchSMSList(GwSmsVO gwSmsVO, PageObject obj)throws Exception {
		
		return gwSmsDAO.searchSmsList(gwSmsVO, obj);
	}

	//查询待发送短信、发送失败且发送次数少于sendCount
	public List<GwSmsVO> searchWaitSendSms(int maxSendCount) throws Exception{
		return gwSmsDAO.searchWaitSendSms(maxSendCount);
	}
}
