package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/** 
 * @ClassName: ProcessService 
 * @Description: TODO(流程进度接口) 
 * @author michael
 * @date 2016-4-29 上午09:35:25 
 *  
 */
public interface ProcessService extends GeneralService<GwProcessVO>{

	/** 
	 * @Title: searchAllProcess 
	 * @Description: TODO(流程进度列表查看) 
	 * @param @param processType 流程类型
	 * @param @param status 流程状态 
	 * @return void    返回类型 
	 * @throws 
	 */
	public PageObject searchAllProcess(String processType,String status,PageObject pageObject) throws Exception;
	
	/** 
	 * @Title: searchUserProcess 
	 * @Description: TODO(用户流程进度列表查看) 
	 * @param @param processIds 流程编码组
	 * @param @param processType 流程类型
	 * @param @param status 流程状态 
	 * @return void    返回类型 
	 * @throws 
	 */
	public List<GwProcessVO> searchUserProcess(Long processId, String processType,String status,PageObject pageObject) throws Exception;
	/** 
	 * @Title: saveProcess 
	 * @Description: TODO(保存流程信息到流程表) 
	 * @param processId 流程ID
	 * @param processType  流程类型
	 * @param createTime 开始时间
	 * @param endTime 结束时间
	 * @param status 当前状态
	 * @param progressStatus 流程所处具体状态
	 * @param @throws Exception    设定文件 
	 * @return GwProcessVO    返回类型 
	 * @throws 
	 */
	public GwProcessVO saveProcess(Long processId,String processType,Date createTime,Date endTime,String status,String progressStatus,Long userId,String stepStatus) throws Exception;
	
	/** 
	 * @Title: updateProcess 
	 * @Description: TODO(更新流程信息到流程表) 
	 * @param processId 流程ID
	 * @param endTime 结束时间
	 * @param status 当前状态
	 * @param @throws Exception    设定文件 
	 * @return GwProcessVO    返回类型 
	 * @throws 
	 */
	public GwProcessVO updateProcess(Long processId,String status,Date endTime) throws Exception;
		
	public GwProcessVO updateProcessStatus(Long processId,String processStatus,String stepStatu,Date endTime,String statu) throws Exception;

	public PageObject  searchProcessByUserId(Long userId, PageObject pageObject,String pType,String pStatus)throws Exception;

	public List<GwProcessVO> findByCreateTime(String creatTime)throws Exception;

	
}
