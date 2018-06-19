package com.quark.cobra.config;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@Profile(value = {"sit", "dev"})
public class SwaggerConfig {
    @Bean
    public Docket scoreApi() {
        Predicate<RequestHandler> swaggerSelector = RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class);
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(swaggerSelector)
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("LoanCRM API")
            .description("")
            .termsOfServiceUrl("http://www.quarkfinance.com/")
            .contact(new Contact("LoanCRM", "www.quarkfinance.com", ""))
            .license("Internal Only")
            .licenseUrl("www.quarkfinance.com")
            .version("0.0.1")
            .build();
    }
}
