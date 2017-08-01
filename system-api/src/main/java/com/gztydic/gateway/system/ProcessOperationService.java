package com.gztydic.gateway.system;


import java.util.Date;
import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;

/** 
 * @ClassName: ProcessOperationService 
 * @Description: TODO(流程详情接口) 
 * @author michael
 * @date 2016-4-29 上午10:25:15 
 *  
 */
public interface ProcessOperationService extends GeneralService<GwProcessOperationVO>{

	/** 
	 * @Title: searchProcessOperation 
	 * @Description: TODO(流程详情) 
	 * @param @param processId 流程ID 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchProcessOperation(Long processId,PageObject pageObject) throws Exception;
	
	/** 
	 * @Title: searchProcessByUserId 
	 * @Description: TODO(根据userid查询流程编码) 
	 * @param @param userId 用户ID 
	 * @return List    返回类型 
	 * @throws 
	 */
	public List searchProcessByUserId(Long userId) throws Exception;
	
	/** 
	 * @Title: saveProcessOperation 
	 * @Description: TODO(保存流程操作信息到流程操作记录表) 
	 * @param operateId 操作编号
	 * @param processId  流程ID
	 * @param userId 用户编码
	 * @param operateContent 操作内容
	 * @param operateTime 操作时间
	 * @param planId 待办任务编码
	 * @param @throws Exception    设定文件 
	 * @return GwProcessOperationVO    返回类型 
	 * @throws 
	 */
	public GwProcessOperationVO saveProcessOperation(Long processId,Long userId,String operateContent, Date operateTime,Long planId,String progressStatus,String dealType,String step) throws Exception;
	
	/** 
	 * @Title: updateProcessOperation 
	 * @Description: TODO(更新流程操作信息到流程操作记录表) 
	 * @param processId 流程编号
	 * @param userId 用户编码
	 * @param operateContent 操作内容
	 * @param operateTime 操作时间
	 * @param @throws Exception    设定文件 
	 * @return GwProcessOperationVO    返回类型 
	 * @throws 
	 */
	public GwProcessOperationVO updateProcessOperation(Long processId,Long userId,String operateContent, Date operateTime,PageObject pageObject) throws Exception;
	
	public GwProcessOperationVO searchProcessOperationByStep(Long processId,String step) throws Exception;

}
