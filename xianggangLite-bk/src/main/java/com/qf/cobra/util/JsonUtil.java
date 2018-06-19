/**
 * @Title:   [JsonUtil.java]
 * @Package: [com.quark.cobra.ndes.util]
 * @author:  [ChangcaiCao] 
 * @CreateDate: [2017年3月21日 下午5:18:27]   
 * @UpdateUser: [ChangcaiCao]   
 * @UpdateDate: [2017年3月21日 下午5:18:27]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description:  [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
package com.qf.cobra.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName: JsonUtil
 * @author:  [ChangcaiCao] 
 * @CreateDate: [2017年3月21日 下午5:18:27]   
 * @UpdateUser: [ChangcaiCao]   
 * @UpdateDate: [2017年3月21日 下午5:18:27]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description:  [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
public class JsonUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper mapper;

	public static String convert(Object obj) {
		String result = null;
		try {
			result = getObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			LOGGER.error("Jackson exception", e);
		}
		return result;
	}
	
	public static <T> T convert(String json, Class<T> t){
		T obj = null;
		try {
			obj = getObjectMapper().readValue(json, t);
		} catch (Exception e) {
			LOGGER.error("Jackson exception", e);
		}
		return obj;
	}
	
	public static ObjectMapper getObjectMapper() {
		synchronized (JsonUtil.class) {
			if(mapper == null){
				mapper = new ObjectMapper();
			}
			
		}
		return mapper;
	}

	public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return getObjectMapper().getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
}
