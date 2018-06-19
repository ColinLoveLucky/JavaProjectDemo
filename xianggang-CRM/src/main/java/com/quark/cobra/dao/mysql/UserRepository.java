package com.quark.cobra.dao.mysql;

import com.quark.cobra.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String>{
    List<User> findByMobile(String mobile);
}
