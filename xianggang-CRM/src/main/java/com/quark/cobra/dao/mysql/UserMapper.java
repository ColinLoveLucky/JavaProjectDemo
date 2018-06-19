package com.quark.cobra.dao.mysql;

import com.quark.cobra.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper{
    /**
     * 更新用户信息
     * 
     * @param user
     * @return
     */
    int updateUserById(User user);
}
