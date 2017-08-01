package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: CommonState 
 * @Description: TODO(公用状态全局变量) 
 * @author davis
 * @date 2014-11-25 下午8:45:23 
 *  
 */
public class CommonState {

	/*审核状态*/
	public static final String WAIT_AUDIT = "1";//待审核
	public static final String PASS = "2";//通过
	public static final String NO_PASS = "0";//不通过
	public static final String REMODIFYRULE="3";//重新修改规则
	/*审核状态*/
	
	/*数据状态*/
	public static final String VALID = "1";//有效
	public static final String INVALID = "0";//无效
	/*数据状态*/
	
	/*结果数据状态*/
	public static final String NORMAL = "1";//正常
	public static final String OUTDAY = "2";//过期
	public static final String DELETE = "3";//已删除
	/*结果数据状态*/
	
	/*数据状态*/
	public static final String ONLINE = "1";//值班
	public static final String NOONLINE = "0";//非值班
	/*数据状态*/
	
	/**模型有效状态*/
	public static final String MODEL_STATUS_VALID = "1";
	/**模型失效状态*/
	public static final String MODEL_STATUS_INVALID = "0";
	
	/**服务有效状态*/
	public static final String SERVICE_STATUS_VALID = "1";
	/**服务失效状态*/
	public static final String SERVICE_STATUS_INVALID = "0";
	
	/**实时服务类型*/
	public static final String SERVICE_TYPE_IMMEDIATELY = "0";
	/**离线服务类型*/
	public static final String SERVICE_TYPE_TIMING = "1";
	
	/**文件类型-原始文件*/
	public static final String DATA_FILE_TYPE_ORIGINAL = "1";
	/**文件类型-输出文件*/
	public static final String DATA_FILE_TYPE_OUT = "2";
	/**文件类型-不合规文件*/
	public static final String DATA_FILE_TYPE_IRREGULAR = "3";
	/**文件类型-下载过来的原始文件*/
	public static final String DATA_FILE_TYPE_LOCAL = "4";
	
	/**字符替换脱敏规则类型*/
	public static final String DESEN_RULE_TYPE_CHAR = "1";
	/**范围替换脱敏规则类型*/
	public static final String DESEN_RULE_TYPE_RANGE = "2";
	
	/**数据来源—挖掘平台*/
	public static final String SERVICE_SOURCE_DATA = "1";
	/**数据来源—108项目*/
	public static final String SERVICE_SOURCE_108 = "2";
	
	/**合规检查规则，长度检查*/
	public static final String RULE_CHECK_TYPE_LENGTH = "1";
	/**合规检查规则，数据字典匹配检查*/
	public static final String RULE_CHECK_TYPE_DICT = "2";
	/**合规检查规则，数值范围检查*/
	public static final String RULE_CHECK_TYPE_RANGE = "3";
	
	/**脱敏规则类型，脱敏并合规检查*/
	public static final String DESEN_TYPE_DESEN = "1";
	/**脱敏规则类型，合规检查*/
	public static final String DESEN_TYPE_CHECK = "2";
	
	/**定时任务开关：开*/
	public static final String JOB_SWITCH_ON = "ON";
	/**定时任务开关：关*/
	public static final String JOB_SWITCH_OFF = "OFF";
	
	/**重做标记*/
	public static final String REDO_TAG = "1";
	
	/**单位-次数*/
	public static final String COUNT_UNIT = "0";
	
	/**单位-分钟*/
	public static final String MINUTE_OF_UNIT = "1";
	
	/**单位-秒*/
	public static final String SECOND_OF_UNIT = "2";
	
	/**配置类型-重新推送最多次数配置*/
	public static final String CONFIG_REPUSH_COUNT = "0";
	
	/**配置类型-重新推送时间间隔*/
	public static final String CONFIG_REPUSH_INTERVAL = "1";
	
	/**配置类型-任务脱敏/检查时间间隔*/
	public static final String TASK_CHECK_TIME_INTERVAL = "2";
	
	/**配置类型-源文件扫描时间间隔*/
	public static final String FILE_SACN_INTERVAL = "3";
	
	/**配置类型-短信重复发送次数*/
	public static final String CONFIG_SMS_COUNT = "4";
}
