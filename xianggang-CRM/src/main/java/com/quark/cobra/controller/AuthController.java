package com.quark.cobra.controller;

import com.quark.cobra.Utils.ResponseCode;
import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.vo.LoginUser;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RestController
@Slf4j
public class AuthController {
    @GetMapping("/user")
    @ApiOperation("token换取用户信息")
    public void  user(OAuth2Authentication authentication,HttpServletResponse response) throws IOException {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)authentication.getDetails();
//        LoginUser loginuser = (LoginUser)authentication.getPrincipal();
        response.sendRedirect("/user/"+authentication.getName()+"?access_token="+details.getTokenValue());
    }

    @GetMapping("/oauth/error")
    public ResponseData error(Principal user) {
        return ResponseData.error(ResponseCode.TOKEN_INVALID,"token失败");
    }
}
