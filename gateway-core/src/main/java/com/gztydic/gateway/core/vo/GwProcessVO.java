package com.gztydic.gateway.core.vo;

import java.util.Date;

import com.gztydic.gateway.core.common.constant.DataTypeConstent;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwProcessVO implements java.io.Serializable {

	// Fields

	private Long processId;
	private String processType;
	private Date createTime;
	private Date endTime;
	private String status;
	private String progressStatus;
	private String processTypeName;
	private String statusName;
	private String stepStatus;

	private Long ownId;
	// Constructors
	


	/** default constructor */
	public GwProcessVO() {
	}

	/** full constructor */
	public GwProcessVO(Long processId, String processType, Date createTime,
			Date endTime, String status, String progressStatus,Long ownId,String stepStatus) {
		super();
		this.processId = processId;
		this.processType = processType;
		this.createTime = createTime;
		this.endTime = endTime;
		this.status = status;
		this.progressStatus = progressStatus;
		this.ownId = ownId;
		this.stepStatus =stepStatus;
	}

	// Property accessors



	public Long getOwnId() {
		return ownId;
	}

	public void setOwnId(Long ownId) {
		this.ownId = ownId;
	}
	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}
	public Long getProcessId() {
		return processId;
	}	

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
		if(DataTypeConstent.SHENQINGQUSHU.equals(processType)){
			this.processTypeName="申请取数";
		}else if (DataTypeConstent.XIUGAIGUIZHESHENPI.equals(processType)) {
			this.processTypeName="修改规则";
		}else if (DataTypeConstent.FUWURENWUCHULI.equals(processType)) {
			this.processTypeName="服务任务";
		}
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		if(DataTypeConstent.EXCUTING.equals(status)){
			this.statusName="执行中";
		}else if (DataTypeConstent.COMPLETED.equals(status)) {
			this.statusName="已结束";
		}
	}

	public String getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	
	public String getProcessTypeName() {
		return processTypeName;
	}
	public String getStatusName() {
		return statusName;
	}
}