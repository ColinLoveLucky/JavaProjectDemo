package com.qf.cobra.task.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 测试Mongo实体类
 */
@Document(collection = "test")
@Getter
@Setter
@NoArgsConstructor
public class TestEntity {

	private String name;
}
