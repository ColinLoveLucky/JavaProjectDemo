package com.qf.cobra.loan.dao.impl;

import com.mongodb.WriteResult;

import com.qf.cobra.loan.dao.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by JokerCheng on 2017/9/6.
 */
public class BaseDaoImpl<T> implements IBaseDao<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public BaseDaoImpl() {
    }

    @Override
    public List<T> findAll() {
        return this.mongoTemplate.findAll(getEntityClass());
    }

    @Override
    public List<T> findAll(Map<String, Object> params) {
        Query query = this.createQuery(params);
        return this.mongoTemplate.find(query, getEntityClass());
    }
    @Override
    public T findOne(Map<String, Object> params) {
        Query query = this.createQuery(params);
        return this.mongoTemplate.findOne(query, getEntityClass());
    }

    @Override
    public void save(Object entity) {
        this.mongoTemplate.insert(entity);
    }


    @Override
    public WriteResult remove(Map<String, Object> params) {
        Query query = this.createQuery(params);
        return this.mongoTemplate.remove(query, getEntityClass());
    }

    @Override
    public T findAndModify(Map<String, Object> params, Map<String, Object> setParams) {
        Query query = this.createQuery(params);
        Update update = this.createUpdate(setParams);
        return this.mongoTemplate.findAndModify(query, update, getEntityClass());
    }

    private Query createQuery(Map<String, Object> params) {
        Query query = new Query();
        Criteria criteria = null;
        int count = 0;

        for(Iterator var5 = params.entrySet().iterator(); var5.hasNext(); ++count) {
            Map.Entry<String, Object> param = (Map.Entry)var5.next();
            if(count == 0) {
                criteria = Criteria.where((String)param.getKey()).is(param.getValue());
            } else {
                criteria.and((String)param.getKey()).is(param.getValue());
            }
        }

        query.addCriteria(criteria);
        return query;
    }

    private Update createUpdate(Map<String, Object> params) {
        Update update = new Update();
        Iterator var3 = params.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry)var3.next();
            update.set((String)param.getKey(), param.getValue());
        }
        return update;
    }

    /**
     * 获取需要操作的实体类class
     *
     * @return
     */
    private Class<T> getEntityClass() {
        return getSuperClassGenricType(getClass(), 0);
    }

    //    @SuppressWarnings("all")
    private Class getSuperClassGenricType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();// 得到泛型父类
        // 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("你输入的索引"
                    + (index < 0 ? "不能小于0" : "超出了参数的总数"));
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

}
