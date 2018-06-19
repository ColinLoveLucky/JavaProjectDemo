package com.quark.cobra.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.quark.cobra.config.CustomDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserQueryResBo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String tenant;//租户
    private String mobile;//手机号
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
}
