package com.zipkin.springzipkin;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
@RestController
public class SpringzipkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringzipkinApplication.class, args);
    }

    @RequestMapping("/hi")
    public String home(){
        return "hi i'm miya!";
    }

    @RequestMapping(value = { "/json" }, method = { RequestMethod.POST }, produces="text/html;charset=UTF-8")
    @ResponseBody
    public String callJson(HttpServletResponse rsp){
      //  rsp.setHeader("Content-Type","application/json;charset=UTF-8");
       // Cookie cookie =new Cookie("hi","hi") ;
       // rsp.addCookie(cookie);
      //  rsp.addHeader("Content-Type","application/json");
       // rsp.setContentType("application/json");
        return "{\"url\":\"http://192.168.1.1/notify\"}";
    }
    @RequestMapping("/miya")
    public String info(){
        return restTemplate.getForObject("http://localhost:9992/info",String.class);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
