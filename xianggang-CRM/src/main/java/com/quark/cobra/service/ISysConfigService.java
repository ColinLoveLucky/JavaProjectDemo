package com.quark.cobra.service;

import com.quark.cobra.entity.SysConfig;

import java.util.Map;

public interface ISysConfigService {
    Map<String,Object> querySysConfig(Map<String,Object> params);
    void save(SysConfig sysConfig);
    void update(SysConfig sysConfig);
    void remove(String key);
    SysConfig findById(String id);
}
