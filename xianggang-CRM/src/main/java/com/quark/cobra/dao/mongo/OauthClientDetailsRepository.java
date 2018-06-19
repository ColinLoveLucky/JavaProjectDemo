package com.quark.cobra.dao.mongo;

import com.quark.cobra.entity.OauthClientDetails;
import com.quark.cobra.entity.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OauthClientDetailsRepository {
    @Autowired
    MongoTemplate mongoTemplate;
    public void save(OauthClientDetails oauthClientDetails) {
        mongoTemplate.save(oauthClientDetails);
    }
    public OauthClientDetails findByClientId(String clientId) {
        Query query = Query.query(Criteria.where("clientId").is(clientId));
        return mongoTemplate.findOne(query, OauthClientDetails.class);
    }
}
