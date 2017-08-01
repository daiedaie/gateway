package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUserVO;
/**
 * 本地Ftp配置
 * @author zjl
 *
 */
public interface LocalFtpConfigService extends GeneralService<GwSysFtpVo>{
	
	/**
	 * 查询配置
	 * @return List<GwSysFtpVo>
	 * @throws Exception
	 */
	public List<GwSysFtpVo> searchList() throws Exception;
	public  List<GwSysFtpVo> searchFtpDetail(String ftpType) throws Exception;
	/**
	 * 新增、修改本地FTP配置
	 */
	public void updateLocalFtpConfig(List<GwSysFtpVo> list,GwUserVO userVO) throws Exception;
	
}
