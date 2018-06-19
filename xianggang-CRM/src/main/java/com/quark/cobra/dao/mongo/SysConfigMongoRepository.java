package com.quark.cobra.dao.mongo;

import com.quark.cobra.entity.SysConfig;
import com.quark.cobra.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SysConfigMongoRepository {
    @Autowired
    MongoTemplate mongoTemplate;
    public void save(SysConfig sysConfig) {
        mongoTemplate.save(sysConfig);
    }
    public String findByKey(String key) {
        Query query = Query.query(Criteria.where("key").is(key));
        SysConfig sysConfig = mongoTemplate.findOne(query, SysConfig.class);
        return sysConfig == null?"":StringUtils.trimToEmpty(sysConfig.getValue());
    }
    public Map<String, Object> querySysConfig(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        Query sysConfigQuery = new Query();

        String key = params.get("key") != null ? params.get("key").toString() : "";
        if (!StringUtils.isEmpty(key)) {
            sysConfigQuery.addCriteria(Criteria.where("key").is(key));
        }
        // 分页前先查出满足条件的记录总数;
        int count = (int) mongoTemplate.count(sysConfigQuery, SysConfig.class);
        result.put("total", count);
        // 数据库分页
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 0;
        Integer pageIndex = params.get("currentPage") != null ? (Integer) params.get("currentPage") : 0;
        Integer start = (pageIndex - 1) * pageSize;
        sysConfigQuery.skip(start);
        sysConfigQuery.limit(pageSize);
        List<SysConfig> list = mongoTemplate.find(sysConfigQuery, SysConfig.class);
        result.put("sysConfig", list);
        return result;
    }
    public void update(SysConfig sysConfig) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(sysConfig.getId()));
        Update update = new Update();
        update.set("key", sysConfig.getKey());
        update.set("value", sysConfig.getValue());
        update.set("notes", sysConfig.getNotes());
        mongoTemplate.updateFirst(query, update,SysConfig.class);
    }
    public void remove(String key){
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        mongoTemplate.remove(query,SysConfig.class);
    }
    public SysConfig findById(String id){
        return mongoTemplate.findById(id,SysConfig.class);
    }
}
