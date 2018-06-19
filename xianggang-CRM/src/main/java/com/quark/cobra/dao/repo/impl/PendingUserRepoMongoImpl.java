package com.quark.cobra.dao.repo.impl;

import com.quark.cobra.dao.mongo.PendingUserRepository;
import com.quark.cobra.dao.repo.PendingUserRepo;
import com.quark.cobra.entity.PendingUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("pendingUserRepo")
@ConditionalOnProperty(name = "datasource.mongo.enable", havingValue = "true",matchIfMissing = false)
public class PendingUserRepoMongoImpl implements PendingUserRepo {
    @Autowired
    PendingUserRepository pendingUserRepository;
    @Override
    public void save(PendingUser pendingUser) {
        pendingUserRepository.save(pendingUser);
    }
}
