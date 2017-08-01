package com.gztydic.gateway.web.quartz;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.GwSysDictDAO;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.gather.webservice.IWsSmsService;
import com.gztydic.gateway.gather.webservice.client.sms.SmsResponse;
import com.gztydic.gateway.system.ConfigService;
import com.gztydic.gateway.system.SmsService;

public class SendSmsJob{

	private final Log log = LogFactory.getLog(SendSmsJob.class);
	
	@Resource(name="wsSmsServiceImpl")
	private IWsSmsService wsSmsService;
	
	@Resource(name="smsServiceImpl")
	private SmsService smsService;
	
	@Resource
	private GwSysDictDAO sysDictDAO;
	
	@Resource(name="configServiceImpl")
	private ConfigService configService;
	
	public static long count = 0;
	
	public void doStartJob() {
		//log.info((++count)+"......发送短信定时任务开始......");
		try {
			boolean jobSwitch = searchJobSwitch();
			//log.info("发送短信定时任务开关："+jobSwitch);
			if(jobSwitch){
				int maxSendCount = searchSmsMaxSendCount();
				List<GwSmsVO> smsList = smsService.searchWaitSendSms(maxSendCount);
				for (GwSmsVO smsVO : smsList) {
					try {
						SmsResponse smsResponse = wsSmsService.sendSms(smsVO);
						smsVO.setSendCount(smsVO.getSendCount()==null?1:smsVO.getSendCount()+1);
						smsVO.setSendStatus(smsResponse.getReturnvalue());
						smsVO.setSendResult(smsResponse.getOperatingreturn());
						smsVO.setSendTime(new Date());
						
						smsService.update(smsVO);
					} catch (Exception e) {
						log.error("smsId="+smsVO.getSmsId()+"短信发送错误："+e.getMessage());
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			log.error(count+"....发送短信错误:"+e.getMessage(),e);
			e.printStackTrace();
		}
		//log.info(count+"......发送短信定时任务结束......");
    }
	
	private boolean searchJobSwitch() throws Exception{
		GwSysDictVO dictVO = sysDictDAO.searchSysDict("DICT_JOB_SWITCH", "sendSmsJob");
		return dictVO!=null&&CommonState.JOB_SWITCH_ON.equals(dictVO.getDictValue())? true:false;
	}
	
	/**
	 * 短信重复发送次数
	 * @return
	 * @throws Exception
	 */
	private int searchSmsMaxSendCount() throws Exception{
		//GwSysDictVO dictVO = sysDictDAO.searchSysDict("DICT_SMS_SEND_COUNT", "smsSendCount");
		//return dictVO!=null?Integer.parseInt(dictVO.getDictValue()):3;
		GwSysCnfigVO smsCountCnfigVO=configService.findByConfigType(CommonState.CONFIG_SMS_COUNT);
		return smsCountCnfigVO!=null?Integer.parseInt(smsCountCnfigVO.getConfigValue().toString()):3;
	}
}
