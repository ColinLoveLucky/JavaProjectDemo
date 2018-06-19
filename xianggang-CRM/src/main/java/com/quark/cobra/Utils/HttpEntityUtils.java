package com.quark.cobra.Utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpEntity设置
 * 
 * @author: XianjiCai
 * @date: 2018/02/02 12:33
 */
public class HttpEntityUtils {

    /**
     * 获取POST请求实体
     *
     * @param tenant
     * @param params
     * @return
     */
    public static HttpEntity<String> getHttpEntityForPost(String params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(params, headers);
    }
    
    /**
     * 获取GET请求实体
     *
     * @param tenant
     * @param client
     * @return
     */
    public static HttpEntity<String> getHttpEntityForGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(headers);
    }
    
    /**
     * 获取POST请求实体
     * Body参数为Map类型
     * 
     * @param tenant
     * @param client
     * @param productId
     * @param access_token
     * @param allRequestParams
     * @return  
     * @return
     * @throws
     */
    public static HttpEntity<Map<String, Object>> getHttpEntity(Map<String, Object> allRequestParams) {
        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.putAll(allRequestParams);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<>(bodyMap, headers);
    }

}
