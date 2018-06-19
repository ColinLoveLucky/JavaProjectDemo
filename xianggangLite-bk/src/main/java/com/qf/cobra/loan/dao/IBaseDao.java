package com.qf.cobra.loan.dao;

import com.mongodb.WriteResult;

import java.util.List;
import java.util.Map;

/**
 * Created by JokerCheng on 2017/9/6.
 */
public interface IBaseDao<T> {
    List findAll();

    List findAll(Map<String, Object> params);

    Object findOne(Map<String, Object> params);

    void save(Object entity);

    WriteResult remove(Map<String, Object> params);

    Object findAndModify(Map<String, Object> params, Map<String, Object> setParams);
}
