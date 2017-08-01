package com.gztydic.gateway.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.vo.GwSysDictVO;

/**
 * 字典值标签
 *
 */
public class SysDictTag extends TagSupport{
	
	private String dictCode;
	private String dictKey;

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			GwSysDictVO dictVO = SysDictManage.getSysDict(dictCode, dictKey);
			if(dictVO != null){
				out.write(dictVO.getDictValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getDictKey() {
		return dictKey;
	}

	public void setDictKey(String dictKey) {
		this.dictKey = dictKey;
	}
}
