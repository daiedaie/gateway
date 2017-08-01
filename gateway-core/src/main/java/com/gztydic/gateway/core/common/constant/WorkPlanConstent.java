package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: WorkPlanConstent 
 * @Description: TODO(待办相关的全局变量定义类) 
 * @author davis
 * @date 2014-11-25 下午12:07:15 
 *  
 */
public class WorkPlanConstent {
	
     /*任务类型*/
	public static final String REGISTE_AUDIT = "1";//注册审核
	public static final String CANCEL_AUDIT = "2";//注销审核
	public static final String UPDATE_AUDIT = "3";//修改审核
	public static final String PERMISS_ALLOT= "4";//权限分配
	public static final String INFO_DESEN_CONF = "5";//服务信息脱敏配置	数据用户申请后由审核人员审核，INFO_DESEN_CONF已作废
	public static final String FIELD_DESEN_CONF = "6";//服务字段脱敏配置
	public static final String GET_DATA_AUDIT = "7";//取数申请审核
	public static final String REGISTE_BACK = "8";//注册退回
	public static final String CANCEL_BACKT= "9";//注销退回
	public static final String GET_DATA_BACK = "10";//取数申请退回
	public static final String MODEL_INFO_APP = "11";//模型信息申请
	public static final String MODEL_INFO_BACK = "12";//模型信息申请退回
	public static final String UPDATE_BACK = "13";//修改申请退回
	public static final String INFO_DESEN_CONF_BACK = "14";//服务信息查看申请退回		
	public static final String INFO_DESEN_CONF_AUDIT = "15";//服务信息脱敏配置审核	
	public static final String INFO_DESEN_CONF_AUDIT_BACK = "16";//服务信息脱敏配置审核退回	数据用户申请后由审核人员审核，INFO_DESEN_CONF_AUDIT_BACK已作废
	public static final String FIELD_DESEN_CONF_AUDIT = "17";//服务字段脱敏配置审核
	public static final String FIELD_DESEN_CONF_AUDIT_BACK = "18";//服务字段脱敏配置审核退回
	public static final String RULE_CHECK_AUDIT_1 = "19";//数据安全管理员做合规检查审核
	public static final String RULE_CHECK_AUDIT_BACK = "20";//合规检查审核退回
	public static final String RULE_CHECK_AUDIT_2 = "21";//审核人员做合规检查审核
	public static final String DATA_REPUSH_SUCCESS = "22";//合规数据重新推送成功通知
	public static final String FTP_VUSER_COMMAND_EXE = "23";//ftp虚拟用户配置命令执行通知
	public static final String DATA_REPUSH_FAILURE = "24";//合规数据重新推送失败通知
	public static final String DATA_FIRST_PUSH_FAILURE = "25";//合规数据首次推送失败通知
	public static final String DATA_CLEAN = "26";//过期数据清理
	public static final String DATA_REPUSH_SUCCESS_FOR_DATA_USER = "27";//合规数据重新推送成功通知
	public static final String SYSTEM_BACKSTAGE_ERROR = "28";//系统后台错误
	public static final String SERVICE_FETCH_AUDIT = "29";//数据安全管理员新增服务并取数审核
	public static final String SERVICE_FETCH_AUDIT2 = "30";//审核人员新增服务并取数审核
	public static final String SERVICE_FETCH_AUDIT_BACK = "31";//新增服务并取数审核退回
	public static final String SERVICE_FETCH_CONFIRM = "32";//创建实时取数任务确认
	public static final String FETCH_APPLY_SUCCESS = "33";//取数申请成功并发G鉴权号给数据用户
	public static final String WEBSERVICE_RULE_CHECK_FAILURE = "34";//webservice合规检查失败
	 /*任务类型*/
	
	/*任务状态*/
	public static final String WAIT_FOR_DEAL = "1";//待处理
	public static final String DEAL_PASS = "2";//处理通过
	public static final String DEAL_NOPASS = "0";//不通过
	/*任务状态*/
	
	//根据用户类型返回管理员管辖的待办任务类型组合串
	public static String getPlanTypes(String userType){
		StringBuffer planTypes = new StringBuffer();
		if(GwUserType.AUDIT_USER.equals(userType)){
			planTypes.append("'"+WorkPlanConstent.REGISTE_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.CANCEL_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.UPDATE_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.GET_DATA_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.PERMISS_ALLOT+"'");
			planTypes.append(",'"+WorkPlanConstent.INFO_DESEN_CONF_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.FIELD_DESEN_CONF_AUDIT+"'");
			planTypes.append(",'"+WorkPlanConstent.RULE_CHECK_AUDIT_2+"'");
			planTypes.append(",'"+WorkPlanConstent.DATA_REPUSH_SUCCESS+"'");
		}else if(GwUserType.SAFE_USER.equals(userType)){
			planTypes.append("'"+WorkPlanConstent.FIELD_DESEN_CONF+"'");
			planTypes.append(",'"+WorkPlanConstent.INFO_DESEN_CONF+"'");
			planTypes.append(",'"+WorkPlanConstent.GET_DATA_BACK+"'");
			planTypes.append(",'"+WorkPlanConstent.FIELD_DESEN_CONF_AUDIT_BACK+"'");
			planTypes.append(",'"+WorkPlanConstent.RULE_CHECK_AUDIT_1+"'");
		}
		return planTypes.toString();
	}
}
