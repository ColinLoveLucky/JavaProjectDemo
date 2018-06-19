package com.qf.cobra.loan.service;

import java.util.Map;

import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.ResponseData;

/**
 * <审核操作类> <功能详细描述>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月19日 下午7:56:32
 */
public interface IAuditService {

	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description TODO
	 * @author HongguangHu
	 * @param auditLevel2
	 * @param appId进件编号
	 * @param auditLevel审核级别
	 * @param params审核表单参数
	 * @return
	 * @since 2017年4月21日 下午1:49:58
	 */
	public ResponseData<?> loanAudit(String appId, String auditLevel, Map<String, Object> params);

	public void updateAppStatus(String appId, String string);

  /**
   * 修改NDES回调borrowerLite之后的进件信息
   * @param appId
   * @param string
   */
  public void updateAppStatusNdes(String appId, String string);

	
	public void updateLoanAppClientId(String appId,String clientId,String tenantId);
	/**
	 * @Title: loanAuditFallBack 
	 * @Description: 处理终审进件回退 
	 * @param @param appId 进件编号
	 * @param @param taskId 任务编号
	 * @param @param callBackRemark 回退理由
	 * @param @return    设定文件 
	 * @return Map<String, Object>   返回类型 
	 * @date 2017年6月30日 下午2:58:07
	 * @author YabinLi
	 * @throws
	 */
	public Map<String, Object> loanAuditFallBack(String appId, String taskId, String callBackRemark);
	/**
	 * @Title: loanAuditFallBackUpdate 
	 * @Description: 修改进件回退对于的数据库之中的字段
	 * @param @param appId    设定文件 
	 * @return void    返回类型 
	 * @date 2017年7月3日 上午10:59:44
	 * @author YabinLi
	 * @throws
	 */
	public void loanAuditFallBackUpdate(String appId);

	public String loanCanncel(String appId,String processInstanceId);
	
	/**
	 * 保存审核历史
	 * @param appId
	 * @param loanAuditOperation
	 */
	public void saveLoanAuditHistory(String appId, LoanAuditOperation loanAuditOperation);
	
	/**
	 * 重新推送审批结果到bpms
	 * @param appId
	 */
	public String rePushBpms(String appId);
}
