package com.qf.cobra.loan.report.base;

import java.util.Map;




public interface IReportService {
	
	public String reportDetail(String phone);


	String convertDetail(Map<String, Object> jsonMap, Map<String, Object> requestBody) throws Exception;



	Map<String, Object> getRequestBodyMap(Map<String, String> map);
	
	
	

}
