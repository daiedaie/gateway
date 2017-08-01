package com.gztydic.gateway.gather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.PreparedExpression;
import org.wltea.expression.datameta.Variable;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.DataGatherDAO;
import com.gztydic.gateway.core.dao.GwDesenServiceFieldDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;
import com.gztydic.gateway.core.view.GwServiceCheckRecordView;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;

/**
 * 数据脱敏处理
 *
 */
@Service
public class DataDesenServiceImpl implements DataDesenService{

	private static final Log log = LogFactory.getLog(DataDesenServiceImpl.class);
	@Resource
	private GwDesenServiceFieldDAO gwDesenServiceFieldDAO;

	@Resource
	private DataGatherDAO dataGatherDAO;
	//数据脱敏
	public List<Map<String,String>> dataDesen(List<Map<String, String>> dataList,List<GwDesenRuleServiceFieldView> desenRuleList,Long serviceId,Long userId,Map<String, String> ignoreFieldMap) throws Exception{
		try {
			if(dataList == null || dataList.size() == 0) return new ArrayList<Map<String,String>>();
			//没有脱敏规则直接返回null
			if(desenRuleList == null || desenRuleList.size() == 0) return new ArrayList<Map<String,String>>();
			
			//脱敏后的List
			List<Map<String,String>> desenResultList = new ArrayList<Map<String,String>>();
			
			//服务字段脱敏规则
			Map<String, GwDesenRuleServiceFieldView> desenMap = new HashMap<String, GwDesenRuleServiceFieldView>();
			for (GwDesenRuleServiceFieldView desen : desenRuleList) {
				desenMap.put(desen.getFieldCode(), desen);
			}
			
			Map<String, String> resultMap = null;
			GwDesenRuleServiceFieldView desen = null;
			PreparedExpression preparedExpression = null;
			log.info("serviceId="+serviceId+" userId="+userId+"脱敏开始;脱敏前数据："+JSONArray.fromObject(dataList).toString());
			log.info("脱敏规则："+JSONArray.fromObject(desenRuleList).toString());
			for (Map<String, String> dataMap : dataList) {
				Iterator<String> it = dataMap.keySet().iterator();
				resultMap = new HashMap<String, String>();
				while (it.hasNext()) {
					String fieldCode = it.next();
					String fieldValue = dataMap.get(fieldCode);
					
					//预览数据中加了rowid，特殊情况使用
					if(ignoreFieldMap != null && StringUtils.isNotBlank(ignoreFieldMap.get(fieldCode))) {
						resultMap.put(fieldCode, fieldValue);
					}
					
					desen = desenMap.get(fieldCode);	//脱敏规则
					if(desen != null){	//desen为空表示将该字段过滤掉
						if(StringUtils.isBlank(fieldValue)) {resultMap.put(fieldCode, "");continue;}
						
						//条件过滤
						if(StringUtils.isNotBlank(desen.getConditionType()) && StringUtils.isNotBlank(desen.getConditionContent())){
							if("in".equals(desen.getConditionType())){
								if((","+desen.getConditionContent()+",").indexOf(","+fieldValue+",") < 0){
									resultMap = null;
									break;
								}
							}else{
								String expression = "";	//计算表达式
								if("=".equals(desen.getConditionType())) {
									expression =  "fieldValue==conditionContent";
								}else {
									expression = "fieldValue" + desen.getConditionType() + "conditionContent";
								}
								//表达式变量
								List<Variable> variables = new ArrayList<Variable>();
								variables.add(Variable.createVariable("conditionContent", Float.parseFloat(desen.getConditionContent())));
								variables.add(Variable.createVariable("fieldValue", Float.parseFloat(fieldValue)));
								// 预编译表达式   
								preparedExpression = ExpressionEvaluator.preparedCompile(expression, variables);
								// 执行表达式   
								Object result = preparedExpression.execute();
								if("false".equals(String.valueOf(result))){
									resultMap = null;
									break;
								}
							}
						}
						
						//字符过滤
						if(CommonState.DESEN_RULE_TYPE_CHAR.equals(desen.getRuleType())){	//字符替换
							fieldValue = fieldValue.replaceAll(desen.getRuleContent(), desen.getReplaceContent());
						}else if(CommonState.DESEN_RULE_TYPE_RANGE.equals(desen.getRuleType())){	//范围替换
							String ruleContent = desen.getRuleContent();
							if(StringUtils.isNotBlank(ruleContent)){
								String[] ruleContents = ruleContent.split(",");
								int start = Integer.parseInt(ruleContents[0])-1;
								int end = ruleContents.length>1? Integer.parseInt(ruleContents[1]) : -1;
								
								int length = fieldValue.length();
								String left = "", right = "";
								if(start <= length){
									left = StringUtils.substring(fieldValue, 0, start);
								}
								if(end >= start && end <= length)
									right = StringUtils.substring(fieldValue, end);
								fieldValue = left +(start<=length?desen.getReplaceContent():"")+ right;
							}
						}
						resultMap.put(fieldCode, fieldValue);
					}
				};
				if(resultMap != null && !resultMap.isEmpty())
					desenResultList.add(resultMap);
			}
			log.info("serviceId="+serviceId+" userId="+userId+"脱敏结束;脱敏后数据："+JSONArray.fromObject(desenResultList).toString());
			return desenResultList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("服务数据脱敏异常"+e.getMessage(),e);
			throw e;
		}
	}
	
	//数据合规检查
	public String dataCheck(List<Map<String, String>> dataList,List<GwRuleCheckServiceFieldView> checkRuleList,Long serviceId,Long taskId) throws Exception{
		//没有规则规则直接返回true
		String result = "";
		try{
			if(checkRuleList == null || checkRuleList.size() == 0) return "";
			ArrayList<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
			Long rowIrregularCount = 0L;
			int dbLimit = 1000;
			int fieldCount = 0;//总合规检查字段数
			HashMap<String,String> checkTypeHash = new HashMap<String,String>();
			HashMap<String,String> checkRuleHash = new HashMap<String,String>();
			for(GwRuleCheckServiceFieldView checkRule:checkRuleList){
				fieldCount += 1;
				checkTypeHash.put(checkRule.getRecorder(), checkRule.getCheckType());
				if("1".equals(checkRule.getCheckType())){ //长度检查
					checkRuleHash.put(checkRule.getRecorder(), checkRule.getCheckRule());
				}else if("2".equals(checkRule.getCheckType())){ //字典数值检查
					String[] values = checkRule.getCheckRule().split(";");
					checkRuleHash.put(checkRule.getRecorder()+"_checkType2", checkRule.getCheckRule());
					for(String value:values){
						checkRuleHash.put(checkRule.getRecorder()+"_"+value, "1");
					}
				}else if("3".equals(checkRule.getCheckType())){ //范围内检查
					String[] values = checkRule.getCheckRule().split(";");
					checkRuleHash.put(checkRule.getRecorder()+"_checkType3", checkRule.getCheckRule());
					checkRuleHash.put(checkRule.getRecorder()+"_begin", values[0]);
					checkRuleHash.put(checkRule.getRecorder()+"_end", values[1]);
				}
			}
			
			Long rowNum = 0L;
			for(Map<String, String> data:dataList){
				int checkResult = 0;
				rowNum += 1;
				String[] fieldValues;
				if (data.get("data").indexOf("|") != -1){
					fieldValues= data.get("data").split("\\|");
				}else{
					fieldValues= new String[]{data.get("data")};
				}
				Long fieldNum = 0L;//fieldNum是输出文件中的字段顺序号，也是数据库中服务输出字段的字段顺序号
				int rowIrregular = 0;//当前行是否有不合规的记录
				String irregularField = ""; //不合规字段，如：不合规字段序号:不合规字段值|不合规字段序号:不合规字段值
				for(String fieldValue:fieldValues){
					fieldNum += 1;
					//出现未定义检查规则的字段，表示该字段为多出来(未配规则)的非法字段
					if(checkTypeHash.get(String.valueOf(fieldNum)) == null){
						checkResult = 3;
						log.error("exists illegal data!rowNum="+rowNum+",rule fieldCount="+fieldCount+", fieldNum="+fieldNum);
						break;
					}
					String type = checkTypeHash.get(String.valueOf(fieldNum));
					String fieldIrregular = "";
					if("2".equals(type)){//字典值检查
						String key = fieldValue;
						if(checkRuleHash.get(fieldNum+"_"+key) == null){
							fieldIrregular = checkRuleHash.get(fieldNum+"_checkType2");
						}
					}else if("3".equals(type)){ //范围检查
						if(Long.valueOf(fieldValue) < Long.valueOf(checkRuleHash.get(fieldNum+"_begin")) || Long.valueOf(fieldValue) > Long.valueOf(checkRuleHash.get(fieldNum+"_end"))){
							fieldIrregular = checkRuleHash.get(fieldNum+"_checkType3");
						}
					}else if("1".equals(type)){ //长度检查
						if(fieldValue.length() > Long.valueOf(checkRuleHash.get(fieldNum.toString()))){
							fieldIrregular = checkRuleHash.get(fieldNum);
						}
					}
					
					//存在不合规的字段
					if(StringUtils.isNotBlank(fieldIrregular)){
						rowIrregular = 1;
						
						irregularField = irregularField+(StringUtils.isBlank(irregularField)?"":"|")+fieldNum+":"+fieldValue;
						//将不合规的字段记录保存到数据库,超过不再继续插入到数据库
						if(rowIrregularCount < dbLimit){
							GwServiceCheckRecordView serviceCheckRecordView =new GwServiceCheckRecordView();
							serviceCheckRecordView.setRowId(rowNum);
							serviceCheckRecordView.setTaskId(taskId);
							serviceCheckRecordView.setServiceId(serviceId);
							serviceCheckRecordView.setCheckType(type);
							serviceCheckRecordView.setCheckRule(fieldIrregular);
							serviceCheckRecordView.setFieldSort(fieldNum);
							gwDesenServiceFieldDAO.saveServiceCheckRecode(serviceCheckRecordView);
						}
						
					}
				}
				
				log.info("===============rule fieldCount="+fieldCount+"===========file fieldNum="+fieldNum+"==checkResult=="+checkResult);
				//判断输出字段是否多于或少于检查规则
				int warnType = 0;
				String dataStr = "";
				if(checkResult == 3){//输出文件字段多于检查规则
					result = "输出文件字段多于检查规则";
					rowIrregularCount += 1;
					warnType = 1;
					dataStr = rowNum+"::"+data.get("data");
					//清除该任务警告信息
					gwDesenServiceFieldDAO.deleteServiceCheckWarn(taskId);
					//插入多列警告信息
					gwDesenServiceFieldDAO.insertServiceCheckWarn(warnType, rowNum, taskId,dataStr);
					
				}else if(fieldCount != fieldNum){//输出文件字段少于检查规则
					dataStr = rowNum+"::"+data.get("data");
					result = "输出文件字段少于检查规则";
					rowIrregularCount += 1;
					warnType = 2;
					//清除该任务警告信息
					gwDesenServiceFieldDAO.deleteServiceCheckWarn(taskId);
					//插入多列警告信息
					gwDesenServiceFieldDAO.insertServiceCheckWarn(warnType, rowNum, taskId,dataStr);
				}
				
				if(checkResult==3 || checkResult==4){
					//清除该任务警告信息
					gwDesenServiceFieldDAO.deleteServiceCheckWarn(taskId);
					//插入多列警告信息
					gwDesenServiceFieldDAO.insertServiceCheckWarn(warnType, rowNum, taskId,data.get("data"));
					continue;
				}
				
				//存在不合规的数据，将行数据插入数据库
				if(rowIrregular == 1){
					dataStr = rowNum+"::"+data.get("data")+">>"+irregularField;
					rowIrregularCount += 1;
					result = "第【"+rowNum+"】行第【"+fieldNum+"】中出现非法数据！！";
					String str = rowNum+"::"+data.get("data");
					//不合规记录数小于等于最大登记数，不合规记录入库
					if (rowIrregularCount <= dbLimit){
						dataGatherDAO.saveOnlineServiceCheckRecord(serviceId, taskId, rowNum, str);
					}
					
				}
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			log.error("合规检查发生异常"+e.getMessage(),e);
			throw e;
			
		}
	}

		
	public static void main(String[] args) {
		String result = "{\"result\": \"true\",\"info\": \"正常返回\",\"serviceType\": \"0\",\"serviceApi\": \"r1411378078388fiqnmchxci\"," +
				"\"data\": [{\"fieldCode001\": \"17002025668\",\"fieldCode002\":\"张三\"}," +
						"{\"fieldCode001\":\"170\",\"fieldCode002\":\"张三，你好\"}]}";
		
		JsonConfig jsonConfig = new JsonConfig();
	    jsonConfig.setRootClass(DataResponse.class);
	    Map<String, Class> classMap = new HashMap<String, Class>();
	    classMap.put("data", Map.class); 	// 指定DataResponse的data字段的内部类型
	    jsonConfig.setClassMap(classMap);
		DataResponse dataResponse = (DataResponse)JSONObject.toBean(JSONObject.fromObject(result), jsonConfig);
		for (Map<String, String> map : dataResponse.getData()) {
			System.out.println(map);
		}
		
		ApplicationContext ac = new ClassPathXmlApplicationContext("spring-core.xml");
		DataDesenServiceImpl s = (DataDesenServiceImpl)ac.getBean("dataDesenServiceImpl");
		try {
//			s.dataDesen(dataResponse.getData(), 12l, 1l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
