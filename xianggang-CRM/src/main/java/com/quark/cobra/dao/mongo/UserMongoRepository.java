package com.quark.cobra.dao.mongo;

import com.mongodb.WriteResult;
import com.quark.cobra.domain.UserQueryResBo;
import com.quark.cobra.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserMongoRepository{
    @Autowired
    MongoTemplate mongoTemplate;
    public List<User> findByMobile(String mobile){
        Query query = Query.query(Criteria.where("mobile").is(mobile));
        return mongoTemplate.find(query,User.class);
    }
    
    public List<User> findByIdNo(String idNo){
        Query query = Query.query(Criteria.where("idNo").is(idNo));
        return mongoTemplate.find(query, User.class);
    }

    public void save(User user){
        mongoTemplate.save(user);
    }

    public int updateUserById(User user){
        try {
            Query query = Query.query(Criteria.where("id").is(user.getId()));
            Update update = new Update();
            PropertyDescriptor[] propertyDescriptor = PropertyUtils.getPropertyDescriptors(user);
            for (int i = 0; i < propertyDescriptor.length; i++) {
                String name = propertyDescriptor[i].getName();
                if(StringUtils.equals(name,"class")||StringUtils.equals(name,"id")){
                    continue;
                }
                Object value = PropertyUtils.getSimpleProperty(user, name);
                if(value == null){
                    continue;
                }
                update.set(name,value);
            }
            WriteResult result = mongoTemplate.upsert(query, update, User.class);
            return result.getN();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
           log.info("读取属性失败,userId={}",user.getId(),e);
        }
        return 0;

//        Query query = Query.query(Criteria.where("discussList._id").is(replyId));
//        Update update = new Update().push("discussList.$.replys", discuss);//.$.元素下标
//        mongoTemplate.updateFirst(query, update, Yues.class);
    }

    public User findById(String id){
        return mongoTemplate.findById(id,User.class);
    }

    public Map<String,Object> queryUser(Map<String,Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        Query userQuery = new Query();

        String name = params.get("idName") != null ? params.get("idName").toString() : "";
        String mobile = params.get("mobile") != null ? params.get("mobile").toString() : "";
        if (!StringUtils.isEmpty(name)) {
//            Pattern namePattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
//            userQuery.addCriteria(Criteria.where("name").regex(namePattern));
            userQuery.addCriteria(Criteria.where("idName").is(name));
        }
        if (!StringUtils.isEmpty(mobile)) {
            userQuery.addCriteria(Criteria.where("mobile").is(mobile));
        }

        userQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC, "updatePwdDate")));
        // 分页前先查出满足条件的记录总数;
        int count = (int) mongoTemplate.count(userQuery, User.class);
        result.put("total", count);
        // 数据库分页
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 0;
        Integer pageIndex = params.get("currentPage") != null ? (Integer) params.get("currentPage") : 0;
        Integer start = (pageIndex - 1) * pageSize;
        userQuery.skip(start);
        userQuery.limit(pageSize);
        List<User> users = mongoTemplate.find(userQuery, User.class);
        result.put("users", users);
        return result;
    }
}
