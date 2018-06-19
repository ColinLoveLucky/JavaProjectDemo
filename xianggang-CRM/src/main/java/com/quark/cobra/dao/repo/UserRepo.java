package com.quark.cobra.dao.repo;

import com.quark.cobra.entity.User;

import java.util.List;
import java.util.Map;

public interface UserRepo {
    User findById(String id);
    List<User> findByMobile(String mobile);
    List<User> findByIdNo(String idNo);
    void  save(User user);
    int updateUserById(User user);
    Map<String,Object> queryUser(Map<String,Object> params);
}
