package com.gztydic.gateway.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwModelDAO;
import com.gztydic.gateway.core.dao.GwModelDataFetchDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.view.ModelFileView;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.system.OperationLogService;


/** 
 * @ClassName: ModelInfoServiceImpl 
 * @Description: TODO(关于模型信息所有处理接口实现类) 
 * @author davis
 * @date 2014-11-19 下午03:04:00 
 *  
 */
@Service
public class ModelInfoServiceImpl extends GeneralServiceImpl<GwModelVO> implements ModelInfoService{

	@Resource
	private GwModelDAO gwModelDAO;
	@Resource
	private GwServiceDAO gwServiceDAO;
	
	@Resource
	private GwModelDataFetchDAO fetchDAO;
	
	@Resource
	private GwModelDataFileDAO gwModelDataFileDAO;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	/** 
	 * @Title: searchList 
	 * @Description: TODO(查询所有模型信息列表) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwModelVO>    返回类型 
	 * @throws 
	 */
	public List<GwModelVO> searchList() throws Exception{
		return gwModelDAO.findAll();
	}
	
	/** 
	 * @Title: searchModelById 
	 * @Description: TODO(根据模型ID查询模型信息对象) 
	 * @param @param modelId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelVO    返回类型 
	 * @throws 
	 */
	public GwModelVO searchModelById(Long modelId) throws Exception{
		return gwModelDAO.findById(modelId);
	}
	
	/** 
	 * @Title: searchServiceById 
	 * @Description: TODO(根据服务ID查询模型信息对象) 
	 * @param @param serviceId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelVO    返回类型 
	 * @throws 
	 */
	public GwServiceVO searchServiceById(Long serviceId) throws Exception{
		return gwServiceDAO.findById(serviceId);
	}
	
	/**
	 * 查询模型列表
	 */
	public PageObject searchServiceList(GwUserVO userVO,GwServiceView serviceView,PageObject pageObject) throws Exception{
		//数据用户、机构用户只能查看自己的服务列表
		if(GwUserType.DATA_USER.equals(userVO.getUserType()) || GwUserType.ORG_USER.equals(userVO.getUserType())){
			pageObject = gwServiceDAO.searchServiceListByUser(userVO, serviceView, pageObject);		
		}else {
			//其他用户查看所有服务列表
			pageObject = gwServiceDAO.searchServiceList(serviceView,pageObject);
		}
		List<Object[]> list = pageObject.getData();
		List viewList = new ArrayList();
		GwServiceView v = null;
		for (Object[] obj : list) {
			v = new GwServiceView();
			v.setServiceCode(obj[0]==null?"":String.valueOf(obj[0]));
			v.setServiceName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setModelName(obj[2]==null?"":String.valueOf(obj[2]));
			v.setServiceId(obj[3]==null?null:Long.parseLong(String.valueOf(obj[3])));
			v.setServiceType(obj[4]==null?"":String.valueOf(obj[4]));
			v.setServiceSource(obj[5]==null?"":String.valueOf(obj[5]));
			v.setFetchCount(obj[6]==null?0:Integer.parseInt(String.valueOf(obj[6])));
			viewList.add(v);
		}
		pageObject.setData(viewList);
		return pageObject;
	}
	
	public GwModelVO searchModel(GwModelVO model){
		return gwModelDAO.findById(model.getModelId());
	}
	public List searchServicefieldOutput(Long serviceId) throws Exception{
		List<Object[]> list = gwServiceDAO.searchServicefieldOutput(serviceId);
		GwServiceFieldVO v;
		List viewList = new ArrayList();
		for(Object[] obj : list){
			v = new GwServiceFieldVO();
			v.setFieldCode(obj[0]==null?"":String.valueOf(obj[0]));
			v.setFieldName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setFieldDesc(obj[2]==null?"":String.valueOf(obj[2]));
			v.setNullable(obj[3]==null?"":String.valueOf(obj[3]));
			v.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			viewList.add(v);
		}
		return viewList;
	}
	
	public List searchServicefieldInput(Long serviceId) throws Exception{
		List<Object[]> list = gwServiceDAO.searchServicefieldInput(serviceId);
		GwServiceFieldVO v;
		List viewList = new ArrayList();
		for(Object[] obj : list){
		    v = new GwServiceFieldVO();
			v.setFieldCode(obj[0]==null?"":String.valueOf(obj[0]));
			v.setFieldName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setFieldDesc(obj[2]==null?"":String.valueOf(obj[2]));
			v.setNullable(obj[3]==null?"":String.valueOf(obj[3]));
			v.setFieldType(obj[4]==null?"":String.valueOf(obj[4]));
			viewList.add(v);
		}
		return viewList;
	}
	public GwServiceView findServiceModelById(Long serviceId) throws Exception {
		List<Object[]> list = gwServiceDAO.findServiceModelById(serviceId);
		GwServiceView v = new GwServiceView();
		if(list != null && list.size() > 0){
			Object[] obj = list.get(0);
			v.setServiceCode(obj[0]==null?"":String.valueOf(obj[0]));
			v.setServiceName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setServiceType(obj[2]==null?"":String.valueOf(obj[2]));
			v.setCycleType(obj[3]==null?"":String.valueOf(obj[3]));
			v.setModelCode(obj[4]==null?"":String.valueOf(obj[4]));
			v.setModelName(obj[5]==null?"":String.valueOf(obj[5]));
			v.setModelVersion(obj[6]==null?"":String.valueOf(obj[6]));
			v.setModelType(obj[7]==null?"":String.valueOf(obj[7]));
			v.setAlgType(obj[8]==null?"":String.valueOf(obj[8]));
			v.setAlgRule(obj[9]==null?"":String.valueOf(obj[9]));
			v.setServiceId(obj[10]==null?null:Long.parseLong(String.valueOf(obj[10])));
			v.setCycleDay(obj[11]==null?null:Long.parseLong(String.valueOf(obj[11])));
		}
		return v;		
	}

	/**
	 * 根据serviceCode、serviceSource查询服务
	 */
	public GwServiceVO searchService(String serviceCode, String serviceSource) throws Exception {
		return gwServiceDAO.searchService(serviceCode, serviceSource);
	}
	
	public int searchServiceCount(String serviceCode, String serviceSource) throws Exception {
		return gwServiceDAO.searchServiceCount(serviceCode, serviceSource);
	}

	/**
	 * 保存或修改服务信息
	 */
	public int updateServiceInfo(GwServiceVO serviceVO,GwUserVO loginUser) throws Exception {
		if(serviceVO.getServiceId()==null){
			serviceVO.setCreateTime(new Date());
			serviceVO.setCreateUser(loginUser.getLoginName());
			serviceVO.setStatus(CommonState.VALID);
			/*serviceVO.setServiceType(CommonState.SERVICE_TYPE_TIMING);*/
			serviceVO.setServiceSource(CommonState.SERVICE_SOURCE_108);
			gwServiceDAO.save(serviceVO);
			//新增服务并保存操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.ADD_SERVICE, loginUser.getLoginName()+"新增serviceCode="+serviceVO.getServiceCode()+",serviceId="+serviceVO.getServiceId()+"的服务");
		}else {
			GwServiceVO dbServiceVO = gwServiceDAO.findById(serviceVO.getServiceId());
			if(!CommonState.SERVICE_SOURCE_108.equals(dbServiceVO.getServiceSource())){
				throw new RuntimeException("非108服务不能修改");
			}
			dbServiceVO.setServiceCode(serviceVO.getServiceCode());
			dbServiceVO.setServiceName(serviceVO.getServiceName());
			dbServiceVO.setCycleType(serviceVO.getCycleType());
			dbServiceVO.setCycleDay(serviceVO.getCycleDay());
			dbServiceVO.setServiceType(serviceVO.getServiceType());
			dbServiceVO.setUpdateTime(new Date());
			dbServiceVO.setUpdateUser(loginUser.getLoginName());
			gwServiceDAO.update(dbServiceVO);
			//修改服务并修改操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.UPDATE_SERVICE, loginUser.getLoginName()+"修改serviceCode="+serviceVO.getServiceCode()+",serviceId="+serviceVO.getServiceId()+"的服务");
		}
		return 1;
	}
	
	/**
	 * 查询服务已被申请的数量
	 * @param serviceId
	 * @return
	 */
	public int searchServiceFetchCount(Long serviceId){
		return fetchDAO.searchServiceFetchCount(serviceId);
	}
	
	public int deleteServiceInfo(GwServiceVO serviceVO,GwUserVO loginUser) throws Exception{
		if(searchServiceFetchCount(serviceVO.getServiceId()) > 0)
			throw new Exception("该服务正在被用户使用，不能删除");
		serviceVO = gwServiceDAO.findById(serviceVO.getServiceId());
		gwServiceDAO.delete(serviceVO);
		operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.UPDATE_SERVICE, loginUser.getLoginName()+"删除serviceCode="+serviceVO.getServiceCode()+",serviceId="+serviceVO.getServiceId()+"的服务");
		return 1;
	}
	
	//根据taskId查询文件记录
	public GwModelDataFileVO searchModelDataFile(Long taskId,String fileType)throws Exception{
		return gwModelDataFileDAO.searchDataFile(taskId, fileType);
	}
	
	//根据fileIds查询文件记录
	public List<ModelFileView> searchModelDataFiles(String fileIds)throws Exception{
		return gwModelDataFileDAO.searchModelDataFiles(fileIds);
	}

	public int addServiceInValid(GwServiceVO serviceVO, GwUserVO loginUser)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
}
