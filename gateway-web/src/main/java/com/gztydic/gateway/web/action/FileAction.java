package com.gztydic.gateway.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.model.ModelDataTaskService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class FileAction extends BaseAction{
	
	private static Log logger = LogFactory.getLog(FileAction.class);
	
	//系统上传的文件下载
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	
	@Resource(name="modelDataTaskServiceImpl")
	private ModelDataTaskService modelDataTaskService;
	
	@Resource
	private GwServiceFieldDAO serviceFieldDAO;
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public void downLoadFile()throws Exception{
		String fileId=request.getParameter("fileId");
		GwUploadFileVO fileVo  = uploadFileService.findById(Long.parseLong(fileId));
		//关于文件下载时采用文件流输出的方式处理：
		response.reset();//可以加也可以不加
		response.setContentType("application/x-download");
		//application.getRealPath("/main/mvplayer/CapSetup.msi");获取的物理路径
		String filePath = fileVo.getFilePath();
		//String filenamedownload = application.getRealPath(System.getProperty("FileRoot.Path")+filePath);//即将下载的文件的相对路径 
		String filenamedownload = ConfigConstants.BASE_UPLOAD_FILE_PATH+filePath;//绝对路径
		//根据不同浏览器区分解决乱码
		String filenamedisplay;
		String agent=request.getHeader("USER-AGENT");
		if(null != agent && -1 != agent.indexOf("Firefox")){//Firefox
			filenamedisplay = new String(fileVo.getRealName().getBytes("UTF-8"),"iso-8859-1");
		}else{//其他
			filenamedisplay = URLEncoder.encode(fileVo.getRealName(), "UTF-8");
		}
		response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);
		java.io.OutputStream out = null;
		java.io.FileInputStream in = null;
		try{
			out = response.getOutputStream();
			in = new FileInputStream(filenamedownload);

			byte[] b = new byte[1024];
			int i = 0;

			while((i = in.read(b)) > 0){
				out.write(b, 0, i);
			}
			out.flush(); 
		}catch(Exception e){
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(文件下载),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "文件下载时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "文件下载");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw new Exception("文件下载出错："+e.getMessage());
		}
		finally{
			if(in != null){
				in.close();
				in = null;
			}
		}
	}
	
	//导出合规检查中的不合规数据
	public void downloadCheckResult() throws Exception{
		try {
			String taskId = request.getParameter("taskId");
			String exportNum = request.getParameter("exportNum");
			
			GwModelDataFetchTaskVO taskVO=modelDataTaskService.searchById(Long.parseLong(taskId));
			GwServiceVO serviceVO=gwServiceService.searchService(taskVO.getServiceId());
			HSSFWorkbook wb = modelDataTaskService.exportCheckResult(taskId,exportNum);
			String fileName = serviceVO.getServiceCode()+"_"+serviceVO.getServiceName()+"_不合规数据.xls";
			//根据不同浏览器区分解决乱码
			String filenamedisplay;
			String agent=request.getHeader("USER-AGENT");
			if(null != agent && -1 != agent.indexOf("Firefox")){//Firefox
				filenamedisplay = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
			}else{//其他
				filenamedisplay = URLEncoder.encode(fileName, "UTF-8");
			}
			response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush(); 
			out.close();
			wb=null;
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(文件下载),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "文件下载时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "文件下载");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw new Exception("文件下载出错："+e.getMessage(),e);
		}
	}
	
	//查询不合规数据，并导出到txt
	public void exportRuleCheckDataForTxt()throws Exception{
		String taskId=request.getParameter("taskId");
		GwModelDataFetchTaskVO taskVO=modelDataTaskService.searchById(Long.parseLong(taskId));
		List<GwServiceFieldVO> fieldCodeList=serviceFieldDAO.searchServiceOutField(taskVO.getServiceId());
		GwServiceVO serviceVO=gwServiceService.searchService(taskVO.getServiceId());
		String filePath=modelDataTaskService.searchRuleCheckDataForTxt(taskVO, fieldCodeList);
		
		String fileName = ""+serviceVO.getServiceCode()+"_"+serviceVO.getServiceName()+"_合规检查结果.txt";
		//根据不同浏览器区分解决乱码
		String filenamedisplay;
		String agent=request.getHeader("USER-AGENT");
		if(null != agent && -1 != agent.indexOf("Firefox")){//Firefox
			filenamedisplay = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
		}else{//其他
			filenamedisplay = URLEncoder.encode(fileName, "UTF-8");
		}
		
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);
		java.io.OutputStream out = null;
		java.io.FileInputStream in = null;
		try{
			out = response.getOutputStream();
			in = new FileInputStream(filePath);

			byte[] b = new byte[1024];
			int i = 0;

			while((i = in.read(b)) > 0){
				out.write(b, 0, i);
			}
			out.flush(); 
			out.close();
		}catch(Exception e){
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(文件下载),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：导出不合规数据到TXT时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "文件下载");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw new Exception("文件下载出错："+e.getMessage(),e);
		}finally{
			if(in != null){
				in.close();
				in = null;
			}
			try {
				File file = new File(filePath);
				if(file.exists()) file.delete();
			} catch (Exception e2) {
				logger.error("下载后删除文件失败:"+e2.getMessage(),e2);
			}
		}
	}
	
}
