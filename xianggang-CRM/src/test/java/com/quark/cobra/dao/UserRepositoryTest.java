package com.quark.cobra.dao;

import com.quark.cobra.AppTest;
import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.dao.mysql.UserMapper;
import com.quark.cobra.dao.mysql.UserRepository;
import com.quark.cobra.entity.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@Transactional
public class UserRepositoryTest extends AppTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    String mobile = "18621503287";

    @Test
    public void findByMobileRepository() throws Exception {
        Assert.assertNotNull(userRepository);
        List<User> userList = userRepository.findByMobile(mobile);
        Assert.assertNotNull(userList);
        Assert.assertEquals(1, userList.size());
        System.out.println(JsonUtil.convert(userList));
    }

    @Test
    public void saveUserRepository() throws Exception {
        Assert.assertNotNull(userRepository);
        Assert.assertNotNull(userMapper);

        DateTime now = DateTime.now();
        User user = User.builder()
            .mobile(mobile)
            .password("123456789")
            .registrationDate(now.toDate())
            .updatePwdDate(now.toDate())
            .build();
        userRepository.save(user);

        List<User> userList = userRepository.findByMobile(mobile);
        Assert.assertNotNull(userList);
        Assert.assertEquals(1, userList.size());
    }

}
