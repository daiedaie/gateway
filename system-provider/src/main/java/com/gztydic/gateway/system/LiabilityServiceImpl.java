package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwDesenServiceFieldDAO;
import com.gztydic.gateway.core.dao.GwModelLiabilityLogDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.DesenServiceFieldView;
import com.gztydic.gateway.core.view.GwModelLiabilityLogView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelLiabilityLogVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 免责日志管理
 *
 */
@Service
@SuppressWarnings("unchecked")
public class LiabilityServiceImpl extends GeneralServiceImpl<GwModelLiabilityLogVO> implements LiabilityService{
	
	@Resource
	private GwServiceFieldDAO serviceFieldDAO;
	
	@Resource
	private GwModelLiabilityLogDAO gwModelLiabilityLogDAO;
	
	@Resource
	private GwDesenServiceFieldDAO gwDesenServiceFieldDAO;
	
	@Resource
	private GwServiceCheckRuleDAO checkRuleDAO;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	
		
	/**
	 * 查询免责日志列表
	 * @param view
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchLiabilityLogList(GwModelLiabilityLogView view,PageObject pageObject) throws Exception{
		pageObject = gwModelLiabilityLogDAO.searchLiabilityList(view, pageObject);
		List<Object[]> list = pageObject.getData();
		List viewList = new ArrayList();
		GwModelLiabilityLogView v = null;
		for (Object[] obj : list) {
			v = new GwModelLiabilityLogView();
			v.setLogId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
			v.setOrgName(obj[3]==null?"":String.valueOf(obj[3]));
			v.setModelCode(obj[4]==null?"":String.valueOf(obj[4]));
			v.setModelName(obj[5]==null?"":String.valueOf(obj[5]));
			v.setModelDataNum(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			v.setDownloadTime(obj[7]==null?null:DateUtil.DateToString5((Date)obj[7]));
			v.setServiceName(obj[8]==null?"":String.valueOf(obj[8]));
			v.setServiceCode(obj[9]==null?"":String.valueOf(obj[9]));
			v.setOrgLoginName(obj[10]==null?"":String.valueOf(obj[10]));
			v.setFieldNum(obj[11]==null?null:Long.parseLong(String.valueOf(obj[11])));
			viewList.add(v);
		}
		pageObject.setData(viewList);
		return pageObject;
	}
	
	/**
	 * 根据log查询日志
	 * @param logId
	 * @return
	 * @throws Exception
	 */
	public GwModelLiabilityLogView searchLiabilityLog(GwModelLiabilityLogView view) throws Exception{
		if(view == null || view.getLogId() == null) throw new Exception("免责日志查询条件不能为空");
		
		List<Object[]> list = gwModelLiabilityLogDAO.searchLiabilityList(view);
		GwModelLiabilityLogView v = new GwModelLiabilityLogView();
		if(list != null && list.size() > 0){
			Object[] obj = list.get(0);
			v.setLogId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
			v.setOrgName(obj[3]==null?"":String.valueOf(obj[3]));
			v.setModelCode(obj[4]==null?"":String.valueOf(obj[4]));
			v.setModelName(obj[5]==null?"":String.valueOf(obj[5]));
			v.setModelDataNum(obj[6]==null?null:Long.parseLong(String.valueOf(obj[6])));
			v.setDownloadTime(obj[7]==null?null:DateUtil.DateToString5((Date)obj[7]));
			v.setServiceName(obj[8]==null?"":String.valueOf(obj[8]));
			v.setModelVersion(obj[9]==null?"":String.valueOf(obj[9]));
			v.setServiceType(obj[10]==null?"":String.valueOf(obj[10]));
			v.setCycleType(obj[11]==null?"":String.valueOf(obj[11]));
			v.setCycleDay(obj[12]==null?"":String.valueOf(obj[12]));
			v.setServiceRemark(obj[13]==null?"":String.valueOf(obj[13]));
			v.setModelType(obj[14]==null?"":String.valueOf(obj[14]));
			v.setStartTime(obj[15]==null?"":String.valueOf(obj[15]));
			v.setAlgType(obj[16]==null?"":String.valueOf(obj[16]));
			v.setModelDesc(obj[17]==null?"":String.valueOf(obj[17]));
			v.setServiceCode(obj[18]==null?"":String.valueOf(obj[18]));
			v.setModelFields(obj[19]==null?"":String.valueOf(obj[19]));
			v.setDesenRuleContent(obj[20]==null?"":String.valueOf(obj[20]));
			v.setModelId(obj[21]==null?null:Long.parseLong(String.valueOf(obj[21])));
			v.setAlgRule(obj[22]==null?"":String.valueOf(obj[22]));

		}
		return v;
	}
	
	/**
	 * 敏感信息追溯列表
	 * @param view
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchDesenList(GwModelLiabilityLogView view,
			PageObject pageObject,String searchBy) throws Exception {
		pageObject = gwModelLiabilityLogDAO.searchDesenList(view, pageObject,searchBy);
		List<Object[]> list = pageObject.getData();
		List viewList = new ArrayList();
		GwModelLiabilityLogView v = null;
		for (Object[] obj : list) {
			v = new GwModelLiabilityLogView();
			v.setLogId(obj[0]==null?null:Long.parseLong(String.valueOf(obj[0])));
			v.setLoginName(obj[1]==null?"":String.valueOf(obj[1]));
			v.setUserName(obj[2]==null?"":String.valueOf(obj[2]));
			v.setOrgId(obj[3]==null?null:Long.parseLong(String.valueOf(obj[3])));
			v.setOrgName(obj[4]==null?"":String.valueOf(obj[4]));
			v.setModelCode(obj[5]==null?"":String.valueOf(obj[5]));
			v.setModelName(obj[6]==null?"":String.valueOf(obj[6]));
			v.setServiceName(obj[7]==null?"":String.valueOf(obj[7]));
			v.setServiceCode(obj[8]==null?"":String.valueOf(obj[8]));
			v.setModelFields(obj[9]==null?"":String.valueOf(obj[9]));
			v.setModelDataNum(obj[10]==null?null:Long.parseLong(String.valueOf(obj[10])));
			v.setDownloadTime(obj[11]==null?null:DateUtil.DateToString5((Date)obj[11]));
			v.setOrgLoginName(obj[12]==null?null:String.valueOf(obj[12]));
			viewList.add(v);
		}
		pageObject.setData(viewList);
		return pageObject;
	}
	
	public GwModelLiabilityLogVO saveLiabilityLog(GwModelDataFetchTaskVO taskVo,GwUserVO loginUser)throws Exception {
		GwModelLiabilityLogVO logVo = new GwModelLiabilityLogVO();
		if(taskVo != null){
			GwServiceVO serviceVO = gwServiceService.searchService(taskVo.getServiceId());
			StringBuffer outPutStr = new StringBuffer();
			StringBuffer desenStr = new StringBuffer();
			if(CommonState.SERVICE_SOURCE_108.equals(serviceVO.getServiceSource())){
				if(taskVo.getCheckBatch()!=null && !"".equals(taskVo.getCheckBatch())){
					List<GwServiceCheckRuleVO> ruleList = checkRuleDAO.searchCheckRuleList(taskVo.getCheckBatch());
					
					//服务输出字段
					GwServiceCheckRuleVO ruleVO = new GwServiceCheckRuleVO();
					for (int i=0;i<ruleList.size();i++) {
						ruleVO = ruleList.get(i);
						outPutStr.append((i==0?"":",")+ruleVO.getFieldCode());
					}
					
					//检查规则
					if(ruleList != null){
						for(int i=0;i<ruleList.size();i++){
							ruleVO = ruleList.get(i);
							desenStr.append(ruleVO.getFieldCode());
							desenStr.append("(");
							desenStr.append(ruleVO.getFieldName());
							desenStr.append(")");
							if(ruleVO.getCheckType() != null){
								desenStr.append("=");
								desenStr.append("检查类型：");
								GwSysDictVO dictVO = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE", ruleVO.getCheckType());
								desenStr.append(dictVO!=null?dictVO.getDictValue():ruleVO.getCheckType()+", ");
								desenStr.append("检查规则：");
								desenStr.append(ruleVO.getCheckRule()+";\n");
							}
						}
					}
				}
				
			}else if(CommonState.SERVICE_SOURCE_DATA.equals(serviceVO.getServiceSource())){
				List<DesenServiceFieldView> desenFieldList = gwDesenServiceFieldDAO.searchListByServiceIdAndUserId(taskVo.getServiceId(),taskVo.getUserId());
				if(desenFieldList != null){
					DesenServiceFieldView fieldVo = new DesenServiceFieldView();
					for(int i=0;i<desenFieldList.size();i++){
						fieldVo = desenFieldList.get(i);
						if(i != 0){
							outPutStr.append(", ");
							desenStr.append("; \n");
						}
						outPutStr.append(fieldVo.getFieldCode());
						desenStr.append(fieldVo.getFieldCode());
						desenStr.append("(");
						desenStr.append(fieldVo.getFieldName());
						desenStr.append(")");
						if(fieldVo.getRuleType() != null || fieldVo.getConditionType() != null){
							desenStr.append("=");
						}
						if(fieldVo.getRuleType() != null){
							desenStr.append("脱敏类型：");
							desenStr.append(fieldVo.getRuleName()+", ");
							desenStr.append("处理定位：");
							desenStr.append(fieldVo.getRuleContent()+", ");
							desenStr.append("替换字符：");
							desenStr.append(fieldVo.getReplaceContent()+", ");
						}
						if(fieldVo.getConditionType() != null){
							desenStr.append("条件类型：");
							desenStr.append(fieldVo.getConditionType()+", ");
							desenStr.append("条件内容：");
							desenStr.append(fieldVo.getConditionContent()+", ");
						}
					}
				}
			}
			logVo.setServiceId(taskVo.getServiceId());
			logVo.setUserId(loginUser.getUserId());
			logVo.setModelFields(outPutStr.toString());
			logVo.setDesenRuleContent(desenStr.toString());
			logVo.setTaskId(taskVo.getTaskId());
			logVo.setModelDataNum(taskVo.getDataNum());
			logVo.setCreateUser(loginUser.getLoginName());
			logVo.setCreateTime(new Date());
			gwModelLiabilityLogDAO.save(logVo);
			
		}
		return logVo;
	}

	/** 查询免责日志统计信息 */
	public Map searchLiabilityCount(GwModelLiabilityLogView view) throws Exception {
		Map map = new HashMap();
		String liabilityDataNum = gwModelLiabilityLogDAO.searchLiabilityDataNum(view);
		String liabilityOutputDataNum = gwModelLiabilityLogDAO.searchLiabilityOutputDataNum(view);
		map.put("liabilityDataNum", liabilityDataNum);
		map.put("liabilityOutputDataNum", liabilityOutputDataNum);
		return map;
	}
	
	//下载文件，点击“取消”，数据量置0
	public void updateDataNum(Long logId)throws Exception{
		gwModelLiabilityLogDAO.updateDataNum(logId);
	}
}
