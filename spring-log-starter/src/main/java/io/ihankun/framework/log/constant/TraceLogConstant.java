package io.ihankun.framework.log.constant;


import io.ihankun.framework.common.v1.id.IdGenerator;
import io.ihankun.framework.log.context.TraceLogContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hankun
 */
public interface TraceLogConstant {
    /**
     * tracceId
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 判断当前线程是否绑定了traceId
     */
    public static boolean isBindTraceId() {
        return TraceLogContext.get() != null;
    }

    /**
     * 获取Mdc中的traceId
     */
    public static String getTraceId() {
        String traceId = TraceLogContext.get();
        if (StringUtils.isEmpty(traceId)) {
            traceId = String.valueOf(IdGenerator.ins().generator());
        }
        return traceId;
    }

    /**
     * 项Mdc中设置traceId
     */
    public static void setTraceId(String traceId) {
        TraceLogContext.set(traceId);
    }
}
