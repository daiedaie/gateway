package com.gztydic.gateway.web.quartz;

import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.GwSysDictDAO;
import com.gztydic.gateway.core.view.ServiceCycleAppView;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.model.ModelDataTaskService;

/** 
 * @ClassName: DataAppJob 
 * @Description: TODO(离线服务数据申请定时任务) 
 * @author davis
 * @date 2014-12-18 上午11:57:06 
 *  
 */
public class DataAppJob{

	private final Log log = LogFactory.getLog(DataAppJob.class);

	@Resource(name="modelDataTaskServiceImpl")
	private ModelDataTaskService modelDataTaskService;
	
	@Resource
	private GwSysDictDAO sysDictDAO;
	
	public static long count = 0;

	public void doStartJob() {
		log.info((++count)+"......离线服务数据申请定时任务开始......");
		//查询需要发起取数申请的服务及周期信息
		try {
			boolean jobSwitch = searchJobSwitch();
			log.info("离线服务申请任务开关："+jobSwitch);
			if(jobSwitch){
				List<ServiceCycleAppView> appList = modelDataTaskService.searchServiceTaskNoLocal();
				if(appList != null){
					ServiceCycleAppView appView = new ServiceCycleAppView();
					for(int i = 0;i<appList.size();i++){
						try {
							appView = appList.get(i);
							log.info(count+"....服务数据申请："+JSONObject.fromObject(appView).toString());
							modelDataTaskService.doServiceDataAppAndUpdate(appView);
						} catch (Exception e) {
							log.error(count+"....数据申请处理错误:"+e.getMessage()+"。data="+JSONObject.fromObject(appView).toString(),e);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(count+"....数据申请处理错误:"+e.getMessage(),e);
			e.printStackTrace();
		}
		log.info(count+"......离线服务数据申请定时任务结束......");
    }
	
	private boolean searchJobSwitch() throws Exception{
		GwSysDictVO dictVO = sysDictDAO.searchSysDict("DICT_JOB_SWITCH", "dataAppJob");
		return dictVO!=null&&CommonState.JOB_SWITCH_ON.equals(dictVO.getDictValue())? true:false;
	}
}
