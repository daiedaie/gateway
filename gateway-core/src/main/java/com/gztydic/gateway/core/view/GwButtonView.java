package com.gztydic.gateway.core.view;

import java.util.Date;

public class GwButtonView implements java.io.Serializable{
	// Fields

		private String buttonCode;
		private String funcCode;
		private String operateType;
		private String operateDesc;
		private String status;
		private Date createTime;
		private String creator;
		private Date modifyTime;
		private String modifier;
		private String remark;
        private boolean flag;
        private String roleCode;
        private String userType;
		// Constructors

		public String getRoleCode() {
			return roleCode;
		}

		public void setRoleCode(String roleCode) {
			this.roleCode = roleCode;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		/** default constructor */
		public GwButtonView() {
		}

		// Property accessors

		public String getButtonCode() {
			return this.buttonCode;
		}

		public void setButtonCode(String buttonCode) {
			this.buttonCode = buttonCode;
		}

		public String getFuncCode() {
			return this.funcCode;
		}

		public void setFuncCode(String funcCode) {
			this.funcCode = funcCode;
		}

		public String getOperateType() {
			return this.operateType;
		}

		public void setOperateType(String operateType) {
			this.operateType = operateType;
		}

		public String getOperateDesc() {
			return this.operateDesc;
		}

		public void setOperateDesc(String operateDesc) {
			this.operateDesc = operateDesc;
		}

		public String getStatus() {
			return this.status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Date getCreateTime() {
			return this.createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public String getCreator() {
			return this.creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public Date getModifyTime() {
			return this.modifyTime;
		}

		public void setModifyTime(Date modifyTime) {
			this.modifyTime = modifyTime;
		}

		public String getModifier() {
			return this.modifier;
		}

		public void setModifier(String modifier) {
			this.modifier = modifier;
		}

		public String getRemark() {
			return this.remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}

		
}
