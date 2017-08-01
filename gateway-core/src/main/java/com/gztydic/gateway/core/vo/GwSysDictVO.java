package com.gztydic.gateway.core.vo;

/**
 * GwSysDictId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwSysDictVO implements java.io.Serializable {

	// Fields

	private String dictCode;
	private String dictName;
	private String dictKey;
	private String dictValue;
	private Long reorder;

	// Constructors

	/** default constructor */
	public GwSysDictVO() {
	}

	/** full constructor */
	public GwSysDictVO(String dictCode, String dictName, String dictKey,
			String dictValue, Long reorder) {
		this.dictCode = dictCode;
		this.dictName = dictName;
		this.dictKey = dictKey;
		this.dictValue = dictValue;
		this.reorder = reorder;
	}

	// Property accessors

	public String getDictCode() {
		return this.dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getDictName() {
		return this.dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getDictKey() {
		return this.dictKey;
	}

	public void setDictKey(String dictKey) {
		this.dictKey = dictKey;
	}

	public String getDictValue() {
		return this.dictValue;
	}

	public void setDictValue(String dictValue) {
		this.dictValue = dictValue;
	}

	public Long getReorder() {
		return this.reorder;
	}

	public void setReorder(Long reorder) {
		this.reorder = reorder;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GwSysDictVO))
			return false;
		GwSysDictVO castOther = (GwSysDictVO) other;

		return ((this.getDictCode() == castOther.getDictCode()) || (this
				.getDictCode() != null
				&& castOther.getDictCode() != null && this.getDictCode()
				.equals(castOther.getDictCode())))
				&& ((this.getDictName() == castOther.getDictName()) || (this
						.getDictName() != null
						&& castOther.getDictName() != null && this
						.getDictName().equals(castOther.getDictName())))
				&& ((this.getDictKey() == castOther.getDictKey()) || (this
						.getDictKey() != null
						&& castOther.getDictKey() != null && this.getDictKey()
						.equals(castOther.getDictKey())))
				&& ((this.getDictValue() == castOther.getDictValue()) || (this
						.getDictValue() != null
						&& castOther.getDictValue() != null && this
						.getDictValue().equals(castOther.getDictValue())))
				&& ((this.getReorder() == castOther.getReorder()) || (this
						.getReorder() != null
						&& castOther.getReorder() != null && this.getReorder()
						.equals(castOther.getReorder())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getDictCode() == null ? 0 : this.getDictCode().hashCode());
		result = 37 * result
				+ (getDictName() == null ? 0 : this.getDictName().hashCode());
		result = 37 * result
				+ (getDictKey() == null ? 0 : this.getDictKey().hashCode());
		result = 37 * result
				+ (getDictValue() == null ? 0 : this.getDictValue().hashCode());
		result = 37 * result
				+ (getReorder() == null ? 0 : this.getReorder().hashCode());
		return result;
	}

}