package com.qf.cobra.task.task.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.task.entity.TestEntity;
import com.qf.cobra.task.repository.BaseMongoRepository;
import com.qf.cobra.task.task.service.TestTaskService;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试定时任务服务接口实现类
 */
@Service
@Slf4j
public class TestTaskServiceImpl implements TestTaskService {
	
	@Autowired
	private BaseMongoRepository<TestEntity> testRepository;

	@Override
	public void test() {
		try {
			TestEntity testEntity = new TestEntity();
			testEntity.setName("test001");
			testRepository.save(testEntity);
		} catch (Exception e) {
			log.error("测试新增发生异常!", e);
		}
	}

}
