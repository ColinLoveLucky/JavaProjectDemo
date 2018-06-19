package com.qf.cobra.kingkong.service;

import com.qf.cobra.pojo.NewPagination;

import java.util.Map;

public interface IUserService {
    void query(NewPagination<Map<String, Object>> pagination)  throws Exception;;
}
