package io.hankun.framework.gateway.filter;


import io.hankun.framework.core.response.ResponseResult;
import io.hankun.framework.gateway.entity.FilterResult;
import io.hankun.framework.gateway.entity.GatewayParam;
import org.springframework.web.server.ServerWebExchange;

public interface IRequestFilter {

    /**
     * 顺序
     * @return
     */
    Integer order();


    /**
     * 过滤器
     * @param exchange
     * @param param
     * @return
     */
    ResponseResult<FilterResult> filter(ServerWebExchange exchange, GatewayParam param);

}
