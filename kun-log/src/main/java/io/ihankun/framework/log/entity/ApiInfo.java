package io.ihankun.framework.log.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hankun
 */
@Data
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 8331334991539998037L;

    /**
     * api的目标服务名称
     */
    private String targetServiceName;

    /**
     * 目标服务的url地址
     */
    private String targetUrl;


    public ApiInfo() {
    }

    public ApiInfo(String targetServiceName, String targetUrl) {
        this.targetServiceName = targetServiceName;
        this.targetUrl = targetUrl;
    }
}
