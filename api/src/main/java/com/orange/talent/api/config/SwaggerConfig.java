package com.orange.talent.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_TITLE = "Lottery API";
    private static final String API_DESCRIPTION = "REST API for lottery";
    private static final String CONTACT_NAME = "Matheus Carneiro";
    private static final String CONTACT_GITHUB = "https://github.com/mcarneirobug";
    private static final String CONTACT_EMAIL = "mccarneiro_@hotmail.com";

    @Bean
    public Docket lotteryApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(basePackage("com.orange.talent.api"))
                .paths(any())
                .build()
                .apiInfo(buildApiInfo());
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version("1.0.0")
                .contact(new Contact(CONTACT_NAME, CONTACT_GITHUB, CONTACT_EMAIL))
                .build();
    }

}
