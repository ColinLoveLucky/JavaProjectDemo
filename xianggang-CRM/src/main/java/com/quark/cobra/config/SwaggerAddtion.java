package com.quark.cobra.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Sets;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile(value = {"sit", "dev"})
public class SwaggerAddtion implements ApiListingScannerPlugin {
    @Override
    public List<ApiDescription> apply(DocumentationContext documentationContext) {
        Set<ResponseMessage> responseMessages = new HashSet<>();
        responseMessages.add( new ResponseMessageBuilder().code(HttpStatus.CREATED.value()).message(HttpStatus.CREATED.getReasonPhrase()).build());
        responseMessages.add( new ResponseMessageBuilder().code(HttpStatus.UNAUTHORIZED.value()).message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build());
        responseMessages.add( new ResponseMessageBuilder().code(HttpStatus.FORBIDDEN.value()).message(HttpStatus.FORBIDDEN.getReasonPhrase()).build());
        responseMessages.add( new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value()).message(HttpStatus.NOT_FOUND.getReasonPhrase()).build());

        List<VendorExtension> extensions = new ArrayList<>();
        extensions.add(new StringVendorExtension("tenat","app"));

        return new ArrayList<ApiDescription>(
            Arrays.asList(
                new ApiDescription(
                    "/oauth/token",  //url
                    "UserToken", //描述
                    Arrays.asList(
                        new OperationBuilder(
                            new CachingOperationNameGenerator())
                            .method(HttpMethod.POST)//http请求类型
                            .extensions(extensions)
                            .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
                            .consumes(Sets.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                            .summary("登录")
//                            .notes("获取token")//方法描述
                            .responseMessages(responseMessages)
                            .tags(Sets.newHashSet("OAuth2.Token"))//归类标签
                            .parameters(
                                Arrays.asList(
                                    new ParameterBuilder()
                                        .description("oauth2鉴权方式")//参数描述
                                        .type(new TypeResolver().resolve(String.class))//参数数据类型
                                        .name("grant_type")//参数名称
                                        .defaultValue("password")//参数默认值
                                        .parameterType("query")//参数类型
                                        .parameterAccess("access")
                                        .required(true)//是否必填
                                        .modelRef(new ModelRef("string")) //参数数据类型
                                        .build(),
                                    new ParameterBuilder()
                                        .description("用户名")
                                        .type(new TypeResolver().resolve(String.class))
                                        .name("username")
                                        .parameterType("query")
                                        .parameterAccess("access")
                                        .required(true)
                                        .modelRef(new ModelRef("string")) //<5>
                                        .build(),
                                    new ParameterBuilder()
                                        .description("密码")
                                        .type(new TypeResolver().resolve(String.class))
                                        .name("password")
                                        .parameterType("query")
                                        .parameterAccess("access")
                                        .required(true)
                                        .modelRef(new ModelRef("string")) //<5>
                                        .build(),
                                    new ParameterBuilder()
                                        .description("租户")
                                        .type(new TypeResolver().resolve(String.class))
                                        .name("client_id")
                                        .parameterType("query")
                                        .parameterAccess("access")
                                        .required(true)
                                        .modelRef(new ModelRef("string")) //<5>
                                        .build(),
                                    new ParameterBuilder()
                                        .description("租户client")
                                        .type(new TypeResolver().resolve(String.class))
                                        .name("client_secret")
                                        .parameterType("query")
                                        .parameterAccess("access")
                                        .required(true)
                                        .modelRef(new ModelRef("string")) //<5>
                                        .build()
                                ))
                            .build()),
                    false)));
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }
}
