//package io.ihankun.framework.gateway.filter;
//
//
//import io.ihankun.framework.core.response.ResponseResult;
//import io.ihankun.framework.gateway.entity.FilterResult;
//import io.ihankun.framework.gateway.entity.GatewayParam;
//import org.springframework.web.server.ServerWebExchange;
//
//public interface IRequestFilter {
//
//    /**
//     * 顺序
//     * @return
//     */
//    Integer order();
//
//
//    /**
//     * 过滤器
//     * @param exchange
//     * @param param
//     * @return
//     */
//    ResponseResult<FilterResult> filter(ServerWebExchange exchange, GatewayParam param);
//
//}
