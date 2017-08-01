package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwNoticeVO;
import com.gztydic.gateway.core.vo.GwUserVO;

public interface NoticeService extends GeneralService <GwNoticeVO>{

	public GwNoticeVO searchNoticeVO(Long noticeId) throws Exception;
	
	public List<GwNoticeVO> searchNoticeList(int maxRow) throws Exception;
	
	public PageObject searchNoticeList(GwNoticeVO notice, PageObject pageObject) throws Exception;
	
	public void saveNotice(GwNoticeVO notice, GwUserVO loginUserVO) throws Exception;
}
