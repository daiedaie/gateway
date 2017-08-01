package com.gztydic.gateway.system;

import java.util.Date;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwOperationLogVO;

/** 
 * @ClassName: OperationLogService 
 * @Description: TODO(系统操作日志接口) 
 * @author davis
 * @date 2014-11-20 上午10:52:18 
 *  
 */
public interface OperationLogService extends GeneralService<GwOperationLogVO>{

	/** 
	 * @Title: saveOperationLog 
	 * @Description: TODO(系统操作日志登记) 
	 * @param @param userId 操作用户ID
	 * @param @param acceptUserId 被处理用户ID
	 * @param @param operateType 操作类型
	 * @param @param OperateContent    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void saveOperationLog(String userCode,String acceptUserCode,String operateType,String OperateContent)throws Exception;
	
	/** 
	 * @Title: searchAllLog 
	 * @Description: TODO(根据指定条件查询操作日志) 
	 * @param @param operCode 操作用户登录名
	 * @param @param acceptCode 被操作用户登录名
	 * @param @param operType 操作类型
	 * @param @param startDate 开始时间
	 * @param @param endDate 结束时间
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllLog(String operCode,String acceptCode,String operType,Date startDate,Date endDate,PageObject pageObject) throws Exception;
	
	/** 
	 * @Title: searchAllLog 
	 * @Description: TODO(查询所有操作日志分页) 
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllLog(PageObject pageObject) throws Exception;
}
