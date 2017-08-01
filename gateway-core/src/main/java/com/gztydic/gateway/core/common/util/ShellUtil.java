package com.gztydic.gateway.core.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
 * @ClassName: ShellUtil 
 * @Description: TODO(linux 命令执行类) 
 * @author davis
 * @date 2014-12-11 下午8:03:34 
 *  
 */
public class ShellUtil {
	private static JSch jsch;  
	private static Session session;

	/** 
	 * 连接到指定的IP 
	 *  
	 * @throws JSchException 
	 */  
	public static void connect(String user, String passwd, String host) throws JSchException {  
	    jsch = new JSch();  
	    session = jsch.getSession(user, host, 22);  
	    session.setPassword(passwd);  
	 
	    java.util.Properties config = new java.util.Properties();  
	    config.put("StrictHostKeyChecking", "no");  
	     session.setConfig(config);  

	    session.connect();  
	}  
	
	/** 
     * 执行相关的命令 
     * @throws JSchException  
     */  
    public static String execCmd(String command, String user, String passwd, String host) throws JSchException {  
       connect(user, passwd, host);  
       BufferedReader reader = null;  
       Channel channel = null;  
       String returnValue = null;
  
       try {
           if(command != null) {
               channel = session.openChannel("exec");  
               ((ChannelExec) channel).setCommand(command);  
               command = null;
               channel.setInputStream(null);  
               ((ChannelExec) channel).setErrStream(System.err);  
 
               channel.connect();  
               InputStream in = channel.getInputStream();  
               reader = new BufferedReader(new InputStreamReader(in));  
               String buf = null;  
               while ((buf = reader.readLine()) != null) {
            	   returnValue = buf;  
               }  
           }  
        } catch (IOException e) {  
           e.printStackTrace();  
        	} catch (JSchException e) {  
        		e.printStackTrace();  
	        } finally {  
		        try {  
		             reader.close();  
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }  
		        channel.disconnect();  
		        session.disconnect();  
	    }  
       return returnValue;
	}  

    public static void main(String[] args){
        String hostname = "10.1.24.131";
        String username = "root";
        String password = "123456";
        try {
			execCmd("/home/gateway/shell/pushFile.sh 10.1.24.131 test test / /home/users/testDataUser/result/10002-3-201503_output.tz 10002-3-201503_output.tz",username,password,hostname);
		} catch (JSchException e) {
			e.printStackTrace();
		}
    }
            


}
