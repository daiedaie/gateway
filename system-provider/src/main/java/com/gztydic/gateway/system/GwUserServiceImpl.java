package com.gztydic.gateway.system;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.helpers.FileUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.dao.GwOrgDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: GwUserServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Carson
 * @date 2014-11-19 上午11:43:04 
 *  
 */
@Service
public class GwUserServiceImpl extends GeneralServiceImpl<GwUserVO> implements GwUserService {
	@Resource
	private GwUserDAO gwUserDAO;
	
	@Resource
	private GwOrgDAO gwOrgDAO;
	
	@Resource(name="gwOrgServiceImpl")
	private GwOrgService gwOrgService;
	
	
	
	@Resource(name="localFtpConfigServiceImpl")
	private LocalFtpConfigService localFtpConfigService;

	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	/**
	 * 根据登录帐号获取用户信息
	 */
	public GwUserVO getGwUser(String loginName) throws Exception {
		// TODO Auto-generated method stub 		
		List<GwUserVO> list = gwUserDAO.findByLoginName(loginName);
		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 更新用户登录时间
	 */
	public void updateGwUserByLoginTime(Long userId, Date loginTime) throws Exception {
		// TODO Auto-generated method stub 		
		GwUserVO gwUserVO = gwUserDAO.findById(userId);
		if(gwUserVO != null) {
			gwUserVO.setLoginTim(loginTime);
			gwUserDAO.update(gwUserVO);
		}
	}
	
	/**
	 * 修改用户密码
	 */
	public void updateGwUserByPwd(Long userId, String loginPwd) throws Exception {
		GwUserVO gwUserVO = gwUserDAO.findById(userId);
		if(gwUserVO != null) {
			gwUserVO.setLoginPwd(loginPwd);
			gwUserDAO.update(gwUserVO);
		}
	}
	
	/**
	 * 修改结果文件加密密码
	 */
	public void updatefileEncryPwd(GwUserVO gwUserVO) throws Exception {
		gwUserDAO.update(gwUserVO);
	}
	
	/**
	 * 保存用户信息，支持机构用户保存。
     * @return 
     */
	public void saveGwUser(GwUserVO gwUser, GwOrgVO gwOrg) throws Exception {
		//先保存组织机构信息后保存用户信息
		if(gwOrg != null){
			gwOrgDAO.save(gwOrg);
			gwUser.setOrgId(gwOrg.getOrgId());
		}
		
		gwUserDAO.save(gwUser);		
	}
	
	/**
	 * 检查用户是否重复
	 */
	public boolean checkGwUser(GwUserVO gwUser) throws Exception {
		List list = gwUserDAO.findByLoginName(gwUser.getLoginName());
		
		if(CollectionUtils.isNotEmpty(list)){
			return true;
		}
		
		return false;
	}
	
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
	public List<GwUserVO> searchUserByOrgAndStatus(Long orgId,String status)throws Exception {
		return gwUserDAO.findByOrgId(orgId,status,null);
	}
	
	/** 
	 * @Title: updateConfrimStatus 
	 * @Description: TODO(修改用户审核状态) 
	 * @param @param userId
	 * @param @param status
	 * @param @param operUserCode
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateConfrimStatus(Long userId,String status,String operUserCode)throws Exception{
		GwUserVO userVo = gwUserDAO.findById(userId);
		if(userVo != null){
			userVo.setConfirmStatus(status);
			userVo.setModifyTime(new Date());
			userVo.setModifier(operUserCode);
			userVo.setStatus(CommonState.VALID);
			gwUserDAO.update(userVo);
		}
	}
	
	/** 
	 * @Title: updateStatus 
	 * @Description: TODO(修改用户状态) 
	 * @param @param userId
	 * @param @param status
	 * @param @param operUserCode
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateStatus(Long userId,String status,String operUserCode)throws Exception{
		GwUserVO userVo = gwUserDAO.findById(userId);
		if(userVo != null){
			userVo.setStatus(status);
			userVo.setModifyTime(new Date());
			userVo.setModifier(operUserCode);
			gwUserDAO.update(userVo);
		}
	}
	
	/** 
	 * @Title: updateOnlineStatus 
	 * @Description: TODO(修改用户值班状态) 
	 * @param @param userId
	 * @param @param status
	 * @param @param operUserCode
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateOnlineStatus(Long userId,String status,String operUserCode)throws Exception{
		GwUserVO userVo = gwUserDAO.findById(userId);
		if(userVo != null){
			userVo.setOnlineStatus(status);
			userVo.setModifyTime(new Date());
			userVo.setModifier(operUserCode);
			gwUserDAO.update(userVo);
		}
	}
	
	/**
	 * 更新用户信息，支持机构用户更新。
     * @return 
     */
	public void updateGwUser(GwUserVO gwUser, GwOrgVO gwOrg) throws Exception {
		//先保存组织机构信息后保存用户信息
		if(gwOrg != null){
			gwOrgDAO.update(gwOrg);
		}
		GwUserVO userVo = gwUserDAO.findById(gwUser.getUserId());
		if(userVo != null){
			userVo.setUserName(gwUser.getUserName());
			userVo.setMoblie(gwUser.getMoblie());
			userVo.setCertNo(gwUser.getCertNo());
			userVo.setEmail(gwUser.getEmail());
			userVo.setAddr(gwUser.getAddr());
			userVo.setModifier(gwUser.getLoginName());
			userVo.setFileId(gwUser.getFileId());
			userVo.setOrgId(gwUser.getOrgId());
			userVo.setModifyTime(new Date());
			userVo.setConfirmStatus(CommonState.WAIT_AUDIT);
			gwUserDAO.update(userVo);
		}
	}
	
	public void updateGwUser(GwUserVO gwUser)throws Exception{
		gwUserDAO.update(gwUser);
	}
	
    public GwUserVO searchById(Long userId)throws Exception{
		
		return gwUserDAO.findById(userId);
	}
    
    public AjaxResult createUser(GwUserVO gwUserVo,GwOrgVO gwOrgVo, GwUploadFileVO fileVo) throws Exception{
    	if(GwUserType.ORG_USER.equals(gwUserVo.getUserType())){
			if(gwOrgService.checkGwOrg(gwOrgVo)){
				return AjaxResult.FAILURE(null, "机构信息已经存在");
			}
		}else{//非机构用户清楚机构信息
			gwOrgVo = null;
		}
		
		if(checkGwUser(gwUserVo)){
			return AjaxResult.FAILURE(null, "用户已经存在");
		}
		Endecrypt endecrypt = new Endecrypt();
		String enPassword = endecrypt.get3DESEncrypt(gwUserVo.getLoginPwd(), SessionConstant.SPKEY_PASSWORD);
		//String filePassword = endecrypt.get3DESEncrypt(gwUserVo.getFileEncryPwd(), SessionConstant.SPKEY_PASSWORD);
		gwUserVo.setFileId(fileVo.getFileId());//附件ID
		gwUserVo.setLoginPwd(enPassword);
		gwUserVo.setFileEncryPwd(gwUserVo.getFileEncryPwd());
		//gwUserVo.setOnlineStatus(onlineStatus);//值班状态设置非值班
		gwUserVo.setConfirmStatus(CommonState.WAIT_AUDIT);
		gwUserVo.setStatus(CommonState.VALID);
		gwUserVo.setCreateTime(new Date());
		gwUserVo.setCreator(gwUserVo.getLoginName());
		
		saveGwUser(gwUserVo, gwOrgVo);
		
		//生成注册审核待办
		String userType = GwUserType.DATA_USER.equals(gwUserVo.getUserType())?"数据":GwUserType.ORG_USER.equals(gwUserVo.getUserType())?"机构":"";
		String planContent = "注册"+userType+"用户："+gwUserVo.getLoginName()+"注册申请，请及时审核。";
		//待办关联表主键
		String extenTableKey = gwUserVo.getUserId().toString();
		String msgContent=userType+"用户："+gwUserVo.getLoginName()+"注册申请，请及时审核。【数据网关平台】";
		workPlanService.saveWorkPlan("用户注册审核",WorkPlanConstent.REGISTE_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, gwUserVo.getUserId(),null,null,msgContent,null);
		//写操作日志
		operationLogService.saveOperationLog(gwUserVo.getLoginName(), gwUserVo.getLoginName(), OperateTypeConstent.REGIST_USER, gwUserVo.getLoginName()+"用户注册！");
		return AjaxResult.SUCCESS();
    }
    
    public AjaxResult reSignUpUser(GwUserVO gwUserVo,GwOrgVO gwOrgVo, String planId) throws Exception{
    	if(GwUserType.ORG_USER.equals(gwUserVo.getUserType())){
			if(gwOrgService.checkReSignUpOrg(gwOrgVo)){
				return AjaxResult.FAILURE(null, "机构信息已经存在");
			}
		}else{//非机构用户清楚机构信息
			gwOrgVo = null;
		}
		//修改待办状态
		Long workPlanId = planId == null?null:Long.valueOf(planId);
		workPlanService.updateWorkPlanState(workPlanId, null,WorkPlanConstent.DEAL_PASS,null);
		
		//用户信息由于未通过审核可以直接覆盖
		updateGwUser(gwUserVo, gwOrgVo);
		
		//生成注册审核待办
		String userType = GwUserType.DATA_USER.equals(gwUserVo.getUserType())?"数据":GwUserType.ORG_USER.equals(gwUserVo.getUserType())?"机构":"";
		String planContent = userType+"用户："+gwUserVo.getLoginName()+"重新注册申请，请及时审核。";
		//待办关联表主键
		String extenTableKey = gwUserVo.getUserId().toString();
		String msgContent=userType+"："+gwUserVo.getLoginName()+"注册申请，请及时审核。【数据网关平台】";
		workPlanService.saveWorkPlan("用户重新注册审核",WorkPlanConstent.REGISTE_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, gwUserVo.getUserId(),null,workPlanId,msgContent,null);
		
		return AjaxResult.SUCCESS();
    }
}
