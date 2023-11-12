package io.ihankun.framework.log.entity;

import lombok.Data;

/**
 * api请求需要记录到threadLocal的属性
 * @author hankun
 */
@Data
public class ApiLog {
    /**
     * 接口url
     */
    private String currentUrl;
    /**
     * 来源接口地址 (链路中最开始的url)
     */
    private String sourceUrl;

    public ApiLog() {
    }

    public ApiLog(String currentUrl, String sourceUrl) {
        this.currentUrl = currentUrl;
        this.sourceUrl = sourceUrl;
    }
}
