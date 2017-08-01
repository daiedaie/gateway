package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.UserView;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;

public interface UserService extends GeneralService<GwUserVO>{
	
	public void save(GwUserVO vo) throws Exception;
		
	public List findByLoginName(Object loginName)throws Exception;
	
	public List searchUserList(GwUserVO user,GwOrgVO org,PageObject obj)throws Exception;
	
	public GwUserVO searchUserDetail(Long id)throws Exception;
	
	public void updateUser(GwUserVO vo,GwUserVO user1)throws Exception;

	public List searchUserListByOrg(GwUserVO user,GwOrgVO org,PageObject obj,GwUserVO user1)throws Exception; 

	/**
	 * 申请用户注销
	 * @throws Exception
	 */
	public void applyCancelUser(GwUserVO loginUser,GwUserVO cancelUser) throws Exception;
	
	public int cancelUser(GwUserVO loginUser,GwUserVO cancelUser) throws Exception;
	
	/**
	 * 查询机构用户下的所有数据用户
	 * @param orgUserId
	 * @return
	 * @throws Exception
	 */
	public List searchDataUserListByOrg(Long orgUserId) throws Exception;
	
	public void updateUserByAudit(GwUserVO vo)throws Exception;
	
	//根据用户类型查询值班人员列表
	public int searchOtherOnlineUser(Long userId,String userType)throws Exception;
	
	public List<GwUserVO> searchOnlineUser(String userType)throws Exception;

	//查询所有机构用户
	public List<UserView> searchOrgUserList()throws Exception;
	
	//查询某个机构下的所有数据用户
	public List<UserView> searchDataUserList(Long orgId)throws Exception;
	
	/**
	 * 
	 * @Title: searchOrgUser 
	 * @Description: TODO(根据机构编码和用户类型查询用户) 
	 * @param @param orgId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwUserVO>    返回类型 
	 * @throws
	 */
	public  List<GwUserVO> searchOrgUser(Long orgId)throws Exception;
}
