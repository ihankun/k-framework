package io.hankun.framework.ai.tools.trace;

import io.hankun.framework.ai.core.record.IDetailRecord;
import io.hankun.framework.ai.core.session.task.MapConfig;
import io.hankun.framework.commons.context.KContextHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Stack;

/**
 * @description:
 * @className: TraceContextHolder
 * @createAt: 2025/8/29 10:42
 * @author: hankun
 */
public class TraceContextHolder {

    public static final String TRACE_CONTEXT = "trace_context";

    public static TraceContext create(String type, MapConfig mapConfig) {
        TraceContext traceContext = new TraceContext(type, new HashMap<>(), mapConfig);
        push(traceContext);
        traceContext.start();
        return traceContext;
    }

    public static TraceContext create(IDetailRecord detailRecord, MapConfig mapConfig) {
        return create(detailRecord.type(), mapConfig);
    }

    public static void push(TraceContext traceContext) {
        Stack<TraceContext> traceContexts = getData();
        if (traceContexts == null) {
            traceContexts = new Stack<>();
            KContextHolder.setData(TRACE_CONTEXT, traceContexts);
        }
        traceContexts.push(traceContext);
    }

    @Nullable
    private static Stack<TraceContext> getData() {
        return KContextHolder.getData(TRACE_CONTEXT);
    }

    public static TraceContext peek() {
        Stack<TraceContext> traceContexts = getData();
        if (traceContexts == null) {
            return null;
        }
        if (traceContexts.isEmpty()) {
            return null;
        }
        return traceContexts.peek();
    }

    public static TraceContext pop() {
        Stack<TraceContext> traceContexts = getData();
        if (traceContexts == null) {
            return null;
        }
        if (traceContexts.isEmpty()) {
            return null;
        }
        return traceContexts.pop();
    }

    public static void removeTraceContext() {
        KContextHolder.removeData(TRACE_CONTEXT);
    }
}
