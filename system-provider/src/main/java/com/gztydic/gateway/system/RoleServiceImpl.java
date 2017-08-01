package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwButtonDAO;
import com.gztydic.gateway.core.dao.GwFuncDAO;
import com.gztydic.gateway.core.dao.GwRoleDAO;
import com.gztydic.gateway.core.dao.GwRoleServiceDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.vo.GwButtonVO;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 群组管理
 * @author liangjiehui
 *
 */
@Service
public class RoleServiceImpl extends GeneralServiceImpl<GwRoleVO> implements RoleService{
  
    @Resource
    private GwRoleDAO gwRoleDao;
    @Resource
	private GwFuncDAO gwFuncDAO;
    @Resource
    private GwRoleServiceDAO gwRoleServiceDAO;
    @Resource
    private GwButtonDAO gwButtonDAO;
    
	public void saveRole(GwRoleVO roleVO,GwUserVO userVO) throws Exception{
		if(roleVO != null){
			roleVO.setRoleCode("group"+super.searchSequenceValue("SEQ_ROLE"));
			roleVO.setStatus(CommonState.VALID);
			roleVO.setCreator(userVO.getLoginName());
			roleVO.setCreateTime(new Date());
			gwRoleDao.save(roleVO);
		}
	}
	
	public void updateRole(GwRoleVO roleVO,GwUserVO userVO) throws Exception{
		if(roleVO != null){
			roleVO.setModifier(userVO.getLoginName());
			roleVO.setModifyTime(new Date());
			gwRoleDao.update(roleVO);
		}
	}
	
	public void deleteRole(String roleCode) throws Exception{
		gwRoleDao.deleteRole(roleCode);
		gwRoleServiceDAO.deleteByRoleCode(roleCode);
	}
	public boolean checkAddRole(GwRoleVO roleVO)throws Exception{
		List roleList=gwRoleDao.findByRoleName(roleVO.getRoleName());
		if(roleList!=null && roleList.size()>0){
			return true;
		}
		return false;
	}
	
	public boolean checkUpdateRole(GwRoleVO roleVO)throws Exception{
		GwRoleVO role=(GwRoleVO)gwRoleDao.findByRoleCode(roleVO.getRoleCode()).get(0);
		if(role.getRoleName().equals(roleVO.getRoleName())){
			return false;
		}else{
			List roleList=gwRoleDao.findByRoleName(roleVO.getRoleName());
			if(roleList!=null && roleList.size()>0)
				return true;
			return false;
		}
	}
	
	public PageObject searchRoleList(GwRoleVO roleVO,PageObject obj)throws Exception{
		return gwRoleDao.searchRoleList(roleVO, obj);
	}

	public List<GwRoleVO> searchRoleByRoleCode(String roleCode) throws Exception {
		return gwRoleDao.findByRoleCode(roleCode);
	}
	
    //查询群组拥有的服务
    public List<GwServiceVO> searchServiceListByRoleCode(String roleCode) throws Exception{
    	return gwRoleServiceDAO.searchServiceListByRoleCode(roleCode);
    }
    
    //查询群组拥有的菜单,按钮
    public List<FuncAndButtonView> searchFuncAndBtnListByRoleCode(String roleCode) throws Exception{
    	List<FuncAndButtonView> funcAndButtonViewList=new ArrayList<FuncAndButtonView>();
    	List<GwFuncVO> funcList=gwFuncDAO.searchFuncListWithRoleCode(roleCode);
    	FuncAndButtonView funcAndButtonView;
    	List<GwButtonVO> buttonList;
    	for(int i=0;i<funcList.size();i++){
    		if(!"-1".equals(funcList.get(i).getParentCode())){
    			funcAndButtonView=new FuncAndButtonView();
        		funcAndButtonView.setFuncCode(funcList.get(i).getFuncCode());
        		funcAndButtonView.setFuncName(funcList.get(i).getFuncName());
        		funcAndButtonView.setFuncDesc(funcList.get(i).getFuncDesc());
        		funcAndButtonView.setParentName(gwFuncDAO.findById(funcList.get(i).getParentCode()).getFuncName());
        		buttonList=gwButtonDAO.searchButtonListByFuncCode(roleCode, funcAndButtonView.getFuncCode());
        		funcAndButtonView.setButtonVOList(buttonList);
        		funcAndButtonViewList.add(funcAndButtonView);
    		}
    		
    	}
    	return funcAndButtonViewList;
    }
}
