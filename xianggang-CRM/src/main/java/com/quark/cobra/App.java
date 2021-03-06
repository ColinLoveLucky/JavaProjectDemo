package com.quark.cobra;

import com.qf.discovery.QfDiscoveryClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@QfDiscoveryClientConfiguration
@EnableCircuitBreaker
@EnableJpaRepositories
@EnableAuthorizationServer
@EnableResourceServer
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
