package com.qf.cobra;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.qf.cobra.feign.service.BpmsService;
import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class BaseTest {
	@Autowired private MongoOperate mongoTemplate;
	@Autowired
    DiscoveryClient discoveryClient;
	@Autowired
	BpmsService bpmsservice;
	@Test
	public void testName() throws Exception {
//		TreeMap<String, Object> params = new TreeMap<String, Object>();
//		params.put("appId", "BR20170527144839128582");
//		LoanApply loanApply = (LoanApply) mongoTemplate.findOne(params, LoanApply.class);
//		System.out.println(loanApply.getAppId());
//		LoanApply loanApply = new LoanApply();
//		loanApply.setAppId("123456");
//		loanApply.setTimestamp(DateUtil.formatCurrentDateTime());
//		mongoTemplate.save(loanApply);
//		System.out.println(discoveryClient.getServices().toString());
		System.out.println(bpmsservice.queryProcessInstance("2854bd86-5246-11e7-b407-02425ee3ef87"));
	}
	
	
}