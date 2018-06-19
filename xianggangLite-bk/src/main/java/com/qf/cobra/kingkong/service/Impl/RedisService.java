package com.qf.cobra.kingkong.service.Impl;

import com.qf.cobra.kingkong.service.IRedisService;
import com.qf.cobra.pojo.NewPagination;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class RedisService implements IRedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void query(NewPagination<Map<String, Object>> pagination) throws Exception {
        Map<String, Object> condition = pagination.getCondition();
        if (condition != null) {
            String key = (String) condition.get("key");
            List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
            int total = 0;
            if (StringUtils.isEmpty(key)) {
                Object[] keys = stringRedisTemplate.keys("*").toArray();
                int pageSize = pagination.getPageSize() != 0 ? pagination.getPageSize() : 1;
                int currentTotal = keys.length / pageSize;
                total = currentTotal == 0 ? currentTotal : currentTotal + 1;
                pagination.setPageTotal(total);
                int size = pagination.getPageSize();
                int start = (pagination.getPageNo() - 1) * size;
                for (int i = 0; i < keys.length; i++) {
                    if (i >= start && i < start + size) {
                        Map<String, Object> record = new Hashtable<>();
                        String value= stringRedisTemplate.opsForValue().get(keys[i]);
                       // record.put(keys[i].toString(), value==null?"":value);
                        record.put("key",keys[i].toString());
                        record.put("value",value==null?"":value);
                        currentPageRecordList.add(record);
                    }
                }
                pagination.setData(currentPageRecordList);
            } else {
                Map<String, Object> record = new Hashtable<>();
                if(stringRedisTemplate.hasKey(key)){
                    String value= stringRedisTemplate.opsForValue().get(key);
                    record.put("key",key);
                    record.put("value",value==null?"":value);
                    currentPageRecordList.add(record);
                    pagination.setData(currentPageRecordList);
                    total = 1;
                    pagination.setPageTotal(total);
                }
            }
        }
    }

    @Override
    public void delete(String key) {
        if(key.equals("all")){
               // stringRedisTemplate.delete(stringRedisTemplate.keys("*"));
        }else {
            stringRedisTemplate.delete(key);
        }
    }

    @Override
    public void save(String key, String value) {
       if(!stringRedisTemplate.hasKey(key)){
           stringRedisTemplate.opsForValue().append(key,value);
       }
    }
}
