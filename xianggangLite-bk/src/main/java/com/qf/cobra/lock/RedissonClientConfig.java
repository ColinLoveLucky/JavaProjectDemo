package com.qf.cobra.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;

/**
 *  获取RedissonClient连接类
 * @author YanyanMao
 *
 */
@Configuration
public class RedissonClientConfig {
	
	@Autowired
	RedisClusterConfiguration redisClusterConfiguration;

    @Bean
    public RedissonClient redissonClient(){
    	Config config = new Config();
    	Set<RedisNode> redisNodeSet = redisClusterConfiguration.getClusterNodes();
    	List<String> redisNodeList = new ArrayList<String>();
    	for (RedisNode redisNode : redisNodeSet) {
    		redisNodeList.add(redisNode.asString());
		}
    	String[] nodeAddress = new String[redisNodeList.size()];
    	redisNodeList.toArray(nodeAddress);

    	config.useClusterServers().addNodeAddress(nodeAddress);
        return Redisson.create(config);
    }

}