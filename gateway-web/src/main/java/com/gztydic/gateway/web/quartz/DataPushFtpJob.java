package com.gztydic.gateway.web.quartz;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.GwSysDictDAO;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.model.ModelDataTaskService;
import com.gztydic.gateway.system.ConfigService;

/** 
 * @ClassName: DataPushFtpJob 
 * @Description: TODO(数据发送到108ftp定时任务) 
 * @author davis
 * @date 2014-12-18 上午11:57:06 
 *  
 */
@Service
public class DataPushFtpJob extends Thread{

	private final Log log = LogFactory.getLog(DataPushFtpJob.class);

	@Resource(name="modelDataTaskServiceImpl")
	private ModelDataTaskService taskService;
	
	@Resource(name="configServiceImpl")
	private ConfigService configService;
	
	@Resource
	private GwSysDictDAO sysDictDAO;
	
	public static long count = 0;
	
	public static long defaultCycle = 30;

	public void doStartJob() {
		//log.info((++count)+"......108服务数据重新发送到ftp定时任务开始......");
		try {
			boolean jobSwitch = searchJobSwitch();
			//log.info("pushFtp定时器任务开关："+jobSwitch);
			if(jobSwitch){
				taskService.doRepush108Ftp();
			}
		} catch (Exception e) {
			log.error(count+"....108服务数据重新发送到ftp错误:"+e.getMessage(),e);
			e.printStackTrace();
		}
		//log.info(count+"......108服务数据重新发送到ftp定时任务结束......");
    }
	
	private boolean searchJobSwitch() throws Exception{
		GwSysDictVO dictVO = sysDictDAO.searchSysDict("DICT_JOB_SWITCH", "dataPushFtpJob");
		return dictVO!=null&&CommonState.JOB_SWITCH_ON.equals(dictVO.getDictValue())? true:false;
	}
	
	private long searchCycleValue(){
		long timeCycle = defaultCycle;
		try {
			GwSysCnfigVO configVO = configService.findByConfigType(CommonState.CONFIG_REPUSH_INTERVAL);
			if(configVO != null && configVO.getConfigValue() != null){
				timeCycle = configVO.getConfigValue();
				
				if(CommonState.MINUTE_OF_UNIT.equals(configVO.getConfigUnit())){
					timeCycle = configVO.getConfigValue() * 60;
				}
			}
		} catch (Exception e) {
			log.error("查询push ftp时间间隔配置出错："+e.getMessage());
			e.printStackTrace();
		}
		return timeCycle;
	}

	public void run() {
		do{
			try {
				doStartJob();
				
				long timeCycle = searchCycleValue();
				//log.info("定时push文件到ftp时间间隔："+timeCycle+"秒");
				Thread.sleep(timeCycle*1000);
			} catch (InterruptedException e) {
				log.error("定时push文件到ftp失败："+e.getMessage());
				e.printStackTrace();
			}
		}while(true);
	}
}
