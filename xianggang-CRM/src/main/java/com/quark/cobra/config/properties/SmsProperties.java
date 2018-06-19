package com.quark.cobra.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "sms.emay")
@Getter
@Setter
public class SmsProperties {
    private String url;
    private String deptType;
    private String besType;
    private String thirdType;
}
