package io.hankun.framework.springcloud.server.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hankun
 */
@Slf4j
@Data
@RefreshScope
@Configuration
@ConfigurationProperties("kun.product.auth")
public class AuthConfiguration {

    /**
     * 整体开关
     */
    private boolean enable = false;

    /**
     * 强制开启，默认非强制，开启后，会对所有Controller进行拦截，只要是通过前端发起的请求，如果没有增加注解，则会报错处理
     */
    private boolean force = false;

    /**
     * 限制模式 默认不开启限制
     */
    private boolean limitMode = false;

    /**
     * 忽略强制检查的地址
     */
    private List<String> ignoreRegex;
}
