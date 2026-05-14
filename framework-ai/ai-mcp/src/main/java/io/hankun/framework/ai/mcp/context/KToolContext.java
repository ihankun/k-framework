package io.hankun.framework.ai.mcp.context;

import io.hankun.framework.commons.utils.ContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: KToolContext
 * @createAt: 2025/5/29 09:22
 * @author: hankun
 */
public class KToolContext {

    public static final String TOOL_CALL_HISTORY = "TOOL_CALL_HISTORY";
    public static final String CHAT_COUNT = "CHAT_COUNT";

    public static final String CONTEXT_KEY = "contextKey";

    private final Map<String, Object> context;

    public KToolContext(Map<String, Object> context) {
        this.context = context;
    }

    public KToolContext() {
        this.context = new HashMap<>(0);
    }


    public void setContextKey(String contextKey) {
        context.put(CONTEXT_KEY, contextKey);
    }

    public String getContextKey() {
        return (String) context.get(CONTEXT_KEY);
    }


    public List<?> getHistory() {
        Object toolCallHistory = context.get(TOOL_CALL_HISTORY);
        if (toolCallHistory == null) {
            return null;
        }
        return (List<?>) toolCallHistory;
    }

    public int getChatCount() {
        Object chatCount = context.get(CHAT_COUNT);
        if (chatCount == null) {
            return 0;
        }
        return (Integer) chatCount;
    }

    public void incrementChatCount() {
        int chatCount = getChatCount();
        context.put(CHAT_COUNT, chatCount + 1);
    }


    public <T> T getData(Class<T> clazz) {
        return ContextUtil.getData(context, clazz.getCanonicalName());
    }

    public <T> void setData(T data, Class<T> clazz) {
        ContextUtil.setData(context, clazz.getCanonicalName(), data);
    }


    public Map<String, Object> toMap() {
        return context;
    }

    public boolean isEmpty() {
        return context.isEmpty();
    }

    @Override
    public String toString() {
        return "ToolContext{" +
                "context=" + context +
                '}';
    }
}
