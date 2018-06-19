package com.quark.cobra.dao.repo.impl;

import com.quark.cobra.dao.mysql.UserMapper;
import com.quark.cobra.dao.mysql.UserRepository;
import com.quark.cobra.dao.repo.UserRepo;
import com.quark.cobra.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userRepo")
@ConditionalOnProperty(name = "datasource.mongo.enable", havingValue = "false",matchIfMissing = false)
public class UserRepoMysqlImpl implements UserRepo {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    public List<User> findByMobile(String mobile){
        return userRepository.findByMobile(mobile);
    }
    public void  save(User user){
        userRepository.save(user);
    }
    public int updateUserById(User user){
        return userMapper.updateUserById(user);
    }

    @Override
    public Map<String, Object> queryUser(Map<String, Object> params) {
        return null;
    }

    public User findById(String id){
        return userRepository.findOne(id);
    }
	
	@Override
	public List<User> findByIdNo(String idNo) {
		return null;
	}
}
