package com.quark.cobra.controller;

import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.domain.RegistrationReceptionReqBo;
import com.quark.cobra.domain.RegistrationReqBo;
import com.quark.cobra.entity.User;
import com.quark.cobra.service.IExtraUserService;
import com.quark.cobra.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/extra/user")
@Slf4j
@Api("extra User相关的action")
public class ExtraUserController {
    @Autowired
    private IExtraUserService extraUserService;

    @ApiOperation("接收其他系统已完成实名的用户")
    @PostMapping("/reception")
    public ResponseData reception(@RequestBody RegistrationReceptionReqBo reqBo) {
        String json = JsonUtil.convert(reqBo);
        log.info("reception : request param: {}",json);
        String userId = extraUserService.userReception(reqBo);
        log.info("reception result: userId:{},request param: {}",userId,json);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        return ResponseData.ok(map);
    }
}
