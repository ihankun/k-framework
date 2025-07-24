package io.ihankun.framework.gateway.global;

import com.alibaba.fastjson.JSON;
import io.ihankun.framework.core.response.ResponseResult;
import io.ihankun.framework.gateway.context.RequestAttrNames;
import io.ihankun.framework.gateway.entity.FilterResult;
import io.ihankun.framework.gateway.entity.GatewayParam;
import io.ihankun.framework.gateway.filter.IRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BusinessFilter extends AbstractGlobalFilter implements GlobalFilter, Ordered, ApplicationContextAware {

    protected Base64.Decoder decoder = Base64.getDecoder();
    protected Base64.Encoder encoder = Base64.getEncoder();

    private ApplicationContext context;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String path = request.getURI().getPath();
        HttpMethod httpMethod = request.getMethod();
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        Map<String, String> params = null;
        String body = null;
        //如果为POST提交或者PUT提交，则取RequestBody
        if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
            body = exchange.getAttribute(RequestAttrNames.REQUEST_BODY);
        } else {
            params = JSON.parseObject(exchange.getAttribute(RequestAttrNames.REQUEST_BODY), Map.class);
        }

        GatewayParam param = new GatewayParam(path, route, httpMethod, contentType, params, body, headers);

        List<IRequestFilter> filterList = fetchFilter();

        ServerWebExchange lastExchange = exchange;

        for (IRequestFilter filter : filterList) {

            //获取过滤器结果
            ResponseResult<FilterResult> result = filter.filter(exchange, param);

            //如果过滤器结果返回错误，直接返回，结束循环
            if (!result.isSuccess()) {
                return flush(exchange, result);
            }
            FilterResult data = result.getData();
            //如果返回的结果成功，且内容为空，且返回内容标识不进行下次循环，则返回结果
            if (data != null && !data.isNext()) {
                return chain.filter(executeResult(data, exchange));
            }
            //其他情况进行循环，直到所有过滤器执行完毕
            lastExchange = executeResult(data, exchange);
        }

        return chain.filter(lastExchange);
    }


    private List<IRequestFilter> fetchFilter() {
        Map<String, IRequestFilter> beans = context.getBeansOfType(IRequestFilter.class);
        return beans.values().stream().sorted(Comparator.comparing(IRequestFilter::order)).collect(Collectors.toList());
    }


    private ServerWebExchange executeResult(FilterResult result, ServerWebExchange exchange) {

        if (result == null) {
            return exchange;
        }

        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
        ServerHttpResponse response = exchange.getResponse();

        //请求头处理
        Map<String, String> requestHeader = result.getRequestHeader();
        List<String> rmRequestHeader = result.getRmRequestHeader();
        if (!CollectionUtils.isEmpty(rmRequestHeader) && !CollectionUtils.isEmpty(requestHeader)) {
            rmRequestHeader.forEach(item -> requestHeader.remove(item));
        }
        if (!CollectionUtils.isEmpty(requestHeader)) {
            requestHeader.entrySet().forEach(entry -> mutate.header(entry.getKey(), entry.getValue()));
        }

        //响应头处理
        Map<String, String> responseHeader = result.getResponseHeader();
        List<String> rmResponseHeader = result.getRmResponseHeader();
        if (!CollectionUtils.isEmpty(rmResponseHeader) && !CollectionUtils.isEmpty(responseHeader)) {
            rmResponseHeader.forEach(item -> responseHeader.remove(item));
        }
        if (!CollectionUtils.isEmpty(responseHeader)) {
            responseHeader.entrySet().forEach(entry -> response.getHeaders().add(entry.getKey(), entry.getValue()));
        }


        return exchange.mutate().request(mutate.build()).build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
