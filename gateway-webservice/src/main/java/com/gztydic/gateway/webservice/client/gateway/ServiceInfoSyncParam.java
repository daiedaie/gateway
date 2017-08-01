package com.gztydic.gateway.webservice.client.gateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;


/**
 *	模型服务信息同步参数 
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceInfoSyncParam {
	
	@XmlElement(required=true, nillable=false)
	private String operType;	//1，新增；2，修改
	
	@XmlElement(required=true, nillable=false)
	private ServiceInfo serviceInfo;
	
	//服务信息
	@XmlType(name="ServiceInfo")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ServiceInfo{
		@XmlElement(required=true, nillable=false)
		private String serviceCode;
		@XmlElement(required=true, nillable=false)
		private String serviceName;
		@XmlElement(required=true, nillable=false)
		private String serviceType;	//0实时、1离线
		@XmlElement(required=true, nillable=false)
		private String cycleType;	//服务周期类型：1年、2季、3月、4周、5日
		@XmlElement(required=true, nillable=false)
		private Long cycleNum;		//服务周期数
		private String inputName;
		private String inputCode;
		private String inputDesc;
		private String outName;
		private String outCode;
		private String outDesc;
		@XmlElement(required=true, nillable=false)
		private String status;		//状态 0失效、1有效
		
		private ModelInfo modelInfo;
		@XmlElement(required=true, nillable=false)
		private List<ServiceField> serviceFieldList;
		
		public String getServiceCode() {
			return serviceCode;
		}
		public void setServiceCode(String serviceCode) {
			this.serviceCode = serviceCode;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getServiceType() {
			return serviceType;
		}
		public void setServiceType(String serviceType) {
			this.serviceType = serviceType;
		}
		public String getCycleType() {
			return cycleType;
		}
		public void setCycleType(String cycleType) {
			this.cycleType = cycleType;
		}
		public Long getCycleNum() {
			return cycleNum;
		}
		public void setCycleNum(Long cycleNum) {
			this.cycleNum = cycleNum;
		}
		public String getInputName() {
			return inputName;
		}
		public void setInputName(String inputName) {
			this.inputName = inputName;
		}
		public String getInputDesc() {
			return inputDesc;
		}
		public void setInputDesc(String inputDesc) {
			this.inputDesc = inputDesc;
		}
		public String getOutName() {
			return outName;
		}
		public void setOutName(String outName) {
			this.outName = outName;
		}
		public String getOutDesc() {
			return outDesc;
		}
		public void setOutDesc(String outDesc) {
			this.outDesc = outDesc;
		}
		public String getInputCode() {
			return inputCode;
		}
		public void setInputCode(String inputCode) {
			this.inputCode = inputCode;
		}
		public String getOutCode() {
			return outCode;
		}
		public void setOutCode(String outCode) {
			this.outCode = outCode;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public ModelInfo getModelInfo() {
			return modelInfo;
		}
		public void setModelInfo(ModelInfo modelInfo) {
			this.modelInfo = modelInfo;
		}
		public List<ServiceField> getServiceFieldList() {
			return serviceFieldList;
		}
		public void setServiceFieldList(List<ServiceField> serviceFieldList) {
			this.serviceFieldList = serviceFieldList;
		}
	}
	
	//服务所属模型
	@XmlType(name="ModelInfo")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ModelInfo{
		@XmlElement(required=true, nillable=false)
		private String modelCode;
		@XmlElement(required=true, nillable=false)
		private String modelName;
		@XmlElement(required=true, nillable=false)
		private String modelVersion;
		@XmlElement(required=true, nillable=false)
		private String modelType;
		@XmlElement(required=true, nillable=false)
		@XmlJavaTypeAdapter(DateAdapter.class)
		private Date onlineTime;
		private String modelDesc;
		private String algType;		//算法类型描述
		private String algRule;	//算法规则描述
		@XmlElement(required=true, nillable=false)
		private String status;		//0失效、1有效
		
		public String getModelCode() {
			return modelCode;
		}
		public void setModelCode(String modelCode) {
			this.modelCode = modelCode;
		}
		public String getModelName() {
			return modelName;
		}
		public void setModelName(String modelName) {
			this.modelName = modelName;
		}
		public String getModelVersion() {
			return modelVersion;
		}
		public void setModelVersion(String modelVersion) {
			this.modelVersion = modelVersion;
		}
		public String getModelType() {
			return modelType;
		}
		public void setModelType(String modelType) {
			this.modelType = modelType;
		}
		public Date getOnlineTime() {
			return onlineTime;
		}
		public void setOnlineTime(Date onlineTime) {
			this.onlineTime = onlineTime;
		}
		public String getModelDesc() {
			return modelDesc;
		}
		public void setModelDesc(String modelDesc) {
			this.modelDesc = modelDesc;
		}
		public String getAlgType() {
			return algType;
		}
		public void setAlgType(String algType) {
			this.algType = algType;
		}
		public String getAlgRule() {
			return algRule;
		}
		public void setAlgRule(String algRule) {
			this.algRule = algRule;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}
	
	//服务字段
	@XmlType(name="ServiceField")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ServiceField{
		@XmlElement(required=true, nillable=false)
		private String fieldCode;
		@XmlElement(required=true, nillable=false)
		private String fieldName;
		@XmlElement(required=true, nillable=false)
		private String gatherType;	//0输入/1输出
		@XmlElement(required=true, nillable=false)
		private String fieldType;
		private String fieldDesc;
		private String nullable;
		@XmlElement(required=true, nillable=false)
		private Long reorder;
		
		public String getFieldCode() {
			return fieldCode;
		}
		public void setFieldCode(String fieldCode) {
			this.fieldCode = fieldCode;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getGatherType() {
			return gatherType;
		}
		public void setGatherType(String gatherType) {
			this.gatherType = gatherType;
		}
		public String getFieldType() {
			return fieldType;
		}
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
		public String getFieldDesc() {
			return fieldDesc;
		}
		public void setFieldDesc(String fieldDesc) {
			this.fieldDesc = fieldDesc;
		}
		public String getNullable() {
			return nullable;
		}
		public void setNullable(String nullable) {
			this.nullable = nullable;
		}
		public Long getReorder() {
			return reorder;
		}
		public void setReorder(Long reorder) {
			this.reorder = reorder;
		}
	}
	
	@XmlType(name="ServiceInfo_DateAdapter")
	public static class DateAdapter extends XmlAdapter<String, Date> {  
	    private SimpleDateFormat yyyyMMddHHmmss = new  SimpleDateFormat("yyyy-mm-dd HH:mm:ss");  
	    public Date unmarshal(String v) throws Exception { 
	    	if(StringUtils.isBlank(v)) return null;
	        return yyyyMMddHHmmss.parse(v);  
	    }
	    public String marshal(Date v) throws Exception { 
	    	if(v == null) return null;
	        return yyyyMMddHHmmss.format(v);  
	    }
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}  
}
