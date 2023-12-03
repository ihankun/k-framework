package io.ihankun.framework.common.http.v1.enums;

/**
 * @author hankun
 */
public enum HttpType {

    /**
     * RestTemplate 通用外部
     */
    R_COMM("rest_template_out"),

    /**
     * RestTemplate 微服务接口
     * 需要增加鉴权信息
     */
    R_GATEWAY("rest_template_gateway");

    private String type;

    HttpType(String type){
        this.type = type;
    }
}
