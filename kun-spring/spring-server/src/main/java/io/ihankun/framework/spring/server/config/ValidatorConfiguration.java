package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import org.hibernate.validator.HibernateValidator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
//
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//
///**
// * @author hankun
// */
//@Configuration
//public class ValidatorConfiguration {
//
//    /**
//     * 启用方法级的校验
//     * @return
//     */
//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor() {
//        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
//        // 设置validator模式为快速失败返回
//        postProcessor.setValidator(validator());
//        return postProcessor;
//    }
//
//    @Bean
//    public Validator validator() {
//        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
//                .configure()
//                .failFast(true)
//                .buildValidatorFactory();
//        return validatorFactory.getValidator();
//    }
//}
