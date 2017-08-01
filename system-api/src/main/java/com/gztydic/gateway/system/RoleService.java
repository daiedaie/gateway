package com.gztydic.gateway.system;

import java.util.List;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;

/**
 * 群组管理
 * @author liangjiehui
 *
 */
public interface RoleService extends GeneralService<GwRoleVO> {
	
	public void saveRole(GwRoleVO roleVO,GwUserVO userVO) throws Exception;
	
	public void updateRole(GwRoleVO roleVO,GwUserVO userVO) throws Exception;
	
	public void deleteRole(String roleCode) throws Exception;
	
	public boolean checkAddRole(GwRoleVO roleVO)throws Exception;
	
	public boolean checkUpdateRole(GwRoleVO roleVO)throws Exception;
	
	public PageObject searchRoleList(GwRoleVO roleVO,PageObject obj)throws Exception;
	
    public List<GwRoleVO> searchRoleByRoleCode(String roleCode)throws Exception;
    
    //查询群组拥有的服务
    public List<GwServiceVO> searchServiceListByRoleCode(String roleCode) throws Exception;
    
    //查询群组拥有的菜单,按钮
    public List<FuncAndButtonView> searchFuncAndBtnListByRoleCode(String roleCode) throws Exception;
	
	
    
}
