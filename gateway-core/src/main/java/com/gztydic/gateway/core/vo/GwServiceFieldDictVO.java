package com.gztydic.gateway.core.vo;

/**
 * GwFieldDict entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwServiceFieldDictVO implements java.io.Serializable {

	// Fields

	private Long fieldId;
	private String dictCode;
	private Long userId;
	private Long desenId;

	// Constructors

	/** default constructor */
	public GwServiceFieldDictVO() {
	}

	/** minimal constructor */
	public GwServiceFieldDictVO(String dictCode) {
		this.dictCode = dictCode;
	}

	/** full constructor */
	public GwServiceFieldDictVO(Long fieldId, String dictCode) {
		this.fieldId = fieldId;
		this.dictCode = dictCode;
	}

	// Property accessors

	public Long getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getDictCode() {
		return this.dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GwServiceFieldDictVO))
			return false;
		GwServiceFieldDictVO castOther = (GwServiceFieldDictVO) other;

		return ((this.getFieldId() == castOther.getFieldId()) || (this
				.getFieldId() != null
				&& castOther.getFieldId() != null && this.getFieldId().equals(
				castOther.getFieldId())))
				&& ((this.getDictCode() == castOther.getDictCode()) || (this
						.getDictCode() != null
						&& castOther.getDictCode() != null && this
						.getDictCode().equals(castOther.getDictCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getFieldId() == null ? 0 : this.getFieldId().hashCode());
		result = 37 * result
				+ (getDictCode() == null ? 0 : this.getDictCode().hashCode());
		return result;
	}

	public Long getDesenId() {
		return desenId;
	}

	public void setDesenId(Long desenId) {
		this.desenId = desenId;
	}
}