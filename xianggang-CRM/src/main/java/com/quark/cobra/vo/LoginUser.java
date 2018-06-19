package com.quark.cobra.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.quark.cobra.config.CustomDateSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

public class LoginUser extends org.springframework.security.core.userdetails.User {
    private String mobile;
    private String idNo;
    private String idName;

    public LoginUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = StringUtils.trimToEmpty(mobile).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = StringUtils.trimToEmpty(idNo).replaceAll("(\\d{6})\\d{8}(\\w{4})","$1********$2");
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
}
