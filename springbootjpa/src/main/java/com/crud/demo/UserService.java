package com.crud.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserEntity findUser(String name){
      return  userRepository.findByName(name);
    }

    public List<UserEntity> findList(){
        return userRepository.findAll();
    }

    public Long save(UserEntity user){
        return userRepository.save(user).getId();
    }

    public List<UserEntity> delete(Long id){
        userRepository.deleteById(id);
        return  userRepository.findAll();
    }

    public List<UserEntity> findByNameLike(String name){
      return  userRepository.findByNameLike(name);
    }
}
