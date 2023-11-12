package io.ihankun.framework.common.error.impl;

import io.ihankun.framework.common.error.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author hankun
 */

@Getter
@AllArgsConstructor
public enum GovernanceErrorCode implements IErrorCode {

    /**
     * 未配置服务鉴权服务端地址
     */
    AUTH_URL_ERROR("auth@1000", "未配置服务鉴权服务端地址,鉴权码:"),

    /**上游服务名称获取失败*/
    UPSTREAM_SERVER_ERROR("auth@1100", "请求被$1拦截,上游服务名称为空， 请检查请求Header中的 Upstream 字段header中未配置,鉴权码:"),

    /**
     * 服务规则获取失败
     */
    NO_RULES_ERROR("auth@1200", "请求被$1拦截,未匹配到服务接入鉴权规则,请检查请求接口鉴权配置,鉴权码:"),

    /**
     * 服务接入请求拒绝
     */
    IN_REFUSE("auth@2200", "请求被$1拦截,请确认上游服务$2是否拥有接口$3调用权限,鉴权码:"),

    /**
     * 服务鉴权失败
     */
    AUTH_ERROR("auth@2900", "请求被$1拦截,请检查鉴权配置,鉴权码:");


    private final String code;
    private final String msg;

    /**
     * 设置前缀
     */
    @Override
    public String prefix() {
        return "";
    }

    @Override
    public String getMsg(){
        return msg+code;
    }
}
