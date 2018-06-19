package com.qf.cobra.mongo.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.qf.cobra.pojo.ProcessVariablesConfig;

@RepositoryRestResource(path = "processconfig")
public interface ProcessVariablesConfigRepository extends MongoRepository<ProcessVariablesConfig, ObjectId>{
// 使用MongoRepository,实体类中必须使用@Id
}
