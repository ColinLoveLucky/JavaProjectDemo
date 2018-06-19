package com.qf.cobra.task.repository;

import org.springframework.stereotype.Repository;

import com.qf.platform.mongodb.dao.AbstractMongodbBaseRepositoryImpl;

/**
 * 共通MongoDB持久化操作类
 */
@Repository
public class BaseMongoRepository<T> extends AbstractMongodbBaseRepositoryImpl<T> {
	
}
