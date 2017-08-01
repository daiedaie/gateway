package com.gztydic.gateway.model;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.view.ModelFileView;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;


/** 
 * @ClassName: ModelInfoService 
 * @Description: TODO(关于模型信息所有处理接口) 
 * @author davis
 * @date 2014-11-19 下午02:41:01 
 *  
 */
public interface ModelInfoService extends GeneralService<GwModelVO>{

	public List<GwModelVO> searchList() throws Exception;
	
	public GwModelVO searchModelById(Long modelId) throws Exception;
	
	public PageObject searchServiceList(GwUserVO userVO,GwServiceView serviceView,PageObject pageObject) throws Exception;
	
	public GwServiceVO searchServiceById(Long serviceId) throws Exception;
	
	public GwServiceView findServiceModelById(Long serviceId) throws Exception;
	
	public List searchServicefieldInput(Long serviceId) throws Exception;
	
	public List searchServicefieldOutput(Long serviceId) throws Exception;
	
	public GwServiceVO searchService(String serviceCode,String serviceSource) throws Exception;
	
	public int searchServiceCount(String serviceCode, String serviceSource) throws Exception;
	
	public int updateServiceInfo(GwServiceVO serviceVO,GwUserVO loginUser) throws Exception;
	
	public int deleteServiceInfo(GwServiceVO serviceVO,GwUserVO loginUser) throws Exception;
	
	//根据taskId查询文件记录
	public GwModelDataFileVO searchModelDataFile(Long taskId,String fileType)throws Exception;
	
	//根据fileIds查询文件记录
	public List<ModelFileView> searchModelDataFiles(String fileIds)throws Exception;
	
}
