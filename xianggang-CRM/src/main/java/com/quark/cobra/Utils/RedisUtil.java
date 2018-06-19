package com.quark.cobra.Utils;

import com.quark.cobra.constant.CrmConstants;

/**
 * 操作Redis工具类
 * 
 * @author: XianjiCai
 * @date: 2018/02/03 16:00
 */
public final class RedisUtil {
	
	/** private constructor */
	private RedisUtil () {}
	
	/**
	 * 获取Redis存储目录
	 * 
	 * @param redisDir redis存储目录
	 * @return
	 */
	public static String getRedisKeyDir(String redisDir) {
		return CrmConstants.CRM_REDIS_KEY_PREFIX + redisDir;
	}
	
	/**
	 * 获取Redis存储key路径
	 * 
	 * @param redisDir redis存储目录
	 * @param key redis存储key
	 * @return
	 */
	public static String getRedisKeyPath(String redisDir, String key) {
		return CrmConstants.CRM_REDIS_KEY_PREFIX + redisDir + key;
	}

}
