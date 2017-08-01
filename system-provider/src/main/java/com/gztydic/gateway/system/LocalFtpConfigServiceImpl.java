package com.gztydic.gateway.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwLocalFtpConfigDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 本地Ftp配置
 * @author zjl
 *
 */
@Service
public class LocalFtpConfigServiceImpl extends GeneralServiceImpl<GwSysFtpVo> implements LocalFtpConfigService{

	private final Log log = LogFactory.getLog(LocalFtpConfigServiceImpl.class);
	@Resource
	private GwLocalFtpConfigDAO gwLocalFtpConfigDAO;
	public List<GwSysFtpVo> searchList() throws Exception{
		return gwLocalFtpConfigDAO.findAll();
	}
/*	public GwSysFtpVo searchFtpDetail(String ftpType) throws Exception {
		return gwLocalFtpConfigDAO.findByType(ftpType);
	}*/
	public List<GwSysFtpVo> searchFtpDetail(String ftpType) throws Exception{
		return gwLocalFtpConfigDAO.findByType( ftpType);
	}
	public GwSysFtpVo search(Class<GwSysFtpVo> clazz, Serializable id)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	private Map<Long, GwSysFtpVo> convert2Map(List<GwSysFtpVo> list){
		Map<Long, GwSysFtpVo> map = new HashMap<Long, GwSysFtpVo>();
		for (GwSysFtpVo vo : list) {
			map.put(vo.getId(), vo);
		}
		return map;
	}
	public void updateLocalFtpConfig(List<GwSysFtpVo> list, GwUserVO userVO)
			throws Exception {
		if(list.size() == 0) throw new Exception("数据存放本地FTP配置不能为空");
		try {
			Map<Long, GwSysFtpVo> cycleMap = convert2Map(searchList());
			for(GwSysFtpVo vo :list){
				GwSysFtpVo ftpviewVo = cycleMap.get(vo.getId());
				if(ftpviewVo==null){//新增
					super.save(vo);
				}else{//修改
					ftpviewVo.setId(vo.getId());
					ftpviewVo.setFtpIp(vo.getFtpIp());
					ftpviewVo.setFtpType(vo.getFtpType());
					ftpviewVo.setFtpPort(vo.getFtpPort());
					super.saveOrUpdate(ftpviewVo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("新增或修改本地ftp出错:"+e.getMessage(),e);
		}
	}

}
