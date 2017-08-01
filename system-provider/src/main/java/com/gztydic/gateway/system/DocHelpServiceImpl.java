package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwDocHelpDAO;
import com.gztydic.gateway.core.dao.GwUploadFileDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwDocHelpVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: GwDocHelpServiceImpl 
 *  
 */
@Service
public class DocHelpServiceImpl extends GeneralServiceImpl<GwDocHelpVO> implements DocHelpService {
	
	@Resource
	private GwDocHelpDAO DocHelpDAO;
	
	@Resource
	private GwUploadFileDAO fileDAO;

	public GwDocHelpVO searchDocHelpVO(Long DocHelpId) throws Exception{
		GwDocHelpVO docVO = super.search(GwDocHelpVO.class, DocHelpId);
		if(docVO != null){
			docVO.setFileVO(fileDAO.findById(docVO.getFileId()));
		}
		return docVO;
	}
	
	public List<GwDocHelpVO> searchDocHelpList(int maxRow) throws Exception{
		return DocHelpDAO.searchDocHelpList(maxRow);
	}
	
	public PageObject searchDocHelpList(GwDocHelpVO DocHelpVO, PageObject pageObject) throws Exception{
		return DocHelpDAO.searchDocHelpList(DocHelpVO, pageObject);
	}
	
	public void saveDocHelp(GwDocHelpVO docHelp,GwUploadFileVO fileVO, GwUserVO loginUserVO) throws Exception{
		if(fileVO.getFileId()==null){
			fileDAO.save(fileVO);
		}
		
		GwDocHelpVO dbDocHelp = docHelp.getDocId()==null?null:search(GwDocHelpVO.class, docHelp.getDocId());
		if(dbDocHelp == null){
			dbDocHelp = new GwDocHelpVO();
			dbDocHelp.setCreateUser(loginUserVO.getLoginName());
			dbDocHelp.setCreateTime(new Date());
		}else {
			dbDocHelp.setUpdateUser(loginUserVO.getLoginName());
			dbDocHelp.setUpdateTime(new Date());
		}
		dbDocHelp.setDocDesc(docHelp.getDocDesc());
		dbDocHelp.setFileId(fileVO.getFileId());;
		saveOrUpdate(dbDocHelp);
	}
}
