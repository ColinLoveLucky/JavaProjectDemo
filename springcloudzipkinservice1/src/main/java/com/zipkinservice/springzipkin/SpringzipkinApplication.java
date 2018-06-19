package com.zipkinservice.springzipkin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class SpringzipkinApplication {



    public static void main(String[] args) {
        SpringApplication.run(SpringzipkinApplication.class, args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping("/hi")
    public String callHome(){
        return restTemplate.getForObject("http://localhost:9991/miya", String.class);
    }
    @RequestMapping("/info")
    public String info(){
        return "i'm service-hi";
    }


}
