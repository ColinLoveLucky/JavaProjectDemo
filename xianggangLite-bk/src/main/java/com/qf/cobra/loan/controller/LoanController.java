package com.qf.cobra.loan.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netfinworks.common.lang.StringUtil;
import com.qf.cobra.loan.report.base.IReportService;
import com.qf.cobra.loan.report.factory.ReportStrategyFactory.InterType;
import com.qf.cobra.loan.service.IElasticSearchService;
import com.qf.cobra.loan.service.IPbocQueryService;
import com.qf.cobra.loan.service.LoanService;
import com.qf.cobra.pojo.CallBackResult;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanNdesRelation;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.pojo.PhoneDetail;
import com.qf.cobra.util.FillLoanApplyUtils;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.ZipUtils;

@RequestMapping("/loan")
@RestController
public class LoanController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);
	@Value("${QC.PbocQueryPhone.sum:100}")
	private String pullPhoneSum;

	@Autowired
	private LoanService loanService;
	@Autowired
	private IElasticSearchService elasticSearchService;
	@Autowired
	private IReportService reportService;
	private static Comparator PhoneRecordComparator = new Comparator<JSONObject>() {
		@Override
		public int compare(JSONObject o1, JSONObject o2) {
			if (o1.containsKey("start_time") && o2.containsKey("start_time")) {
				return (o1.getString("start_time").compareTo(o2.getString("start_time"))) * -1;
			}
			return 0;
		}
	};

	@RequestMapping(value = "/loanApplyInfo/rule", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ResponseData<Map<String, Object>> queryRuleResultByAppId(@RequestBody Map<String, Object> params) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			String appId = MapUtils.getString(params, "appId");
			List<LoanNdesRelation> dataList = loanService.queryAllRuleResultByAppId(appId);
			Map<String, Object> data = new HashMap<>();
			data.put("ndesResult", dataList);
			responseData.setData(data);
		} catch (Exception e) {
			LOGGER.error("查询非运营商规则结果失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("查询非运营商规则结果失败!");
		}
		return responseData;
	}

	@PostMapping("/searchAllApply")
	@SuppressWarnings("unchecked")
	public ResponseData<Pagination<Map<String, Object>>> searchAllApply(
			@RequestBody Pagination<Map<String, Object>> pagination) {
		ResponseData<Pagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
		try {
			loanService.searchAllApply(pagination);
			responseData.setData(pagination);
		} catch (Exception e) {
			LOGGER.error("查询进件信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	@PostMapping("/searchUserApply")
	@SuppressWarnings("unchecked")
	public ResponseData<Pagination<Map<String, Object>>> searchUserApply(
			@RequestBody Pagination<Map<String, Object>> pagination) {
		ResponseData<Pagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
		try {
			loanService.searchUserApply(pagination);
			responseData.setData(pagination);
		} catch (Exception e) {
			LOGGER.error("查询进件信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	@GetMapping("/getEncodeLoanMsg/{appId}")
	public ResponseData<Map<String, Object>> getEncodeLoanMsg(@PathVariable("appId") String appId) {
		ResponseData<Map<String, Object>> responseData = ResponseUtil.defaultResponse();
		try {
			responseData.setData(ImmutableMap.of("encodeLoanMsg", loanService.getEncodeLoanMsg(appId)));
		} catch (Exception e) {
			LOGGER.error("获取加密进件信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg(e.getMessage());
		}
		return responseData;
	}

	// /**
	// * @Package com.quark.cobra.bizapp.loan.controller
	// * @Description 组合筛选进件信息
	// * @author HongguangHu
	// * @param name
	// * @param date
	// * @param status
	// * @return
	// * @since 2017年4月19日 上午10:24:53
	// */
	// @RequestMapping(value = "/loanApplyInfos", method = RequestMethod.GET)
	// public ResponseData<List<LoanApply>> queryLoanApplyInfos(String name,
	// String date, String status) {
	// SessionUtil.getCurrentUser();
	// ResponseData<List<LoanApply>> responseData =
	// ResponseUtil.defaultResponse();
	// List<LoanApply> data = loanService.queryLoanInfo(name, date, status);
	// responseData.setData(data);
	// return responseData;
	// }
	//
	// /**
	// * @Package com.quark.cobra.bizapp.loan.controller
	// * @Description 根据流程节点查询进件[流程]列表
	// * @author HongguangHu
	// * @param nodeId
	// * 流程节点
	// * @return
	// * @since 2017年4月19日 上午10:24:13
	// */
	// @RequestMapping(value = "/loanApplyInfos/{type}/{status}", method =
	// RequestMethod.GET)
	// public ResponseData<List<LoanApply>>
	// queryLoanApplyInfosByNodeId(@PathVariable("status") String status,
	// @PathVariable("type") String type) {
	// ResponseData<List<LoanApply>> responseData =
	// ResponseUtil.defaultResponse();
	// List<LoanApply> data = loanService.queryLoanInfosByStatusAndType(status,
	// type);
	// responseData.setData(data);
	// return responseData;
	// }

	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @Description 查询申请进件
	 * @author HongguangHu
	 * @param appId
	 * @return
	 * @since 2017年4月20日 下午8:36:45
	 */
	@RequestMapping(value = "/loanApplyInfo", method = RequestMethod.POST)
	@SuppressWarnings("unchecked")
	public ResponseData<LoanApply> queryLoanApplyByAppId(@RequestBody Map<String, Object> params) {
		ResponseData<LoanApply> responseData = ResponseUtil.defaultResponse();
		try {
			String appId = MapUtils.getString(params, "appId");
			LoanApply data = loanService.queryLoanInfoByAppId(appId);
			Map<String, Object> loanData = data.getLoanData();
			FillLoanApplyUtils.fillMaps(loanData, FillLoanApplyUtils.getDefaults());
			responseData.setData(data);
		} catch (Exception e) {
			LOGGER.error("查询进件详情信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("查询借款详情信息失败!");
		}
		return responseData;
	}

	/**
	 * @Title: getmobileChecked @Description: 获取进件手机号和电话号历史校验结果 @param @param
	 * params @param @return 设定文件 @return ResponseData<List<Map<String,Object>>>
	 * 返回类型 @date 2017年7月18日 下午6:27:39 @author YabinLi @throws
	 */
	@RequestMapping(value = "/mobileChecked", method = RequestMethod.POST)
	public ResponseData<List<Map<String, Object>>> getmobileChecked(@RequestBody Map<String, Object> params) {
		ResponseData<List<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
		try {
			String appId = MapUtils.getString(params, "appId");
			responseData.setData(elasticSearchService.getMobileChecked(appId));
		} catch (Exception e) {
			LOGGER.error("获取进件手机号和电话号历史校验信息失败", e);
			responseData.setCode(ResponseCode.SYSTEM_ERROR);
			responseData.setMsg("获取借款手机号和电话号历史校验信息失败!");
		}
		return responseData;
	}

	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @Description 獲取通話詳單
	 * @param params
	 * @return
	 */
	@PostMapping("/pullPhoneRecordsPage")
	@SuppressWarnings("unchecked")
	public ResponseData<PhoneDetail> pullPhoneRecordsPage(@RequestBody Map<String, Object> pagination) {
		ResponseData<PhoneDetail> responseData = ResponseUtil.defaultResponse();
		try {
			String appId = (String) pagination.get("appId");
			LoanApply loanApply = loanService.queryLoanInfoByAppId(appId);
			Map<String, Object> personalInfo = MapUtils.getMap(loanApply.getLoanData(), "personalInfo");
			String phoneNo = (String) personalInfo.get("mobilePhone");
			String idCard = (String) personalInfo.get("idCard");
			// 获得通话详单
			// 将接口需要的参数放入map中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("phoneNo", phoneNo);
			map.put("idCard", idCard);
			map.put("type", "detail");
			String result = reportService.reportDetail(JsonUtil.convert(map));
			try {
				Map<String, Object> jsonMap = JsonUtil.convert(result, Map.class);
				if (jsonMap.get("data") != null) {
					Map<String, Object> tempMap = (Map<String, Object>) jsonMap.get("data");
					// 目前只使用聚信立所以此判断不需要
					// String interName = jsonMap.get("inter_name").toString();
					// 目前只查询聚信立报告
					if (tempMap.containsKey("dataKey")
							&& String.valueOf(tempMap.get("dataKey")).startsWith("JXL_tsp")) {
						// 返回的数据通过gzip压缩，并加密成base64，需通过base64转换后 解压
						Base64 base64 = new Base64();
						byte[] t = base64.decode(tempMap.get("rawData").toString().getBytes());
						JSONObject data = JSONObject.parseObject(new String(ZipUtils.uncompress(t), "UTF-8"));
						JSONArray list = data.getJSONObject("raw_data").getJSONObject("members")
								.getJSONArray("transactions").getJSONObject(0).getJSONArray("calls");
						list.sort(PhoneRecordComparator);
						List<Object> lists = new ArrayList<Object>();
						int len = list.size() > Integer.valueOf(pullPhoneSum) ? Integer.valueOf(pullPhoneSum)
								: list.size();

						for (int i = 0; i < len; i++) {
							JSONObject datasrc = list.getJSONObject(i);
							Map<String, Object> item = new HashMap<String, Object>();
							item.put("OTHER_CELL_PHONE", datasrc.get("other_cell_phone"));
							item.put("INIT_TYPE", datasrc.get("init_type"));
							item.put("CALL_PLACE", datasrc.get("place"));
							item.put("START_TIME", datasrc.get("start_time"));
							item.put("USE_TIME", datasrc.get("use_time"));
							lists.add(item);
						}
						PhoneDetail phoneDetail = new PhoneDetail();
						phoneDetail.setLists(lists);
						phoneDetail.setPerson(personalInfo);
						responseData.setData(phoneDetail);
					}
				}
				LOGGER.info("获得通话详单,手机号:{}", phoneNo);
			} catch (Exception e) {
				LOGGER.info("{}该用户没有通话详单", phoneNo);
				responseData.setData(null);
				return responseData;
			}
		} catch (Exception e) {
			LOGGER.error("查询通话详单失败", e);
			responseData.setData(null);
		}
		return responseData;
	}

	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @Description 查询进件详情
	 * @param params
	 * @return
	 */
	@PostMapping("/loanDetail")
	@SuppressWarnings("unchecked")
	public ResponseData<String> queryLoanDetail(@RequestBody Map<String, Object> appIds) {
		ResponseData<String> response = ResponseUtil.defaultResponse();
		try {
			String appId = (String) appIds.get("appId");
			if (StringUtil.isNotBlank(appId)) {
				Map<String, Object> map = loanService.queryLoanDetail(appId);
				response.setData(JsonUtil.convert(map));
			} else {
				String appIdLike = (String) appIds.get("appIdLike");
				response.setData(loanService.loanDetail(appIdLike));
			}

		} catch (Exception e) {
			LOGGER.error("查询进件详情接口失败", e);
			response.setCode(ResponseCode.SYSTEM_ERROR);
			response.setMsg("查询借款详失败" + e);
		}
		return response;
	}

	@GetMapping("/loanDetail/{appId}")
	@SuppressWarnings("unchecked")
	public ResponseData<String> qLoanDetail(@PathVariable("appId") String appId) {
		ResponseData<String> response = ResponseUtil.defaultResponse();
		try {
			if (StringUtil.isNotBlank(appId)) {
				Map<String, Object> map = loanService.queryLoanDetail(appId);
				response.setData(JsonUtil.convert(map));
			}
		} catch (Exception e) {
			LOGGER.error("查询进件详情接口失败", e);
			response.setCode(ResponseCode.SYSTEM_ERROR);
			response.setMsg("查询借款详情失败" + e);
		}
		return response;
	}

	/**
	 * @Package com.quark.cobra.bizapp.loan.controller
	 * @Description 查询回退理由
	 * @param params
	 * @return
	 */
	@PostMapping("/queryFinalCalBackResult")
	@SuppressWarnings("unchecked")
	public ResponseData<List<CallBackResult>> queryFinalCalBackResult(@RequestBody Map<String, String> map) {
		ResponseData<List<CallBackResult>> response = ResponseUtil.defaultResponse();
		try {
			String appId = map.get("appId");
			if (StringUtil.isNotBlank(appId)) {
				String type = map.get("type");
				List<CallBackResult> list = loanService.queryCallBackResult(appId, type);
				response.setCode(ResponseCode.SUCCESS);
				response.setData(list);
			}
		} catch (Exception e) {
			LOGGER.error("查询回退理由接口失败", e);
			response.setCode(ResponseCode.SUCCESS);
			response.setData(new ArrayList<CallBackResult>());
			// response.setMsg("查询终审回退理由失败"+e);
		}
		return response;
	}

}
