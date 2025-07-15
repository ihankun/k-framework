package io.ihankun.framework.core.invocation;

import io.ihankun.framework.core.context.UpstreamInfo;

/**
 * @author hankun
 */
public interface InvocationCollector {

    /**
     * 获取当前服务名称
     *
     * @return 当前服务名称
     */
    String getCurrentService();

    /**
     * 收集调用信息
     *
     * @param currentService 当前服务名称
     * @param sourceService  来源服务名称
     * @param upstreamInfo   上游信息
     * @param requestUri     请求uri
     */
    void collectInvocation(String currentService, String sourceService,
                           UpstreamInfo upstreamInfo, String requestUri);

    /**
     * 收集调用信息
     *
     * @param currentService 当前服务名称
     * @param sourceService  来源服务名称
     * @param upstreamInfo   上游信息
     * @param requestUri     请求uri
     * @param authResult     授权结果
     * @param ip             ip
     */
    void collectFailedInvocation(String currentService, String sourceService,
                                 UpstreamInfo upstreamInfo, String requestUri, String authResult, String ip);
}
