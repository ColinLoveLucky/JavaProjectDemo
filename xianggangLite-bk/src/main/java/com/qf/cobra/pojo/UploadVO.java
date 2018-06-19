package com.qf.cobra.pojo;

/**
 * <影像文件实体类>
 * 
 * @author HongguangHu
 * @version [版本号, V1.0]
 * @since 2017年4月18日 上午11:19:38
 */
public class UploadVO {
	private String bizCode;// 接入系统业务编号 保持唯一性即可 [进件编号]
	private String idNo;// 证件号 可根据实际情况来定 不能为空
	private String idType;// 证件类型 可根据实际情况来定
	private String userName;// 影像所属用户
	private String createdUser;
	private String changedUser;

	// 本次上传文件ID
	private String savedId;

	// 文件注释参数
	private String fileType; // 影像文件类型
	private String fileName; // 文件名
	private byte[] fileContent; // 文件内容
	private String fileNote;
	private String fileId;
	private String loginUser;// 影像上传用户

	public String getBizCode() {
		return bizCode;
	}
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	public String getChangedUser() {
		return changedUser;
	}
	public void setChangedUser(String changedUser) {
		this.changedUser = changedUser;
	}
	public String getSavedId() {
		return savedId;
	}
	public void setSavedId(String savedId) {
		this.savedId = savedId;
	}
	public String getFileNote() {
		return fileNote;
	}
	public void setFileNote(String fileNote) {
		this.fileNote = fileNote;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getLoginUser() {
		return loginUser;
	}
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
