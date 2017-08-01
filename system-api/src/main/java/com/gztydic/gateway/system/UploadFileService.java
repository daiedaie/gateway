package com.gztydic.gateway.system;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwUploadFileVO;

/** 
 * @ClassName: UploadFileService 
 * @Description: TODO(系统附件接口方法类) 
 * @author davis
 * @date 2014-12-2 下午4:52:36 
 *  
 */
public interface UploadFileService extends GeneralService<GwUploadFileVO>{

	public void save(GwUploadFileVO fileVo) throws Exception;
	
	public GwUploadFileVO findById(Long fileId) throws Exception;
}
