package com.gztydic.gateway.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;


@Repository
public class DataGatherDAO extends HibernateGenericDao {
	
	/**
	 * 查询离线服务预览抽样数据
	 * 数据结构map[rowid][字段编码]
	 * @param serviceId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchOfflinePreviewServiceData(GwModelDataFetchTaskVO taskVO,PageObject pageObject) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String gatewayFieldCode = "gateway_"+taskVO.getFieldCode();
			con = super.getCurrentSession().connection();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			//先抽样查出原始数据，再根据rowid查出脱敏数据
			String dataSql = "select * from gw_service_"+taskVO.getServiceId()+" where "+gatewayFieldCode+"="+taskVO.getFieldValue();
			dataSql = "select row_.* from (select rownum rownum1,t.* from ("+dataSql+") t) row_ order by row_.rownum1 ";
			int count = super.findCountBySql(dataSql, null);
			if(!"random".equals(pageObject.getOrderby()) && pageObject.getOrderby() != null) dataSql += pageObject.getOrderby();
			
	        dataSql = "select * from ("+dataSql+") tmp where 1=1 ";
	        if(pageObject.getChooseDataCount() > 0){	//抽取数据量
	        	if("%".equals(pageObject.getNumType())){	//按百分比取数
	        		pageObject.setChooseDataCount(count * pageObject.getChooseDataCount() / 100);
	        		if(pageObject.getChooseDataCount()<=0)	//数据量太少百分比后可能为0
	        			pageObject.setChooseDataCount(1);
	        	}
	        	
				if("random".equals(pageObject.getOrderby())){
					//随机取页码数
			        int pageCount = count / pageObject.getChooseDataCount();
			        
					int currPage = pageObject.getRandomPage()==0?(int)(Math.random()*pageCount)+1:pageObject.getRandomPage();		//总页数中取随机页数
					dataSql += "and tmp.rownum1 > "+(currPage-1)*pageObject.getChooseDataCount()+
							   " and tmp.rownum1 <= "+currPage*pageObject.getChooseDataCount();
					pageObject.setRandomPage(currPage);
				}else if("desc".equals(pageObject.getOrderby())){
	        		dataSql += "and tmp.rownum1 > "+(count-pageObject.getChooseDataCount());	//从后往前取
	        	}else {
	        		dataSql += "and tmp.rownum1 <= "+pageObject.getChooseDataCount();	//从前往后取数
		        }
	        }
	        
			count = super.findCountBySql(dataSql, null);	//重新计算总数
	        int pageCount = count / pageObject.getPageSize();
			if(count % pageObject.getPageSize() != 0) pageCount++;
	        pageObject.setDataCount(count);
	        pageObject.setPageCount(pageCount);
			
	        int start = 0,end = 0;
	        start = (pageObject.getCurPage()-1) * pageObject.getPageSize();
	        end = pageObject.getCurPage() * pageObject.getPageSize();
	        //先查需要抽取的数据，然后再分页
			dataSql = "select * from (select rownum rownum2,tmp2.* from ("+dataSql+") tmp2 ) tmp3 " +
					"where tmp3.rownum2 > "+start+" and tmp3.rownum2<="+end;
			
			logger.info("查询原始服务数据sql="+dataSql);
			ps = con.prepareStatement(dataSql);
			rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			Map<Integer, String> dataCodeMap = new LinkedHashMap<Integer, String>();
			String columnName = "";
			for(int i=1;i<=columnCount;i++){
				columnName = metaData.getColumnName(i);
				if(isServiceField(columnName))//判断是不是服务自身的字段，不是的忽略
					dataCodeMap.put(i,columnName);
			}
			Map dataValueMap = new LinkedHashMap();
			Map<String, Object> originalDataMap = new LinkedHashMap<String, Object>();
			String dataRowIds = "";
			while(rs.next()){
				String rowid = rs.getString("gateway_row_id");
				dataRowIds += (dataRowIds.equals("")?"":",")+"'"+rowid+"'";
				dataValueMap = new LinkedHashMap();
				for(int i=1;i<=columnCount;i++){
					columnName = dataCodeMap.get(i);
					if(columnName != null)
						dataValueMap.put(columnName, rs.getObject(i));
				}
				originalDataMap.put("rowid_"+rowid, dataValueMap);	//原始数据map[rowid][字段编码]
			}
			resultMap.put("dataFieldMap", dataCodeMap);
			resultMap.put("originalDataMap", originalDataMap);
			
			if(StringUtils.isNotBlank(dataRowIds)){
				//根据rowid查询分页的数据
				String desenCountSql = "select * from gw_service_desen_"+taskVO.getServiceId()+" where gateway_task_id="+taskVO.getTaskId(); 
				String desenDataSql = "select * from gw_service_desen_"+taskVO.getServiceId()+" where gateway_task_id="+taskVO.getTaskId()+" and "+gatewayFieldCode+"="+taskVO.getFieldValue() + 
									  " and gateway_row_id in ("+dataRowIds+")";  
				int desenDataCount = super.findCountBySql(desenCountSql, null);
				
				logger.info("查询脱敏服务数据sql="+desenDataSql);
				ps = con.prepareStatement(desenDataSql);
				rs = ps.executeQuery();
				
				//脱敏数据的表结构元数据
				metaData = rs.getMetaData();
				columnCount = metaData.getColumnCount();
				Map<Integer, String> desenCodeMap = new LinkedHashMap<Integer, String>();
				columnName = "";
				for(int i=1;i<=columnCount;i++){
					columnName = metaData.getColumnName(i);
					if(isServiceField(columnName))//判断是不是服务自身的字段，不是的忽略
						desenCodeMap.put(i,columnName);
				}
				
				Map<String, Object> desenDataMap = new LinkedHashMap<String, Object>();
				while(rs.next()){
					String rowid = rs.getString("gateway_row_id");
					dataValueMap = new LinkedHashMap();
					for(int i=1;i<=columnCount;i++){
						columnName = desenCodeMap.get(i);
						if(columnName != null)
						dataValueMap.put(columnName, rs.getObject(i));
					}
					desenDataMap.put("rowid_"+rowid, dataValueMap);
				}
				resultMap.put("desenDataCount", desenDataCount);
				resultMap.put("desenDataMap", desenDataMap);
				
				//脱敏字段数
				String desenColumnCountSql = "select count(1) from gw_desen_service_field where service_id="+taskVO.getServiceId()+" and user_id="+taskVO.getUserId();
				int desenColumnCount = super.findIntBySql(desenColumnCountSql, null);
				resultMap.put("desenColumnCount", desenColumnCount);
			}
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//判断是不是服务自身的字段，不是的忽略
	private boolean isServiceField(String columnName){
		//用于排序的字段
		if("ROWNUM1".equals(columnName) || "ROWNUM2".equals(columnName) || "RID".equals(columnName) 
				|| "GATEWAY_ROW_ID".equals(columnName) || "GATEWAY_TASK_ID".equals(columnName) 
				|| "GATEWAY_YEAR_ID".equals(columnName) || "GATEWAY_MONTH_ID".equals(columnName) //自定义的服务周期字段
				|| "GATEWAY_QUARTER_ID".equals(columnName) || "GATEWAY_WEEK_ID".equals(columnName) || "GATEWAY_DAY_ID".equals(columnName))
			return false;
		return true;
	}
	
	public  Map<String, String> searchServiceName(Long serviceId)throws Exception{
		Map< String, String> map = new HashMap<String, String>();
		String sql = "select field_code,field_name from GW_SERVICE_FIELD where service_id = " +serviceId;	
		List<Object[]> list=super.findListBySql(sql, null, null);
		if(list != null){
			List<GwServiceFieldVO> taskList = new ArrayList<GwServiceFieldVO>();
			GwServiceFieldVO vo = null;
			for(Object[] obj : list){
				vo = new GwServiceFieldVO();
				vo.setFieldCode(obj[0]==null?null:String.valueOf(obj[0]));
				vo.setFieldName(obj[1]==null?null:String.valueOf(obj[1]));
				taskList.add(vo);
			}
			for(GwServiceFieldVO gwServiceFieldVO : taskList){
				map.put(gwServiceFieldVO.getFieldCode(), gwServiceFieldVO.getFieldName());
			}
		}
		return map;
	}
	
	/**
	 * 将实时服务输出数据保存到缓存表
	 * @param serviceVO
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int saveOnlineServiceData(GwServiceVO serviceVO,GwModelDataFetchTaskVO taskVO, List<Map<String, String>> data) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			String gatewayFieldCode = "gateway_task_id";
			String gatewayPrimaryKey = "gateway_row_id";	//自定义主键ID，用于抽样、合规检查
			String serviceIsExist = "select COUNT(1) from all_tables t where t.table_name='GW_SERVICE_"+serviceVO.getServiceId()+"'";
			//判断缓存表是否存在，不存在则创建
			if(super.findIntBySql(serviceIsExist, null) == 0){
				String fieldSql = "select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID=? order by field_code ";
				List<Object[]> fieldList = super.findListBySql(fieldSql, new Object[]{serviceVO.getServiceId()}, null);
				List<String> createFieldList = new ArrayList<String>();
				createFieldList.add(gatewayPrimaryKey+" number");	
				createFieldList.add(gatewayFieldCode+" varchar2(50)");
				for (Object[] obj : fieldList) {
					createFieldList.add(obj[0].toString() + " " + obj[1].toString());
				}
				String createTableSql = "create table GW_SERVICE_"+serviceVO.getServiceId()+" ("+StringUtils.join(createFieldList.toArray(new String[]{}), ",")+")";
				super.executeSql(createTableSql, null);	//创建缓存表
				
				//创建组合主键约束
				String primaryKey = "alter table GW_SERVICE_"+serviceVO.getServiceId()+" add constraint " +
						   			"PK_service_"+serviceVO.getServiceId()+" primary key("+gatewayPrimaryKey+","+gatewayFieldCode+")";
				super.executeSql(primaryKey,null);
			}
			
			//根据输出的字段+值组装sql
			List<String> fieldList = new ArrayList<String>();
			fieldList.add(gatewayPrimaryKey);	//第一列自定义主键字段 GATEWAY_ROW_ID
			fieldList.add(gatewayFieldCode);	//第二列自定义字段 gateway_task_id
			String params = "?,?";
			List<List<String>> valueList = new ArrayList<List<String>>();
			List<String> values = null;
			for (int i=0;i<data.size();i++) {
				Map<String, String> map = data.get(i);
				values = new ArrayList<String>();
				values.add(String.valueOf(i+1));	//第一列自定义字段GATEWAY_ROW_ID的值
				values.add(taskVO.getTaskId().toString());	//第二列自定义字段gateway_task_id的值
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = map.get(key);
					if(i==0) {
						fieldList.add(key);	//第一条数据的key作为字段名
						params += ",?";
					}
					values.add(value);
				}
				valueList.add(values);
			}
			if(valueList.size() > 0){
				String insertSql = "insert into GW_SERVICE_"+serviceVO.getServiceId()+"("+StringUtils.join(fieldList.toArray(new String[]{}), ",")+") " +
						"values("+params+")";
				con = super.getCurrentSession().connection();
				con.setAutoCommit(false);
				ps = con.prepareStatement(insertSql);
				for (int i=0;i<valueList.size();i++) {
					values = valueList.get(i);	//批处理，设置参数
					for (int j = 0; j < values.size(); j++) {
						ps.setObject(j+1, values.get(j));
					}
					ps.addBatch();
					
					if(i % 50 == 0) {
						ps.executeBatch();
						ps.clearBatch();
					}
				}
				ps.executeBatch();
				con.commit();
				return 1;
			}
			return 0;
		} catch (Exception e) {
			if(con != null) con.rollback();
 			throw new Exception("新增缓存数据失败："+e.getMessage(), e);
 		} finally {
			try {
				if(con != null) con.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				logger.error("关闭数据库连接出错", e2);
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 将脱敏后的数据保存到缓存表
	 * @param serviceVO
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public int saveOnlineServiceDesenData(GwServiceVO serviceVO, List<Map<String, String>> data) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			String gatewayFieldCode = "gateway_task_id";
			String gatewayPrimaryKey = "gateway_row_id";	//自定义主键ID，用于抽样、合规检查
			String serviceIsExist = "select COUNT(1) from all_tables t where t.table_name='GW_SERVICE_DESEN_"+serviceVO.getServiceId()+"'";
			//判断缓存表是否存在，不存在则创建
			if(super.findIntBySql(serviceIsExist, null) == 0){
				String fieldSql = "select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID=? order by field_code ";
				List<Object[]> fieldList = super.findListBySql(fieldSql, new Object[]{serviceVO.getServiceId()}, null);
				List<String> createFieldList = new ArrayList<String>();
				createFieldList.add(gatewayPrimaryKey+" number");	
				createFieldList.add(gatewayFieldCode+" varchar2(50)");
				for (Object[] obj : fieldList) {
					createFieldList.add(obj[0].toString() + " " + obj[1].toString());
				}
				String createTableSql = "create table GW_SERVICE_DESEN_"+serviceVO.getServiceId()+" ("+StringUtils.join(createFieldList.toArray(new String[]{}), ",")+")";
				super.executeSql(createTableSql, null);	//创建缓存表
				
				String primaryKey = "alter table GW_SERVICE_DESEN_"+serviceVO.getServiceId()+" add constraint " +
						   			"PK_service_desen_"+serviceVO.getServiceId()+" primary key("+gatewayPrimaryKey+","+gatewayFieldCode+")";
				super.executeSql(primaryKey,null);
			}
			//根据输出的字段+值组装sql
			List<String> fieldList = new ArrayList<String>();
			String params = "";
			List<List<String>> valueList = new ArrayList<List<String>>();
			List<String> values = null;
			for (int i=0;i<data.size();i++) {
				Map<String, String> map = data.get(i);
				values = new ArrayList<String>();
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = map.get(key);
					if(i==0) {
						fieldList.add(key);	//第一条数据的key作为字段名
						params += ("".equals(params)?"":",")+"?";
					}
					values.add(value);
				}
				valueList.add(values);
			}
			if(valueList.size() > 0){
				String insertSql = "insert into GW_SERVICE_DESEN_"+serviceVO.getServiceId()+"("+StringUtils.join(fieldList.toArray(new String[]{}), ",")+") " +
						"values("+params+")";
				con = super.getCurrentSession().connection();
				con.setAutoCommit(false);
				ps = con.prepareStatement(insertSql);
				for (int i=0;i<valueList.size();i++) {
					values = valueList.get(i);	//批处理，设置参数
					for (int j = 0; j < values.size(); j++) {
						ps.setObject(j+1, values.get(j));
					}
					ps.addBatch();
					
					if(i % 50 == 0) {
						ps.executeBatch();
						ps.clearBatch();
					}
				}
				ps.executeBatch();
				con.commit();
				return 1;
			}
			return 0;
		} catch (Exception e) {
			if(con != null) con.rollback();
			e.printStackTrace();
 			throw new Exception("新增缓存数据失败："+e.getMessage(), e);
 		} finally {
			try {
				if(con != null) con.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				logger.error("关闭数据库连接出错", e2);
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 保存不合规记录
	 * @param serviceVO
	 * @param gateway_task_id
	 * @param gateway_row_id
	 * @param row_data
	 * @return
	 * @throws Exception
	 */
	public int saveOnlineServiceCheckRecord(Long serviceId, Long gateway_task_id,Long gateway_row_id,String row_data) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		try {
			String serviceIsExist = "select COUNT(1) from all_tables t where t.table_name='GW_SERVICE_CHECK_"+serviceId+"'";
			//判断缓存表是否存在，不存在则创建
			if(super.findIntBySql(serviceIsExist, null) == 0){
				String createTableSql = "create table GW_SERVICE_CHECK_"+serviceId+" (gateway_row_id number,gateway_task_id number,row_data varchar2(4000))";
				super.executeSql(createTableSql, null);	//创建缓存表
				//建表后加索引
				String primaryKey = "alter table GW_SERVICE_CHECK_"+serviceId+" add constraint " +
						   			"uq_gw_service_check_"+serviceId+" primary key(gateway_task_id,gateway_row_id)";
				super.executeSql(primaryKey,null);
			}
			String insertSql = "insert into GW_SERVICE_CHECK_"+serviceId +"values("+gateway_row_id+","+gateway_task_id+",'"+row_data+"')";
			con = super.getCurrentSession().connection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(insertSql);
			con.commit();
			return 1;
		} catch (Exception e) {
			if(con != null) con.rollback();
			e.printStackTrace();
 			throw new Exception("新增缓存数据失败："+e.getMessage(), e);
 		} finally {
			try {
				if(con != null) con.close();
				if(ps != null) ps.close();
			} catch (Exception e2) {
				logger.error("关闭数据库连接出错", e2);
				e2.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * 获取实时服务的缓存数据
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, String>> searchOnlineServiceCacheData(GwModelDataFetchTaskVO taskVO,Map<String, String> ignoreFieldMap) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String gatewayFieldCode = "GATEWAY_TASK_ID";
			con = getCurrentSession().connection();
			String sql = "select * from gw_service_"+taskVO.getServiceId()+" where "+gatewayFieldCode+"="+taskVO.getTaskId();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			
			//缓存表的服务输出字段名字
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			String columnName = "";
			Object columnValue = null;
			List<String> columnList = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				columnName = metaData.getColumnName(i);
				if(ignoreFieldMap==null||ignoreFieldMap.get(columnName) == null)
					columnList.add(columnName);
			}
			
			//读取缓存表的数据，组装成List<Map<String, String>>格式
			List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
			Map<String, String> dataMap = null;
			while (rs.next()) {
				dataMap = new LinkedHashMap<String, String>();
				for (int i = 0; i < columnList.size(); i++) {
					columnName = columnList.get(i);
					columnValue = rs.getObject(columnName);
					dataMap.put(columnName, columnValue==null?"":String.valueOf(columnValue));
				}
				dataList.add(dataMap);
			}
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("获取实时服务缓存数据出错", e);
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(con != null) con.close();
			} catch (Exception e2) {
				logger.error("关闭数据库连接出错", e2);
			}
		}
	}
	
	/**
	 * 获取实时服务的脱敏后的数据
	 * @param taskId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, String>> searchOnlineServiceDesenData(GwModelDataFetchTaskVO taskVO,Map<String, String> ignoreFieldMap) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String gatewayFieldCode = "GATEWAY_TASK_ID";
			con = getCurrentSession().connection();
			String sql = "select * from gw_service_desen_"+taskVO.getServiceId()+" where "+gatewayFieldCode+"="+taskVO.getTaskId();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			
			//缓存表的服务输出字段名字
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			String columnName = "";
			Object columnValue = null;
			List<String> columnList = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				columnName = metaData.getColumnName(i);
				if(ignoreFieldMap==null||ignoreFieldMap.get(columnName) == null)
					columnList.add(columnName);
			}
			
			//读取缓存表的数据，组装成List<Map<String, String>>格式
			List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
			Map<String, String> dataMap = null;
			while (rs.next()) {
				dataMap = new LinkedHashMap<String, String>();
				for (int i = 0; i < columnList.size(); i++) {
					columnName = columnList.get(i);
					columnValue = rs.getObject(columnName);
					dataMap.put(columnName, columnValue==null?"":String.valueOf(columnValue));
				}
				dataList.add(dataMap);
			}
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("获取实时服务缓存数据出错", e);
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(con != null) con.close();
			} catch (Exception e2) {
				logger.error("关闭数据库连接出错", e2);
			}
		}
	}
	
	/**
	 * 根据taskId分页查找不合规的数据
	 * @param taskVO
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchRuleCheckAuditList(GwServiceVO serviceVO,Long taskId,PageObject pageObject) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = super.getCurrentSession().connection();
			List<Map> list=new ArrayList<Map>();
			String dataSql = "select * from gw_service_check_"+serviceVO.getServiceId()+" where GATEWAY_TASK_ID= "+taskId+" and gateway_row_id>0 order by gateway_row_id";
			dataSql = "select row_.* from (select rownum rownum1,t.* from ("+dataSql+") t) row_ order by row_.rownum1 ";
			int count = super.findCountBySql(dataSql, null);
			pageObject.setPageSize(20);
	        int pageCount = count / pageObject.getPageSize();
			if(count % pageObject.getPageSize() != 0) pageCount++;
	        pageObject.setDataCount(count);
	        pageObject.setPageCount(pageCount);
			
	        int start = 0,end = 0;
	        start = (pageObject.getCurPage()-1) * pageObject.getPageSize();
	        end = pageObject.getCurPage() * pageObject.getPageSize();
	        //先查需要抽取的数据，然后再分页
			dataSql = "select * from (select rownum rownum2,tmp2.* from ("+dataSql+") tmp2 ) tmp3 " +
					"where tmp3.rownum2 > "+start+" and tmp3.rownum2<="+end;
			
			ps = con.prepareStatement(dataSql);
			rs = ps.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			Map<Integer, String> dataCodeMap = new LinkedHashMap<Integer, String>();
			String columnName = "";
			for(int i=1;i<=columnCount;i++){
				columnName = metaData.getColumnName(i);
				if(!"ROWNUM1".equals(columnName) && !"ROWNUM2".equals(columnName) && !"GATEWAY_TASK_ID".equals(columnName))//判断是不是服务自身的字段，不是的忽略
					dataCodeMap.put(i,columnName);
			}
			Map dataValueMap = new LinkedHashMap();
			/*while(rs.next()){
				dataValueMap = new LinkedHashMap();
				for(int i=1;i<=columnCount;i++){
					columnName = dataCodeMap.get(i);
					if(columnName != null)
						dataValueMap.put(columnName, rs.getObject(i));
					
				}
				
				list.add(dataValueMap);
			}*/
			//update by davis
			while(rs.next()){
				dataValueMap = new LinkedHashMap();
				if(rs.getObject(3) != null){
					dataValueMap.put("ROW_ID", rs.getObject(3));
				}
				if(rs.getObject(5) != null){
					String resultStr = rs.getObject(5).toString();
					String split = "\\|";
					if(CommonState.SERVICE_SOURCE_DATA.equals(serviceVO.getServiceSource())) split = ",";
					String[] filedArr = resultStr.split(split);
					for(int i=1;i<=filedArr.length;i++){
						columnName = ""+i+"";
						dataValueMap.put(columnName, filedArr[i-1]);
					}
				}
				
				list.add(dataValueMap);
			}
			pageObject.setData(list);
			return pageObject;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//根据taskId查询出所有不合规的字段
	@SuppressWarnings("unchecked")
	public Map searchRuleCheckAuditField(GwModelDataFetchTaskVO taskVO,String rowId) throws Exception{
		GwModelDataFetchVO fetchVO = super.findById(GwModelDataFetchVO.class, null, taskVO.getFetchId());
		
		String sql="select c.row_id,c.field_sort,c.check_type,c.check_rule,c.rule_type,c.rule_content,c.replace_content,c.condition_type,c.condition_content  " +
				"from gw_service_check_record c " +
				"where c.task_id="+taskVO.getTaskId()+" and c.row_id in("+rowId+")";
		List<Object[]> fieldList=super.findListBySql(sql, null, null);
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map recordMap = new HashMap();
		Map<String,String> rowMap=null;
		GwSysDictVO dictVO = null;
		for(Object[] obj:fieldList){
			rowId = obj[0].toString();
			String fieldCode = obj[1].toString();
			rowMap = (Map<String, String>)recordMap.get("row_"+rowId);
			if(rowMap == null) rowMap = new HashMap<String, String>();
			rowMap.put("fieldCode_"+fieldCode, fieldCode);
			
			//合规检查的不合规规则
			if(CommonState.DESEN_TYPE_CHECK.equals(fetchVO.getDesenType())) {
				dictVO = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE", obj[2]==null?"":obj[2].toString());
				rowMap.put("checkType_"+fieldCode, dictVO==null?"":dictVO.getDictValue());
				rowMap.put("checkRule_"+fieldCode, obj[3]==null?"":obj[3].toString());
			}else if(CommonState.DESEN_TYPE_DESEN.equals(fetchVO.getDesenType())){
				//脱敏并合规检查的不合规规则
				dictVO = SysDictManage.getSysDict("DICT_DESEN_RULE_TYPE", obj[4]==null?"":obj[4].toString());
				rowMap.put("ruleType_"+fieldCode, dictVO==null?"":dictVO.getDictValue());
				rowMap.put("ruleContent_"+fieldCode, obj[5]==null?"":obj[5].toString());
				rowMap.put("replaceContent_"+fieldCode, obj[6]==null?"":obj[6].toString());
				rowMap.put("conditionType_"+fieldCode, obj[7]==null?"":obj[7].toString());
				rowMap.put("conditionContent_"+fieldCode, obj[8]==null?"":obj[8].toString());
			}
			recordMap.put("row_"+rowId, rowMap);
		}
		recordMap.put("desenType", String.valueOf(fetchVO.getDesenType()));
		return recordMap;
	}
	
	public List<String> searchFieldTitleList(GwServiceVO serviceVO,Long taskId) throws Exception{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = super.getCurrentSession().connection();
			String dataSql = "select row_data from gw_service_check_"+serviceVO.getServiceId()+" where GATEWAY_TASK_ID= "+taskId+" and gateway_row_id=0";
			ps = con.prepareStatement(dataSql);
			rs = ps.executeQuery();
			List<String> list = new ArrayList<String>(); 
			if(rs.next()){
				String rowData = rs.getString("row_data");
				String[] titles = rowData.split(",");
				for (String title : titles) {
					list.add(title);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{
				if(ps != null) ps.close();
				if(rs != null) rs.close();
				if(con != null) con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}