package com.quark.cobra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;

@Configuration
public class CustomerAuthorizationServerConfigurer extends
    AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore tokenStore;

    @Resource(name = "customerClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        TokenStoreUserApprovalHandler userApprovalHandler = new
            TokenStoreUserApprovalHandler();
        userApprovalHandler.setTokenStore(tokenStore);
        userApprovalHandler.setClientDetailsService(clientDetailsService);
        userApprovalHandler.setRequestFactory(new
            DefaultOAuth2RequestFactory(clientDetailsService));
        return userApprovalHandler;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
        throws Exception {
        oauthServer.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
        throws Exception {
        endpoints.tokenStore(tokenStore)
            .userApprovalHandler(userApprovalHandler())
            .userDetailsService(userDetailsService)//缺少会使得refresh_token时提示IllegalStateException, UserDetailsService is required
            .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
        throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.authenticationProvider(authenticationProvider);//在配置
    }
}
