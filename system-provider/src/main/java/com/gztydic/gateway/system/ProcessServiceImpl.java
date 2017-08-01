package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwProcessDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/** 
 * @ClassName: ProcessServiceImpl 
 * @Description: TODO(流程进度列表实现接口) 
 * @author michael
 * @date 2016-4-29 上午10:54:39 
 *  
 */
@Service
public class ProcessServiceImpl extends GeneralServiceImpl<GwProcessVO> implements ProcessService {
	
	@Resource
	private GwProcessDAO gwProcessDAO;
	
	
	/** 
	 * @Title: searchAllProcess 
	 * @Description: TODO(根据指定条件查询流程列表) 
	 * @param @param processType 流程类型
	 * @param @param status 流程状态
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllProcess(String processType,String status,PageObject pageObject) throws Exception{
		return gwProcessDAO.findAllByPage(processType,status,pageObject);
	}
	
	public List<GwProcessVO> searchUserProcess(Long processId,
			String processType, String status, PageObject pageObject)
			throws Exception {
		
		return gwProcessDAO.findUserProcessByPage(processId, processType, status, pageObject);
	}
	public GwProcessVO saveProcess(Long processId, String processType,
			Date createTime, Date endTime, String status, String progressStatus,Long userId,String stepStatus)
			throws Exception {
		
		GwProcessVO gwProcessVO= new GwProcessVO(processId,processType,createTime,endTime,status,progressStatus,userId,stepStatus);
		gwProcessDAO.save(gwProcessVO);
		
		return gwProcessVO;
	}

	public GwProcessVO updateProcess(Long processId, String status,
			Date endTime) throws Exception {
		GwProcessVO gwProcessVO = gwProcessDAO.findById(processId);
		if (gwProcessVO != null && "0".equals(gwProcessVO.getStatus().trim())) {
			throw new Exception("流程已处理完，不能重复处理");
		} else if (gwProcessVO != null) {
			gwProcessVO.setStatus(status);
			gwProcessVO.setEndTime(endTime);
			gwProcessDAO.update(gwProcessVO);
		}
		
		return gwProcessVO;
	}	
	
	public GwProcessVO updateProcessStatus(Long processId, String progressStatus,
			String stepStatus,Date endTime,String status) throws Exception {
		GwProcessVO gwProcessVO = gwProcessDAO.findById(processId);
		if (gwProcessVO != null && "0".equals(gwProcessVO.getStatus().trim())) {
			throw new Exception("流程已处理完，不能重复处理");
		} else if (gwProcessVO != null) {
			gwProcessVO.setStepStatus(stepStatus);
			gwProcessVO.setProgressStatus(progressStatus);
			gwProcessVO.setEndTime(endTime);
			if(status != ""&&status != null){
				gwProcessVO.setStatus(status);
			}
			gwProcessDAO.update(gwProcessVO);
		}
		
		return gwProcessVO;
	}	
	
	public PageObject searchProcessByUserId(Long userId,PageObject pageObject,String pType,String pStatus) throws Exception{
		return gwProcessDAO.searchProcessByUserId(userId, pageObject,pType,pStatus);
	}

	public List<GwProcessVO> findByCreateTime(String createTime)throws Exception{
		
		return gwProcessDAO.findByCreateTime(createTime);
	}
}
