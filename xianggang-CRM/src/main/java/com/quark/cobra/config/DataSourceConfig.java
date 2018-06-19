package com.quark.cobra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

//@Configuration
public class DataSourceConfig {
//    @Bean
//    @Primary
//    @ConditionalOnExpression("${datasource.mongo.enable:false}")
//    @ConfigurationProperties(prefix = "datasource.mongo")
//    public DataSource dataSourceMongo(){
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @Primary
//    @ConditionalOnExpression("${datasource.mysql.enable:false}")
//    @ConfigurationProperties(prefix = "datasource.mysql")
//    public DataSource dataSourceMySql(){
//        return DataSourceBuilder.create().build();
//    }
}
