package com.qf.cobra.loan.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qf.cobra.loan.service.IImagesService;
import com.qf.cobra.util.JsonUtil;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;

@RestController
public class UploadController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
	@Autowired
	IImagesService imageService;
	@PostMapping("/upload")
	public ResponseData upload(@RequestParam(value = "file", required = false) MultipartFile[] files, @RequestParam Map<String, Object> params) throws IOException {
		ResponseData defaultResponse = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> uploadFile = null;
			for (MultipartFile file : files) {
				LOGGER.info("文件上传参数:{}", params);
				uploadFile = imageService.uploadFile(file, params);
			}
			if (uploadFile != null) {
				defaultResponse.setData(uploadFile);
				LOGGER.info("文件上传返回:{}", JsonUtil.convert(defaultResponse));
				return defaultResponse;
			} else {
				defaultResponse.setCode(ResponseCode.SYSTEM_ERROR);
				return defaultResponse;
			}
		} catch (Exception e) {
			LOGGER.error("文件上传失败", e);
			defaultResponse.setCode(ResponseCode.SYSTEM_ERROR);
			defaultResponse.setMsg("文件上传失败!");
		}
		return defaultResponse;
	}
	@PostMapping("/remove")
	public ResponseData removeFile(@RequestParam Map<String, Object> params) throws IOException {
		ResponseData defaultResponse = ResponseUtil.defaultResponse();
		try {
			Map<String, Object> remove = null;
			remove = imageService.removeFile(params);
			if (remove != null) {
				defaultResponse.setData(remove);
				return defaultResponse;
			} else {
				defaultResponse.setCode(ResponseCode.SYSTEM_ERROR);
				return defaultResponse;
			}
		} catch (Exception e) {
			LOGGER.error("文件删除失败", e);
			defaultResponse.setCode(ResponseCode.SYSTEM_ERROR);
			defaultResponse.setMsg("文件删除失败");
		}
		return defaultResponse;
	}
}