package com.qf.cobra.mongo.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.qf.cobra.pojo.VersionConfig;

@RepositoryRestResource(path = "versionconfig")
public interface VersionConfigRepository extends MongoRepository<VersionConfig, ObjectId>{
// 使用MongoRepository,实体类中必须使用@Id
}
