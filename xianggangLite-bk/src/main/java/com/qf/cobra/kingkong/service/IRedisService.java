package com.qf.cobra.kingkong.service;

import com.qf.cobra.pojo.NewPagination;

import java.util.List;
import java.util.Map;

public interface IRedisService {
   void query(NewPagination<Map<String, Object>> pagination) throws Exception;

    void delete(String key);

    void save(String key,String value);

}
