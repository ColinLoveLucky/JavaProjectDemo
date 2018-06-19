package com.qf.cobra.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: JedisClusterConfig
 * @author: [YanweiQin]
 * @CreateDate: [2017/5/17 18:12]
 * @UpdateUser: [YanweiQin]
 * @UpdateDate: [2017/5/17 18:12]
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [redis cluslter 配置]
 * @version: [V1.0]
 */
@Configuration
public class JedisClusterConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JedisClusterConfig.class);

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
            redisNodeList = new ArrayList<>();
            //增加Redis Cluster 节点
            for (int i = 0; i < redisNodes.length; i++) {
                String[] redisNode = redisNodes[i].split(":");
                redisNodeList.add(new RedisNode(redisNode[0], Integer.parseInt(redisNode[1])));
            }
        } catch (RestClientException e) {
            LOG.error("Connect to the CacheCloud exception", e);
        } catch (NumberFormatException e) {
            LOG.error("Digital formatting exception", e);
        }
        return redisNodeList;
    }


    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(){
        Assert.notNull(setRedisNode(),"Redis failed to initialize");
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
        LOG.info("redis initialization is complete");
        return redisTemplate;
    }
}