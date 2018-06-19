package com.quark.cobra.service;

import com.quark.cobra.domain.*;
import com.quark.cobra.entity.OauthClientDetails;
import com.quark.cobra.entity.User;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface IUserService {
    /**
     * 创建用户
     * @param registrationReqBo
     * @return
     */
    User buildUser(RegistrationReqBo registrationReqBo);

    List<User> findByMobile(String mobile);
    
    /**
     * 查找用户信息
     * 
     * @param idNo
     * @return
     */
    List<User> findUserByIdNo(String idNo);

    void saveUser(@NonNull User user);
    
    /**
     * 实名认证
     * 
     * @param realNameAuthReq
     * @return
     */
    UserInfoResBo realNameAuth(UserInfoReqBo realNameAuthReq);
    
    /**
     * 获取用户信息
     * 
     * @param userId
     * @return
     */
    User getUserById(String userId);
    
    /**
     * 忘记密码
     * 
     * @param user
     * @param forgotPasswordReqBo
     */
    void forgotPassword(User user, ForgotPasswordReqBo forgotPasswordReqBo);
    
    /**
     * 修改密码
     * 
     * @param user
     * @param modifyPasswordReqBo
     */
    void modifyPassword(User user, ModifyPasswordReqBo modifyPasswordReqBo);

    /**
     *
     * @param cipherPassword 上送RSA加密后的密码
     * @param cryptoPassword 数据库加盐加密后的密码
     * @return
     */
     boolean matches(String cipherPassword,String cryptoPassword);

    /**
     * 密码转换
     * @param cipherPassword  上送RSA加密后的密码
     * @return cryptoPassword 数据库加盐加密后的密码
     */
     String encryptChange(String cipherPassword);

    OauthClientDetails findOauthClientDetailsByClientId(String clientId);

    /*
    *查询用户信息
     */
    Map<String,Object> queryUser(Map<String,Object> params);

}
