package com.gztydic.gateway.core.view;

/** 
 * @ClassName: UserAuditView 
 * @Description: TODO(用户审核列表对象) 
 * @author davis
 * @date 2014-12-3 下午1:58:57 
 *  
 */
/** 
 * @ClassName: UserAuditView 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author davis
 * @date 2014-12-3 下午2:06:11 
 *  
 */ 
public class UserAuditView implements java.io.Serializable{

	private Long userId;
	private Long orgId;
	private String userType;
	private String userName;
	private String loginName;
	private String orgName;
	private String orgHeadName;
	private Long planId;
	private String planType;
	private String planState;
	private String extenTableKey;
	
	public UserAuditView() {
		
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgHeadName() {
		return orgHeadName;
	}
	public void setOrgHeadName(String orgHeadName) {
		this.orgHeadName = orgHeadName;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getPlanState() {
		return planState;
	}
	public void setPlanState(String planState) {
		this.planState = planState;
	}
	public String getExtenTableKey() {
		return extenTableKey;
	}
	public void setExtenTableKey(String extenTableKey) {
		this.extenTableKey = extenTableKey;
	}
}
