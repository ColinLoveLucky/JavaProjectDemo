package com.qf.cobra.kingkong.service;

import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.SysConfig;


import java.util.Map;

public interface ISysConfigService {
    void query(NewPagination<Map<String, Object>> pagination)  throws Exception;;
    void save(SysConfig sysConfig);
    void edit(SysConfig sysConfig);
    void remove(String key);
    SysConfig findById(String id);
}
