package com.quark.cobra.dao.repo.impl;

import com.quark.cobra.dao.mongo.SysConfigMongoRepository;
import com.quark.cobra.dao.mongo.UserMongoRepository;
import com.quark.cobra.dao.repo.SysConfigRepo;
import com.quark.cobra.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("sysConfigRepo")
@ConditionalOnProperty(name = "datasource.mongo.enable", havingValue = "true",matchIfMissing = false)
public class SysConfigRepoMongoImpl implements SysConfigRepo {
    @Autowired
    private SysConfigMongoRepository sysConfigMongoRepository;
    @Override
    public void save(SysConfig sysConfig) {
        sysConfigMongoRepository.save(sysConfig);
    }

    @Override
    public String findByKey(String key) {
        return sysConfigMongoRepository.findByKey(key);
    }

    @Override
    public Map<String, Object> querySysConfig(Map<String, Object> params) {
        return sysConfigMongoRepository.querySysConfig(params);
    }

    @Override
    public void update(SysConfig sysConfig) {
         sysConfigMongoRepository.update(sysConfig);
    }

    @Override
    public void remove(String key) {
        sysConfigMongoRepository.remove(key);
    }

    @Override
    public SysConfig findById(String id) {
        return sysConfigMongoRepository.findById(id);
    }


}
