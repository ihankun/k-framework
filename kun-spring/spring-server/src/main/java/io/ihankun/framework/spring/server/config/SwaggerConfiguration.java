package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import com.google.common.base.Function;
//import com.google.common.base.Optional;
//import com.google.common.base.Predicate;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.env.Environment;
//import org.springframework.util.ClassUtils;
//import org.springframework.util.StringUtils;
//import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
//import springfox.documentation.RequestHandler;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.regex.Pattern;
//
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//@Slf4j
//@Import(BeanValidatorPluginsConfiguration.class)
//@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
//public class SwaggerConfiguration implements BeanFactoryPostProcessor, ApplicationContextAware {
//
//    private ApplicationContext context;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        log.info("SwaggerConfiguration.application.init");
//        context = applicationContext;
//    }
//
//    @Data
//    static class Group {
//        private String title;
//        private String description;
//        private String author;
//        private String version;
//        private String scanPackage;
//    }
//
//
//    private ApiInfo apiInfo(String title, String description, String name, String version) {
//
//        String applicationName = System.getProperty("spring.application.name");
//        if (!StringUtils.isEmpty(applicationName)) {
//            title = title + "(" + applicationName + ")";
//        }
//
//        return new ApiInfoBuilder()
//                .title(title)
//                .description(description)
//                .termsOfServiceUrl("127.0.0.1")
//                .version(version)
//                .build();
//    }
//
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        Environment environment = context.getEnvironment();
//        String format = "swagger.group-list.group%d.%s";
//        int tryCount = 20;
//        for (int i = 0; i < tryCount; i++) {
//            String title = environment.getProperty(String.format(format, i, "title"));
//            String description = environment.getProperty(String.format(format, i, "description"));
//            String author = environment.getProperty(String.format(format, i, "author"));
//            String version = environment.getProperty(String.format(format, i, "version"));
//            String scanPackage = environment.getProperty(String.format(format, i, "scanPackage"));
//            if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(scanPackage)) {
//                Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                        .groupName(title)
//                        .apiInfo(apiInfo(title, description, author, version)).select()
//                        .apis(basePackage(scanPackage))
//                        .paths(PathSelectors.any())
//                        .build();
//                beanFactory.registerSingleton("Docket#" + i, docket);
//                log.info("SwaggerConfiguration.register.docket,title={},package={}", title, scanPackage);
//            }
//        }
//    }
//
//    public static Predicate<RequestHandler> basePackage(final String basePackage) {
//        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
//    }
//
//    /**
//     * 匹配包名是否符合
//     *
//     * @param regex 基础包名
//     * @return
//     */
//    private static Function<Class<?>, Boolean> handlerPackage(final String regex) {
//
//        return input -> {
//            if (input.getPackage() == null) {
//                return Boolean.FALSE;
//            }
//            boolean flag = Pattern.matches(regex, input.getPackage().getName());
//            if (flag) {
//                return true;
//            }
//            flag = ClassUtils.getPackageName(input).startsWith(regex);
//            return flag;
//        };
//    }
//
//    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
//        return Optional.fromNullable(input.declaringClass());
//    }
//}
