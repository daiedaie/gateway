package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwOrgVO;

/** 
 * @ClassName: GwOrgService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Carson
 * @date 2014-11-19 上午11:37:12 
 *  
 */
public interface GwOrgService extends GeneralService <GwOrgVO>{
	/**
	 * 检查机构是否重复
	 */
	public boolean checkGwOrg(GwOrgVO gwOrg) throws Exception;
	
	/**
	 * 获取机构
	 */
	public List<GwOrgVO> getGwOrgList() throws Exception;
	
	/**
	 * 获取有效机构用户
	 */
	public List<GwOrgVO> getGwOrgListByStatus() throws Exception;
	
	/**
     * 检查重新注册的机构名称是否重复
     */
    public boolean checkReSignUpOrg(GwOrgVO gwOrg) throws Exception;
}
