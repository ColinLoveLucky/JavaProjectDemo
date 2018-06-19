package com.quark.cobra.dao.repo.impl;

import com.quark.cobra.dao.mongo.OauthClientDetailsRepository;
import com.quark.cobra.dao.repo.OauthClientDetailsRepo;
import com.quark.cobra.entity.OauthClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("oauthClientDetailsRepo")
@ConditionalOnProperty(name = "datasource.mongo.enable", havingValue = "true",matchIfMissing = false)
public class OauthClientDetailsMongoImpl implements OauthClientDetailsRepo {
    @Autowired
    private OauthClientDetailsRepository oauthClientDetailsRepository;

    @Override
    public void save(OauthClientDetails oauthClientDetails) {
        oauthClientDetailsRepository.save(oauthClientDetails);
    }

    @Override
    public OauthClientDetails findByClientId(String clientId) {
        return oauthClientDetailsRepository.findByClientId(clientId);
    }
}
