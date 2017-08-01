package com.gztydic.gateway.core.common.constant;

/** 
 * 任务数据就绪状态
 */
public class DataProgressStatus {
	
	public final static String TASK_ERROR = "-1";
	
	/**准备取数*/
	public final static String READY_DATA = "0";
	
	/**已发起请求*/
	public final static String REQUEST_DATA = "1";
	
	/**挖掘平台数据生成*/
	public final static String DATA_READY = "2";
	
	/**开始取数*/
	public final static String START_DOWNLOAD = "3";
	
	/**完成取数*/
	public final static String DOWNLOAD_FINISH = "4";
	
	/**脱敏处理*/
	public final static String DATA_DESEN = "5";
	
	/**结果文件生成*/
	public final static String RESULT_FINISH = "6";
	
	/**合规检查中*/
	public final static String RULE_CHECK = "7";
	
	/**一次审核中*/
	public final static String RULE_CHECK_AUDIT = "8";
	
	/**合规检查文件非法*/
	public final static String RULE_CHECK_INVALID = "9";
	
	/**合规检查通过，文件就绪，可以下载*/
	public final static String RULE_CHECK_SUCCESS = "10";
	
	/**合规文件非法，非法服务终止*/
	public final static String RULE_CHECK_STOP = "11";
	
	/**合规检查全部通过并已推送文件到108，服务已送达*/
	public final static String RULE_CHECK_PUSHED = "12";
	
	/**合规检查通过，服务送达失败，重试中*/
	public final static String RULE_CHECK_REPUSH = "13";
	
	/**合规检查通过，文件推送中*/
	public final static String RULE_CHECK_PUSHING = "14";
	
	/**手工终止*/
	public final static String MANUAL_STOP = "15";
	
	/**二次审核中*/
	public final static String RULE_CHECK_AUDIT_SECOND = "16";
	
}
