package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.FuncAuthUtil;
import com.gztydic.gateway.core.dao.GwButtonDAO;
import com.gztydic.gateway.core.dao.GwFuncDAO;
import com.gztydic.gateway.core.dao.GwRoleDAO;
import com.gztydic.gateway.core.dao.GwRoleFuncDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.dao.GwSmsDAO;
import com.gztydic.gateway.core.dao.GwUserButtonDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.dao.GwUserFuncDAO;
import com.gztydic.gateway.core.dao.GwUserRoleDAO;
import com.gztydic.gateway.core.dao.GwUserServiceDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.GwButtonView;
import com.gztydic.gateway.core.view.RoleView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwUserButtonVO;
import com.gztydic.gateway.core.vo.GwUserFuncVO;
import com.gztydic.gateway.core.vo.GwUserRoleVO;
import com.gztydic.gateway.core.vo.GwUserServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

@Service
public class UserAuthServiceImpl extends GeneralServiceImpl<GwUserServiceVO> implements UserAuthService {
	
	@Resource
	private GwRoleDAO gwRoleDao;
	@Resource
	private GwButtonDAO gwButtonDAO;
	@Resource
	private GwFuncDAO gwFuncDAO;
	@Resource
	private GwUserRoleDAO gwUserRoleDAO;
	@Resource
	private GwUserFuncDAO gwUserFuncDAO;
	@Resource
	private GwRoleFuncDAO gwRoleFuncDAO;
	@Resource
	private GwUserButtonDAO gwUserButtonDAO;
	@Resource
	private GwUserDAO gwUserDAO;
	@Resource
	private GwServiceDAO gwServiceDAO;
	@Resource
	private GwSmsDAO gwSmsDAO;
	@Resource
	private GwUserServiceDAO gwUserServiceDAO;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	//查詢群组列表并并关联用户ID
	public List<RoleView> searchRoleListByUserId(Long userId) throws Exception{
		List<Object[]> roleList=gwRoleDao.searchRoleListByUserId(userId);
		RoleView roleView=null;
		List<RoleView> roleViewList=new ArrayList<RoleView>();
		for(Object[] obj : roleList){
			roleView=new RoleView();
			roleView.setRoleCode(obj[0]==null?"":String.valueOf(obj[0]));
			roleView.setRoleName(obj[1]==null?"":String.valueOf(obj[1]));
			roleView.setRoleDesc(obj[2]==null?"":String.valueOf(obj[2]));
			roleView.setUserId(obj[3]==null?0:Long.valueOf(obj[3].toString()));
			roleViewList.add(roleView);
		}
		return roleViewList;
	}
	
	//查詢按钮列表并并关联用户ID
	public List<GwButtonView> searchBtnListByUserType(String userType,String funcCode)throws Exception{
		List<Object[]> list= gwButtonDAO.searchButtonListByUserType(userType);
		GwButtonView button=null;
		List<GwButtonView> funcButtonList=new ArrayList<GwButtonView>();
		for(Object[] obj : list){
			button=new GwButtonView();
			button.setButtonCode(obj[0]==null?"":String.valueOf(obj[0]));
			button.setFuncCode(obj[1]==null?"":String.valueOf(obj[1]));
			button.setOperateDesc(obj[2]==null?"":String.valueOf(obj[2]));
			button.setUserType(obj[3]==null?"":String.valueOf(obj[3]));
			if(funcCode.equals(button.getFuncCode())){
				funcButtonList.add(button);
				
			}
		}
		return funcButtonList;
	}
	
	//根据菜单编码查询对应的功能按钮列表
	public List<GwButtonVO> searchBtnListByFuncCode(String funcCode)throws Exception{
		return gwButtonDAO.findByFuncCode(funcCode);
	}
	
	
	//查询菜单，按钮列表并关联用户类型
	public List<FuncAndButtonView> searchFuncAndBtnList(String userType,String source)throws Exception{
		List<Object[]> list = gwFuncDAO.searchFuncListByUserType(userType,source);
		FuncAndButtonView view = null;
		List<FuncAndButtonView> funcButtonList=new ArrayList<FuncAndButtonView>();
		List<GwButtonView> viewList;
		for(Object[] obj : list){
			view = new FuncAndButtonView();
			view.setFuncCode(obj[0]==null?"":String.valueOf(obj[0]));
			view.setFuncName(obj[1]==null?"":String.valueOf(obj[1]));
			view.setParentName(obj[2]==null?"":String.valueOf(obj[2]));
			view.setUserType(obj[3]==null?"":String.valueOf(obj[3]));
			List<GwButtonVO> buttonList=searchBtnListByFuncCode(view.getFuncCode());
			viewList=searchBtnListByUserType(userType,view.getFuncCode());
			view.setButtonList(viewList);
			funcButtonList.add(view);
		}
		return funcButtonList;
	}
	
	//查询未被授权的用户服务
	public List<GwServiceVO> searchUnchooseServiceList(Long userId)throws Exception{
		return gwServiceDAO.searchUnchooseServiceByUserId(userId);
	}
	
	//查询已被授权的用户服务
	public List<GwServiceVO> searchChooseServiceList(Long userId)throws Exception{
		return gwServiceDAO.searchchooseServiceByUserId(userId);
	}
	
	//根据条件查询未被授权的用户服务
	public List<GwServiceVO> searchServiceListByService(GwServiceVO serviceVO)throws Exception{
		return gwServiceDAO.searchchooseServiceByIdAndName(serviceVO);
	}
	
	//查询多个模型下的服务列表
	public List<GwServiceVO> searchServiceListByModelId(GwServiceVO serviceVO)throws Exception{
		List<GwServiceVO> serviceList=searchServiceListByService(serviceVO);
		List<GwServiceVO> list=new ArrayList<GwServiceVO>();
		Map map=new HashMap();
		for(int i=0;i<serviceList.size();i++){
			if(serviceList.get(i).getModelId()!=null){
				map.put(serviceList.get(i).getModelId(), serviceList.get(i).getModelId());
			}
		}
		Iterator it=map.keySet().iterator();
	    while(it.hasNext()) {
	    	Long modelId=(Long)it.next();
	    	List<GwServiceVO> serviceVOList=gwServiceDAO.findByModelId(modelId);
	    	for(int i=0;i<serviceVOList.size();i++){
	    		if(!checkService(serviceList, serviceVOList.get(i).getServiceId())){
	    			list.add(serviceVOList.get(i));
	    		}	
	    	}
	    }
	    return list;
	}
	
	public boolean checkService(List<GwServiceVO> serviceList,Long serviceId)throws Exception{
		boolean isExist = false;      
		for(int i=0;i<serviceList.size();i++){
			if(serviceList.get(i).getServiceId()==serviceId){
				isExist = true;     
				break;  
			}
		}
		return isExist;
	}
	
	//修改所有机构用户的取数优先级
	public void updateUserListLevel(String userSortList)throws Exception{
		if(StringUtils.isNotBlank(userSortList)){
			String[] orgUserArray=userSortList.split(",");
			int runLevel=1;
			for(int i=0;i<orgUserArray.length;i++){
				gwUserDAO.updateUserListLevel(orgUserArray[i], runLevel);
				runLevel++;
			}
		}
	}
	
	//用户授权
	public void saveUserAuth (String usertype,Long workPlanId,Long userId,String orgUserSortList,String dataUserSortList,String chooseRoles,
			String chooseServices,String chooseServiceCodes,Long operUserId)throws Exception{
		GwUserVO acceptUser=gwUserDAO.findById(userId);
	    GwUserVO dealUser=gwUserDAO.findById(operUserId);
	    String operationContent = "";
		//如果是对机构用户授权，保存群组，服务授权，并且修改所有机构用户的取数优先级
		if(GwUserType.ORG_USER.equals(usertype)){
			updateUserListLevel(orgUserSortList);
			saveUserAuthRole(userId, chooseRoles);
			saveUserAuthService(userId, chooseServices, acceptUser);
			operationContent = dealUser.getLoginName()+"对用户"+acceptUser.getLoginName()+"进行优先级、群组、服务分配！"+"分配的服务编码("+chooseServiceCodes+")";
		}else if(GwUserType.DATA_USER.equals(usertype)){ //如果是对数据用户授权，只需修改同一个机构下数据用户的取数优先级
			updateUserListLevel(dataUserSortList);
			operationContent = dealUser.getLoginName()+"对用户"+acceptUser.getLoginName()+"进行优先级分配！";
		}
		
		if(workPlanId != null){
			workPlanService.updateWorkPlanState(workPlanId, null, WorkPlanConstent.DEAL_PASS, operUserId);
		}
		
	    //写操作日志
		operationLogService.saveOperationLog(dealUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.ALLOT_USER, operationContent);
	}
	
	//用户群组授权
	public void saveUserAuthRole(Long userId,String chooseRoles)throws Exception{
		gwUserRoleDAO.deleteByUserId(userId);
		if(!"".equals(chooseRoles)){
			String[] roleArray=chooseRoles.split(",");
			GwUserRoleVO userRoleVO;
			for(int i=0;i<roleArray.length;i++){
				userRoleVO=new GwUserRoleVO();
				userRoleVO.setRoleCode(roleArray[i]);
				userRoleVO.setUserId(userId);
				gwUserRoleDAO.save(userRoleVO);
			}
		}
	}
	
	//用户类型菜单,按钮分配
	public void saveUserTypeFunc(String userType,String chooseFuncs,String chooseBtns)throws Exception{
		gwUserFuncDAO.deleteByUserType(userType);
		Map funcMap=new HashMap();
		if(!"".equals(chooseFuncs)){
			String[] funcArray=chooseFuncs.split(",");
			for(int i=0;i<funcArray.length;i++){
				 GwFuncVO vo =gwFuncDAO.findById(funcArray[i]);
				 funcMap.put(vo.getParentCode(), vo.getParentCode());
				 funcMap.put(vo.getFuncCode(), vo.getFuncCode());
			}
		}
		
		Iterator it=funcMap.keySet().iterator();
		GwUserFuncVO  funcVO;
	    while(it.hasNext()) {
		     funcVO=new GwUserFuncVO();
	         String funcCode=(String)it.next();
	         funcVO.setFuncCode(funcCode);
	         funcVO.setUserType(userType);
	         gwRoleFuncDAO.save(funcVO);
	    }
	    
	    gwUserButtonDAO.deleteByUserType(userType);
		Map btnMap=new HashMap();
		if(!"".equals(chooseBtns)){
			String[] btnArray=chooseBtns.split(",");
			for(int i=0;i<btnArray.length;i++){
				btnMap.put(btnArray[i], btnArray[i]);
			}
		}
		
		it=btnMap.keySet().iterator();
		GwUserButtonVO  btnVO;
	    while(it.hasNext()) {
	    	 btnVO=new GwUserButtonVO();
	         String btnCode=(String)it.next();
	         btnVO.setButtonCode(btnCode);
	         btnVO.setUserType(userType);
	         gwUserButtonDAO.save(btnVO);
	    }
	    
	    //初始化菜单及权限数据
	    initFuncData();
	}
	
	//用户按钮授权
	public void saveUserAuthBtn(String userType,String chooseBtns)throws Exception{
		gwUserButtonDAO.deleteByUserType(userType);
		Map map=new HashMap();
		if(!"".equals(chooseBtns)){
			String[] btnArray=chooseBtns.split(",");
			for(int i=0;i<btnArray.length;i++){
				 map.put(btnArray[i], btnArray[i]);
			}
		}
		
		Iterator it=map.keySet().iterator();
		GwUserButtonVO  btnVO;
	    while(it.hasNext()) {
	    	 btnVO=new GwUserButtonVO();
	         String btnCode=(String)it.next();
	         btnVO.setButtonCode(btnCode);
	        /* btnVO.setUserId(userId);*/
	         gwUserButtonDAO.save(btnVO);
	    }
	}
	
	//用户服务授权
	public void saveUserAuthService(Long userId,String chooseServices,GwUserVO acceptUser)throws Exception{
		
		//给这个机构用户的数据用户发送一条短信
	    List<GwUserVO> dataUserList= gwUserDAO.searchDataUser(acceptUser.getOrgId());
	    String userMoblie="";
	    GwSmsVO gwSmsVO;
	    String msgContent;
	    for(GwUserVO gwUserVO:dataUserList){
	    	userMoblie+=(userMoblie==""?gwUserVO.getMoblie():"|"+gwUserVO.getMoblie());
	    }
	    if(!"".equals(chooseServices)){
			String[] serviceArray=chooseServices.split(",");
			for(int i=0;i<serviceArray.length;i++){
				List<GwUserServiceVO> list=gwUserServiceDAO.findByServiceIdAndUserId(userId.toString(),serviceArray[i]);
				if(null!=list && list.size()==0){
					GwServiceVO gwServiceVO=gwServiceDAO.findById(Long.valueOf(serviceArray[i]));
					msgContent="系统新开通了"+gwServiceVO.getServiceName()+"服务，你可以登录提交服务取数申请，请知晓!";
					gwSmsVO=new GwSmsVO();
					gwSmsVO.setSendStatus(null);
					gwSmsVO.setSendResult(null);
					gwSmsVO.setSendCount(0);
					gwSmsVO.setCreateTime(new Date());
					gwSmsVO.setSendTime(null);
					gwSmsVO.setSmsContent(msgContent);
					gwSmsVO.setSmsMobile(userMoblie);
					gwSmsDAO.saveOrUpdate(gwSmsVO);
				}
			}
		}
	    
		gwUserServiceDAO.deleteByUserId(userId);
		Map map=new HashMap();
		if(!"".equals(chooseServices)){
			String[] serviceArray=chooseServices.split(",");
			for(int i=0;i<serviceArray.length;i++){
				map.put(serviceArray[i], serviceArray[i]);
			}
		}
		
		Iterator it=map.keySet().iterator();
		GwUserServiceVO  userServiceVO;
	    while(it.hasNext()) {
	    	userServiceVO=new GwUserServiceVO();
	    	userServiceVO.setServiceId(Long.parseLong(it.next().toString()));
	    	userServiceVO.setUserId(userId);
	        gwUserServiceDAO.save(userServiceVO);
	    }
	    
	    
	}
	
	public void initFuncData() throws Exception{
		FuncAuthUtil.menuMap = new HashMap<String, GwFuncVO>();
		FuncAuthUtil.menuAuthMap = new HashMap<String, String>();
		
		List<GwFuncVO> funcList = gwFuncDAO.findAll();
		for (GwFuncVO func : funcList) {
			if(func.getFuncUrl() != null){
				String url = func.getFuncUrl();
				if(url.indexOf("?")>-1) url = url.substring(0,url.indexOf("?"));
				FuncAuthUtil.menuMap.put(url, func);
			}
		}
		
		List<GwUserFuncVO> userFuncList = gwUserFuncDAO.findAll();
		for (GwUserFuncVO vo : userFuncList) {
			FuncAuthUtil.menuAuthMap.put(vo.getUserType()+"_"+vo.getFuncCode(), vo.getFuncCode());
		}
	}

	public void saveUserService(Long userId, Long serviceId) throws Exception {
		// TODO Auto-generated method stub
		GwUserServiceVO userServiceVO=new GwUserServiceVO();
    	userServiceVO.setServiceId(serviceId);
    	userServiceVO.setUserId(userId);
        gwUserServiceDAO.save(userServiceVO);
	}
}
