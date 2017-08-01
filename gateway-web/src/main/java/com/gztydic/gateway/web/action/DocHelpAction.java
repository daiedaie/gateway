package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.vo.GwDocHelpVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.DocHelpService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class DocHelpAction extends BaseAction{
	
	private static final Log log = LogFactory.getLog(DocHelpAction.class);
	
	private static final long serialVersionUID = 1L;
	
	private GwDocHelpVO doc;

	@Resource(name="docHelpServiceImpl")
	private DocHelpService docHelpService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	
	public String searchDoc() throws Exception{
		doc = docHelpService.searchDocHelpVO(doc.getDocId());
		return "doc";
	}
	
	public String searchDocList() throws Exception{
		if(pageObject == null) {
			pageObject = new PageObject();
		}
		pageObject = docHelpService.searchDocHelpList(doc, pageObject);
		return "docList";
	}
	
	public void saveDoc() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUserVO = getLoginUser();
			String preFileId = request.getParameter("preFileId");
			GwUploadFileVO fileVO = upLoadFile("03", String.valueOf(loginUserVO.getUserId()));
			if(fileVO.getFilePath()==null){
				fileVO = uploadFileService.findById(Long.valueOf(preFileId));
			}
			docHelpService.saveDocHelp(doc, fileVO, loginUserVO);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			log.error("保存帮助文档错误:"+e.getMessage());
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(保存帮助文档),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "保存帮助文档错误："+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "保存帮助文档错误 ");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String editDoc() throws Exception{
		doc = docHelpService.searchDocHelpVO(doc.getDocId());
		return "updateDoc";
	}
	
	public void deleteDoc() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			docHelpService.delete(doc);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除帮助文档),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "删除帮助文档错误："+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "删除帮助文档");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			log.error("删除帮助文档错误:"+e.getMessage());
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	public GwDocHelpVO getDoc() {
		return doc;
	}

	public void setDoc(GwDocHelpVO doc) {
		this.doc = doc;
	}
}
