package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwDocHelpVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;

public interface DocHelpService extends GeneralService <GwDocHelpVO>{

	public GwDocHelpVO searchDocHelpVO(Long DocHelpId) throws Exception;
	
	public List<GwDocHelpVO> searchDocHelpList(int maxRow) throws Exception;
	
	public PageObject searchDocHelpList(GwDocHelpVO DocHelp, PageObject pageObject) throws Exception;
	
	public void saveDocHelp(GwDocHelpVO DocHelp,GwUploadFileVO fileVO, GwUserVO loginUserVO) throws Exception;
}
