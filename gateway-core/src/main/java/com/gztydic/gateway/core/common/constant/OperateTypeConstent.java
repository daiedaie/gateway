package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: OperateTypeConstent 
 * @Description: TODO(操作日志相关全局变量) 
 * @author davis
 * @date 2014-11-27 上午9:49:31 
 *  
 */
public class OperateTypeConstent {

	/*操作类型-DICT_OPERATE_TYPE*/
	//用户注册-101,第一位表示模块，最后两位是功能序号
	public static final String REGIST_USER = "101";//用户注册
	public static final String ADD_USER= "102";//用户新增
	public static final String UPDATE_USER = "103";//用户修改
	public static final String CANCEL_USER = "104";//用户注销
	public static final String SEARCH_USER = "105";//用户查询
	public static final String AUDIT_USER = "106";//用户审核
	public static final String DETAIL_USER = "107";//用户明细
	public static final String ALLOT_USER= "108";//用户权限分配
	public static final String SEARCH_ORG = "109";//机构查询
	public static final String DETAIL_ORG = "110";//机构明细
	public static final String USERTYPR_MANAGE = "111";//用户类型管理
	public static final String USER_REGIST_AUDIT = "112";//用户注册审核
	public static final String USER_UPDATE_AUDIT = "113";//用户修改审核
	public static final String ADD_ROLE = "201";//群组新增
	public static final String SEARCH_ROLE = "202";//群组查询
	public static final String UPDATE_ROLE = "203";//群组修改
	public static final String PERMISSED_ROLE = "204";//群组授权
	public static final String DELETE_ROLE = "205";//群组删除
	public static final String SEARCH_UM = "301";//用户脱敏服务查看
	public static final String RULE_CONF = "302";//服务字段脱敏规则配置
	public static final String DETAIL_MODEL = "303";//服务信息明细
	public static final String INFO_UM_CONF = "304";//用户服务信息脱敏配置
	public static final String CYCLE_DATA = "305";//数据周期配置
	public static final String TIMER_CONF = "306";//定时器配置
	public static final String REPUSH_COUNT_CONF = "306";//定时器配置
	public static final String SEARCH_APP_UM = "401";//用户服务申请列表查看
	public static final String APP_UM = "402";//用户服务申请
	public static final String SEARCH_DATA_TASK = "403";//服务取数任务查看
	public static final String STOP_DATA_TASK = "404";//服务取数任务终止
	public static final String DOWNLOAD_DATA = "405";//服务数据下载
	public static final String SEARCH_MODEL = "406";//服务信息列表查看
	public static final String ADD_SERVICE = "407";//新增服务
	public static final String UPDATE_SERVICE = "408";//修改服务
	public static final String DELETE_SERVICE = "409";//删除服务
	public static final String REDO_SERVICE = "410";//重跑服务
	public static final String CREATE_SERVICE = "411";//确认创建服务
	public static final String SEARCH_DESEN_LOG = "501";//免责日志列表查看
	public static final String DETAIL_DESEN_LOG = "502";//免责日志明细
	public static final String SEARCH_DESEN_INFO = "503";//敏感信息追溯
	public static final String SEARCH_OPER_LOG = "504";//操作日志查询
	public static final String SEARCH_PLAN = "505";//待办列表查询
	public static final String DEAL_PLAN = "506";//待办处理
	public static final String DATA_AUDIT = "507";//取数审批
	public static final String RESULT_OUPUT_FIRST_AUDIT = "508";//结果输出一次审核
	public static final String RESULT_OUPUT_SECOND_AUDIT = "509";//结果输出一次审核
	
	/*操作类型*/
}
