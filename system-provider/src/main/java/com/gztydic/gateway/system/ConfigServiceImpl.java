package com.gztydic.gateway.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.dao.ConfigDAO;
import com.gztydic.gateway.core.dao.GwSysCnfigDAO;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;

@Service
public class ConfigServiceImpl implements ConfigService {
	
	@Resource
	private ConfigDAO configDAO;
	@Resource
	private GwSysCnfigDAO gwSysCnfigDAO;
	
	public void updateJobTimer(Long jobNo,String proName,String times)throws Exception{
		configDAO.updateJobTimer(jobNo, proName, times);
		configDAO.callCreateDataTask();
	}
	
	public List searchNextDate(Long jobNo)throws Exception{
		return configDAO.searchNextDate(jobNo);
	}
	
	public GwSysCnfigVO findByConfigType(String configType)throws Exception{
		List<GwSysCnfigVO> list=gwSysCnfigDAO.findByConfigType(configType);
		return list!=null&&list.size()>0?list.get(0):null;
	}
	
	public void saveOrUpdateConfig(GwSysCnfigVO cnfigVO)throws Exception{
		List<GwSysCnfigVO> list=gwSysCnfigDAO.findByConfigType(cnfigVO.getConfigType());
		if(list!=null && list.size()==0){
			GwSysCnfigVO gwSysCnfigVO=new GwSysCnfigVO();
			gwSysCnfigVO.setConfigType(cnfigVO.getConfigType());
			gwSysCnfigVO.setConfigUnit(cnfigVO.getConfigUnit());
			gwSysCnfigVO.setConfigValue(cnfigVO.getConfigValue());
			gwSysCnfigDAO.save(gwSysCnfigVO);
		}else{
			GwSysCnfigVO gwSysCnfigVO=list.get(0);
			gwSysCnfigVO.setConfigUnit(cnfigVO.getConfigUnit());
			gwSysCnfigVO.setConfigValue(cnfigVO.getConfigValue());
			gwSysCnfigDAO.update(gwSysCnfigVO);
		}
	}
}
