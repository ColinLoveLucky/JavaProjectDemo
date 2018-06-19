package com.qf.cobra.log.service;

import com.qf.cobra.pojo.SystemLog;

public interface ISystemLogService {

	/**
	 * 记录操作日志
	 * @param systemLog
	 */
	void writeLog(SystemLog systemLog);
	
}
