package com.quark.cobra.service.impl;


import com.quark.cobra.dao.repo.SysConfigRepo;
import com.quark.cobra.entity.SysConfig;
import com.quark.cobra.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysConfigService implements ISysConfigService {
    @Autowired
    private SysConfigRepo sysConfigRepo;
    @Override
    public Map<String, Object> querySysConfig(Map<String, Object> params) {
        return sysConfigRepo.querySysConfig(params);
    }

    @Override
    public void save(SysConfig sysConfig) {
        sysConfigRepo.save(sysConfig);
    }

    @Override
    public void update(SysConfig sysConfig) {
        sysConfigRepo.update(sysConfig);
    }

    @Override
    public void remove(String key) {
        sysConfigRepo.remove(key);
    }

    @Override
    public SysConfig findById(String id) {
        return sysConfigRepo.findById(id);
    }
}
