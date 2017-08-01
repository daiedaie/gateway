package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.gztydic.gateway.core.dao.GwButtonDAO;
import com.gztydic.gateway.core.dao.GwFuncDAO;
import com.gztydic.gateway.core.dao.GwRoleButtonDAO;
import com.gztydic.gateway.core.dao.GwRoleDAO;
import com.gztydic.gateway.core.dao.GwRoleFuncDAO;
import com.gztydic.gateway.core.dao.GwRoleServiceDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.GwButtonView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwRoleButtonVO;
import com.gztydic.gateway.core.vo.GwRoleFuncVO;
import com.gztydic.gateway.core.vo.GwRoleServiceVO;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

/**
 * 群组菜单分配
 * @author liangjiehui
 *
 */
@Service
public class RoleAuthServiceImpl extends GeneralServiceImpl<GwRoleFuncVO> implements RoleAuthService{
	
	@Resource
	private GwFuncDAO gwFuncDAO;
	@Resource
	private GwRoleDAO gwRoleDAO;
	@Resource
	private GwRoleFuncDAO gwRoleFuncDAO;
	@Resource
	private GwServiceDAO gwServiceDAO;
	@Resource
	private GwRoleServiceDAO gwRoleServiceDAO;
	@Resource
	private GwButtonDAO gwButtonDAO;
	@Resource
	private GwRoleButtonDAO gwRoleButtonDAO;
	
	//查询按钮列表并关联群组
	public List<GwButtonView> searchButtonListByRoleCode(String roleCode,String funcCode) throws Exception{
		List<Object[]> list= gwButtonDAO.searchButtonListByRoleCode(roleCode);
		GwButtonView button=null;
		List<GwButtonView> funcButtonList=new ArrayList<GwButtonView>();
		for(Object[] obj : list){
			button=new GwButtonView();
			button.setButtonCode(obj[0]==null?"":String.valueOf(obj[0]));
			button.setFuncCode(obj[1]==null?"":String.valueOf(obj[1]));
			button.setOperateDesc(obj[2]==null?"":String.valueOf(obj[2]));
			button.setRoleCode(obj[3]==null?"":String.valueOf(obj[3]));
			if(funcCode.equals(button.getFuncCode())){
				funcButtonList.add(button);	
			}
		}
		return funcButtonList;
	}
		
	//查询菜单，按钮列表并关联群组
	public List<FuncAndButtonView> searchFuncAndBtnListByRoleCode(String roleCode) throws Exception{
		List<Object[]> list = gwFuncDAO.searchFuncListByRoleCode(roleCode);
		FuncAndButtonView view = null;
		List<FuncAndButtonView> funcButtonList=new ArrayList<FuncAndButtonView>();
		List<GwButtonView> viewList;
		for(Object[] obj : list){
			view = new FuncAndButtonView();
			view.setFuncCode(obj[0]==null?"":String.valueOf(obj[0]));
			view.setFuncName(obj[1]==null?"":String.valueOf(obj[1]));
			view.setParentCode(obj[2]==null?"":String.valueOf(obj[2]));
			view.setRoleCode(obj[3]==null?"":String.valueOf(obj[3]));
			if(!"-1".equals(view.getParentCode())){
				List<GwButtonVO> buttonList=searchButtonListByFuncCode(view.getFuncCode());
				viewList=searchButtonListByRoleCode(roleCode,view.getFuncCode());
				view.setButtonList(viewList);
				view.setParentName(gwFuncDAO.findById(view.getParentCode()).getFuncName());
				funcButtonList.add(view);
			}
		}
		
		return funcButtonList;
	}
	
	
	
	//根据菜单查询对应的按钮列表
	public List<GwButtonVO> searchButtonListByFuncCode(String funcCode)throws Exception{
		return gwButtonDAO.findByFuncCode(funcCode);
	}
	
	//查询服务列表并关联群组
	public List<GwServiceView> searchServiceListByRoleCode(String roleCode,String sort,String asc)throws Exception{
		List<Object[]> list= gwServiceDAO.searchServiceListByRoleCode(roleCode, sort, asc);
		List<GwServiceView> serviceList=new ArrayList<GwServiceView>();
		GwServiceView serviceView;
		for(Object[] obj : list){
			serviceView=new GwServiceView();
			serviceView.setServiceId(obj[0]==null?0:Long.valueOf(obj[0].toString()));
			serviceView.setServiceCode(obj[1]==null?"":String.valueOf(obj[1]));
			serviceView.setServiceName(obj[2]==null?"":String.valueOf(obj[2]));
			serviceView.setModelName(obj[3]==null?"":String.valueOf(obj[3]));
			serviceView.setServiceType(obj[4]==null?"":String.valueOf(obj[4]));
			serviceView.setCycleType(obj[5]==null?"":String.valueOf(obj[5]));
			serviceView.setModelId(obj[6]==null?0:Long.valueOf(obj[6].toString()));
			serviceView.setRoleCode(obj[7]==null?"":String.valueOf(obj[7]));
			serviceView.setCycleDay(obj[8]==null?null:Long.parseLong(String.valueOf(obj[8])));
			
			serviceList.add(serviceView);
		}
		return serviceList;
	}
	
	//群组授权
	public void updateRoleAuth(String roleCode,String chooseFuncs,String chooseButtons,
			String chooseServices)throws Exception{
		updateRoleAuthFunc(roleCode,chooseFuncs);
		updateRoleAuthBtn(roleCode,chooseButtons);
		updateRoleAuthService(roleCode,chooseServices);
	}
	
	//群组菜单授权
	public void updateRoleAuthFunc(String roleCode,String chooseFuncs)throws Exception{
		gwRoleFuncDAO.deleteByRoleCode(roleCode);
		Map<String, String> map = new HashMap<String, String>();
		if(!"".equals(chooseFuncs)){
			GwRoleFuncVO  funcVO;
			String[] funcArray=chooseFuncs.split(",");
			for(int i=0;i<funcArray.length;i++){
				 GwFuncVO vo =gwFuncDAO.findById(funcArray[i]);
				 map.put(vo.getParentCode(), vo.getParentCode());
				 map.put(vo.getFuncCode(), vo.getFuncCode());
			}
			Iterator it=map.keySet().iterator();
		    while(it.hasNext()) {
			     funcVO=new GwRoleFuncVO();
		         String parentCode=(String)it.next();
		         funcVO.setFuncCode(parentCode);
		         funcVO.setRoleCode(roleCode);
		         gwRoleFuncDAO.save(funcVO);
		    }
		}
		
	}
	
	//群组按钮授权
	public void updateRoleAuthBtn(String roleCode,String chooseButtons)throws Exception{
		gwRoleButtonDAO.deleteByRoleCode(roleCode);
		if(!"".equals(chooseButtons)){
			GwRoleButtonVO buttonVO;
			String[] buttonArray=chooseButtons.split(",");
			for(int i=0;i<buttonArray.length;i++){
				buttonVO=new GwRoleButtonVO();
				buttonVO.setButtonCode(buttonArray[i]);
				buttonVO.setRoleCode(roleCode);
				gwRoleButtonDAO.save(buttonVO);
			}
		}
		
	}
	
	//群组服务授权
	public void updateRoleAuthService(String roleCode,String chooseServices)throws Exception{
		gwRoleServiceDAO.deleteByRoleCode(roleCode);
		GwRoleServiceVO  roleServiceVO;
		Map map=new HashMap();
		if(!"".equals(chooseServices)){
			String[] serviceArray=chooseServices.split(",");
			for(int i=0;i<serviceArray.length;i++){
				roleServiceVO=new GwRoleServiceVO();
				roleServiceVO.setServiceId(Long.parseLong(serviceArray[i]));
			    roleServiceVO.setRoleCode(roleCode);
			    gwRoleFuncDAO.save(roleServiceVO);
			}
		}
	}
	
	//查询所有群组分别拥有的菜单按钮
	public List<List<FuncAndButtonView>> searchRoleFuncAndBtnList() throws Exception{
		FuncAndButtonView funcAndButtonView;
		List<GwButtonView> buttonList;
		List<List<FuncAndButtonView>> list=new ArrayList<List<FuncAndButtonView>>();
		List<FuncAndButtonView> funcAndButtonViewList;
		List<GwRoleVO> roleList=gwRoleDAO.findAll();
		for(GwRoleVO roleVO:roleList){
			funcAndButtonViewList=new ArrayList<FuncAndButtonView>();
			List<GwRoleFuncVO> funcList=gwRoleFuncDAO.findByRoleCode(roleVO.getRoleCode());
			for(GwRoleFuncVO roleFuncVO:funcList){
				funcAndButtonView=new FuncAndButtonView();
				funcAndButtonView.setFuncCode(roleFuncVO.getFuncCode());
				funcAndButtonView.setRoleCode(roleFuncVO.getRoleCode());
				buttonList=gwRoleButtonDAO.searchRoleBtnList(roleFuncVO.getRoleCode(), roleFuncVO.getFuncCode());
				funcAndButtonView.setButtonList(buttonList);
				funcAndButtonViewList.add(funcAndButtonView);
			}
			list.add(funcAndButtonViewList);
		}
		return list;
	}
	
	//查询所有群组分别拥有的服务
	public Map<String, List> searchServiceListByAllRole()throws Exception{
		Map<String, List> map=new HashMap<String, List>();
		List list;
		List<GwRoleVO> roleList=gwRoleDAO.findAll();
		for(GwRoleVO roleVO:roleList){
			list=gwRoleServiceDAO.searchServiceListbyRole(roleVO.getRoleCode());
			map.put(roleVO.getRoleCode(), list);
		}
		return map;
	}
	
}