package com.qf.cobra.kingkong.service.Impl;

import com.qf.cobra.feign.service.CrmService;
import com.qf.cobra.kingkong.service.IUserService;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.system.service.impl.DictServiceImpl;
import com.qf.cobra.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CrmService crmService;
    @Override
    public void query(NewPagination<Map<String, Object>> pagination) throws Exception {
        Map<String, Object> condition =pagination.getCondition();
        try {
            String name = (String) condition.get("idName");
            String mobile = (String) condition.get("mobile");
            int size = pagination.getPageSize();
            int currentPage = pagination.getPageNo();
            condition.put("idName", name);
            condition.put("mobile", mobile);
            condition.put("pageSize", size);
            condition.put("currentPage", currentPage);
            String rspStr = crmService.queryUsers(condition);
            LOGGER.info("调用CRM查询用户信息，请求参数：{}，返回：{}", name + "_" + mobile, rspStr);
            Map<String, Object> rspMap = JsonUtil.convert(rspStr, Map.class);
            if ("20000".equals(String.valueOf(rspMap.get("code")))) {
                Map<String, Object> data = (Map<String, Object>) rspMap.get("data");
                int total = (int) data.get("total");
                List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");
                if (!org.springframework.util.CollectionUtils.isEmpty(users)) {
                    List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
                    for (Map<String, Object> user : users) {
                        Map<String,Object> record=new Hashtable<String,Object>();
                        record.put("id", user.get("id"));
                        record.put("tenant",user.get("tenant")!=null?user.get("tenant"):"");
                        record.put("mobile", user.get("mobile")!=null?user.get("mobile"):"");
                        record.put("idNo", user.get("idNo")!=null?user.get("idNo"):"");
                        record.put("idName",  user.get("idName")!=null? user.get("idName"):"");
                        record.put("sex", user.get("sex")!=null?user.get("sex"):"");
                        record.put("age", user.get("age")!=null?user.get("age"):"");
                        record.put("maritalStatus",  user.get("maritalStatus")!=null?user.get("maritalStatus"):"");
                        record.put("educationLevel", user.get("educationLevel")!=null? user.get("educationLevel"):"");
                        record.put("registrationDate",  user.get("registrationDate")!=null?user.get("registrationDate"):"");
                        record.put("updatePwdDate",  user.get("updatePwdDate")!=null?user.get("updatePwdDate"):"");
                        record.put("certificationDate", user.get("certificationDate")!=null?user.get("certificationDate"):"");
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
            LOGGER.error("查询用户信息失败", e);
            throw e;
        }
    }
}
