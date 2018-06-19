package com.oauth.demo;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    @Column(updatable = false,nullable = false)
    @Size(min=0,max=100)
    private String username;

    @Size(min=0,max=100)
    private String password;

    @Email
    @Size(min=0,max=100)
    private String email;

    private boolean activated;

    @Size(min=0,max=100)
    @Column(name="activationkey")
    private String activationKey;

    @Size(min=0,max=100)
    @Column(name="resetpasswordkey")
    private String resetPasswordKey;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "authority"))
    private Set<Authority> authorities;
}
