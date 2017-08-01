package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 机构查看
 * @author liangjiehui
 *
 */
public interface OrgService extends GeneralService<GwOrgVO> {
	
	public PageObject searchOrgList(GwOrgVO vo,PageObject pageObject)throws Exception;
	
	public GwOrgVO searchOrg(Long orgId)throws Exception;
	
	public GwUserVO searchOrgUser(Long orgId)throws Exception;
	
	public List<GwUserVO> searchDataUser(Long orgId)throws Exception;
	
	public boolean checkUpdateOrg(GwOrgVO orgVO)throws Exception;
	
	public void updateOrgAndUser(GwUserVO userVO,GwOrgVO orgVO,GwUserVO loginUser)throws Exception;
	
	public void updateOrg(GwOrgVO orgVO,GwUserVO loginUser)throws Exception;
	
	public void updateOrgUser(GwUserVO userVO,GwUserVO loginUser)throws Exception;
}
