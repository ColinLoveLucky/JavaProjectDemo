package com.qf.cobra.kingkong.service.Impl;

import com.qf.cobra.feign.service.CrmService;
import com.qf.cobra.kingkong.service.ISysConfigService;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.SysConfig;
import com.qf.cobra.system.service.impl.DictServiceImpl;
import com.qf.cobra.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysConfigService implements ISysConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);
    @Autowired
    private CrmService crmService;

    @Override
    public void query(NewPagination<Map<String, Object>> pagination) throws Exception {
        Map<String, Object> condition = pagination.getCondition();
        try {
            String key = (String) condition.get("key");
            int size = pagination.getPageSize();
            int currentPage = pagination.getPageNo();
            condition.put("key", key);
            condition.put("pageSize", size);
            condition.put("currentPage", currentPage);
            String rspStr = crmService.querySysConfig(condition);
            LOGGER.info("调用CRM查询系统配置信息，请求参数：{}，返回：{}", key, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                Map<String, Object> data = (Map<String, Object>) rspMap.get("data");
                int total = (int) data.get("total");
                List<Map<String, Object>> sysConfigs = (List<Map<String, Object>>) data.get("sysConfig");
                if (!org.springframework.util.CollectionUtils.isEmpty(sysConfigs)) {
                    List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> sycConfig : sysConfigs) {
                        Map<String, Object> record = new Hashtable<String, Object>();
                        record.put("key", sycConfig.get("key"));
                        record.put("value", sycConfig.get("value") != null ? sycConfig.get("value") : "");
                        record.put("notes", sycConfig.get("notes") != null ? sycConfig.get("notes") : "");
                        record.put("id", sycConfig.get("id") != null ? sycConfig.get("id") : "");
                        currentPageRecordList.add(record);
                    }
                    pagination.setData(currentPageRecordList);
                    pagination.setPageTotal(total);
                } else {
                    pagination.setData(null);
                    pagination.setPageTotal(0);
                }
            }
        } catch (Exception e) {
            LOGGER.error("查询系统配置信息失败", e);
            throw e;
        }
    }

    @Override
    public void save(SysConfig sysConfig) {
        crmService.saveSysConfig(sysConfig);
    }

    @Override
    public void edit(SysConfig sysConfig) {
        crmService.editSysConfig(sysConfig);
    }

    @Override
    public void remove(String key) {
        crmService.removeSysConfig(key);
    }

    @Override
    public SysConfig findById(String id) {
        SysConfig result=new SysConfig();
        try {
            String rspStr = crmService.findSysConfigById(id);
            LOGGER.info("调用CRM查询系统配置信息，请求参数：{}，返回：{}", id, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                Map<String, Object> data=(Map<String,Object>)rspMap.get("data");
                result.setId(data.get("id")!=null?data.get("id").toString():"");
                result.setKey(data.get("key")!=null?data.get("key").toString():"");
                result.setValue(data.get("value")!=null?data.get("value").toString():"");
                result.setNotes(data.get("notes")!=null?data.get("notes").toString():"");
            }
        } catch (Exception e) {
            LOGGER.error("查询系统配置信息失败", e);
            throw e;
        }
        return result;
    }

}
