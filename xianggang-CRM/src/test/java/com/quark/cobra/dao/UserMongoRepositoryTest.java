package com.quark.cobra.dao;

import com.quark.cobra.AppTest;
import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.dao.mongo.UserMongoRepository;
import com.quark.cobra.entity.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserMongoRepositoryTest extends AppTest {
    @Autowired
    private UserMongoRepository userMongoRepository;
    String mobile = "18621503286";
    @Test
    public void findByMobile() throws Exception {
        Assert.assertNotNull(userMongoRepository);
        List<User> userList = userMongoRepository.findByMobile(mobile);
        Assert.assertNotNull(userList);
        Assert.assertEquals(1, userList.size());
        System.out.println(userList.get(0).getId());
    }

    @Test
    public void save() throws Exception {
        Assert.assertNotNull(userMongoRepository);

        DateTime now = DateTime.now();
        User user = User.builder()
            .mobile(mobile)
            .password("123456789")
            .registrationDate(now.toDate())
            .updatePwdDate(now.toDate())
            .build();
        userMongoRepository.save(user);
    }

    @Test
    public void updateUserById() throws Exception {
        Assert.assertNotNull(userMongoRepository);
        userMongoRepository.updateUserById(User.builder().id("5a7561a38615e401c8944c91").idNo("123").idName("abc").build());
    }
    @Test
    public void findById() throws Exception {
        User user = userMongoRepository.findById("5a7561a38615e401c8944c91");
        Assert.assertNotNull(user);
        System.out.println(JsonUtil.convert(user));
    }
}
