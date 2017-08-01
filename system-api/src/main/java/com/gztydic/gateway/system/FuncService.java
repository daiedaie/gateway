package com.gztydic.gateway.system;

import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 功能菜单管理
 *
 */
public interface FuncService extends GeneralService<GwFuncVO> {

	public Map searchMenuList(GwUserVO userVO) throws Exception;
}
