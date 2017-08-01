package com.gztydic.gateway.core.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;

public class PushFtpUtil {

	private static final Log log = LogFactory.getLog(PushFtpUtil.class);
	
	public static boolean pushFileToFtp(GwModelDataFileVO dataFileVO) throws Exception {
		try {

			String command =ConfigConstants.FTP_PUSH_FILE_SHELL+ " "+dataFileVO.getFtpIp()+" \""+dataFileVO.getFtpUser()+"\" \""+dataFileVO.getFtpPassword()
					+"\" "+dataFileVO.getFilePath()+"/"+dataFileVO.getFileName()+" "+dataFileVO.getFileName();
			log.error("command="+command);
			String returnValue = ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
			log.error("returnValue="+returnValue);
			if(returnValue==null || returnValue.indexOf(dataFileVO.getFileName())==-1){
				log.error("taskId="+dataFileVO.getTaskId()+",fileId="+dataFileVO.getFileId()+" push文件到FTP("+dataFileVO.getFtpIp()+")失败，原因："+returnValue);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("108输出文件push到ftp失败："+e.getMessage());
			return false;
		}
	}
	
}
