package com.quark.cobra;

import com.quark.cobra.config.RedisTemplateTokenStore;
import com.quark.cobra.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class AppTest {

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

@Autowired private IUserService userService;

    @Test
    public void delete(){
//        RedisTemplateTokenStore redisTemplateTokenStore = (RedisTemplateTokenStore)tokenStore;
//        RedisTemplate redisTemplate = redisTemplateTokenStore.getRedisTemplate();
//        OAuth2AccessToken accessToken = tokenStore.readAccessToken("59896024-11d6-45a9-aab9-306d890c036c");
//        OAuth2AccessToken accessToken2 = (OAuth2AccessToken)redisTemplate.opsForValue().get("crm:access:59896024-11d6-45a9-aab9-306d890c036c");
//        Set<String> keys = redisTemplate.keys("crm:access:" + "*");
//        System.out.println(keys);
//        keys = stringRedisTemplate.keys("crm:access:" + "*");
//        System.out.println(keys);
//        redisTemplate.delete(keys);
        System.out.println(userService.matches("g7kJdz3EkExTDa0ZIpG6_5Q3bU00CTDVO1CWGq7LXjEvQ7YcSfuPZi0xchp8q2uqlJv.BH3vjVdaFohPwFzW6EDC1UjgRHorE2aXTZVb1EDVAUqbzbwIrZ_5qFFFSfMkmPlDYUQ7iAjOQsTcgLMDUbl92q4QAgHWZWjVtSIKu7E-","$2a$10$ixM/IWjj5adkA575m/WwC..uKfjMpPoGIHbgs9z7d5E0JJiG4RHjm"));
    }

}
