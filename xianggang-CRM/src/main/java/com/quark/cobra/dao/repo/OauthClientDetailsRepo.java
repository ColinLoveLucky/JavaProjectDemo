package com.quark.cobra.dao.repo;

import com.quark.cobra.entity.OauthClientDetails;

public interface OauthClientDetailsRepo {
    void save(OauthClientDetails oauthClientDetails);
    OauthClientDetails findByClientId(String clientId);
}
