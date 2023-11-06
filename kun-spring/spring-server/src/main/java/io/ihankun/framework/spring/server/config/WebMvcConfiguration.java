package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.module.SimpleModule;
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.TimeZone;
//
///**
// * @author hankun
// */
//@EnableWebMvc
//@Configuration
//@Order(Integer.MIN_VALUE)
//public class WebMvcConfiguration implements WebMvcConfigurer {
//
//    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
//    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
//    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // 忽略 JSON 字符串中不识别的属性
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        // 忽略无法转换的对象
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//
//        // PrettyPrinter 格式化输出
//        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//
//        // NULL 不参与序列化
//        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        // 指定时区
//        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//
//        // 日期类型字符串处理
//        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT));
//        SimpleModule simpleModule = new SimpleModule();
//
//        // Java8日期处理
//        simpleModule.addSerializer(LocalDateTime.class,
//                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
//        simpleModule.addSerializer(LocalDate.class,
//                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
//        simpleModule.addSerializer(LocalTime.class,
//                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
//        simpleModule.addDeserializer(LocalDateTime.class,
//                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
//        simpleModule.addDeserializer(LocalDate.class,
//                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
//        simpleModule.addDeserializer(LocalTime.class,
//                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
//
//        /**
//         * 序列换成json时,将所有的long变成string
//         * 因为js中得数字类型不能包含所有的java long值
//         */
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        objectMapper.registerModule(simpleModule);
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        /*List<MediaType> mediaTypes = Arrays.asList(
//                MediaType.APPLICATION_JSON,
//                MediaType.TEXT_PLAIN,
//                MediaType.TEXT_HTML,
//                MediaType.TEXT_XML,
//                MediaType.APPLICATION_OCTET_STREAM);
//        jackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);*/
//        converters.add(jackson2HttpMessageConverter);
//
//    }
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        // 解决静态资源无法访问
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//
//        // 解决swagger无法访问
//        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//
//        // 解决swagger的js文件无法访问
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//
//        //解决knife4j中doc.html无法访问的问题
//        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
//    }
//}
