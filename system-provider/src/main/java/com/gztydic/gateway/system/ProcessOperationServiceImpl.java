package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwProcessOperationDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;

/** 
 * @ClassName: ProcessOperationServiceImpl 
 * @Description: TODO(流程进度详情实现接口) 
 * @author michael
 * @date 2016-4-29 上午10:35:25 
 *  
 */
@Service
public class ProcessOperationServiceImpl extends GeneralServiceImpl<GwProcessOperationVO> implements ProcessOperationService {

	private final Log log = LogFactory.getLog(ProcessOperationServiceImpl.class);
	
	@Resource
	private GwProcessOperationDAO gwProcessOperationDAO;
	/** 
	 * @Title: searchProcessOperation 
	 * @Description: TODO(流程进度详情查看) 
	 * @param @param processId 流程ID 
	 * @throws 
	 */
	public PageObject searchProcessOperation(Long processId,PageObject pageObject) throws Exception {
		return gwProcessOperationDAO.findByProcessId(processId,pageObject);
	}

	public List searchProcessByUserId(Long userId)
			throws Exception {		
		return gwProcessOperationDAO.findByUserId(userId);
	}
	
	public GwProcessOperationVO saveProcessOperation(
			Long processId, Long userId, String operateContent,
			Date operateTime, Long planId,String progressStatus,String dealType,String step) throws Exception {
		GwProcessOperationVO gwProcessOperationVO = new GwProcessOperationVO(processId,userId,operateContent,operateTime,planId,progressStatus,dealType,step);
		gwProcessOperationDAO.save(gwProcessOperationVO);
		
		return gwProcessOperationVO;
}
	
	public GwProcessOperationVO updateProcessOperation(Long processId,
			Long userId, String operateContent, Date operateTime,PageObject pageObject)
			throws Exception {
		//GwProcessOperationVO gwProcessOperationVO = gwProcessOperationDAO.findById(processId);
//		if (gwProcessOperationVO != null) {
//			gwProcessOperationVO.setOperateTime(operateTime);
//			gwProcessOperationVO.setOperateContent(operateContent);
//			gwProcessOperationVO.setUserId(userId);
//			gwProcessOperationDAO.update(gwProcessOperationVO);
//		}
		List<GwProcessOperationVO> list = gwProcessOperationDAO.findByProcessId(processId, pageObject).getData();
		if (list != null && list.size()>0) {
			for ( GwProcessOperationVO vo : list) {
				vo.setOperateTime(operateTime);
				vo.setOperateContent(operateContent);
				vo.setUserId(userId);
				gwProcessOperationDAO.update(vo);
			}
		}
		List<GwProcessOperationVO> list2 = gwProcessOperationDAO.findByProcessId(processId, pageObject).getData();
		GwProcessOperationVO processOperationVo = new GwProcessOperationVO();
		if (list2 != null && list2.size()>0) {
			for ( GwProcessOperationVO ovo : list2) {
				BeanUtils.copyProperties(ovo, processOperationVo);
			}
		}
		
		return processOperationVo;
	}

	public GwProcessOperationVO searchProcessOperationByStep(Long processId,
			String step) throws Exception {
		return gwProcessOperationDAO.findByProcessIdStep(processId, step);
	}

	

}
