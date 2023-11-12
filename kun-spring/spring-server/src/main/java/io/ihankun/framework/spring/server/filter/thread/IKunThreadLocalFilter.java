package io.ihankun.framework.spring.server.filter.thread;

import feign.RequestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hankun
 */
public interface IKunThreadLocalFilter {

    /**
     * 上下文名称
     *
     * @return
     */
    String name();

    /**
     * 执行顺序，越小越靠前
     *
     * @return
     */
    Integer order();

    /**
     * 从 ThreadLocal 中获取信息设置到请求头
     *
     * @param template
     */
    String writeHeader(RequestTemplate template);

    /**
     * 从请求头中获取信息设置到 ThreadLocal
     *
     * @param request
     */
    String readHeader(HttpServletRequest request);

    /**
     * 清理上线程下文
     */
    void clearThreadLocal();
}
