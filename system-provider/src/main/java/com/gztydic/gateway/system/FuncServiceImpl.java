package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.GwFuncDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.GwFuncView;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 功能菜单管理
 *
 */
@Service
public class FuncServiceImpl extends GeneralServiceImpl<GwFuncVO> implements FuncService{
	
	@Resource
	private GwFuncDAO gwFuncDAO;
	
	public Map searchMenuList(GwUserVO userVO) throws Exception {
		Map<String, GwFuncView> viewMap = new LinkedHashMap<String, GwFuncView>();
		try {
			List<GwFuncVO> list = new ArrayList<GwFuncVO>();
			//未审核通过的，只能显示用户查看页面
			if(!CommonState.PASS.equals(userVO.getConfirmStatus())){
				list = gwFuncDAO.searchFuncListByNoPass();
			}else {
				list = gwFuncDAO.searchFuncList(userVO.getUserType());
			}
			if (list == null) return null;
			
			for (GwFuncVO vo : list) {
				String parentCode = "-1".equals(vo.getParentCode()) ? vo.getFuncCode() : vo.getParentCode();
				GwFuncView parentView = viewMap.get(parentCode);
				if(parentView == null) parentView = new GwFuncView();
				
				if("-1".equals(vo.getParentCode())){	//父菜单
					PropertyUtils.copyProperties(parentView, vo);
				}else {
					List<GwFuncView> viewList = parentView.getViewList();	//子菜单列表
					if(viewList == null) viewList = new ArrayList<GwFuncView>();
					
					//父菜单关联子菜单
					GwFuncView view = new GwFuncView();
					PropertyUtils.copyProperties(view, vo);
					viewList.add(view);
					parentView.setViewList(viewList);
				}
				viewMap.put(parentCode, parentView);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("查询用户菜单出错",e);
		}
		return viewMap;
	}
}
