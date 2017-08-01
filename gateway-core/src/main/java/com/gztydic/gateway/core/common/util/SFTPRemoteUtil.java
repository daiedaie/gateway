package com.gztydic.gateway.core.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPRemoteUtil {
	
	private static final Log log = LogFactory.getLog(SFTPRemoteUtil.class);
	
	/**
	 * 上传文件
	 * @param file
	 * @param fileName
	 * @return
	 */
	public static String upload(String host,int port,String userName,String password,File file,String fileName){
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		try{
			session = jsch.getSession(userName, host,port);
			session.setPassword(password);
			Properties properties = new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			session.connect();
			
			channel = (Channel)session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp)channel;
			
//			createPath(sftp,PATH); //如果路径不存在 先创建
			
			sftp.put(new FileInputStream(file),fileName);
			sftp.rename(fileName, getNewFileName(fileName)); //修改名字
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			channel.disconnect();
			session.disconnect();
			channel = null;
			session = null;
		}
		
		
		return null;
	}
	
	public static String getNewFileName(String fileName){
		String extName = fileName.substring(fileName.lastIndexOf("."),fileName.length());
		String newFileName = String.valueOf(System.currentTimeMillis());
		return newFileName+extName;
		
	}
	
	/**
	 * 删除文件
	 * @throws Exception 
	 */
	public static void deleteFile(String host,String userName,String password,int port,List<String> fileNameList) throws Exception{
		JSch jsch = new JSch();
		Session session  = null;
		Channel channel  = null;
		try{
			session = jsch.getSession(userName, host, port);
			session.setPassword(password);
			Properties properties = new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			session.setTimeout(10000);
			session.connect();
			
			channel = (Channel)session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp)channel;
			for (String fileName : fileNameList) {
				log.info("delete file="+fileName);
				try {
					if(StringUtils.isBlank(fileName) || "/".equals(fileName) || fileName.indexOf(".")==0){
						log.error("文件名："+fileName+"不符合规范，不能执行删除");
						continue;
					}
					sftp.rm(fileName);
				} catch (Exception e) {
					log.error("删除ftp文件("+fileName+")失败："+e.getMessage(),e);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("删除ftp文件失败："+e.getMessage(),e);
			throw e;
		}finally{
			if(channel != null){
				channel.disconnect();
				session.disconnect();
				channel = null;
			}
			session = null;
		}
	}
	

	/**
	 * 创建路径
	 * @param sftp
	 * @param PATH
	 * @throws SftpException 
	 */
	private static void createPath(ChannelSftp sftp,String path) throws SftpException {
		StringBuffer buffer = new StringBuffer();
		String[] paths = path.split("/");
		for(String name  : paths ){
			try {
				buffer.append("/").append(name);
				sftp.cd(buffer.toString());
			} catch (SftpException e) {
				sftp.mkdir(buffer.toString());
				sftp.cd(buffer.toString());
				e.printStackTrace();
			}
		}
	}
	
}
