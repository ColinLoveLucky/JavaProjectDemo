package com.quark.cobra.dao.repo;

import com.quark.cobra.AppTest;
import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserRepoTest extends AppTest {
    @Autowired
    private UserRepo userRepo;
    @Test
    public void findByMobile() throws Exception {
        Assert.assertNotNull(userRepo);
        List<User> userList =  userRepo.findByMobile("18621503286");
        Assert.assertNotNull(userList);
        Assert.assertEquals(1, userList.size());
        System.out.println(JsonUtil.convert(userList));
    }

    @Test
    public void findByMobileAndPassword() throws Exception {
    }

    @Test
    public void save() throws Exception {
    }

    @Test
    public void updateUserById() throws Exception {
    }

}
