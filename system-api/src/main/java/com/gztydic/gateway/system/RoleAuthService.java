package com.gztydic.gateway.system;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.GwButtonView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwRoleFuncVO;

/**
 * 群组菜单分配
 * @author liangjiehui
 *
 */
public interface RoleAuthService extends GeneralService<GwRoleFuncVO> {
	
	//根据菜单查询按钮列表并关联群组
	public List<GwButtonView> searchButtonListByRoleCode(String roleCode,String funcCode) throws Exception;
	
	//查询菜单，按钮列表并关联群组
	public List<FuncAndButtonView> searchFuncAndBtnListByRoleCode(String roleCode) throws Exception;
	
	//根据菜单查询对应的按钮列表
	public List<GwButtonVO> searchButtonListByFuncCode(String funcCode)throws Exception;
	
	//查询服务列表并关联群组
	public List<GwServiceView> searchServiceListByRoleCode(String roleCode,String sort,String asc)throws Exception;
	
	//群组授权
	public void updateRoleAuth(String roleCode,String chooseFuncs,String chooseButtons,
			String chooseServices)throws Exception;
	
	//群组菜单授权
	public void updateRoleAuthFunc(String roleCode,String chooseFuncs)throws Exception;
	
	//群组按钮授权
	public void updateRoleAuthBtn(String roleCode,String chooseButtons)throws Exception;
	
	//群组服务授权
	public void updateRoleAuthService(String roleCode,String chooseServices)throws Exception;
	
	//查询所有群组分别拥有的菜单按钮
	public List<List<FuncAndButtonView>> searchRoleFuncAndBtnList() throws Exception;
	
	//查询所有群组分别拥有的服务
	public Map<String, List> searchServiceListByAllRole()throws Exception;
	
}
