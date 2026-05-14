package io.hankun.framework.ai.core.http;

import lombok.Data;

/**
 * @description:
 * @className: HttpConfig
 * @createAt: 2025/7/7 10:06
 * @author: hankun
 */
@Data
public class HttpConfig {

    private Integer connectTimeout = 20;
    private Integer readTimeout = 20;
    private Integer writeTimeout = 20;
    private Integer poolMaxIdleConnections = 20;
    private Integer poolKeepAliveDuration = 300;
}
