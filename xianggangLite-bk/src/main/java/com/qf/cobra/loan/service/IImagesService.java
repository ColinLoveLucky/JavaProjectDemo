package com.qf.cobra.loan.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.qf.cobra.util.ResponseData;

public interface IImagesService {
	/**
	 * @Package com.quark.cobra.bizapp.loan.service
	 * @Description TODO
	 * @author HongguangHu
	 * @param appId进件编号
	 * @param params录入参数
	 * @return
	 * @since 2017年4月18日 下午5:25:17
	 */
	// public ResponseData<?> enteringInformations(List<MultipartFile> files,
	// String appId, String params);

	ResponseData<?> enteringInformations(String appId, Map<String, Object> params);

	Map<String, Object> uploadFile(MultipartFile file, Map<String, Object> params);

	Map<String, Object> removeFile(Map<String, Object> params);
}
