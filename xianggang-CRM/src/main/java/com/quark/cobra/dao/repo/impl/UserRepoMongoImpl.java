package com.quark.cobra.dao.repo.impl;

import com.quark.cobra.dao.mongo.UserMongoRepository;
import com.quark.cobra.dao.repo.UserRepo;
import com.quark.cobra.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userRepo")
@ConditionalOnProperty(name = "datasource.mongo.enable", havingValue = "true",matchIfMissing = false)
public class UserRepoMongoImpl implements UserRepo {
    @Autowired
    private UserMongoRepository userMongoRepository;

    public List<User> findByMobile(String mobile){
        return userMongoRepository.findByMobile(mobile);
    }
    public void  save(User user){
        userMongoRepository.save(user);
    }
    public int updateUserById(User user){
        return userMongoRepository.updateUserById(user);
    }

    @Override
    public Map<String,Object> queryUser(Map<String,Object> params) {
        return userMongoRepository.queryUser(params);
    }

    public User findById(String id) {
        return userMongoRepository.findById(id);
    }
	
	@Override
	public List<User> findByIdNo(String idNo) {
		return userMongoRepository.findByIdNo(idNo);
	}
}
