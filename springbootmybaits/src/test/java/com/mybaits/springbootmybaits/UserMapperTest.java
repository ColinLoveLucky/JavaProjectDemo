package com.mybaits.springbootmybaits;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {
    /**
     *
     */
    @Autowired
    private UserMapper userMaper;

    @Test
    @Transactional
    public void test(){

        userMaper.insert("winterchen", "123456", "12345678910");
        User u = userMaper.findUserByPhone("12345678910");
        Assert.assertEquals("winterchen", u.getName());
    }
}
