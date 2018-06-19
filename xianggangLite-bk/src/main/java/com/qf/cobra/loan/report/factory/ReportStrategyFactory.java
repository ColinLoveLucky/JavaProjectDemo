package com.qf.cobra.loan.report.factory;




import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.qf.cobra.loan.report.base.IReportService;
import com.qf.cobra.loan.report.service.impl.ReportHLServiceImpl;
import com.qf.cobra.loan.report.service.impl.ReportJXLServiceImpl;


@Service
public class ReportStrategyFactory {

	@Resource
	private ReportJXLServiceImpl reportJXLServiceImpl;
	@Resource
	private ReportHLServiceImpl reportHLServiceImpl;
	
	
	
	@SuppressWarnings("static-access")
	public IReportService getReportStrategyHandler(InterType interType){
    	if(InterType.JXL_MOBILE_BILL_SUMMARY == interType){
    		return reportJXLServiceImpl;
    	}else{
    		return reportHLServiceImpl;
    	}
	}
	
	
	public enum InterType{
		JXL_MOBILE_BILL_SUMMARY("JXL_MOBILE_BILL_SUMMARY","report_data"),
		HULU_MOBILE_BILL_SUMMARY("HULU_MOBILE_BILL_SUMMARY","data");
		private String code;
		private String message;

		private InterType(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	
}
