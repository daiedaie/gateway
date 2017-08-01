package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwNoticeDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwNoticeVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: GwNoticeServiceImpl 
 *  
 */
@Service
public class NoticeServiceImpl extends GeneralServiceImpl<GwNoticeVO> implements NoticeService {
	
	@Resource
	private GwNoticeDAO noticeDAO;

	public GwNoticeVO searchNoticeVO(Long noticeId) throws Exception{
		return super.search(GwNoticeVO.class, noticeId);
	}
	
	public List<GwNoticeVO> searchNoticeList(int maxRow) throws Exception{
		return noticeDAO.searchNoticeList(maxRow);
	}
	
	public PageObject searchNoticeList(GwNoticeVO noticeVO, PageObject pageObject) throws Exception{
		return noticeDAO.searchNoticeList(noticeVO, pageObject);
	}
	
	public void saveNotice(GwNoticeVO notice, GwUserVO loginUserVO) throws Exception{
		GwNoticeVO dbNotice = notice.getNoticeId()==null?null:search(GwNoticeVO.class, notice.getNoticeId());
		if(dbNotice == null){
			dbNotice = new GwNoticeVO();
			dbNotice.setCreateUser(loginUserVO.getLoginName());
			dbNotice.setCreateTime(new Date());
		}else {
			dbNotice.setUpdateUser(loginUserVO.getLoginName());
			dbNotice.setUpdateTime(new Date());
		}
		dbNotice.setNoticeTitle(notice.getNoticeTitle());
		dbNotice.setNoticeContent(notice.getNoticeContent());
		saveOrUpdate(dbNotice);
	}
}
