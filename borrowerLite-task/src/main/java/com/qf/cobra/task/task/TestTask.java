package com.qf.cobra.task.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.qf.cobra.task.task.service.TestTaskService;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务例子
 */
@Component
@LTS
@Slf4j
public class TestTask {
	
	@Autowired
	private TestTaskService testTaskService;
	
	/**
	 * 测试定时任务方法
	 */
	@JobRunnerItem(shardValue = "cron_borrowerlite_testTask")
	public void execute() {
		long startTime = System.currentTimeMillis();
		try {
			log.info("<====测试定时任务执行开始...");
			testTaskService.test();
		} catch (Exception e) {
			log.error("===测试定时任务执行发生异常!", e);
		} finally {
			long endTime = System.currentTimeMillis() - startTime;
			log.info("====>=测试定时任务执行结束!耗时:{}", endTime);
		}		
	}

}
