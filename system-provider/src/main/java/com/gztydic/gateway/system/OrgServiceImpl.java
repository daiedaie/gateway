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
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwModifyRecordDAO;
import com.gztydic.gateway.core.dao.GwOrgDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.OrgView;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

@Service
public class OrgServiceImpl  extends GeneralServiceImpl<GwOrgVO> implements OrgService{
	
	@Resource
	private GwOrgDAO gwOrgDAO;
	@Resource
	private GwUserDAO gwUserDAO;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService ;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService ;
	@Resource
	private GwModifyRecordDAO gwModifyRecordDAO;
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	
	public PageObject searchOrgList(GwOrgVO vo, PageObject pageObject)
			throws Exception {
		GwUserVO userVO=new GwUserVO();
		userVO.setUserType(GwUserType.ORG_USER);
		pageObject=gwOrgDAO.searchOrgList(vo, pageObject,userVO);
		List<Object[]> list = pageObject.getData();
		List viewList = new ArrayList();
		OrgView view=null;
		for (Object[] obj : list) {
			view=new OrgView();
			view.setOrgId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			view.setOrgName(obj[1]==null?"":String.valueOf(obj[1]));
			view.setOrgHeadName(obj[2]==null?"":String.valueOf(obj[2]));
			view.setCertType(obj[3]==null?"":String.valueOf(obj[3]));
			view.setCertNo(obj[4]==null?"":String.valueOf(obj[4]));
			view.setUserId(obj[5]==null?null:Long.parseLong(String.valueOf(obj[5])));
			view.setLoginName(obj[6]==null?"":String.valueOf(obj[6]));
			view.setConfirmStatus(obj[7]==null?"":String.valueOf(obj[7]));
			viewList.add(view);
		}
		pageObject.setData(viewList);
		return pageObject;
	}

	public GwOrgVO searchOrg(Long orgId)throws Exception{
		return gwOrgDAO.findById(orgId);
	}
	
	public GwUserVO searchOrgUser(Long orgId)throws Exception{
		return (GwUserVO)gwUserDAO.searchOrgUser(orgId).get(0);
	}
	
	public List<GwUserVO> searchDataUser(Long orgId)throws Exception{
		return gwUserDAO.searchDataUser(orgId);
	}
	
	public void updateOrgAndUser(GwUserVO userVO,GwOrgVO orgVO,GwUserVO loginUser)throws Exception{
		GwUserVO dbUserVO = gwUserDAO.findById(userVO.getUserId());
		GwOrgVO dbOrgVO=gwOrgDAO.findById(orgVO.getOrgId());
		if(userButtonService.searchAuthorization(WorkPlanConstent.UPDATE_AUDIT, loginUser.getUserType())){	
			//超级管理员、审核用户直接修改、不需要审核
			dbUserVO.setUserName(userVO.getUserName());
			dbUserVO.setMoblie(userVO.getMoblie());
			dbUserVO.setCertNo(userVO.getCertNo());
			dbUserVO.setEmail(userVO.getEmail());
			dbUserVO.setAddr(userVO.getAddr());
			if(userVO.getFileId()!=null) dbUserVO.setFileId(userVO.getFileId());
			dbOrgVO.setOrgName(orgVO.getOrgName());
			dbOrgVO.setCertType(orgVO.getCertType());
			dbOrgVO.setCertNo(orgVO.getCertNo());
			dbOrgVO.setOrgHeadName(orgVO.getOrgHeadName());
			dbOrgVO.setOrgTel(orgVO.getOrgTel());
			dbOrgVO.setRegCode(orgVO.getRegCode());
			dbOrgVO.setOrgAddr(orgVO.getOrgAddr());
			updateOrg(dbOrgVO, loginUser);
			updateOrgUser(dbUserVO, loginUser);
		}else {
			Integer batchId = this.searchSequenceValue(ConfigConstants.SEQ_MODIFY_RECORD_BATCH);
			String extenTableKey =userVO.getUserId()+","+batchId.toString();
			String recordCode = String.valueOf(userVO.getUserId());
			String content = "机构用户："+userVO.getLoginName()+"修改申请，请及时审核。";
			String msgContent="机构用户："+userVO.getLoginName()+"用户信息修改申请，请审核。【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("机构用户修改", WorkPlanConstent.UPDATE_AUDIT, content, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(), null,null,msgContent,null);
			Map map=new HashMap();
			map.put("userId",userVO.getUserId().toString());
			map.put("batchId", batchId.toString());
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
			GwModifyRecordVO modifyOrg = null;
			GwModifyRecordVO modifyOrgUser = null;
			if(!equals(dbUserVO.getUserName(),userVO.getUserName())){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_name");
				modifyOrgUser.setBeforeValue(dbUserVO.getUserName());
				modifyOrgUser.setAfterValue(userVO.getUserName());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(!equals(dbUserVO.getMoblie(),userVO.getMoblie())){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_moblie");
				modifyOrgUser.setBeforeValue(dbUserVO.getMoblie());
				modifyOrgUser.setAfterValue(userVO.getMoblie());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(!equals(dbUserVO.getCertNo(),userVO.getCertNo())){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_certno");
				modifyOrgUser.setBeforeValue(dbUserVO.getCertNo());
				modifyOrgUser.setAfterValue(userVO.getCertNo());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(!equals(dbUserVO.getEmail(),userVO.getEmail())){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_email");
				modifyOrgUser.setBeforeValue(dbUserVO.getEmail());
				modifyOrgUser.setAfterValue(userVO.getEmail());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(!equals(dbUserVO.getAddr(),userVO.getAddr())){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_addr");
				modifyOrgUser.setBeforeValue(dbUserVO.getAddr());
				modifyOrgUser.setAfterValue(userVO.getAddr());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(userVO.getFileId() != null && dbUserVO.getFileId()!=userVO.getFileId()){
				modifyOrgUser=new GwModifyRecordVO();
				modifyOrgUser.setRecordCode(recordCode);
				modifyOrgUser.setTableCode("gw_user");
				modifyOrgUser.setColumsCode("user_fileid");
				modifyOrgUser.setBeforeValue(dbUserVO.getFileId().toString());
				modifyOrgUser.setAfterValue(userVO.getFileId().toString());
				modifyOrgUser.setStatus(CommonState.NO_PASS);
				modifyOrgUser.setCreateTime(new Date());
				modifyOrgUser.setCreator(loginUser.getLoginName());
				modifyOrgUser.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrgUser);
			}
			if(!equals(dbOrgVO.getOrgName(),orgVO.getOrgName())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("org_name");
				modifyOrg.setBeforeValue(dbOrgVO.getOrgName());
				modifyOrg.setAfterValue(orgVO.getOrgName());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getCertType(),orgVO.getCertType())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("cert_type");
				modifyOrg.setBeforeValue(dbOrgVO.getCertType());
				modifyOrg.setAfterValue(orgVO.getCertType());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getCertNo(),orgVO.getCertNo())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("cert_no");
				modifyOrg.setBeforeValue(dbOrgVO.getCertNo());
				modifyOrg.setAfterValue(orgVO.getCertNo());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getOrgHeadName(),orgVO.getOrgHeadName())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("org_head_name");
				modifyOrg.setBeforeValue(dbOrgVO.getOrgHeadName());
				modifyOrg.setAfterValue(orgVO.getOrgHeadName());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getOrgTel(),orgVO.getOrgTel())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("org_tel");
				modifyOrg.setBeforeValue(dbOrgVO.getOrgTel());
				modifyOrg.setAfterValue(orgVO.getOrgTel());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getRegCode(),orgVO.getRegCode())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("reg_code");
				modifyOrg.setBeforeValue(dbOrgVO.getRegCode());
				modifyOrg.setAfterValue(orgVO.getRegCode());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
			if(!equals(dbOrgVO.getOrgAddr(),orgVO.getOrgAddr())){
				modifyOrg=new GwModifyRecordVO();
				modifyOrg.setRecordCode(recordCode);
				modifyOrg.setTableCode("gw_org");
				modifyOrg.setColumsCode("org_addr");
				modifyOrg.setBeforeValue(dbOrgVO.getOrgAddr());
				modifyOrg.setAfterValue(orgVO.getOrgAddr());
				modifyOrg.setStatus(CommonState.NO_PASS);
				modifyOrg.setCreateTime(new Date());
				modifyOrg.setCreator(loginUser.getLoginName());
				modifyOrg.setBatchId(batchId);
				gwModifyRecordDAO.save(modifyOrg);
			}
		}
	}
	
	private boolean equals(String str1,String str2){
		if(StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) return true;
		if(str2.equals(str1)) return true;
		return false;
	}
	
	public void updateOrg(GwOrgVO orgVO,GwUserVO loginUser)throws Exception{
		if(orgVO!=null){
			orgVO.setModifier(loginUser.getLoginName());
			orgVO.setModifyTime(new Date());
			gwOrgDAO.update(orgVO);
		}
	}
	
	public void updateOrgUser(GwUserVO userVO,GwUserVO loginUser)throws Exception{
		if(userVO!=null){
			userVO.setModifier(loginUser.getLoginName());
			userVO.setModifyTime(new Date());
			gwUserDAO.update(userVO);
		}
	}
	
	public boolean checkUpdateOrg(GwOrgVO orgVO)throws Exception{
		GwOrgVO org=(GwOrgVO)gwOrgDAO.findById(orgVO.getOrgId());
		if(org.getOrgName().equals(orgVO.getOrgName())){
			return false;
		}else{
			List orgList=gwOrgDAO.findByOrgName(orgVO.getOrgName());
			if(orgList!=null && orgList.size()>0)
				return true;
			return false;
		}
	}

}
