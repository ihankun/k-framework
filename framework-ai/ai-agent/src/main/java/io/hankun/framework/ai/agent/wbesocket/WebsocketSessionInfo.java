package io.hankun.framework.ai.agent.wbesocket;


import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.user.LoginUserContext;
import io.hankun.framework.core.context.user.LoginUserInfo;

/**
 * @description:
 * @className: WebsocketSessionInfo
 * @createAt: 2025/12/31 09:35
 * @author: hankun
 */
public record WebsocketSessionInfo(String sessionId, String originConversationId,
                                   String actuallyConversationId,
                                   String code, String domain, LoginUserInfo loginUserInfo) {

    public static WebsocketSessionInfo of(String sessionId, String conversationId, String code) {
        return new WebsocketSessionInfo(sessionId, conversationId, buildConversationId(code, conversationId)
                , code, DomainContext.get(), LoginUserContext.get());
    }


    public static String buildConversationId(String code, String conversationId) {
        return code + ":" + conversationId;
    }
}
