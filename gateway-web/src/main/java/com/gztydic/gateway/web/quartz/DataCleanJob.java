package com.gztydic.gateway.web.quartz;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.GwSysDictDAO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.system.DataCycleService;

/** 
 * @ClassName: DataCleanJob 
 * @Description: TODO(数据清理定时任务) 
 * @author davis
 * @date 2014-12-18 上午11:57:06 
 *  
 */
public class DataCleanJob{

	private final Log log = LogFactory.getLog(DataCleanJob.class);

	@Resource(name="dataCycleServiceImpl")
	private DataCycleService dataCycleService;
	
	@Resource
	private GwSysDictDAO sysDictDAO;
	
	public static long count = 0;

	public void doStartJob() {
		log.info((++count)+"......数据查询定时任务开始......");
		try {
			boolean jobSwitch = searchJobSwitch();
			log.info("定时任务开关："+jobSwitch);
			if(jobSwitch){
				dataCycleService.searchCleanCacheData();
			}
		} catch (Exception e) {
			log.error(count+"....数据清理处理错误:"+e.getMessage(),e);
			e.printStackTrace();
		}
		log.info(count+"......数据查询定时任务结束......");
    }
	
	private boolean searchJobSwitch() throws Exception{
		GwSysDictVO dictVO = sysDictDAO.searchSysDict("DICT_JOB_SWITCH", "dataCleanJob");
		return dictVO!=null&&CommonState.JOB_SWITCH_ON.equals(dictVO.getDictValue())? true:false;
	}
	
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-*.xml");
		DataCleanJob job = (DataCleanJob)ac.getBean("dataCleanJob");
		job.doStartJob();
	}
}
