package io.ihankun.framework.spring.server.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hankun
 */
@Slf4j
public class RequestLogUtil {

    /**
     * 此参数主要为了防止打印日志时，超大的对象导致内存溢出(只判断了入参，响应结果未判断)
     */
    private static final int MAX_LOG_LENGTH = 4 * 1024 * 1024;

    public static void log(String format, Object arg1, Object arg2) {
        if (log.isDebugEnabled()) {
            //debug可以全量打印body内容，日志内容不会被截断
            log.debug(format, arg1, arg2);
        } else if (log.isInfoEnabled()) {
            log.info(format, arg1, arg2);
        }
    }

    /**
     * 根据内容长度决定是否打印日志（防止打印超大请求内容时内存溢出）
     *
     * @param contentLength
     * @param url
     * @param body
     */
    public static void logWithContentLength(long contentLength, Object url, Object body) {
        //日志超长并且不是debug级别，则不打印日志详细内容
        if (contentLength > MAX_LOG_LENGTH && !log.isDebugEnabled()) {
            log("请求地址:{},参数长度超过:{},不再打印", url, MAX_LOG_LENGTH);
        } else {
            log("请求地址:{},请求参数:{}", url, body);
        }
    }
}
