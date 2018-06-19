package com.quark.cobra.dao.mongo;

import com.quark.cobra.entity.PendingUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PendingUserRepository {
    @Autowired
    MongoTemplate mongoTemplate;
    public void save(PendingUser pendingUser) {
        mongoTemplate.save(pendingUser);
    }
}
