package com.gztydic.gateway.core.common.config;

import java.util.Properties;

/**、
 * 配置常量
 * @author Administrator
 *
 */
public class ConfigConstants {
	
	private static Properties p = new Properties();
	static{
		try {
			p.load(ConfigConstants.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("加载config.properties配置文件出错"+e.getMessage());
		}
	}

	//ftp配置
	public final static String SEQ_MODIFY_RECORD_BATCH= "SEQ_MODIFY_RECORD_BATCH";
	public final static String BASE_UPLOAD_FILE_PATH = p.getProperty("BASE_UPLOAD_FILE_PATH");
	public final static String FTP_SERVER_IP = p.getProperty("FTP_SERVER_IP");
	public final static String FTP_SERVER_PORT = p.getProperty("FTP_SERVER_PORT");
	public final static String FTP_SERVER_USER = p.getProperty("FTP_SERVER_USER");
	public final static String FTP_SERVER_PASSWORD = p.getProperty("FTP_SERVER_PASSWORD");
	public final static String FTP_108_ORIGINAL_FILEPATH = p.getProperty("FTP_108_ORIGINAL_FILEPATH");
	public final static String FTP_DATA_ORIGINAL_FILEPATH = p.getProperty("FTP_DATA_ORIGINAL_FILEPATH");
	public final static String GET_FILE_ROWS_COMMAND = p.getProperty("GET_FILE_ROWS_COMMAND");
	public final static String RENAME_FILE_COMMAND = p.getProperty("RENAME_FILE_COMMAND");
	
	//108的合规检查审核通过后将文件push到ftp
	public final static String FTP_108_IP = p.getProperty("FTP_108_IP");
	public final static String FTP_108_USER = p.getProperty("FTP_108_USER");
	public final static String FTP_108_PASSWORD = p.getProperty("FTP_108_PASSWORD");
	public final static String FTP_108_PATH = p.getProperty("FTP_108_PATH");
	
	//创建用户、密码、目录的shell
	public final static String FTP_CHANGE_PASSWD_SHELL = p.getProperty("FTP_CHANGE_PASSWD_SHELL");
	public final static String FTP_CREATE_USER_SHELL = p.getProperty("FTP_CREATE_USER_SHELL");
	public final static String FTP_CREATE_PATH_SHELL = p.getProperty("FTP_CREATE_PATH_SHELL");
	public final static String FTP_PUSH_FILE_SHELL = p.getProperty("FTP_PUSH_FILE_SHELL");
	//创建虚拟ftp用户和密码
	public final static String VUSER_FTP_CREATE_SHELL = p.getProperty("VUSER_FTP_CREATE_SHELL");
	public final static String VUSER_FTP_UPDATE_SHELL = p.getProperty("VUSER_FTP_UPDATE_SHELL");
	//ftp连接检查脚本
	public final static String FTP_CHECK_SHELL = p.getProperty("FTP_CHECK_SHELL");
	
	//挖掘平台webservice配置
	public final static String DATA_WEBSERVICE_URL = p.getProperty("DATA_WEBSERVICE_URL");
	public final static String DATA_SYSTEM_ID = p.getProperty("DATA_SYSTEM_ID");
	public final static String DATA_USERNAME = p.getProperty("DATA_USERNAME");
	public final static String DATA_PASSWORD = p.getProperty("DATA_PASSWORD");
	
	//数据特区webservice配置
	public final static String DATA_AREA_WEBSERVICE_URL = p.getProperty("DATA_AREA_WEBSERVICE_URL");
	public final static String DATA_AREA_BASE_WSDL = p.getProperty("DATA_AREA_BASE_WSDL");
	public final static String DATA_AREA_METHOD = p.getProperty("DATA_AREA_METHOD");
	
	//服务取数任务定时器编码
	public final static String DATA_FETCH_TASK_JOB_NO = p.getProperty("DATA_FETCH_TASK_JOB_NO");
	
	//短信接口
	public final static String SMS_WEBSERVICE_URL = p.getProperty("SMS_WEBSERVICE_URL");
	public final static String SMS_CUSTOMER_ID = p.getProperty("SMS_CUSTOMER_ID");
	public final static String SMS_CUSTOMER_PASSPORT = p.getProperty("SMS_CUSTOMER_PASSPORT");
	
	//虚拟ftp用户目录
	public final static String FTP_USER_PATH = p.getProperty("FTP_USER_PATH");
}
