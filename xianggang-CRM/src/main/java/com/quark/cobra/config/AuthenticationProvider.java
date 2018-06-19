package com.quark.cobra.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quark.cobra.service.IUserService;
import com.quark.cobra.vo.LoginUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AuthenticationProvider extends
    AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthenticationProvider() throws Exception {
        Cache<Object, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterAccess(1, TimeUnit.MINUTES)
            .build();
        UserCache userCache = new SpringCacheBasedUserCache(new GuavaCache(
            "auth_usercache", cache));
        setUserCache(userCache);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username,
                                       UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        String password = String.valueOf(authentication.getCredentials());
        List<com.quark.cobra.entity.User> userList = userService.findByMobile(username);
        if (CollectionUtils.isNotEmpty(userList) && userList.size() == 1) {
            com.quark.cobra.entity.User user = userList.get(0);
            if(userService.matches(password,user.getPassword())){
                LoginUser loginUser = new LoginUser(user.getId(), user.getPassword(), authorities);
                loginUser.setIdName(user.getIdName());
                loginUser.setIdNo(user.getIdNo());
                loginUser.setMobile(user.getMobile());
                return loginUser;
            }else{
                throw new InternalAuthenticationServiceException("error user");
            }
        } else {
            throw new InternalAuthenticationServiceException("error user");
        }
    }

}
