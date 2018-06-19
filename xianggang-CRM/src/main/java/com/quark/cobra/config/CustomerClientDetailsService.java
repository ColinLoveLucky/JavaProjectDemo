package com.quark.cobra.config;

import com.quark.cobra.entity.OauthClientDetails;
import com.quark.cobra.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomerClientDetailsService implements ClientDetailsService {
    @Autowired
    private IUserService userService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        OauthClientDetails oauthClientDetails = userService.findOauthClientDetailsByClientId(clientId);
        if(oauthClientDetails==null){
            throw new ClientRegistrationException(String.format("Client with id %s not found", clientId));

        }
        BaseClientDetails baseClientDetails =  new BaseClientDetails(oauthClientDetails.getClientId(),
            oauthClientDetails.getResourceIds(),
            oauthClientDetails.getScope(),
            oauthClientDetails.getAuthorizedGrantTypes(),
            oauthClientDetails.getAuthorities(),
            oauthClientDetails.getWebServerRedirectUri());
        baseClientDetails.setClientSecret(oauthClientDetails.getClientSecret());
        baseClientDetails.setAccessTokenValiditySeconds(oauthClientDetails.getAccessTokenValidity());
        baseClientDetails.setRefreshTokenValiditySeconds(oauthClientDetails.getRefreshTokenValidity());
        return baseClientDetails;
    }
}
