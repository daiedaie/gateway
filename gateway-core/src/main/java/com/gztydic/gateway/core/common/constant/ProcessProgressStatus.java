package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: ProcessProgressStatus 
 * @Description: TODO(流程进度状态) 
 * @author michael
 * @date 2016-07-09 下午5:41:00 
 *  
 */
public class ProcessProgressStatus {
	
		
	/**创建*/
	public final static String CREATE_SERVICE = "1";
	/**修改规则*/
	public final static String UPDATE_RULE = "1";
	/**申请取数*/
	public final static String REQUEST_DATA = "1";
	/**取数*/
	public final static String GOT_DATA = "2";
	/**数据处理*/
	public final static String DATA_HANDLE = "3";
	/**一次审批*/
	public final static String RULE_CHECK_AUDIT = "4";
	/**二次审批*/
	public final static String RULE_CHECK_AUDIT_SECOND = "5";
	/**数据推送*/
	public final static String DATA_PUSHING = "6";
	/**已完成*/
	public final static String DATA_PUSHED = "7";
	
	
	
}
