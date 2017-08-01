package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.GwButtonView;
import com.gztydic.gateway.core.view.RoleView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 用户权限分配
 * @author liangjiehui
 *
 */
public interface UserAuthService extends GeneralService<GwUserServiceVO>{
	
	//查詢群组列表并并关联用户ID
	public List<RoleView> searchRoleListByUserId(Long userId) throws Exception;
	
	//根据菜单编码查询对应的功能按钮列表
	public List<GwButtonVO> searchBtnListByFuncCode(String funcCode)throws Exception;
	
	//查詢按钮列表并并关联用户类型
	public List<GwButtonView> searchBtnListByUserType(String userType,String funcCode)throws Exception;
	
	//查询菜单，按钮列表并关联用户用户类型
	public List<FuncAndButtonView> searchFuncAndBtnList(String userType,String source)throws Exception;
	
	//查询未被授权的用户服务
	public List<GwServiceVO> searchUnchooseServiceList(Long userId)throws Exception;
	
	//查询已被授权的用户服务
	public List<GwServiceVO> searchChooseServiceList(Long userId)throws Exception;
	
	//根据条件查询未被授权的用户服务
	public List<GwServiceVO> searchServiceListByService(GwServiceVO serviceVO)throws Exception;
	
	//查询多个模型下的服务列表
	public List<GwServiceVO> searchServiceListByModelId(GwServiceVO serviceVO)throws Exception;
	
	public boolean checkService(List<GwServiceVO> serviceList,Long serviceId)throws Exception;
	
	//修改用户的取数优先级
	public void updateUserListLevel(String userSortList)throws Exception;
	
	//用户群组授权
	public void saveUserAuthRole(Long userId,String chooseRoles)throws Exception;
	
	//用户按钮授权
	public void saveUserAuthBtn(String userType,String chooseBtns)throws Exception;
	
	//用户服务授权
	public void saveUserAuthService(Long userId,String chooseServices,GwUserVO acceptUser)throws Exception;
	
	//用户授权
	public void saveUserAuth (String userType,Long workPlanId,Long userId,String orgUserSortList,String dataUserSortList,String chooseRoles,
			String chooseServices,String chooseServiceCodes,Long operUserId)throws Exception;
	//用户类型菜单,按钮授权
	public void saveUserTypeFunc(String userType,String chooseFuncs,String chooseBtns)throws Exception;
	
	public void initFuncData() throws Exception;

	public void saveUserService(Long userId, Long serviceId)throws Exception;

}
