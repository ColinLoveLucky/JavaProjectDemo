package com.qf.cobra.loan.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.qf.cobra.pojo.CallBackResult;
import com.qf.cobra.pojo.CarrieroperatorResult;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.pojo.TaskNodeInfo;
import com.qf.cobra.pojo.TaskNodeInfo.ReceiveServiceEnum;

public interface LoanService {

	public TaskNodeInfo updateTaskStatus(String appId,ReceiveServiceEnum serviceEnum);

	/**
	 * 添加进件申请  <接收来自loanApp系统的进件信息>
	 * @param json
	 * @param tenantId
	 * @param clientId
	 */
	public void receiveLoanApply(String json);

	/**
	 * 添加进件申请 <接收来自Frontal的进件信息>
	 * @param json
	 * @param tenantId
	 * @param clientId
	 */
	public LoanApply addLoanApply(JSONObject jsonObject);

	/**
	 * 默认提交任务-无流程变量
	 * 
	 * @param appId
	 * @param taskId
	 */
	@Deprecated
	public Boolean compeleteTask(String appId, String taskId, String... result);

	/**
	 * 提交任务-自定义流程变量
	 * 
	 * @param appId
	 * @param taskId
	 */
	public Boolean compeleteTask(String appId, String taskId, List<Map<String, String>> variables);

	/**
	 * 按不同的流程版本-提交任务
	 * 
	 * @param appId
	 * @param taskId
	 */
	// public Boolean compeleteTask(String appId, String taskId,
	// String taskDefinitionKey, String... result) throws Exception;
	/**
	 * 默认提交回调任务-无流程变量
	 * 
	 * @param appId
	 * @param processInstanceId
	 * @param result
	 *            贷前 1000 PASS 1001 REJECT 1100 in blackList 1110 repeat 贷中 5000
	 *            5001
	 */
	public Boolean compeleteReciveTask(String appId, String processInstanceId, String taskId, String... result);

	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description 根据流程中的节点id查询进件信息
	 * @author HongguangHu
	 * @param status
	 * @param type
	 * @return
	 * @since 2017年4月19日 下午10:18:14
	 */
	public List<LoanApply> queryLoanInfosByStatusAndType(String status, String type);

	public LoanApply queryLoanInfoByAppId(String appId);

	/**
	 * @Package com.qf.cobra.loan.service
	 * @Description 非运营商规则结果
	 * @param appId
	 * @return
	 * @since 2017年5月8日 下午4:16:16
	 */
	public LoanNdesRelation queryRuleResultByAppId(String appId);

	public List<LoanNdesRelation> queryAllRuleResultByAppId(String appId);

	public void searchAllApply(Pagination<Map<String, Object>> pagination) throws Exception;
	public void searchUserApply(Pagination<Map<String, Object>> pagination) throws Exception;


	String loanDetail(String appIds) throws Exception;

	Map<String, Object> queryLoanDetail(String appId) throws Exception;

	public void saveCarrieroperatorResult(CarrieroperatorResult carrieroperatorResult) throws Exception;

	public List<CarrieroperatorResult> getCarrieroperatorByAppId(String appId) throws Exception;

	public void reStartProcess(String appId) throws Exception;

	public void rePushKafka(String appId,String transactionId,String policyCode);
  public void rePushKafka(String appId);

  /**
	 * 查询并修改loanNdesRelation
	 * @param params
	 * @param setParams
	 * @param loanNdesRelation
	 * @return
	 */
	public LoanNdesRelation queryAndUpdateNdes(Map<String,Object> params,Map<String,Object> setParams);

	public List<CallBackResult> queryCallBackResult(String appId,String type);

	public void checkoutAppStore();

	public List<Map<String, Object>> getTasksByInstanceId(String instanceId);

	public void checkoutAuditor();

	public boolean enableStoreReject(String applyId);

	public String getFirstTaskIdByInstanceId(String instanceId);

	public String reCompeleteReciveTask(String appId, String transactionId, String policyCode);

	public String reCompleteLoanIn(String appId);

	/**
	 * 修改进件状态
	 * @param appId
	 * @param status
	 * @return
	 */
	public void saveLoanAppStatus(String appId,LoanApply.AppStatusEnum status);

  /**
   * 修改获取小贷的进件对应的进件状态
   * @param appId
   * @param status
   * @return
   */
  public void saveLoanAppStatusXdt(String appId,LoanApply.AppStatusEnum status);

	/**
	 * 同步进件信息到ncobra
	 * @param appId
	 */
	public boolean pushLoanAppToNCobra(String appId);
	
	/**
	 * 开启borrower进件流程
	 * @param loanInfo
	 * @throws Exception
	 */
	public void startBorrowerProcess(LoanApply loanInfo) throws Exception;

	public String getEncodeLoanMsg(String appId);

	public void receiveLoanApplyDetail(String json);
	
}
