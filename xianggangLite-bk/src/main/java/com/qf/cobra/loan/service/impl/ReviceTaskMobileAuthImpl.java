package com.qf.cobra.loan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qf.cobra.loan.service.ErrorNoticeService;
import com.qf.cobra.loan.service.ReviceTask;
import com.qf.cobra.qyjapi.service.IQyjapiService;
import com.qf.cobra.util.DictItem;

@Service("MobileAuthStartReviceTask")
public class ReviceTaskMobileAuthImpl extends ReviceTask {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private IQyjapiService qyjapiService;
	@Autowired
	private ErrorNoticeService errorNoticeService;
	//通知夸时代系统,流程已完成资料上传,请完成运营商认证
	@Override
	public Boolean excute(String appId) {
		Boolean isSuccessCommit = false;
		try {
			if(StringUtils.isNotEmpty(appId)){
				LOGGER.info("进件编号:{}", appId);
				if(StringUtils.isNotEmpty(appId)){
					LOGGER.info("开启调用App运营商接口认证！");
					isSuccessCommit = qyjapiService.triggerCertifyTask(appId, DictItem.QYJ_APP_CERTIFY);
					LOGGER.info("调用App运营商接口认证结束！");
				}else{
					LOGGER.info("进件编号:{}  异常", appId);
				}
			}else{
				LOGGER.info("进件编号:{}  异常", appId);
			}
		} catch (Exception e) {
			errorNoticeService.notice(appId,"调用App运营商接口认证失败","调用App运营商接口认证失败", e);
		}
		return isSuccessCommit;
	}
}
