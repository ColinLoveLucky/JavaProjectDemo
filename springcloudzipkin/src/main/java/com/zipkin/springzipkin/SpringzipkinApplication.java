package com.zipkin.springzipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zipkin.server.EnableZipkinServer;

@SpringBootApplication
@EnableZipkinServer
public class SpringzipkinApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringzipkinApplication.class, args);
    }

}
