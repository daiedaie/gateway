package com.gztydic.gateway.core.view;

import java.util.Date;

public class RoleView implements java.io.Serializable{
	
  // Fields
  private String roleCode;
  private String roleName;
  private String roleDesc;
  private String status;
  private Date createTime;
  private String creator;
  private Date modifyTime;
  private String modifier;
  private String remark;
  private Long userId;

  /** default constructor */
  public RoleView() {
  }

  public String getRoleCode() {
	return roleCode;
  }

  public void setRoleCode(String roleCode) {
	this.roleCode = roleCode;
  }

  public String getRoleName() {
	return roleName;
  }

  public void setRoleName(String roleName) {
	this.roleName = roleName;
  }

  public String getRoleDesc() {
	return roleDesc;
  }

  public void setRoleDesc(String roleDesc) {
 	this.roleDesc = roleDesc;
  }

  public String getStatus() {
	return status;
  }

  public void setStatus(String status) {
	this.status = status;
  }

  public Date getCreateTime() {
	return createTime;
  }

  public void setCreateTime(Date createTime) {
	this.createTime = createTime;
  }

  public String getCreator() {
	return creator;
  }

  public void setCreator(String creator) {
	this.creator = creator;
 }

  public Date getModifyTime() {
	return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
	this.modifyTime = modifyTime;
  }

  public String getModifier() {
	return modifier;
  }

  public void setModifier(String modifier) {
 	this.modifier = modifier;
  }

  public String getRemark() {
	return remark;
  }

 public void setRemark(String remark) {
	this.remark = remark;
 }

public Long getUserId() {
	return userId;
}

public void setUserId(Long userId) {
	this.userId = userId;
}


  

}
