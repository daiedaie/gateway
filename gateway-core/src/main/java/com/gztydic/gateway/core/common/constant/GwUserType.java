package com.gztydic.gateway.core.common.constant;

/** 
 * @ClassName: GwUserType 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Carson
 * @date 2014-11-20 下午5:41:00 
 *  
 */
public class GwUserType {
	/** 数据用户	 */
	public static final String DATA_USER = "dataUser";
	/** 机构用户	 */
	public static final String ORG_USER = "orgUser";
	/** 运维用户 */
	public static final String MAINATE_USER = "mainateUser";
	/** 审核用户 */
	public static final String AUDIT_USER = "auditUser";
	/** 数据安全管理员 */
	public static final String SAFE_USER = "safeUser";
	/** 超级管理员 */
	public static final String SUPER_USER = "superUser";
	
	public static String getUserTypeName(String userTypeCode){
		if(DATA_USER.equals(userTypeCode))
			return "数据用户";
		if(ORG_USER.equals(userTypeCode))
			return "机构用户";
		if(MAINATE_USER.equals(userTypeCode))
			return "运维用户";
		if(AUDIT_USER.equals(userTypeCode))
			return "审核管理员";
		if(SAFE_USER.equals(userTypeCode))
			return "数据安全管理员";
		if(SUPER_USER.equals(userTypeCode))
			return "超级管理员";
		return "";
	}
}
