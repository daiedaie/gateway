package com.gztydic.gateway.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.common.util.ShellUtil;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.FuncService;
import com.gztydic.gateway.system.GwOrgService;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.LocalFtpConfigService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserButtonService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class GwUserAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(GwUserAction.class);
	private GwSysFtpVo ftpVo;


	private GwUserVO gwUserVo;
	private GwOrgVO gwOrgVo;
	
	private String userName;
	private String password;
	private String code;
	private String message;
	
	//修改密码参数
	private String oldPwd;
	private String newPwd;
	private String reNewPwd;
	private String needFilePwd;
	@Resource(name="localFtpConfigServiceImpl")
	private LocalFtpConfigService localFtpConfigService;
	@Resource(name="gwUserServiceImpl")
	private GwUserService gwUserService;

	@Resource(name="gwOrgServiceImpl")
	private GwOrgService gwOrgService;
	
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	
	@Resource(name="funcServiceImpl")
	private FuncService funcService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	//增加用户
	public void addGwUser() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUploadFileVO fileVo = this.upLoadFile("01",gwUserVo.getLoginName());
			uploadFileService.save(fileVo);//保存附件
			if(null!=gwUserVo.getFtpType()){
				if (gwUserVo.getFtpType().equals("2")){//使用gateway服务器
					List<GwSysFtpVo> GwSysFtpVoList =localFtpConfigService.searchFtpDetail(gwUserVo.getFtpType());
					gwUserVo.setFtpIp(GwSysFtpVoList.get(0).getFtpIp());
					gwUserVo.setFtpPort(GwSysFtpVoList.get(0).getFtpPort());
					gwUserVo.setFtpUsername(gwUserVo.getLoginName()+"_down");
					gwUserVo.setFtpPassword(gwUserVo.getLoginPwd()+"_down");
				}
			}
			ajaxResult = gwUserService.createUser(gwUserVo, gwOrgVo, fileVo);
		} catch (Exception e) {
			log.error("用户注册失败"+e.getMessage(),e);
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户新增),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户新增时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户新增");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "保存失败");
		}
		AppHelper.writeOut(ajaxResult,AppHelper.CONTENT_TYPE_HTML, response);
	}
	
	//重新注册用户
    public void reSignUpUser() throws Exception{
    	AjaxResult ajaxResult = null;
    	try {
    		String planId = request.getParameter("planId");
        	
        	//附件处理
    		if(gwUserVo.getFileId() == null){
        		GwUploadFileVO fileVo = this.upLoadFile("01",gwUserVo.getLoginName());
        		uploadFileService.save(fileVo);//保存附件
        		gwUserVo.setFileId(fileVo.getFileId());
        	}
    		ajaxResult = gwUserService.reSignUpUser(gwUserVo, gwOrgVo, planId);
		} catch (Exception e) {
			log.error("用户重新注册失败"+e.getMessage(),e);
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(重新注册用户),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "重新注册用户时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：重新注册用户时发生错误！原因"+e.getMessage()+"【数据服务网关】",null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "重新注册用户");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "保存失败");
		}
		AppHelper.writeOut(ajaxResult,AppHelper.CONTENT_TYPE_HTML, response);
    }
	
	//获取用户
	public void getGwUser() throws Exception{
		message = null;
		AjaxResult ajaxResult = null;
		try {
			String sessionCode = (String)request.getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_CODE_INFO);
			
			if(StringUtils.isEquals(code.toUpperCase(), sessionCode)){
				gwUserVo = gwUserService.getGwUser(userName);
				
				if(gwUserVo != null ){
					if(!CommonState.VALID.equals(gwUserVo.getStatus())){
						message = "错误：用户已注销，请联系管理员处理。";	
					}else{
						//需要增加用户有效处理
						Endecrypt endecrypt = new Endecrypt();
						
						String enPassword = endecrypt.get3DESEncrypt(password, SessionConstant.SPKEY_PASSWORD);
		
						if(!StringUtils.isEquals(enPassword, gwUserVo.getLoginPwd())){
							message = "错误：密码不正确。";
						}else{
							gwUserService.updateGwUserByLoginTime(gwUserVo.getUserId(), new Date());
						}
					}
				}else{
					message = "错误：用户不存在。";
				}
			}else{
				message = "错误：验证码不正确。";			
			}
		
			if(message == null){
				message = "登录成功";
				getSession().setAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO, gwUserVo);
				ajaxResult = AjaxResult.SUCCESS(null, message);
				
				initSystemData();
			}else{
				ajaxResult = AjaxResult.FAILURE(null, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除帮助文档),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "获取用户时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户查询");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.FAILURE(null,"系统异常，登录失败。原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//初始化系统数据
	private void initSystemData() throws Exception{
		//设置用户功能按钮操作权限
		Map buttonMap = userButtonService.findByPropertyToMap(gwUserVo);
		getSession().setAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_BUTTON, buttonMap);
		
		Map parentFuncMap = funcService.searchMenuList(getLoginUser());
		getSession().setAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_FUNC, parentFuncMap);
	}

	/**
	 * 注册页面
	 * @Title: singupView 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public String register() throws Exception {
		
		List<GwOrgVO> gwOrgs = gwOrgService.getGwOrgListByStatus();
		/*List<GwOrgVO> gwOrgs = gwOrgService.getGwOrgList();*/
		if(ftpVo != null){				
			ftpVo.setFtpType("2");
		}else{
			ftpVo = new GwSysFtpVo();
			ftpVo.setFtpType("2");
		}
		List<GwSysFtpVo> GwSysFtpVoList =localFtpConfigService.searchFtpDetail(ftpVo.getFtpType());
		if (GwSysFtpVoList.size()>0){
			for (GwSysFtpVo vo:GwSysFtpVoList){
				vo.setId(GwSysFtpVoList.get(0).getId());
				vo.setFtpIp(GwSysFtpVoList.get(0).getFtpIp());
				vo.setFtpPort(GwSysFtpVoList.get(0).getFtpPort());
				vo.setFtpType(GwSysFtpVoList.get(0).getFtpType());
				request.setAttribute("GwSysFtpVo", vo);
			}
			
		}
		
		request.setAttribute("gwOrgs", gwOrgs);
		
		return SUCCESS;
	}
	

	/**
	 * 修改密码
	 * @Title: modifyGwUserByPwd 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public void updateGwUserByPwd() throws Exception {
		message = null;
		AjaxResult ajaxResult = null;
		
		GwUserVO gwUser = (GwUserVO)getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
		
		
		if(!StringUtils.isEquals(newPwd, reNewPwd)){
			message = "错误：新密码两次输入不一致";
		}else{
		
			Endecrypt endecrypt = new Endecrypt();
			String enOldPwd = endecrypt.get3DESEncrypt(oldPwd, SessionConstant.SPKEY_PASSWORD);
			
			if(StringUtils.isEquals(enOldPwd, gwUser.getLoginPwd())){
				String enNewPwd = endecrypt.get3DESEncrypt(newPwd, SessionConstant.SPKEY_PASSWORD);
				gwUserService.updateGwUserByPwd(gwUser.getUserId(), enNewPwd);
				if(GwUserType.DATA_USER.equals(gwUser.getUserType())){
					//修改ftp虚拟用户密码
					String command = ConfigConstants.VUSER_FTP_UPDATE_SHELL + " "+gwUser.getLoginName() +" "+newPwd;
					ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
					/* 数据用户创建时已取消创建账号，所以取消修改密码
					//修改密码
					String command = ConfigConstants.FTP_CHANGE_PASSWD_SHELL + " "+gwUser.getLoginName() +" "+newPwd;
					ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
					*/
				}
			}else{
				message = "错误：原始密码不匹配";
			}
		}
		
		if(message == null){
			message = "密码修改成功";
			ajaxResult = AjaxResult.SUCCESS(null, message);
		}else{
			ajaxResult = AjaxResult.FAILURE(null, message);
		}
		
		AppHelper.writeOut(ajaxResult, response);
		
	}
	
	/**
	 * 修改结果文件加密密码
	 * @throws Exception
	 */
	public void updatefileEncryPwd() throws Exception {
		message = null;
		AjaxResult ajaxResult = null;
		
		GwUserVO gwUser = (GwUserVO)getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
		gwUser = gwUserService.searchById(gwUser.getUserId());
		if("1".equals(needFilePwd)){
			if(null==gwUser.getFileEncryPwd() || "".equals(gwUser.getFileEncryPwd())){
				gwUser.setNeedFilePwd(needFilePwd);
				gwUser.setFileEncryPwd(newPwd);
				gwUserService.updatefileEncryPwd(gwUser);
			}else{
				if(!StringUtils.isEquals(newPwd, reNewPwd)){
					message = "错误：新密码两次输入不一致";
				}else{
					if(StringUtils.isEquals(oldPwd, gwUser.getFileEncryPwd())){
						gwUser.setNeedFilePwd(needFilePwd);
						gwUser.setFileEncryPwd(newPwd);
						gwUserService.updatefileEncryPwd(gwUser);
					}else{
						message = "错误：原始密码不匹配";
					}
				}
			}
		}else{
			if(null==gwUser.getFileEncryPwd() || "".equals(gwUser.getFileEncryPwd())){
				gwUser.setNeedFilePwd(needFilePwd);
				gwUser.setFileEncryPwd(newPwd);
				gwUserService.updatefileEncryPwd(gwUser);
			}else{
				if(StringUtils.isEquals(oldPwd, gwUser.getFileEncryPwd())){
					gwUser.setNeedFilePwd(needFilePwd);
					gwUser.setFileEncryPwd(newPwd);
					gwUserService.updatefileEncryPwd(gwUser);
				}else{
					message = "错误：原始密码不匹配";
				}
			}
		}
		
		
		if(message == null){
			message = "文件加密密码修改成功";
			ajaxResult = AjaxResult.SUCCESS(null, message);
		}else{
			ajaxResult = AjaxResult.FAILURE(null, message);
		}
		
		AppHelper.writeOut(ajaxResult, response);
		
	}
	
	
	public String searchUserFileEncryPwd() throws Exception{
		try{
			GwUserVO gwUser =gwUserService.searchById(getLoginUser().getUserId());
			request.setAttribute("gwUser", gwUser);
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询用户文件密码),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询用户文件密码");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "fileEncryPwd";
		
	}
	
	/**
	 * 退出登录
	 * @Title: logout 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public String logout() throws Exception {
		request.getSession().removeAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
		request.getSession().invalidate();
		
		return SUCCESS;
	}
	
	//push ftp验证 
	public void ftpCheck() throws Exception{
		String ftpIp = request.getParameter("ftpIp");
		String ftpUsername = request.getParameter("ftpUsername");
		String ftpPassword = request.getParameter("ftpPassword");
		AjaxResult ajaxResult = null;
		try {
			ajaxResult =ftpCheckCom(ftpIp, ftpUsername, ftpPassword);
		} catch (Exception e) {
			log.error("ftp 测试不通过:"+e.getMessage(),e);
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(push ftp验证),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "push ftp验证");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "ftp 测试不通过");
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	private AjaxResult ftpCheckCom(String ftpIp, String ftpUsername,String ftpPassword)throws Exception{
		AjaxResult ajaxResult = null;
		String command =ConfigConstants.FTP_CHECK_SHELL +" "+ftpIp+" \""+ftpUsername+"\" \""+ftpPassword+"\"";
		
		log.info("command："+command);
		String result = ShellUtil.execCmd(command,ConfigConstants.FTP_SERVER_USER,ConfigConstants.FTP_SERVER_PASSWORD,ConfigConstants.FTP_SERVER_IP);
//		String result = ShellUtil.execCmd(command,"gateway","123456","10.1.24.134");
		log.info(result);
		if(result != null){
			if("Not connected.".equals(result)){
				return AjaxResult.ERROR(null, "ftp 地址不可到达");
			}else if("Login failed.".equals(result) || "Login incorrect.".equals(result)){
				return AjaxResult.ERROR(null, "ftp 用户或密码错误");
			}else{
				return AjaxResult.ERROR(null, "ftp 测试不通过");
			}
		}else{
			return AjaxResult.SUCCESS(null, "ftp 测试通过");
		}
	}
	
	
	public void setGwOrgVo(GwOrgVO gwOrgVo) {
		this.gwOrgVo = gwOrgVo;
	}

	public GwUserVO getGwUserVo() {
		return gwUserVo;
	}

	public GwOrgVO getGwOrgVo() {
		return gwOrgVo;
	}

	public void setGwUserVo(GwUserVO gwUserVo) {
		this.gwUserVo = gwUserVo;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getReNewPwd() {
		return reNewPwd;
	}

	public void setReNewPwd(String reNewPwd) {
		this.reNewPwd = reNewPwd;
	}

	public String getNeedFilePwd() {
		return needFilePwd;
	}

	public void setNeedFilePwd(String needFilePwd) {
		this.needFilePwd = needFilePwd;
	}
	public GwSysFtpVo getFtpVo() {
		return ftpVo;
	}

	public void setFtpVo(GwSysFtpVo ftpVo) {
		this.ftpVo = ftpVo;
	}
	

}
