package com.quark.cobra.config;

import com.quark.cobra.service.IUserService;
import com.quark.cobra.vo.LoginUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserService userService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //此处s 与 AuthenticationProvider中返回的UserDetail.username保持一致
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        com.quark.cobra.entity.User user = userService.getUserById(s);
        if (user != null) {
            LoginUser loginUser = new LoginUser(s, user.getPassword(), authorities);
            loginUser.setIdName(user.getIdName());
            loginUser.setIdNo(user.getIdNo());
            loginUser.setMobile(user.getMobile());
            return loginUser;
        }else{
            throw new UsernameNotFoundException("未找到用户");
        }

    }
}
