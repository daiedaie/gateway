package com.gztydic.gateway.system;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.dao.GwOrgDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwOrgVO;

/** 
 * @ClassName: GwOrgServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Carson
 * @date 2014-11-19 上午11:43:04 
 *  
 */
@Service
public class GwOrgServiceImpl extends GeneralServiceImpl<GwOrgVO> implements GwOrgService {
	@Resource
	private GwOrgDAO gwOrgDAO;

	/**
	 * 检查机构是否重复
	 */
	public boolean checkGwOrg(GwOrgVO gwOrg) throws Exception {
		List list = gwOrgDAO.findByOrgName(gwOrg.getOrgName());
		
		if(CollectionUtils.isNotEmpty(list)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取机构
	 */
	public List<GwOrgVO> getGwOrgList() throws Exception{
		return gwOrgDAO.findAll();
	}
	
	/**
	 * 获取有效机构
	 */
	public List<GwOrgVO> getGwOrgListByStatus() throws Exception{
		String hql = "from GwOrgVO a where exists (select 1 from GwUserVO b where b.orgId = a.orgId and b.confirmStatus = ? and b.status = ? and b.userType=?) ";
		return gwOrgDAO.findByHql(hql, new Object[]{CommonState.PASS, CommonState.VALID, GwUserType.ORG_USER});
	}
	
	/**
     * 检查重新注册的机构名称是否重复
     */
    public boolean checkReSignUpOrg(GwOrgVO gwOrg) throws Exception {
            List list = gwOrgDAO.findByRepetOrgName(gwOrg.getOrgName(), gwOrg.getOrgId());
            
            if(CollectionUtils.isNotEmpty(list)){
                    return true;
            }
            return false;
    }
}
