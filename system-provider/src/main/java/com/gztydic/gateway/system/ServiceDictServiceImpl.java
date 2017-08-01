package com.gztydic.gateway.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwServiceDictDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDictAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceFieldDictDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwServiceDictVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictAuditVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

@Service
public class ServiceDictServiceImpl extends GeneralServiceImpl<GwServiceDictVO> implements ServiceDictService {
	
	@Resource
	private GwServiceDictDAO gwServiceDictDAO;
	
	@Resource
	private GwServiceFieldDictDAO gwServiceFieldDictDAO;
	
	@Resource
	private GwServiceFieldDictAuditDAO fieldDictAuditDAO;
	
	public List<GwServiceDictVO> searchGroupDict() throws Exception{
		return gwServiceDictDAO.searchGroupDict();
	}
	
	public Map<String, List<GwServiceDictVO>> searchDictMap() throws Exception{
		List<GwServiceDictVO> list = gwServiceDictDAO.searchDictList();
		Map<String, List<GwServiceDictVO>> map = new LinkedHashMap<String, List<GwServiceDictVO>>();
		for (GwServiceDictVO vo : list) {
			List dictList = map.get(vo.getDictCode());
			if(dictList == null) dictList = new ArrayList();
			dictList.add(vo);
			map.put(vo.getDictCode(), dictList);
		}
		return map;
	}

	public Map searchFieldDictMap(Long userId,GwServiceVO serviceVO) throws Exception {
		List<GwServiceFieldDictVO> list = gwServiceFieldDictDAO.searchList(userId,serviceVO);
		Map<String, String> map = new HashMap<String, String>();
		for (GwServiceFieldDictVO v : list) {
			//页面json解析key不能为数字
			map.put("fieldId"+v.getFieldId().toString(), v.getDictCode());
		}
		return map;
	}
	
	public Map searchFieldDictMapByBatch(Long batch) throws Exception {
		List<GwServiceFieldDictVO> list = gwServiceFieldDictDAO.searchListByBatch(batch);
		Map<String, String> map = new HashMap<String, String>();
		for (GwServiceFieldDictVO v : list) {
			//页面json解析key不能为数字
			map.put("fieldId"+v.getFieldId().toString(), v.getDictCode());
		}
		return map;
	}
	
	public Map searchFieldDictAuditMap(Long batch) throws Exception {
		List<GwServiceFieldDictAuditVO> list = fieldDictAuditDAO.searchListByBatch(batch);
		Map<String, String> map = new HashMap<String, String>();
		for (GwServiceFieldDictAuditVO v : list) {
			//页面json解析key不能为数字
			map.put("fieldId"+v.getFieldId().toString(), v.getDictCode());
		}
		return map;
	}
}
