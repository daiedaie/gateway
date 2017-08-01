package com.gztydic.gateway.system;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwUploadFileDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwUploadFileVO;

/** 
 * @ClassName: UploadFileServiceImpl 
 * @Description: TODO(系统附件相关接口实现类) 
 * @author davis
 * @date 2014-12-2 下午4:54:56 
 *  
 */
@Service
public class UploadFileServiceImpl  extends GeneralServiceImpl<GwUploadFileVO> implements UploadFileService {
	
	@Resource
	private GwUploadFileDAO gwUploadFileDAO;
	
	public void save(GwUploadFileVO fileVo) throws Exception{
		gwUploadFileDAO.save(fileVo);
	}
	
	public GwUploadFileVO findById(Long fileId) throws Exception{
		return gwUploadFileDAO.findById(fileId);
	}

}
