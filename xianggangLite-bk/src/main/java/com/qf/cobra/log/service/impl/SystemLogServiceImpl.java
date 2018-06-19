package com.qf.cobra.log.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.qf.cobra.log.service.ISystemLogService;
import com.qf.cobra.pojo.SystemLog;

@Service
public class SystemLogServiceImpl implements ISystemLogService {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void writeLog(SystemLog systemLog) {
		mongoTemplate.save(systemLog);
	}
	
}
