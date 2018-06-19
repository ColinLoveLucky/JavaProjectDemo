package com.quark.cobra.dao.repo;

import com.quark.cobra.AppTest;
import com.quark.cobra.entity.SysConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SysConfigRepoTest extends AppTest {
    @Autowired
    private SysConfigRepo sysConfigRepo;
    @Test
    public void save() throws Exception {
//        sysConfigRepo.save(SysConfig.builder()
//            .key("IP_BLACK_LIST")
//            .value("")
//            .notes("IP黑名单")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("IP_WHITE_LIST")
//            .value("::1,::ffff:172.16.36.63")
//            .notes("IP白名单")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("MOBILE_BLACK_LIST")
//            .value("")
//            .notes("手机黑名单")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("MOBILE_WHITE_LIST")
//            .value("")
//            .notes("手机白名单")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("IP_MAX_COUNT")
//            .value("0")
//            .notes("ip最大发送次数")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("MOBILE_MAX_COUNT")
//            .value("0")
//            .notes("手机最大发送次数")
//            .build());
//        sysConfigRepo.save(SysConfig.builder()
//            .key("NEED_IMAGE_CAPTCHA_COUNT")
//            .value("0")
//            .notes("当发送N次以后,需要图形验证码")
//            .build());
        sysConfigRepo.save(SysConfig.builder()
            .key("CAPTCHA_MAX_TIMEOUT")
            .value("30")
            .notes("验证码有效时间,单位分钟")
            .build());
        sysConfigRepo.save(SysConfig.builder()
            .key("COUNT_MAX_TIMEOUT")
            .value("60")
            .notes("发送次数统计时间,单位分钟")
            .build());
    }

    @Test
    public void findByKey() throws Exception {
        String value = "";
        System.out.println(value);
        System.out.println(Arrays.asList(value.split(",")));
    }

}
