package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwModifyRecordDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.UserView;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

@Service
public class UserServiceImpl extends GeneralServiceImpl<GwUserVO> implements UserService {
	@Resource
	private GwUserDAO gwUserDAO;
	@Resource
	private GwModifyRecordDAO gwModifyRecordDAO;	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService ;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;

	public void save(GwUserVO vo) throws Exception {
		gwUserDAO.save(vo);
	}

	public List findByLoginName(Object loginName) throws Exception {
		return gwUserDAO.findByLoginName(loginName);
	}

	public List searchUserList(GwUserVO user, GwOrgVO org, PageObject pageObject)throws Exception {
		List<Object[]> list = gwUserDAO.searchUserList(user, org, pageObject);	
		List viewList = new ArrayList();
		UserView view=null;
		for (Object[] obj : list) {
			view=new UserView();
			view.setLoginName(obj[0]==null?"":String.valueOf(obj[0]));
			view.setUserName(obj[1]==null?"":String.valueOf(obj[1]));
			view.setConfirmStatus(obj[2]==null?"":String.valueOf(obj[2]));
			view.setUserType(obj[3]==null?"":String.valueOf(obj[3]));
			view.setOrgName(obj[4]==null?"":String.valueOf(obj[4]));
			view.setOnlineStatus(obj[5]==null?"":String.valueOf(obj[5]));
			view.setUserId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setOrgId(obj[7]==null?null:Long.parseLong(String.valueOf(obj[7])));
			viewList.add(view);
		}
		return viewList;
	}

	public GwUserVO searchUserDetail(Long id) throws Exception {
		return gwUserDAO.findById(id);
	}
	
	public void updateUser(GwUserVO user,GwUserVO loginUser) throws Exception{
		GwUserVO dbUserVO = super.search(GwUserVO.class, user.getUserId());
		if(userButtonService.searchAuthorization(WorkPlanConstent.UPDATE_AUDIT, loginUser.getUserType())){	
			//超级管理员、审核用户直接修改、不需要审核
			dbUserVO.setUserName(user.getUserName());
			dbUserVO.setMoblie(user.getMoblie());
			dbUserVO.setCertNo(user.getCertNo());
			dbUserVO.setEmail(user.getEmail());
			dbUserVO.setRemark(user.getRemark());
			dbUserVO.setAddr(user.getAddr());
			if(user.getFileId()!=null) dbUserVO.setFileId(user.getFileId());
			gwUserDAO.update(dbUserVO);
		}else {
			Integer batchId = this.searchSequenceValue(ConfigConstants.SEQ_MODIFY_RECORD_BATCH);
			String extenTableKey ="";
			String recordCode = String.valueOf(user.getUserId());
			//生成待办任务
			String msgContent=SysDictManage.getSysDict("DICT_USER_TYPE", user.getUserType()).getDictValue()+"："+user.getLoginName()+"用户信息修改申请，请审核。【数据网关平台】";
			GwWorkPlanVO workPlanVO = workPlanService.saveWorkPlan("用户修改申请", WorkPlanConstent.UPDATE_AUDIT, user.getLoginName()+"用户修改申请", WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(), null,null,msgContent,null);		 	
			Map<String,String>  paramMap= new HashMap<String, String>();
			paramMap.put("userId", String.valueOf(user.getUserId()));
			paramMap.put("batchId", String.valueOf(batchId));
			workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);
			GwModifyRecordVO modify = null;
			
			if(!eqauls(dbUserVO.getUserName(),user.getUserName())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_name");
				modify.setBeforeValue(dbUserVO.getUserName());
				modify.setAfterValue(user.getUserName());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getMoblie(),user.getMoblie())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_moblie");
				modify.setBeforeValue(dbUserVO.getMoblie());
				modify.setAfterValue(user.getMoblie());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getCertNo(),user.getCertNo())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_certno");
				modify.setBeforeValue(dbUserVO.getCertNo());
				modify.setAfterValue(user.getCertNo());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getEmail(),user.getEmail())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_email");
				modify.setBeforeValue(dbUserVO.getEmail());
				modify.setAfterValue(user.getEmail());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getRemark(),user.getRemark())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_remark");
				modify.setBeforeValue(dbUserVO.getRemark());
				modify.setAfterValue(user.getRemark());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getAddr(),user.getAddr())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_addr");
				modify.setBeforeValue(dbUserVO.getAddr());
				modify.setAfterValue(user.getAddr());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}if(user.getFileId() != null && dbUserVO.getFileId()!=user.getFileId()){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("user_fileid");
				modify.setBeforeValue(dbUserVO.getFileId().toString());
				modify.setAfterValue(user.getFileId().toString());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getPushFtp(),user.getPushFtp())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("push_ftp");
				modify.setBeforeValue(dbUserVO.getPushFtp());
				modify.setAfterValue(user.getPushFtp());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getFtpIp(),user.getFtpIp())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("ftp_ip");
				modify.setBeforeValue(dbUserVO.getFtpIp());
				modify.setAfterValue(user.getFtpIp());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getFtpPort(),user.getFtpPort())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("ftp_port");
				modify.setBeforeValue(dbUserVO.getFtpPort());
				modify.setAfterValue(user.getFtpPort());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getFtpPath(),user.getFtpPath())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("ftp_path");
				modify.setBeforeValue(dbUserVO.getFtpPath());
				modify.setAfterValue(user.getFtpPath());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getFtpUsername(),user.getFtpUsername())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("ftp_username");
				modify.setBeforeValue(dbUserVO.getFtpUsername());
				modify.setAfterValue(user.getFtpUsername());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getFtpPassword(),user.getFtpPassword())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("ftp_password");
				modify.setBeforeValue(dbUserVO.getFtpPassword());
				modify.setAfterValue(user.getFtpPassword());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getWebserviceUrl(),user.getWebserviceUrl())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("webservice_url");
				modify.setBeforeValue(dbUserVO.getWebserviceUrl());
				modify.setAfterValue(user.getWebserviceUrl());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getWebserviceMethod(),user.getWebserviceMethod())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("webservice_method");
				modify.setBeforeValue(dbUserVO.getWebserviceMethod());
				modify.setAfterValue(user.getWebserviceMethod());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
			if(!eqauls(dbUserVO.getBaseWsdl(),user.getBaseWsdl())){
				modify = new GwModifyRecordVO();
				modify.setRecordCode(recordCode);
				modify.setTableCode("gw_user");
				modify.setColumsCode("base_wsdl");
				modify.setBeforeValue(dbUserVO.getBaseWsdl());
				modify.setAfterValue(user.getBaseWsdl());
				modify.setStatus(CommonState.NO_PASS);
				modify.setCreateTime(new Date());
				modify.setCreator(loginUser.getLoginName());
				modify.setModifier(null);
				modify.setModifyTime(null);
				modify.setBatchId(batchId);
				gwModifyRecordDAO.save(modify);
			}
		}
	}
	
	private boolean eqauls(String db,String view){
		if(StringUtils.isBlank(db) && StringUtils.isBlank(view)) return true;
		if(view != null && view.equals(db)) return true;
		if(db != null && db.equals(view)) return true;
		return false;
	}
	
	public List searchUserListByOrg(GwUserVO user, GwOrgVO org, PageObject pageObject,GwUserVO user1)throws Exception {
		List<Object[]> list = gwUserDAO.searchUserListByOrg(user, org, pageObject,user1);	
		List viewList = new ArrayList();
		UserView view=null;
		for (Object[] obj : list) {
			view=new UserView();
			view.setLoginName(obj[0]==null?"":String.valueOf(obj[0]));
			view.setUserName(obj[1]==null?"":String.valueOf(obj[1]));
			view.setConfirmStatus(obj[2]==null?"":String.valueOf(obj[2]));
			view.setUserType(obj[3]==null?"":String.valueOf(obj[3]));
			view.setOrgName(obj[4]==null?"":String.valueOf(obj[4]));
			view.setOnlineStatus(obj[5]==null?"":String.valueOf(obj[5]));
			view.setUserId(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			view.setOrgId(obj[7]==null?null:Long.parseLong(String.valueOf(obj[7])));
			viewList.add(view);
		}
		return viewList;
	} 
	
	/**
	 * 查询机构用户下的所有数据用户
	 * @param orgUserId
	 * @return
	 * @throws Exception
	 */
	public List searchDataUserListByOrg(Long orgUserId)throws Exception {
		return gwUserDAO.findByOrgId(orgUserId, CommonState.VALID, GwUserType.DATA_USER);
	} 


	/**
	 * 申请用户注销，生成待办
	 * @param cancelUserVO
	 * @throws Exception
	 */
	public void applyCancelUser(GwUserVO loginUser,GwUserVO cancelUser) throws Exception{
		String content = "";
		String msgContent="";
		if(GwUserType.ORG_USER.equals(loginUser.getUserType())){
			//机构用户申请注销，待办内容
			content += "机构用户"+loginUser.getLoginName()+"申请注销";
			msgContent+="机构用户:"+loginUser.getLoginName()+"申请注销";
			if(GwUserType.ORG_USER.equals(cancelUser.getUserType())){
				content += "该机构用户及所有数据用户";
				msgContent+="该机构用户及所有数据用户也将注销，请及时审核。【数据网关平台】";
			}else if(GwUserType.DATA_USER.equals(cancelUser.getUserType())){
				content += "该机构下的数据用户"+cancelUser.getLoginName();
				msgContent+="该机构的数据用户"+cancelUser.getLoginName()+"也将注销，请及时审核。【数据网关平台】";
			}
		}
		else
			content += "数据用户"+loginUser.getLoginName()+"申请注销，请及时审核。【数据网关平台】";
		workPlanService.saveWorkPlan("用户注销申请", WorkPlanConstent.CANCEL_AUDIT, content, 
				WorkPlanConstent.WAIT_FOR_DEAL, null, String.valueOf(cancelUser.getUserId()), loginUser.getUserId(), null,null,msgContent,null);
	}
	
	public int cancelUser(GwUserVO loginUser,GwUserVO cancelUser) throws Exception{
		if(GwUserType.ORG_USER.equals(cancelUser.getUserType())){	//机构用户，需要把机构下的所有用户都注销
			if(cancelUser.getOrgId() == null) throw new Exception("注销机构用户失败，用户"+cancelUser.getLoginName()+"的机构编码为空");
			return gwUserDAO.cancelUserByOrg(loginUser,cancelUser);
		}else {
			cancelUser.setConfirmStatus(CommonState.NO_PASS);
			cancelUser.setStatus(CommonState.INVALID);	//其他用户直接修改为注销状态
			cancelUser.setModifyTime(new Date());
			cancelUser.setModifier(loginUser.getLoginName());
			super.saveOrUpdate(cancelUser);
			return 1;
		}
	}
	public void updateUserByAudit(GwUserVO vo)throws Exception{		
		gwUserDAO.update(vo);
	}
	
	//根据用户类型查询值班人员列表
	public int searchOtherOnlineUser(Long userId,String userType)throws Exception{
		return gwUserDAO.searchOtherOnlineUser(userId,userType);
	}
	//根据用户类型查询值班人员列表
	public List<GwUserVO> searchOnlineUser(String userType)throws Exception{
		return gwUserDAO.searchOnlineUser(userType);
	}	
	//查询所有机构用户
	public List<UserView> searchOrgUserList()throws Exception{
		return gwUserDAO.searchOrgUserList();
	}
	
	//查询某个机构下的所有数据用户
	public List<UserView> searchDataUserList(Long orgId)throws Exception{
		return gwUserDAO.searchDataUserList(orgId);
	}
	
	/**
	 * 
	 * @Title: searchOrgUser 
	 * @Description: TODO(根据机构编码和用户类型查询用户) 
	 * @param @param orgId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwUserVO>    返回类型 
	 * @throws
	 */
	public  List<GwUserVO> searchOrgUser(Long orgId)throws Exception{
		 return gwUserDAO.searchOrgUser(orgId);
	}
}
