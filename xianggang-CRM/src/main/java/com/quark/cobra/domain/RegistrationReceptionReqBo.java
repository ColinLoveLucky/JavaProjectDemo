package com.quark.cobra.domain;

import com.quark.cobra.enums.EducationLevelEnums;
import com.quark.cobra.enums.MaritalStatusEnums;
import com.quark.cobra.enums.SexEnums;
import lombok.Data;
import lombok.NonNull;

/**
 *  目前仅供暖薪贷使用
 *
 *  以下字段(sex,maritalStatus,educationLevel)枚举值定义与系统一致且为非必输项,故使用字符串类型而非枚举型
 *
 */
@Data
public class RegistrationReceptionReqBo extends BaseReqBo {
    @NonNull private String mobile;
    @NonNull private String realname;
    @NonNull private String idCard;
    @NonNull private String sex;
    @NonNull private Integer age;
    @NonNull private String maritalStatus;
    @NonNull private String educationLevel;
}
