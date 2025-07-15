//package io.ihankun.framework.gateway.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
///**
// * 跨域配置
// * @author hankun
// */
//@Configuration
//@Slf4j
//public class CorsFilterConfiguration implements ApplicationContextAware {
//
//    private static final String MAX_AGE = "18000L";
//    private ApplicationContext context;
//    private static final String KEY_CORS = "spring.config.cors";
//    private static final String KEY_CORS_ALLOWHEADERS = "spring.cors-allow-headers";
//
//    @Bean
//    public WebFilter corsFilter() {
//
//        return (ServerWebExchange ctx, WebFilterChain chain) -> {
//
//            if (context == null) {
//                return chain.filter(ctx);
//            }
//
//            String cors = context.getEnvironment().getProperty(KEY_CORS, "false");
//            if (!Boolean.TRUE.toString().equals(cors)) {
//                return chain.filter(ctx);
//            }
//
//            ServerHttpRequest request = ctx.getRequest();
//            ServerHttpResponse response = ctx.getResponse();
//            HttpHeaders headers = response.getHeaders();
//            headers.set("Cache-Control", "no-cache");
//            String origin = request.getHeaders().getOrigin();
//
//            if (log.isDebugEnabled()) {
//                log.debug("CorsFilterConfiguration.corsFilter.start,origin={},path={}", origin, request.getPath().contextPath());
//            }
//
//            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//            String corsAllowHeaders = context.getEnvironment().getProperty(KEY_CORS_ALLOWHEADERS, "Authorization,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type," +
//                    "Token,ehrviewToken,Gateway");
//            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, corsAllowHeaders + "," + corsAllowHeaders.toLowerCase());
//            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
//            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
//            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
//            if (request.getMethod() == HttpMethod.OPTIONS) {
//                response.setStatusCode(HttpStatus.OK);
//                return Mono.empty();
//            }
//
//            return chain.filter(ctx);
//        };
//    }
//
//    @Bean
//    public ServerCodecConfigurer serverCodecConfigurer() {
//        return new DefaultServerCodecConfigurer();
//    }
//
//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        return new HiddenHttpMethodFilter() {
//            @Override
//            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//                return chain.filter(exchange);
//            }
//        };
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.context = applicationContext;
//    }
//}
