package com.gztydic.gateway.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.hibernate.HibernateGenericDao;
import com.gztydic.gateway.core.vo.GwDocHelpVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;

/**
 * A data access object (DAO) providing persistence and search support for GwOrg
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see com.gztydic.gateway.core.vo.GwDocHelpVO
 * @author MyEclipse Persistence Tools
 */
@Repository
public class GwDocHelpDAO extends HibernateGenericDao {
	private static final Log log = LogFactory.getLog(GwDocHelpDAO.class);
	// property constants
	public static final String DOC_ID = "docId";
	public static final String DOC_DESC = "docDesc";
	public static final String FILE_ID = "fileId";
	public static final String CREATE_USER = "createUser";
	public static final String CREATE_TIME = "createTime";
	public static final String UPDATE_USER = "updateUser";
	public static final String UPDATE_TIME = "updateTime";

	public GwDocHelpVO findById(java.lang.Long id) {
		log.debug("getting GwDocHelpVO instance with id: " + id);
		try {
			GwDocHelpVO instance = (GwDocHelpVO) getCurrentSession().get(
					"com.gztydic.gateway.core.vo.GwDocHelpVO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding GwDocHelpVO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from GwDocHelpVO as model where model."
					+ propertyName + "= ?";
			Query queryObject = getCurrentSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findAll() {
		log.debug("finding all GwDocHelpVO instances");
		try {
			String queryString = "from GwDocHelpVO";
			Query queryObject = getCurrentSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public List<GwDocHelpVO> searchDocHelpList(int maxRow) throws Exception{
		String sql = "select * from (select t.doc_id,t.doc_desc,t.create_user,t.create_time,f.file_id,f.real_name from gw_doc_help t " +
				"inner join gw_upload_file f on f.file_id=t.file_id order by t.create_Time desc) where rownum<=? ";
		List<Object[]> list = super.findListBySql(sql, new Object[]{maxRow}, null);
		List<GwDocHelpVO> docList = new ArrayList<GwDocHelpVO>();
		for (Object[] obj : list) {
			GwDocHelpVO docVO = new GwDocHelpVO();
			docVO.setDocId(obj[0]==null?0:Long.parseLong(String.valueOf(obj[0])));
			docVO.setDocDesc(obj[1]==null?"":String.valueOf(obj[1]));
			docVO.setCreateUser(obj[2]==null?"":String.valueOf(obj[2]));
			docVO.setCreateTime(obj[3]==null?null:DateUtil.StringTODate(String.valueOf(obj[3])));
			
			GwUploadFileVO fileVO = new GwUploadFileVO();
			fileVO.setFileId(obj[4]==null?0:Long.parseLong(String.valueOf(obj[4])));
			fileVO.setRealName(obj[5]==null?"":String.valueOf(obj[5]));
			docVO.setFileVO(fileVO);
			docList.add(docVO);
		}
		return docList;
	}
	
	public PageObject searchDocHelpList(GwDocHelpVO doc, PageObject pageObject) throws Exception{
		String sql = "select t.doc_id,t.doc_desc,t.create_user,t.create_time,f.file_id,f.real_name from gw_doc_help t " +
				"inner join gw_upload_file f on f.file_id=t.file_id where 1=1 ";
		List paramList = new ArrayList();
		if(doc != null && doc.getFileVO() != null){
			if(StringUtils.isNotBlank(doc.getFileVO().getRealName())){
				sql += "and f.real_name like ? ";
				paramList.add("%"+doc.getFileVO().getRealName()+"%");
			}
		}
		sql += "order by t.create_Time desc";
		pageObject = super.findListBySql(sql, paramList.toArray(), pageObject, null);
		List<Object[]> list = pageObject.getData();
		List<GwDocHelpVO> docList = new ArrayList<GwDocHelpVO>();
		for (Object[] obj : list) {
			GwDocHelpVO docVO = new GwDocHelpVO();
			docVO.setDocId(obj[0]==null?0:Long.parseLong(String.valueOf(obj[0])));
			docVO.setDocDesc(obj[1]==null?"":String.valueOf(obj[1]));
			docVO.setCreateUser(obj[2]==null?"":String.valueOf(obj[2]));
			docVO.setCreateTime(obj[3]==null?null:DateUtil.StringTODate(String.valueOf(obj[3])));
			
			GwUploadFileVO fileVO = new GwUploadFileVO();
			fileVO.setFileId(obj[4]==null?0:Long.parseLong(String.valueOf(obj[4])));
			fileVO.setRealName(obj[5]==null?"":String.valueOf(obj[5]));
			docVO.setFileVO(fileVO);
			docList.add(docVO);
		}
		pageObject.setData(docList);
		return pageObject;
	}
}