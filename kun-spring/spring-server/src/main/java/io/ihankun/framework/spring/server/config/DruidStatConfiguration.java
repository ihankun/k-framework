package io.ihankun.framework.spring.server.config;//package com.ihankun.core.spring.server.config;
//
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@ConditionalOnClass(value = ServletRegistrationBean.class)
//public class DruidStatConfiguration {
//
//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//        //登录查看信息的账号密码, 用于登录Druid监控后台
//        String userName = "druid";
//        String password = "druid";
//        servletRegistrationBean.addInitParameter("loginUsername", userName);
//        servletRegistrationBean.addInitParameter("loginPassword", password);
//        //是否能够重置数据.
//        servletRegistrationBean.addInitParameter("resetEnable", "false");
//
//        log.info("DruidStatConfiguration.druidServlet.start,username={},password={}", userName, password);
//
//        return servletRegistrationBean;
//    }
//
//    /**
//     * 注册Filter信息, 监控拦截器
//     *
//     * @return
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new WebStatFilter());
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
//}
