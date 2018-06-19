package com.quark.cobra.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.quark.cobra.constant.CrmConstants.CRM_REDIS_KEY_PREFIX;

public class RedisTemplateTokenStore implements TokenStore {
    private static final String ACCESS = CRM_REDIS_KEY_PREFIX+"access:";
    private static final String AUTH_TO_ACCESS = CRM_REDIS_KEY_PREFIX+"auth_to_access:";
    private static final String AUTH = CRM_REDIS_KEY_PREFIX+"auth:";
    private static final String REFRESH_AUTH = CRM_REDIS_KEY_PREFIX+"refresh_auth:";
    private static final String ACCESS_TO_REFRESH = CRM_REDIS_KEY_PREFIX+"access_to_refresh:";
    private static final String REFRESH = CRM_REDIS_KEY_PREFIX+"refresh:";
    private static final String REFRESH_TO_ACCESS = CRM_REDIS_KEY_PREFIX+"refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = CRM_REDIS_KEY_PREFIX+"client_id_to_access:";
    private static final String UNAME_TO_ACCESS = CRM_REDIS_KEY_PREFIX+"uname_to_access:";

    private RedisTemplate<String,Object> redisTemplate ;

    public RedisTemplate<String,Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        OAuth2AccessToken accessToken = (OAuth2AccessToken) redisTemplate.opsForValue().get(AUTH_TO_ACCESS+key);
        if (accessToken != null
            && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            // Keep the stores consistent (maybe the same user is represented by this authentication but the details
            // have changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        return (OAuth2Authentication) this.redisTemplate.opsForValue().get(AUTH +  token);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        return (OAuth2Authentication) this.redisTemplate.opsForValue().get( REFRESH_AUTH+token);
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

        this.redisTemplate.opsForValue().set(ACCESS+ token.getValue(), token);
        this.redisTemplate.opsForValue().set(AUTH +token.getValue(), authentication);
        this.redisTemplate.opsForValue().set(AUTH_TO_ACCESS+authenticationKeyGenerator.extractKey(authentication), token);
        if (!authentication.isClientOnly()) {
            redisTemplate.opsForList().rightPush(UNAME_TO_ACCESS+getApprovalKey(authentication), token) ;
        }

        redisTemplate.opsForList().rightPush(CLIENT_ID_TO_ACCESS+authentication.getOAuth2Request().getClientId(), token) ;

        if (token.getExpiration() != null) {

            int seconds = token.getExpiresIn();
            redisTemplate.expire(ACCESS+ token.getValue(), seconds, TimeUnit.SECONDS) ;
            redisTemplate.expire(AUTH+ token.getValue(), seconds, TimeUnit.SECONDS) ;

            redisTemplate.expire(AUTH_TO_ACCESS+ authenticationKeyGenerator.extractKey(authentication), seconds, TimeUnit.SECONDS) ;
            redisTemplate.expire(CLIENT_ID_TO_ACCESS+authentication.getOAuth2Request().getClientId(), seconds, TimeUnit.SECONDS) ;
            redisTemplate.expire(UNAME_TO_ACCESS+ getApprovalKey(authentication), seconds, TimeUnit.SECONDS) ;
        }
        if (token.getRefreshToken() != null && token.getRefreshToken().getValue() != null) {
            this.redisTemplate.opsForValue().set( REFRESH_TO_ACCESS+   token.getRefreshToken().getValue(), token.getValue());
            this.redisTemplate.opsForValue().set(ACCESS_TO_REFRESH+token.getValue(), token.getRefreshToken().getValue());
        }
    }

    private String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication()
            .getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private String getApprovalKey(String clientId, String userName) {
        return clientId + (userName==null ? "" : ":" + userName);
    }

    public void removeAccessToken(OAuth2AccessToken accessToken) {
        removeAccessToken(accessToken.getValue());
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return (OAuth2AccessToken) this.redisTemplate.opsForValue().get(ACCESS+tokenValue);
    }

    public void removeAccessToken(String tokenValue) {
        OAuth2AccessToken removed = (OAuth2AccessToken) redisTemplate.opsForValue().get(ACCESS+tokenValue);
        // Don't remove the refresh token - it's up to the caller to do that
        OAuth2Authentication authentication = (OAuth2Authentication) this.redisTemplate.opsForValue().get(AUTH+tokenValue);

        this.redisTemplate.delete(AUTH+tokenValue);
        redisTemplate.delete(ACCESS+tokenValue);
        this.redisTemplate.delete(ACCESS_TO_REFRESH +tokenValue);

        if (authentication != null) {
            this.redisTemplate.delete(AUTH_TO_ACCESS+authenticationKeyGenerator.extractKey(authentication));

            String clientId = authentication.getOAuth2Request().getClientId();redisTemplate.opsForList().leftPop(CLIENT_ID_TO_ACCESS+clientId);

            this.redisTemplate.delete(AUTH_TO_ACCESS+authenticationKeyGenerator.extractKey(authentication));
        }
    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        this.redisTemplate.opsForValue().set(REFRESH+refreshToken.getValue(), refreshToken);
        this.redisTemplate.opsForValue().set( REFRESH_AUTH + refreshToken.getValue(), authentication);
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return (OAuth2RefreshToken) this.redisTemplate.opsForValue().get(REFRESH+tokenValue);
    }

    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        this.redisTemplate.delete( REFRESH + tokenValue);
        this.redisTemplate.delete( REFRESH_AUTH + tokenValue);
        this.redisTemplate.delete(REFRESH_TO_ACCESS +tokenValue);
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {

        String token = (String) this.redisTemplate.opsForValue().get( REFRESH_TO_ACCESS  +refreshToken) ;

        if (token != null) {
            redisTemplate.delete(ACCESS+ token);
        }
    }

    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<Object> result =    redisTemplate.opsForList().range(UNAME_TO_ACCESS+ getApprovalKey(clientId, userName), 0, -1);

        if (result == null || result.size() == 0) {
            return Collections.<OAuth2AccessToken> emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(result.size());

        for(Iterator<Object> it = result.iterator(); it.hasNext();){
            OAuth2AccessToken accessToken = (OAuth2AccessToken) it.next();
            accessTokens.add(accessToken);
        }

        return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<Object> result =    redisTemplate.opsForList().range((CLIENT_ID_TO_ACCESS+clientId), 0, -1);

        if (result == null || result.size() == 0) {
            return Collections.<OAuth2AccessToken> emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(result.size());

        for(Iterator<Object> it = result.iterator();it.hasNext();){
            OAuth2AccessToken accessToken = (OAuth2AccessToken) it.next();
            accessTokens.add(accessToken);
        }

        return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);
    }
}