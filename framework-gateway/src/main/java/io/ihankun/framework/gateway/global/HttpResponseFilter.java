package io.ihankun.framework.gateway.global;

import io.ihankun.framework.gateway.context.RequestAttrNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class HttpResponseFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        //此数值要大一些，使过滤顺序在最后执行，优先级低于其他过滤器（LoadBalancerClientFilter）
        return 20000;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange).doFinally(signalType -> {

            String traceId = exchange.getRequest().getHeaders().getFirst(RequestAttrNames.TRACE_ID);

            log.info("traceId={},请求结束", traceId);
        });
    }
}
