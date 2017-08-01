package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwSmsVO entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwSmsVO implements java.io.Serializable {

	// Fields

	private Long smsId;
	private String smsMobile;
	private String smsContent;
	private String sendStatus;
	private String sendResult;
	private Integer sendCount;
	private Date createTime;
	private Date sendTime;

	// Constructors

	/** default constructor */
	public GwSmsVO() {
	}

	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}

	public String getSmsMobile() {
		return smsMobile;
	}

	public void setSmsMobile(String smsMobile) {
		this.smsMobile = smsMobile;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public Integer getSendCount() {
		return sendCount;
	}

	public void setSendCount(Integer sendCount) {
		this.sendCount = sendCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
}