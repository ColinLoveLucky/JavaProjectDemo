package com.quark.cobra.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class JedisClusterConfig {
    @Autowired
    private Environment environment;
    @Autowired
    private RestTemplate restTemplate;

    public List<RedisNode> setRedisNode() {
        List<RedisNode> redisNodeList = null;
        try {
            Map<String, String> redisNodeMap = restTemplate.getForObject(environment.getProperty("redis.cluster.url"), Map.class);
            String nodes = redisNodeMap.get("shardInfo");
            String[] redisNodes = nodes.split(" ");
            redisNodeList = new ArrayList<RedisNode>();
            //增加Redis Cluster 节点
            for (int i = 0; i < redisNodes.length; i++) {
                String[] redisNode = redisNodes[i].split(":");
                redisNodeList.add(new RedisNode(redisNode[0], Integer.parseInt(redisNode[1])));
            }
        } catch (RestClientException e) {
            log.error("Connect to the CacheCloud exception", e);
        } catch (NumberFormatException e) {
            log.error("Digital formatting exception", e);
        }
        return redisNodeList;
    }


    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        Assert.notNull(setRedisNode(), "Redis failed to initialize");
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(setRedisNode());
        redisClusterConfiguration.setMaxRedirects(5);
        return redisClusterConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisClusterConfiguration());
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        log.info("redis initialization is complete");
        return redisTemplate;
    }


    //TODO
    @Bean
    public TokenStore tokenStore() {
//        return InMemoryTokenStore();
//        return new JwtTokenStore(null);

        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String,Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.afterPropertiesSet();
        RedisTemplateTokenStore tokenStore = new RedisTemplateTokenStore();
        tokenStore.setRedisTemplate(redisTemplate);
        return tokenStore;
    }
}
