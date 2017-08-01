package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.vo.GwSysCnfigVO;

/**
 * 修改定时器
 * @author liangjiehui
 *
 */
public interface ConfigService{
	
	public void updateJobTimer(Long jobNo,String proName,String times)throws Exception;
	
	public List searchNextDate(Long jobNo)throws Exception;
	
	public GwSysCnfigVO findByConfigType(String configType)throws Exception;
	
	public void saveOrUpdateConfig(GwSysCnfigVO cnfigVO)throws Exception;
}
