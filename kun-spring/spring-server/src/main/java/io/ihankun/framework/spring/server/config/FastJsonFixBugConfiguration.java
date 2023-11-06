package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.parser.ParserConfig;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author hankun
// */
//@Slf4j
//@Configuration
//public class FastJsonFixBugConfiguration {
//
//    static {
//
//        try {
//            //开启安全模式，会禁用autoType功能（目前未用到此功能）
//            ParserConfig.getGlobalInstance().setSafeMode(true);
//        } catch (Exception e) {
//            log.info("FastJsonFixBugConfiguration.static initializer.exception,e={}", e.getMessage());
//        }
//
//        //是否输出值为null的字段,默认为false
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue.getMask();
//        //数值字段如果为null,输出为0,而非null
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullNumberAsZero.getMask();
//        //List字段如果为null,输出为[],而非null
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullListAsEmpty.getMask();
//        //字符类型字段如果为null,输出为 "",而非null
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullStringAsEmpty.getMask();
//
//        log.info("FastJsonFixBugConfiguration.static.initializer.success");
//
//    }
//}
