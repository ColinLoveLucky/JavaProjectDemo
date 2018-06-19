package com.qf.cobra.loan.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.WriteResult;
import com.qf.cobra.loan.service.IImagesService;
import com.qf.cobra.pojo.LoanApply;
import com.qf.cobra.pojo.LoanAuditHistory;
import com.qf.cobra.pojo.UploadVO;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.FileUtils;
import com.qf.cobra.util.LoanAuditOperation;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import com.qf.cobra.util.SessionUtil;
/**
 * <录入申请信息> <业务层>
 *
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月18日 下午5:35:47
 */
@Service
public class ImageServiceImpl implements IImagesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
	// 图片上传地址
	@Value("${images.image.uploadUrl}")
	private String imageUploadUrl;
	// 非图片文件上传地址
	@Value("${images.file.uploadUrl}")
	private String fileUploadUrl;

	// 文件访问地址
	@Value("${images.file.downloadUrl}")
	private String fileLownloadUrl;
	// http://sit-qcredit-01:4323/fileViewer/fileViewer/getFile?id=/2017/04/19/8f675397-7d5d-4c18-843c-4d6a5ca40ef7.pdf
	// 图片访问地址
	@Value("${images.image.downloadUrl}")
	private String imageDownloadUrl;
	// http://sit-qcredit-01:4323/fileViewer/fileViewer/getImage?id=99543
	// 图片访问地址
	@Value("${images.image.deleteUrl}")
	private String imageDeleteUrl;

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private RestTemplate restTemplate;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public ResponseData<?> enteringInformations(String appId, Map<String, Object> params) {
		ResponseData resVal = ResponseUtil.defaultResponse();
		Map<String, Object> loanParams = (Map<String, Object>) params.get("loanData");
		Query query = Query.query(Criteria.where("appId").is(appId));
		LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
		Map<String, Object> loanData = loanApply.getLoanData();
		loanData.putAll(loanParams);
		Update update = new Update();
		update.set("loanData", loanData);
		WriteResult upsert = mongoTemplate.updateFirst(query, update, LoanApply.class);

		// 保存录入信息进入历史数据
		LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
		loanAuditHistory.setUserId(SessionUtil.getCurrentUser().getUserId());
		loanAuditHistory.setOperate(LoanAuditOperation.LOAN_INPUT.getValue());
		loanAuditHistory.setBizData(params);
		loanAuditHistory.setAppId(appId);
		loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
		mongoTemplate.save(loanAuditHistory);
		if (upsert.isUpdateOfExisting()) {
			resVal.setMsg("录入成功！");
		} else {
			resVal.setCode(30000);
			resVal.setMsg("信息录入失败！");
		}
		return resVal;
	}
	@Override
	public Map<String, Object> uploadFile(MultipartFile file, Map<String, Object> params) {
		String userId = "";// SessionUtil.getCurrentUser().getUserId();
		String appId = MapUtils.getString(params, "appId");// 进件编号
		String userName = MapUtils.getString(params, "userName");// 影像所有者
		String fileType = MapUtils.getString(params, "fileType");// 文件类型
		String userIdCard = MapUtils.getString(params, "userIdCard");// 影像所有者身份证号
		Map<String, Object> image = new HashMap<String, Object>();
		String fileName = file.getOriginalFilename();// 文件名
		try {
			InputStream ins = file.getInputStream();
			byte[] bytes = FileUtils.getBytes(ins);
			UploadVO uploadModel = new UploadVO();
			uploadModel.setFileContent(bytes); // pdf文件的二进制流
			// 文件图片
			if (StringUtils.endsWithIgnoreCase(fileName, "jpg") || StringUtils.endsWithIgnoreCase(fileName, "jpeg") || StringUtils.endsWithIgnoreCase(fileName, "png")
					|| StringUtils.endsWithIgnoreCase(fileName, "bmp") || StringUtils.endsWithIgnoreCase(fileName, "gif")) {// 是图片
				uploadModel.setBizCode(appId); // 接入系统业务编号,保持唯一性即可,进件编号
				uploadModel.setIdNo(userIdCard); // 证件号,可根据实际情况来定,不能为空
				uploadModel.setIdType("Id1"); // 证件类型 可根据实际情况来定
				uploadModel.setFileType(fileType); // 影像类型,接入系统具体跟影像系统开发人员沟通来定
				uploadModel.setFileName(fileName); // 影像文件名 必须带扩展名
				uploadModel.setUserName(userName); // 影像所属用户
				uploadModel.setLoginUser(userId);
				LOGGER.info("图片文件, 接入系统业务编号: "+appId+", 证件号:"+userIdCard+", 影像类型: "+fileType+", 影像文件名: "+fileName+".");
				// // 影像上传用户
				Map<?, ?> result = null;
				synchronized(this){ 
					result = restTemplate.postForObject(imageUploadUrl, uploadModel, Map.class);
				}
				if(result == null){
					LOGGER.info("图片文件, 影像上传返回对象为null！");
				}
				String imageId = (String) result.get("okTagMsg");
				LOGGER.info("图片文件, 影像上传返回imageId："+imageId);
				image.put("imagePath", imageDownloadUrl + imageId);
				image.put("thumbImagePath", imageDownloadUrl + imageId + "&picType=small&picLoc=idc");
				image.put("fileId", imageId);
			} else {// 非图片文件
				uploadModel.setFileName(fileName); // 文件名 必须带扩展名
				LOGGER.info("非图片文件, 接入系统业务编号: "+appId+", 证件号:"+userIdCard+", 影像类型: "+fileType+", 影像文件名: "+fileName+".");
				Map<?, ?> result = restTemplate.postForObject(fileUploadUrl, uploadModel, Map.class);
				if(result == null){
					LOGGER.info("非图片文件, 影像上传返回对象为null！");
				}
				String filefPath = (String) result.get("okTagMsg");
				LOGGER.info("非图片文件, 影像上传返回filefPath："+filefPath);
				image.put("filePath", fileLownloadUrl + filefPath);
			}
			image.put("fileType", fileType);
			image.put("userName", userName);
			image.put("loginUser", userId);
			image.put("createTime", DateUtil.formatCurrentDateTime());
			image.put("fileName", fileName);

			Query query = Query.query(Criteria.where("appId").is(appId));
			LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
			if (loanApply != null) {
				Map<String, Object> loanData = loanApply.getLoanData();
				if (loanData.containsKey("images")) {
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> images = (List<Map<String, Object>>) loanData.get("images");
					images.add(image);
				} else {
					List<Map<String, Object>> images = new ArrayList<Map<String, Object>>();
					images.add(image);
					loanData.put("images", images);
				}
				Update update = new Update();
				update.set("loanData", loanData);
				WriteResult upsert = mongoTemplate.updateFirst(query, update, LoanApply.class);
				// 保存影像信息进入历史数据
				LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
				loanAuditHistory.setUserId(userId);
				loanAuditHistory.setOperate(LoanAuditOperation.LOAN_INPUT_UPLOAD_IMAGES.getValue());
				loanAuditHistory.setAppId(appId);
				loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
				loanAuditHistory.setBizData(image);
				mongoTemplate.save(loanAuditHistory);
				if (upsert.isUpdateOfExisting()) {
					return image;
				} else {
					return null;
				}
			}else{
				LOGGER.info("查询appId："+appId+"失败。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;

	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> removeFile(Map<String, Object> params) {
		String userId = "userId";// SessionUtil.getCurrentUser().getUserId();
		String appId = MapUtils.getString(params, "appId");
		String fileIdPre = MapUtils.getString(params, "fileId");
		if (appId != null && fileIdPre != null) {
			String realUrl = imageDeleteUrl + "?fileId=" + fileIdPre + "&loginUser=" + userId;
			LOGGER.info("影像删除fileId=" + fileIdPre + "&loginUser=" + userId);
			String result = restTemplate.getForObject(realUrl, String.class);
			if (result != null && result.contains("okTagMsgOK")) {
				Query query = Query.query(Criteria.where("appId").is(appId));
				LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
				Map<String, Object> image = null;
				if (loanApply != null) {
					Map<String, Object> loanData = loanApply.getLoanData();
					List<Map<String, Object>> images = (List<Map<String, Object>>) loanData.get("images");
					for (int i = 0; i < images.size(); i++) {
						Map<String, Object> map = images.get(i);
						String fileId = MapUtils.getString(map, "fileId");
						if(fileId == null){
							image = images.remove(i);
						}else{
							if (map != null && org.apache.commons.lang3.StringUtils.equalsIgnoreCase(fileIdPre, fileId)) {
								image = images.remove(i);
							}
						}
					}
					Update update = new Update();
					update.set("loanData", loanData);
					WriteResult upsert = mongoTemplate.updateFirst(query, update, LoanApply.class);

					// 保存影像信息进入历史数据
					LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
					loanAuditHistory.setUserId(userId);
					loanAuditHistory.setOperate(LoanAuditOperation.LOAN_INPUT_DELETE_IMGES.getValue());
					loanAuditHistory.setAppId(appId);
					loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
					loanAuditHistory.setBizData(image);
					mongoTemplate.save(loanAuditHistory);
					if (upsert.isUpdateOfExisting()) {
						return image;
					} else {
						return null;
					}
				}
			}else{
				LOGGER.info("影像删除fileId=" + fileIdPre + "&loginUser=" + userId+"，删除失败。");
				LOGGER.info("删除在mongoDB之中的fileId=null的异常图片信息。");
				if(appId != null){
					Query query = Query.query(Criteria.where("appId").is(appId));
					LoanApply loanApply = mongoTemplate.findOne(query, LoanApply.class);
					Map<String, Object> image = null;
					if (loanApply != null) {
						Map<String, Object> loanData = loanApply.getLoanData();
						List<Map<String, Object>> images = (List<Map<String, Object>>) loanData.get("images");
						for (int i = 0; i < images.size(); i++) {
							Map<String, Object> map = images.get(i);
							String fileId = MapUtils.getString(map, "fileId");
							if(fileId == null){
								image = images.remove(i);
							}
						}
						Update update = new Update();
						update.set("loanData", loanData);
						WriteResult upsert = mongoTemplate.updateFirst(query, update, LoanApply.class);

						// 保存影像信息进入历史数据
						LoanAuditHistory loanAuditHistory = new LoanAuditHistory();
						loanAuditHistory.setUserId(userId);
						loanAuditHistory.setOperate(LoanAuditOperation.LOAN_INPUT_DELETE_IMGES.getValue());
						loanAuditHistory.setAppId(appId);
						loanAuditHistory.setTimestamp(DateUtil.formatCurrentDateTime());
						loanAuditHistory.setBizData(image);
						mongoTemplate.save(loanAuditHistory);
						upsert.isUpdateOfExisting();
					}
				}
			}
		}
		return null;
	}
}
