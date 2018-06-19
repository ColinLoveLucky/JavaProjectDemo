package com.quark.cobra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class SpringMongoConfig {
    /**
     *  去掉_class列
     * @param converter
     */
    @Autowired
    public void mappingMongoConverter(MappingMongoConverter converter){
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
}
