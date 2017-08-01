package com.gztydic.gateway.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwUserButtonDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwUserButtonVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: UserButtonServiceImpl 
 * @Description: TODO(用户与系统功能按钮关系表接口实现类) 
 * @author davis
 * @date 2014-12-3 上午11:23:20 
 *  
 */
@Service
public class UserButtonServiceImpl extends GeneralServiceImpl<GwUserButtonVO> implements UserButtonService{ 

	@Resource
	private GwUserButtonDAO gwUserButtonDAO;
	
    //根据属性值查看用户功能按钮关系对象map
	public Map findByPropertyToMap(GwUserVO loginUser) throws Exception{
		List<String> buttonList = gwUserButtonDAO.searchUserButton(loginUser);
		Map ubMap = new HashMap();
		for(String btnCode : buttonList){
			ubMap.put(btnCode, btnCode);
		}
		return ubMap;
	}
	
	/**
	 * 
	 * @Title: searchPlanTypeByUserType 
	 * @Description: TODO(根据用户类型查询待办类型字典编码) 
	 * @param userType
	 * @throws Exception    设定文件 
	 * @return List    返回类型 
	 */
	public String searchPlanTypeByUserType(String userType) throws Exception{
		List<String> planTypeList = gwUserButtonDAO.searchPlanTypeByUserType(userType);
		String planTypeStr = "";
		if(planTypeList  != null){
			for(String planType : planTypeList){
				planTypeStr+= planTypeStr==""?planType:","+planType;
			}
		}
		return planTypeStr;
	}
	
	//用于用户审核的用户配置的待办
	public String searchLimitPlanTypeByUserType(String userType) throws Exception{
		List<String> planTypeList = gwUserButtonDAO.searchPlanTypeByUserType(userType);
		String planTypeStr = "";
		if(planTypeList  != null){
			for(String planType : planTypeList){
				planTypeStr+= planTypeStr==""?"'"+planType+"'":",'"+planType+"'";
			}
		}
		return planTypeStr;
	}
	
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
	public Boolean searchAuthorization(String planType,String userType)throws Exception{
		List<String> planTypeList = gwUserButtonDAO.searchUserButton(planType,userType);
		Boolean result = false;
		if(planTypeList != null && planTypeList.size()>0){
			result = true;
		}
		
		return result;
	}
}
