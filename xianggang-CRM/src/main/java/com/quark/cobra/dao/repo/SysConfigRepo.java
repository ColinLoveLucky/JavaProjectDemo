package com.quark.cobra.dao.repo;

import com.quark.cobra.entity.SysConfig;

import java.util.Map;

public interface SysConfigRepo {
    void save(SysConfig sysConfig);
    String findByKey(String key);
    Map<String,Object> querySysConfig(Map<String,Object> params);
    void update(SysConfig sysConfig);
    void remove(String key);
    SysConfig findById(String key);
}
