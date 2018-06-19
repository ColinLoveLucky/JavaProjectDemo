package com.quark.cobra.dao.repo;

import com.quark.cobra.AppTest;
import com.quark.cobra.entity.OauthClientDetails;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import static org.junit.Assert.*;

public class OauthClientDetailsRepoTest extends AppTest {
    @Autowired OauthClientDetailsRepo oauthClientDetailsRepo;
    @Test
    public void save() throws Exception {
        oauthClientDetailsRepo.save(OauthClientDetails.builder()
            .clientId("quark_qyj-borrower_qyj")
            .clientSecret("secret")
            .authorizedGrantTypes("password,refresh_token")
            .scope("read,write,openid")
            .accessTokenValidity(7200)
            .refreshTokenValidity(8000)
            .build());
    }
    @Test
    public void findByClientId() throws Exception {
        OauthClientDetails oauthClientDetails = oauthClientDetailsRepo.findByClientId("quark_qyj-borrower_qyj");
        Assert.assertNotNull(oauthClientDetails);
    }

}
