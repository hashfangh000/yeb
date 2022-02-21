package com.fh.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2配置类
 *
 * @author fanghao on 2022/1/15
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    /**
     * Docket规定扫描哪些包下面 生成swagger文档
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)      //文档类型
                .apiInfo(apiInfo())                         //
                .select()                                   //选择扫描哪个包
                .apis(RequestHandlerSelectors.basePackage("com.fh.server.controller"))        //包
                .paths((PathSelectors.any()))               //路径 任何路径
                .build()
                .securityContexts(securityContexts())
                .securitySchemes(securitySchemes());
    }


    private List<ApiKey> securitySchemes(){
        //设置请求头
        ArrayList<ApiKey> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "Header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts(){
        //设置需要登录认证的路劲
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/hello/.*"));
        return result;
    }

    //swagger全局拦截配置
    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    //返回默认授权
    private List<SecurityReference> defaultAuth() {
        ArrayList<SecurityReference> result = new ArrayList<>();
        //  授权范围(所有的接口)
        AuthorizationScope authorizationScope = new AuthorizationScope("global","accessEveryThing");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0]=authorizationScope;
        result.add(new SecurityReference("Authorization",authorizationScopes));
        return result;

    }

    /**
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("云E办接口文档")
                .description("云E办接口文档")
                .contact(new Contact("fanghao", "http:localhost:8081/doc.html", "hashfangh000@163.com"))
                .version("1.0").build();
    }
}