package com.gztydic.gateway.core.view;

/** 
 * @ClassName: UserModelAppVo 
 * @Description: TODO(用户模型取数任务申请实体类) 
 * @author davis
 * @date 2014-11-19 下午03:51:19 
 *  
 */
public class UserModelAppVo implements java.io.Serializable { 
	
	private Long userId;
	private Long modelId;
	private String modelCode;
	private String modelName;
	private String fetchType;
	private String cycleType;
	private Long cycleNum;
	private String auditStatus;
	
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the modelId
	 */
	public Long getModelId() {
		return modelId;
	}
	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	/**
	 * @return the modelCode
	 */
	public String getModelCode() {
		return modelCode;
	}
	/**
	 * @param modelCode the modelCode to set
	 */
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the fetchType
	 */
	public String getFetchType() {
		return fetchType;
	}
	/**
	 * @param fetchType the fetchType to set
	 */
	public void setFetchType(String fetchType) {
		this.fetchType = fetchType;
	}
	/**
	 * @return the cycleType
	 */
	public String getCycleType() {
		return cycleType;
	}
	/**
	 * @param cycleType the cycleType to set
	 */
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	/**
	 * @return the cycleNum
	 */
	public Long getCycleNum() {
		return cycleNum;
	}
	/**
	 * @param cycleNum the cycleNum to set
	 */
	public void setCycleNum(Long cycleNum) {
		this.cycleNum = cycleNum;
	}
	/**
	 * @return the auditStatus
	 */
	public String getAuditStatus() {
		return auditStatus;
	}
	/**
	 * @param auditStatus the auditStatus to set
	 */
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	

}
