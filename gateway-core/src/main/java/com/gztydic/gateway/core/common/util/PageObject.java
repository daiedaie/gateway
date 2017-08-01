package com.gztydic.gateway.core.common.util;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class PageObject implements Serializable {
	
	private int pageSize = 10; // 每页的记录数

	private int curPage = 1; // 当前页

	private List data; // 当前页中存放的记录

	private int dataCount; // 总记录数

	private int pageCount;// 总页数
	
	private String sort = "";
	
	private String asc = "";
	
	private String defaultSort = "";
	
	private String numType;			//百分比或者数量	
	private int chooseDataCount;	//需要查询的数据量
	private int randomPage;
	private String orderby;		
	
	public PageObject() {}
	
	public PageObject(int pageSize){
		this.pageSize=pageSize;
	}

	//获取当前页数
	public int getCurPage() {
		if (curPage < 1) {
			curPage = 1;
		}
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	/**
	 * 获取总页数
	 * @return
	 */
	public int getPageCount() {
		if (dataCount > 0) {
			pageCount = dataCount % pageSize == 0 ? (dataCount / pageSize) : (dataCount / pageSize + 1);
		}else {
			pageCount = 0;
		}
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 获取每页的记录数
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 该页是否有下一页.
	 */
	public boolean hasNextPage() {
		return this.curPage < this.getPageCount();
	}

	/**
	 * 该页是否有上一页.
	 */
	public boolean hasPrePage() {
		return this.curPage > 1;
	}
	
	public int getPrePage() {
		return curPage <= 1 ? 1 : curPage-1;
	}
	
	public int getNextPage() {
		return curPage < getPageCount() ? curPage+1 : getPageCount();
	}

	
	/**
	 * 数据查询开始位置
	 * @return
	 */
	public int getBeginPoint() {
		return (getCurPage() - 1) * getPageSize();
	}

	/**
	 * 实例化
	 * @param request
	 * @return
	 */
	public static PageObject getInstance(HttpServletRequest request) {
		PageObject pageObject = new PageObject();
		pageObject.reqProperty(request);
		return pageObject;
	}
	
	/***
	 * 实例化，传每页显示记录数
	 * @param request
	 * @param pageSize
	 * @return
	 */
	public static PageObject getInstance(HttpServletRequest request,int pageSize){
		PageObject pageObject=new PageObject(pageSize);
		pageObject.reqProperty(request);
		return pageObject;
	}
	
	public void reqProperty(HttpServletRequest request) {
		String curPage = null, pageSize = null,dataCount=null;
		curPage = request.getParameter("curPage");//请求的页码
		if (curPage != null && curPage != "") {
			try {
				this.curPage = Integer.valueOf(curPage).intValue();
			} catch (NumberFormatException ex) {}
		}

		pageSize = request.getParameter("pageSize");//每页显示记录数
		if (pageSize != null && pageSize != "") {
			try {
				this.pageSize = Integer.valueOf(pageSize).intValue();
			} catch (NumberFormatException ex) {}
		}
		
		dataCount = request.getParameter("dataCount");//总记录数
		if (dataCount != null && dataCount != "") {
			try {
				this.dataCount = Integer.valueOf(dataCount).intValue();
			} catch (NumberFormatException ex) {}
		}
	}
	
	/**
	 * @description: 获取成分页页码闭区间，0为开始，1为结束
	 * @param:linkNumber 分页链接数量，默认(为0时)为6
	 * @return:
	 * @throws:
	 */
	public int[] getLinkInterregional(int linkNumber)
	{
		if(linkNumber<=0)
		{
			linkNumber=6;
		}
        //只有这样才会计算
        getPageCount();
		int left=(int) Math.floor((linkNumber-1)/2);
		int right=linkNumber-1-left;
		int start=curPage-left;
		int end=curPage+right;
		if(start<1)
		{
			end-=start-1;
			start=1;
			if(end>pageCount)
			{
				end=pageCount;
			}
		}
		else if(end>pageCount)
		{
			start-=end-pageCount;
			end=pageCount;
			if(start<1)
			{
				start=1;
			}
		}
		int returnInts[]=new int[]{start,end};
		return returnInts;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getNumType() {
		return numType;
	}

	public void setNumType(String numType) {
		this.numType = numType;
	}

	public int getChooseDataCount() {
		return chooseDataCount;
	}

	public void setChooseDataCount(int chooseDataCount) {
		this.chooseDataCount = chooseDataCount;
	}

	public int getRandomPage() {
		return randomPage;
	}

	public void setRandomPage(int randomPage) {
		this.randomPage = randomPage;
	}

	public String getDefaultSort() {
		return defaultSort;
	}

	public void setDefaultSort(String defaultSort) {
		this.defaultSort = defaultSort;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getAsc() {
		return asc;
	}

	public void setAsc(String asc) {
		this.asc = asc;
	}
}
