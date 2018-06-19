package com.oauth.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {
   @RequestMapping(method=RequestMethod.GET)
    public String sayHello(){
        return "Hello User!";
    }
}
