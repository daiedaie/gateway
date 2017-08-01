package com.gztydic.gateway.core.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gztydic.gateway.core.vo.GwButtonVO;

/** 
 * @ClassName: FuncAndButtonView 
 * @Description: TODO(菜单和按钮权限列表实体类) 
 * @author davis
 * @date 2014-11-25 下午1:26:53 
 *  
 */
public class FuncAndButtonView implements Serializable{
	
	private String funcCode;
	private String funcName;
	private String funcUrl;
	private String iconUrl;
	private String funcDesc;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;
	private String parentCode;
	private String parentName;
	private Long funcSort;
	private String roleCode;
	private String userType;
	private List<GwButtonView> buttonList;
	private List<GwButtonVO> buttonVOList;
	public List<GwButtonVO> getButtonVOList() {
		return buttonVOList;
	}
	public void setButtonVOList(List<GwButtonVO> buttonVOList) {
		this.buttonVOList = buttonVOList;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getFuncCode() {
		return funcCode;
	}
	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public String getFuncUrl() {
		return funcUrl;
	}
	public void setFuncUrl(String funcUrl) {
		this.funcUrl = funcUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getFuncDesc() {
		return funcDesc;
	}
	public void setFuncDesc(String funcDesc) {
		this.funcDesc = funcDesc;
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
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public Long getFuncSort() {
		return funcSort;
	}
	public void setFuncSort(Long funcSort) {
		this.funcSort = funcSort;
	}
	public List<GwButtonView> getButtonList() {
		return buttonList;
	}
	public void setButtonList(List<GwButtonView> buttonList) {
		this.buttonList = buttonList;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

}
