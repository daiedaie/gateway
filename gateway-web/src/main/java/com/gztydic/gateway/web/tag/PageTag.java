package com.gztydic.gateway.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.gztydic.gateway.core.common.util.PageObject;

/**
 * 分页标签
 *
 */
public class PageTag extends TagSupport{
	
	private PageObject pageObject;
	
	private int linkNumber = 5;	//显示页码按钮数

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			out.write(getPageHtml());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	
	public String getPageHtml(){
		if(pageObject == null){
			pageObject = new PageObject();
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("<div class=\"page_wrap clearfix\">");
		bf.append("<div class=\"paginator\">");
		if(pageObject.getCurPage()==1)
			bf.append("<span class=\"page-start\">＜上一页</span>");
		else 
			bf.append("<a href=\"javascript:void(0)\" onclick=\"jumpPage('"+pageObject.getPrePage()+"')\">＜上一页</a>");
		
		int[] links = pageObject.getLinkInterregional(linkNumber);
		int start = links[0],end = links[1];
		for (int i = 1; i <= pageObject.getPageCount(); i++) {
			if(i==1){	//显示第一页
				bf.append("<a href=\"javascript:void(0)\" "+((i == pageObject.getCurPage())?"class=\"page-this\"":"onclick=jumpPage('1')")+">1</a>");
				if(start>2)	//当从第3页码开始时
					bf.append("<span>...</span>");
			}
			else if(i>=start && i<=end){	//在区间内才显示
				bf.append("<a href=\"javascript:void(0)\" "+((i == pageObject.getCurPage())?"class=\"page-this\"":"onclick=jumpPage('"+i+"')")+">"+i+"</a>");
			}
			else if(i==pageObject.getPageCount() && i > 1){	//显示最后页的页码
				if(end<(pageObject.getPageCount()-1))	//当区间最后一页小于倒数第二页
					bf.append("<span>...</span>");
				bf.append("<a href=\"javascript:void(0)\" "+((i == pageObject.getCurPage())?"class=\"page-this\"":"onclick=jumpPage('"+pageObject.getPageCount()+"')")+">"+pageObject.getPageCount()+"</a>");
			}
		}
		if(pageObject.getCurPage()==pageObject.getPageCount() || pageObject.getPageCount()==0)
			bf.append("<span class=\"page-start\">下一页＞</span>");
		else
			bf.append("<a href=\"javascript:void(0)\" onclick=\"jumpPage('"+pageObject.getNextPage()+"')\">下一页＞</a>");
		bf.append("<b>跳到第<input type=\"text\" id=\"jumpPageSearch\" value=\""+pageObject.getCurPage()+"\"/>页<input name=\"\" type=\"button\" value=\"确定\" onclick=\"jumpPage(jumpPageSearch.value)\"/></b>");
		
		bf.append("&nbsp;&nbsp;&nbsp;第"+pageObject.getCurPage()+"/"+pageObject.getPageCount()+"页，共"+pageObject.getDataCount()+"条记录");
		
		bf.append("<input type=\"hidden\" name=\"pageObject.curPage\" id=\"curPage\" value=\""+pageObject.getCurPage()+"\"/>"); 
		bf.append("<input type=\"hidden\" name=\"pageObject.pageSize\" id=\"pageSize\" value=\""+pageObject.getPageSize()+"\"/>");
		bf.append("<input type=\"hidden\" id=\"pageCount\" value=\""+pageObject.getPageCount()+"\"/>");
		bf.append("<input type=\"hidden\" name=\"pageObject.sort\" id=\"sort\" value=\""+pageObject.getSort()+"\"/>");
		bf.append("<input type=\"hidden\" name=\"pageObject.asc\" id=\"asc\" value=\""+pageObject.getAsc()+"\"/>");
		bf.append("<input type=\"hidden\" name=\"pageObject.defaultSort\" id=\"defaultSort\" value=\""+pageObject.getDefaultSort()+"\"/>");
		bf.append("</div></div>");
		return bf.toString();
	}
	
	public PageObject getPageObject() {
		return pageObject;
	}

	public void setPageObject(PageObject pageObject) {
		this.pageObject = pageObject;
	}

	public int getLinkNumber() {
		return linkNumber;
	}

	public void setLinkNumber(int linkNumber) {
		this.linkNumber = linkNumber;
	}
}
