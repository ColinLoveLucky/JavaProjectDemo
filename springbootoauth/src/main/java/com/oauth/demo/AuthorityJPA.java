package com.oauth.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityJPA extends JpaRepository<Authority,String> {
}
