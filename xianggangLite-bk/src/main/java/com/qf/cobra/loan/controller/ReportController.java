package com.qf.cobra.loan.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qf.cobra.loan.report.base.IReportService;
import com.qf.cobra.loan.report.factory.ReportStrategyFactory;
import com.qf.cobra.loan.report.factory.ReportStrategyFactory.InterType;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.ZipUtils;

@RequestMapping("/report")
@RestController
public class ReportController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
	@Autowired
	private IReportService reportService;
	@Autowired
	private ReportStrategyFactory reportStrategyFactory;
	/**
	 * @Package com.quark.cobra.bizapp.report.controller
	 * @Description 查询进件详情
	 * @param requestBody(需要phoneNo,contacts(List))
	 * @return
	 */
	@PostMapping("/reportDetail")
	@SuppressWarnings("unchecked")
	public ResponseData<String> queryLoanDetail(@RequestBody Map<String, String> requestBody) {
		ResponseData<String> response = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> reportMap = reportService.getRequestBodyMap(requestBody);
			//将接口需要的参数放入map中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("phoneNo", reportMap.remove("phoneNo"));
			map.put("idCard", reportMap.remove("idCard"));
			map.put("type", "summary");
			String result = reportService.reportDetail(JsonUtil.convert(map));
			Map<String, Object> jsonMap = JsonUtil.convert(result, Map.class);
			String newResult="未查询到互联网资讯报告";
			if(jsonMap.get("data") !=null){
				Map<String, Object> tempMap = (Map<String, Object>) jsonMap.get("data");
				//目前只使用聚信立所以此判断不需要
				//String interName = jsonMap.get("inter_name").toString();
				//目前只查询聚信立报告
				if(tempMap.containsKey("dataKey")&&String.valueOf(tempMap.get("dataKey")).startsWith("JXL_tsp")){
					String interName =InterType.JXL_MOBILE_BILL_SUMMARY.getCode();
					//返回的数据通过gzip压缩，并加密成base64，需通过base64转换后 解压
					Base64 base64 = new Base64();
					byte[] t = base64.decode(tempMap.get("rawData").toString().getBytes());
					tempMap.put("rawData", JsonUtil.convert(new String(ZipUtils.uncompress(t), "UTF-8"), Map.class));
//					jsonMap.put("data", tempMap);
					//解析不同版本的数据并根据传入的联系人添加list至result
					newResult = reportStrategyFactory.getReportStrategyHandler(InterType.valueOf(interName)).convertDetail(tempMap, reportMap);
				}
				
				response.setCode(ResponseCode.SUCCESS);
				response.setData(newResult);
			}else{
				response.setCode(ResponseCode.SUCCESS);
				response.setData(null);
				response.setMsg(newResult);
			}
		}catch (Exception e) {
			LOGGER.error("查询互联网资讯报告失败", e);
			response.setCode(ResponseCode.SYSTEM_ERROR);
			response.setMsg("查询互联网资讯报告失败，请联系管理员");
		}
		return  response;
	}

}
