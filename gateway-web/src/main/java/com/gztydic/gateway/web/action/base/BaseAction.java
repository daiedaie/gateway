package com.gztydic.gateway.web.action.base;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.WorkPlanService;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = 1L;
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	protected PageObject pageObject;
	
	protected File upload;
	protected String uploadFileName;
	protected String imageFileName;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="processServiceImpl")
	private ProcessService processService;	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public HttpSession getSession(){
		return request.getSession();
	}
	
	public GwUserVO getLoginUser(){
		return (GwUserVO)getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
	}
	
	//上传文件
	public GwUploadFileVO upLoadFile(String fileType,String userId) throws Exception{
		GwUploadFileVO  documentsInfoVO=new GwUploadFileVO();
		if(this.getUpload() != null){
			//不同模块用途文件存放不同目录
			String fileContent = "system";
            if("01".equals(fileType)){//用户管理模块
            	fileContent = "userManage";
            }else if("02".equals(fileType)){
            	fileContent = "desenManage";
            }
			int par = uploadFileName.lastIndexOf(".");// 对最后一个“.”结束的文件定位
			String fin = uploadFileName.substring(par);// 截取扩展名
			imageFileName = new Date().getTime() + fin;// 以时间命名
			String filePath = ConfigConstants.BASE_UPLOAD_FILE_PATH+"/uploadFile/"+fileContent+"/";
			
			if(!new File(filePath).exists())
				new File(filePath).mkdirs();
			
			FileUtils.copyFile(upload, new File(filePath + File.separator + imageFileName));
			String savePath = "/uploadFile/"+fileContent+"/"+imageFileName;
			
			documentsInfoVO.setCreateTime(new Date());
			documentsInfoVO.setCreateUser(userId);
			documentsInfoVO.setFileType(fileType);
			documentsInfoVO.setRealName(uploadFileName);
			documentsInfoVO.setFilePath(savePath);
		}
		return documentsInfoVO;
	}
	
	/** 
	 * @Title: setProcessId 
	 * @Description: TODO(根据待办任务表中查询最大流程ID,设置新的流程编码：存在自动加1，否者给个默认值如yyyyMM0001) 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String setProcessId() throws Exception{
		if(pageObject == null) {
			pageObject = new PageObject();
		}
		String maxProcessId="";
		String str = "";
		String bb = "";
		//List<GwWorkPlanVO> workPlanList= workPlanService.findAllByPage(pageObject).getData();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
        String curDate = sdf.format(new Date());  
		//List<GwWorkPlanVO> workPlanList = workPlanService.findByCreateTime(curDate);
		List<GwProcessVO> processList = processService.findByCreateTime(curDate);
		List<Integer> dlist=new ArrayList<Integer>();
		if(processList != null && processList.size()>0){
			for(GwProcessVO gwProcessVO : processList){
				dlist.add(Integer.parseInt(gwProcessVO.getProcessId().toString()));
			}
		}
		if(dlist.size()>0 && dlist != null){
			maxProcessId=Collections.max(dlist).toString();
		}

		if (StringUtils.isNotBlank(maxProcessId)){
			bb = maxProcessId.substring(6,10);//截取后4位
			long cc = new Long(bb) + 1;//转为整数并加1
			if (cc >= 0 && cc < 10) {
				str = "000"+cc;
			}else if (cc >= 10 && cc < 100) {
				str = "00"+cc;
			}else if (cc >= 100 && cc < 1000) {
				str = "0"+cc;
			}else {
				str = ""+cc;
			}
		}else {
			str = "0001";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		String processId = dateFormat.format(new Date()) + str;
		System.out.print("-----2---------processId:"+processId);

		return processId;
	}	
	
	public PageObject getPageObject() {
		return pageObject;
	}
	public void setPageObject(PageObject pageObject) {
		this.pageObject = pageObject;
	}
	public File getUpload() {
		return upload;
	}
	public void setUpload(File upload) {
		this.upload = upload;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

}
