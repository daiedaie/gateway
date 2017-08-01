package com.gztydic.gateway.system;

import java.util.Date;
import java.util.List;

import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: GwUserService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Carson
 * @date 2014-11-19 上午11:37:12 
 *  
 */
public interface GwUserService extends GeneralService <GwUserVO>{
	/**
	 * 根据登录帐号获取用户信息
	 */
	public GwUserVO getGwUser(String loginName) throws Exception;
	
	/**
	 * 更新用户登录时间
	 */
	public void updateGwUserByLoginTime(Long userId, Date loginTime) throws Exception;
	
	/**
	 * 修改用户密码
	 */
	public void updateGwUserByPwd(Long userId, String loginPwd) throws Exception;
	
	/**
	 * 修改结果文件加密密码
	 */
	public void updatefileEncryPwd(GwUserVO gwUserVO) throws Exception;
	
	/**
	 * 保存用户信息，支持机构用户保存。
     * @return 
     */
	public void saveGwUser(GwUserVO gwUser, GwOrgVO gwOrg) throws Exception;
	
	/**
	 * 检查用户是否重复
	 */
	public boolean checkGwUser(GwUserVO gwUser) throws Exception;
	
	/** 
	 * @Title: searchUserByOrgAndStatus 
	 * @Description: TODO(根据组织ID和用户状态查询用户列表) 
	 * @param @param orgId
	 * @param @param status
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwUserVO>    返回类型 
	 * @throws 
	 */
	public List<GwUserVO> searchUserByOrgAndStatus(Long orgId,String status)throws Exception;
	
	//修改用户审核状态
	public void updateConfrimStatus(Long userId,String status,String operUserCode)throws Exception;
	
	//修改用户状态
	public void updateStatus(Long userId,String status,String operUserCode)throws Exception;
	
	//修改用户值班状态
	public void updateOnlineStatus(Long userId,String status,String operUserCode)throws Exception;
	
	/**
	 * 更新用户信息，支持机构用户更新。
     * @return 
     */
	public void updateGwUser(GwUserVO gwUser, GwOrgVO gwOrg) throws Exception;
	
	public void updateGwUser(GwUserVO gwUser)throws Exception;
	
	public GwUserVO searchById(Long userId)throws Exception;
	
	public AjaxResult createUser(GwUserVO gwUserVo,GwOrgVO gwOrgVo,GwUploadFileVO fileVo) throws Exception;
	
	public AjaxResult reSignUpUser(GwUserVO gwUserVo,GwOrgVO gwOrgVo,String planId) throws Exception;
}
