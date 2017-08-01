package com.gztydic.gateway.core.common.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwSysDictDAO;
import com.gztydic.gateway.core.vo.GwSysDictVO;

/**
 * 字典管理
 *
 */
@Service
public class SysDictManage {
	
    private static final Log log = LogFactory.getLog(SysDictManage.class);
	
	private static GwSysDictDAO gwSysDictDAO;

	private static Map<String, Map<String, GwSysDictVO>> dictMap = null;
	
	/**
	 * 初始化字典数据
	 * @throws Exception
	 */
	public static void initSysDict(){
		try {
			List<GwSysDictVO> list = getGwSysDictDAO().searchSysDictList();
			Map<String, GwSysDictVO> map = null;
			dictMap = new HashMap<String, Map<String,GwSysDictVO>>();
			for (GwSysDictVO vo : list) {
				map = dictMap.get(vo.getDictCode());
				if(map == null) map = new LinkedHashMap<String, GwSysDictVO>();
				map.put(vo.getDictKey(), vo);
				
				dictMap.put(vo.getDictCode(), map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("初始化字典数据出错"+e.getMessage(),e);
		}
	}
	
	/**
	 * 获得字典组
	 * @param dictCode
	 * @return
	 */
	public static Map<String, GwSysDictVO> getSysDict(String dictCode){
		if(dictMap==null) initSysDict();
		return dictMap.get(dictCode);
	}
	
	/**
	 * 根据dictCode,dictKey取唯一字典
	 * @param dictCode
	 * @param dictKey
	 * @return
	 */
	public static GwSysDictVO getSysDict(String dictCode,String dictKey){
		Map<String, GwSysDictVO> map = getSysDict(dictCode);
		return map == null ? null : map.get(dictKey);
	}

	public static GwSysDictDAO getGwSysDictDAO() {
		return gwSysDictDAO;
	}

	@Resource
	public void setGwSysDictDAO(GwSysDictDAO gwSysDictDAO) {
		SysDictManage.gwSysDictDAO = gwSysDictDAO;
	}
}
