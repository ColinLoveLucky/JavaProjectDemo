package com.crud.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/users")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value="/",method = RequestMethod.GET)
    public List<UserEntity> users(){
        return userService.findList();
    }
    @RequestMapping(value="/save",method = RequestMethod.POST)
    public Long save(@RequestBody  UserEntity entity){
        return userService.save(entity);
    }

    @RequestMapping(value="/delete",method = RequestMethod.GET)
    public List<UserEntity> delete(Long id){
      return  userService.delete(id);
    }
    @RequestMapping(value="/likes",method = RequestMethod.GET)
    public List<UserEntity> findLikeByName(String name){
        return userService.findByNameLike(name);
    }

    @RequestMapping(value="/querys",method = RequestMethod.GET)
    public UserEntity findUser(String name){
        return userService.findUser(name);
    }
}
