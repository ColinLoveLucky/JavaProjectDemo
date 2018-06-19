package com.oauth.demo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
public class Authority {
    @Id
    @NotNull
    @Size(min=0,max=100)
    private String name;
}
