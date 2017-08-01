package com.gztydic.gateway.web.tag;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.vo.GwSysDictVO;

/**
 * 字典列表标签
 *
 */
public class SysDictListTag extends TagSupport{
	
	private String dictCode;
	private String defaultValue;	//默认值
	private String tagType;
	private String tagName;
	private String tagId;

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			Map<String, GwSysDictVO> map = SysDictManage.getSysDict(dictCode);
			
			Iterator<String> it = map.keySet().iterator();
			GwSysDictVO vo = null;
			StringBuffer bf = new StringBuffer();
			while (it.hasNext()) {
				vo = map.get(it.next());
				
				if("checkbox".equals(tagType)){
					bf.append("<label><input type=\"checkbox\" name=\""+(tagName==null?vo.getDictCode():tagName)+"\" id=\""+(tagId==null?"":tagId)+"\" value=\""+vo.getDictKey()+"\" "+(defaultValue.equals(vo.getDictKey())?"checked=\"checked\"":"")+"/>"+vo.getDictValue()+"</label>");
				}else if("radio".equals(tagType)){
					bf.append("<label><input type=\"radio\" name=\""+(tagName==null?vo.getDictCode():tagName)+"\" id=\""+(tagId==null?"":tagId)+"\" value=\""+vo.getDictKey()+"\" "+(defaultValue.equals(vo.getDictKey())?"checked=\"checked\"":"")+"/>"+vo.getDictValue()+"</label>");
				}else if("option".equals(tagType) || "select".equals(tagType)){
					bf.append("<option value=\""+vo.getDictKey()+"\" "+(vo.getDictKey().equals(defaultValue)?"selected=\"selected\"":"")+">"+vo.getDictValue()+"</option>");
				}
			}
			String result = "";
			if("select".equals(tagType)){
				result += "<select name=\""+(tagName==null?vo.getDictCode():tagName)+"\" id=\""+(tagId==null?"":tagId)+"\">";
				result += "<option value=\"\">-请选择-</option>";
				result += bf.toString();
				result += "</select>";
			}else{
				result = bf.toString();
			}
			out.write(result);
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

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}
