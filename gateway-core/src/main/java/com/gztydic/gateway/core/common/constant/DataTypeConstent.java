package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: DataTypeConstent 
 * @Description: TODO(数据类型全局变量类) 
 * @author davis
 * @date 2014-12-16 下午5:40:08 
 *  
 */
public class DataTypeConstent {

	/*服务周期类型*/
	public static final String  OFFLINE = "1";//离线
	public static final String ONLINE = "0";//实时
	/*服务周期类型*/
	
	/*数据来源*/
	public static final String  DATACENTER = "2";//数据中心
	public static final String CACHE = "1";//缓存表
	/*数据来*/
	
	/*数据类型*/
	public static final String  FILE = "1";//文件
	public static final String TABLE = "2";//数据表
	/*数据类型*/
	
	/*流程类型*/
	public static final String  SHENQINGQUSHU = "1";//申请取数
	public static final String  XIUGAIGUIZHESHENPI = "2";//修改规则
	public static final String  FUWURENWUCHULI = "3";//服务任务
	/*流程类型*/
	
	/*流程状态*/
	public static final String  EXCUTING = "1";//执行中
	public static final String  COMPLETED = "0";//已结束
	/*流程状态*/
	
	/*进度*/
	public static final String  APPFETCH = "1";//申请取数
	public static final String  FIRSTAUDIT = "2";//一次审核
	public static final String  SECONDAUDIT = "3";//二次审核
	
	public static final String  MODIFYRULE = "1";//修改规则
	public static final String  M_FIRSTAUDIT = "2";//一次审核
	public static final String  M_SECONDAUDIT = "3";//二次审核
	
	public static final String  CREATE = "1";//创建
	public static final String  FETCH = "2";//取数
	public static final String  DATADEAL = "3";//数据处理
	public static final String  F_FIRSTAUDIT = "4";//一次审核
	public static final String  F_SECONDAUDIT = "5";//二次审核
	public static final String  DATASEND = "6";//数据推送
	public static final String  FINISHED = "7";//已推送

	/*进度*/
	
	/*流程步骤*/
	public static final String  S_FIRST = "11";//申请取数
	public static final String  S_SECOND = "12";//一次审核
	public static final String  S_THIRD = "13";//二次审核
	
	public static final String  X_FIRST  = "21";//修改规则
	public static final String  X_SECOND = "22";//一次审核
	public static final String  X_THIRD = "23";//二次审核
	
	public static final String  F_FIRST = "31";//创建
	public static final String  F_SECOND = "32";//取数
	public static final String  F_THIRD = "33";//开始服务数据脱敏处理
	public static final String  F_FOURTH  = "34";//开始服务数据合规检查
	public static final String  F_FIFTH = "35";//一次审核
	public static final String  F_SIXTH = "36";//二次审核
	public static final String  F_SEVENTH = "37";//开始数据推送
	/*流程步骤*/


}
