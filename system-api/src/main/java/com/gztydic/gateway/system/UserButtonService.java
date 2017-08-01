package com.gztydic.gateway.system;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwUserButtonVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: UserButtonService 
 * @Description: TODO(用户按钮关系接口表) 
 * @author davis
 * @date 2014-12-3 上午11:23:46 
 *  
 */
public interface UserButtonService  extends GeneralService<GwUserButtonVO>{

	public Map findByPropertyToMap(GwUserVO loginUser) throws Exception;
	
	/**
	 * 
	 * @Title: searchPlanTypeByUserType 
	 * @Description: TODO(根据用户类型查询待办类型字典编码) 
	 * @param userType
	 * @throws Exception    设定文件 
	 * @return List    返回类型 
	 */
	public String searchPlanTypeByUserType(String userType) throws Exception;
	
	public String searchLimitPlanTypeByUserType(String userType) throws Exception;
	
	/**
	 * 
	 * @Title: searchUserButton 
	 * @Description: TODO(根据任务类型和用户类型查询是否有权限) 
	 * @param @param planType
	 * @param @param userType
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<String>    返回类型 
	 * @throws
	 */
	public Boolean searchAuthorization(String planType,String userType)throws Exception;
}


