package com.qf.cobra.pojo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qf.cobra.config.CustomDateSerializer;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

public class User {
    private String id;
    private String tenant;//租户
    private String mobile;//手机号
    private String password;//密码
    private String idNo;//身份证
    private String idName;//姓名
    /** 性别 */
    private String sex;
    /** 年龄 */
    private Integer age;
    /** 婚姻状况 */
    private String maritalStatus;
    /** 教育程度 */
    private String educationLevel;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date registrationDate;//注册时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updatePwdDate;//最近一次密码更新时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date certificationDate;//实名时间时间

    public String getId(){
        return this.id;
    }
    public void setId(String  id) {
         this.id=id;
    }
    public String getTenant(){
        return this.tenant;
    }
    public void setTenant(String tenant){
        this.tenant=tenant;
    }
    public String  getMobile(){
        return this.mobile;
    }
    public void setMobile(String mobile){
        this.mobile=mobile;
    }
    public String  getPassword(){
        return  this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNo(){
        return idNo;
    }
    public void setIdNo(String idNo){
        this.idNo=idNo;
    }
    public String getIdName(){
        return this.idName;
    }
    public  void setIdName(String idName){
        this.idName=idName;
    }
    public String getSex(){
        return  this.sex;
    }
    public void setSex(String sex){
        this.sex=sex;
    }
    public Integer getAge(){
        return this.age;
    }
    public  void setAge(Integer age){
        this.age=age;
    }
    public String getMaritalStatus(){
        return maritalStatus;
    }
    public void setMaritalStatus(String maritalStatus){
        this.maritalStatus=maritalStatus;
    }
    public String getEducationLevel(){
        return educationLevel;
    }
    public void setEducationLevel(String educationLevel){
        this.educationLevel=educationLevel;
    }
    public Date  getRegistrationDate(){
        return this.registrationDate;
    }
    public void setRegistrationDate(Date registrationDate){
        this.registrationDate=registrationDate;
    }
    public  Date getUpdatePwdDate(){
        return this.updatePwdDate;
    }
    public  void setUpdatePwdDate(Date updatePwdDate){
        this.updatePwdDate=updatePwdDate;
    }
    public  Date getCertificationDate(){
        return  this.certificationDate;
    }
    public void setCertificationDate(Date certificationDate){
        this.certificationDate=certificationDate;
    }
}



