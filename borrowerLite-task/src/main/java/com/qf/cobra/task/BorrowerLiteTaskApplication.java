package com.qf.cobra.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.github.ltsopensource.spring.boot.annotation.EnableTaskTracker;

@SpringBootApplication
@EnableTaskTracker
@ComponentScan({"com.qf.cobra", "com.qf.platform"})
public class BorrowerLiteTaskApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BorrowerLiteTaskApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BorrowerLiteTaskApplication.class, args);
    }
}
