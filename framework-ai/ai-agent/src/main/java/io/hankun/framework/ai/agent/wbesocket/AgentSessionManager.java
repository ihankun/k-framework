package io.hankun.framework.ai.agent.wbesocket;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.hankun.framework.ai.agent.audio.KAudioData;
import io.hankun.framework.ai.agent.call.CallExecutorService;
import io.hankun.framework.ai.agent.call.KCallExecutor;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.core.context.user.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @description:
 * @className: AgentSessionManager
 * @createAt: 2025/12/23 14:17
 * @author: hankun
 */
@Slf4j
@Component
public class AgentSessionManager {


    public static class SessionHandler {
        private final KCallExecutor executor;
        private volatile WebSocketSession session;

        public SessionHandler(KCallExecutor executor) {
            this.executor = executor;
        }

        public void init(boolean needTts) {
            executor.setWithTts(needTts);
            executor.setResultHandler(new Consumer<KAudioData>() {
                @Override
                public void accept(KAudioData kAudioData) {
                    String data = JSON.toJSONString(kAudioData);
                    try {
                        if (session != null && session.isOpen()) {
                            session.sendMessage(new TextMessage(data));
                        }
                    } catch (IOException e) {
                        log.error("发送数据失败: {}", e.getMessage());
                    }
                }
            });
            executor.initData();
        }
    }

    private final Cache<@NotNull String, SessionHandler> handlerMap;

    private final Map<String, WebsocketSessionInfo> sessionMap = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> userConversationMap = new ConcurrentHashMap<>();

    private final CallExecutorService callExecutorService;


    public AgentSessionManager(CallExecutorService callExecutorService) {
        this.callExecutorService = callExecutorService;
        handlerMap = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(5))
                .evictionListener((RemovalListener<@NotNull String, @NotNull SessionHandler>) (key, value, cause) -> {
                    if (value != null) {
                        value.executor.finish();
                    }
                    log.info("会话【{}】已关闭", key);
                })
                .build();
    }

    protected String buildUserKey(WebsocketSessionInfo sessionInfo) {
        LoginUserInfo userInfo = sessionInfo.loginUserInfo();
        String domain = sessionInfo.domain();
        return buildUserKey(userInfo, domain);
    }

    protected static @Nullable String buildUserKey(LoginUserInfo userInfo, String domain) {
        if (userInfo != null) {
            return domain + ":" + userInfo.getUserId();
        }
        return null;
    }

    public void init(WebSocketSession session, String conversationId, String code, Boolean needTts,
                     KToolContext context, Consumer<Throwable> errorHandler) {
        init(session, conversationId, code, needTts, context, errorHandler, null);
    }

    public void init(WebSocketSession session, String conversationId, String code, Boolean needTts,
                     KToolContext context, Consumer<Throwable> errorHandler, Consumer<KCallExecutor> initOperator) {
        WebsocketSessionInfo info = WebsocketSessionInfo.of(session.getId(), conversationId, code);
        String realConversationId = info.actuallyConversationId();
        handlerMap.asMap().compute(realConversationId,
                new BiFunction<String, SessionHandler, SessionHandler>() {
                    @Override
                    public SessionHandler apply(String string, SessionHandler sessionHandler) {
                        if (sessionHandler == null) {
                            sessionHandler = new SessionHandler(callExecutorService.create(code, realConversationId, context
                                    , TaskExecConfig.builder().clearWhenFailed(false).build()));
                            KCallExecutor executor = sessionHandler.executor;
                            executor.setErrorHandler(errorHandler);
                            sessionHandler.session = session;
                            if (initOperator != null) {
                                initOperator.accept(executor);
                            }
                            sessionHandler.init(needTts);
                        } else {
                            sessionHandler.session = session;
                        }
                        return sessionHandler;
                    }
                });
        sessionMap.put(session.getId(), info);
        String userKey = buildUserKey(info);
        //使用compute而不是computeIfAbsent后再add，确保和移除时的computeIfPresent存在竞争，避免数据不一致
        userConversationMap.compute(userKey, new BiFunction<String, Set<String>, Set<String>>() {
            @Override
            public Set<String> apply(String s, Set<String> conversations) {
                if (conversations == null) {
                    conversations = new CopyOnWriteArraySet<>();
                }
                conversations.add(realConversationId);
                return conversations;
            }
        });
    }

    public void removeSession(WebsocketSessionInfo sessionInfo) {
        handlerMap.asMap().computeIfPresent(sessionInfo.actuallyConversationId(), new BiFunction<String, SessionHandler, SessionHandler>() {
            @Override
            public SessionHandler apply(String s, SessionHandler sessionHandler) {
                if (sessionHandler.session != null) {
                    try {
                        sessionHandler.session.close(CloseStatus.SERVER_ERROR);
                    } catch (IOException e) {
                        log.error("会话【{}】websocket关闭异常: ", sessionHandler.executor.getSessionId(), e);
                    }
                    sessionMap.remove(sessionHandler.session.getId());
                }
                try {
                    sessionHandler.executor.close();
                } catch (IOException e) {
                    log.error("会话【{}】关闭异常: ", sessionHandler.executor.getSessionId(), e);
                }
                return null;
            }
        });
        removeUser(sessionInfo);
    }

    public void addData(WebsocketSessionInfo sessionInfo, String message) {
        SessionHandler sessionHandler = handlerMap.getIfPresent(sessionInfo.actuallyConversationId());
        if (sessionHandler == null) {
            log.warn("会话不存在，无法处理文本: {}", sessionInfo);
            return;
        }
        sessionHandler.executor.receiveData(message);
    }

    public void addData(WebsocketSessionInfo sessionInfo, ByteBuffer message) {
        SessionHandler sessionHandler = handlerMap.getIfPresent(sessionInfo.actuallyConversationId());
        if (sessionHandler == null) {
            log.warn("会话不存在，无法处理二进制数据: {}", sessionInfo);
            return;
        }
        sessionHandler.executor.inputBytes(message);
    }

    public WebsocketSessionInfo getSessionInfo(WebSocketSession session) {
        return sessionMap.get(session.getId());
    }

    public WebSocketSession getSession(String code, String conversationId) {
        String actuallyConversationId = WebsocketSessionInfo.buildConversationId(code, conversationId);
        SessionHandler sessionHandler = handlerMap.getIfPresent(actuallyConversationId);
        if (sessionHandler == null) {
            return null;
        }
        return sessionHandler.session;
    }

    public List<WebSocketSession> getSessionByUser(String domain, LoginUserInfo loginUserInfo) {
        String userKey = buildUserKey(loginUserInfo, domain);
        Set<String> conversations = userConversationMap.get(userKey);
        if (CollectionUtils.isEmpty(conversations)) {
            return List.of();
        }
        List<WebSocketSession> sessions = new ArrayList<>();
        for (String conversation : conversations) {
            SessionHandler sessionHandler = handlerMap.getIfPresent(conversation);
            if (sessionHandler != null) {
                sessions.add(sessionHandler.session);
            }
        }
        return sessions;
    }

    public boolean exists(WebsocketSessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }
        return handlerMap.getIfPresent(sessionInfo.actuallyConversationId()) != null;
    }

    /**
     * 续期缓存，防止因长时间无操作导致缓存被淘汰
     * @param sessionInfo 会话信息
     * @return true表示会话存在且续期成功，false表示会话不存在
     */
    public boolean renew(WebsocketSessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return false;
        }
        SessionHandler sessionHandler = handlerMap.getIfPresent(sessionInfo.actuallyConversationId());
        return sessionHandler != null;
    }

    public void close(WebSocketSession session) {
        sessionMap.remove(session.getId());
        WebsocketSessionInfo sessionInfo = sessionMap.get(session.getId());
        if (sessionInfo != null) {
            removeUser(sessionInfo);
        }
    }

    public void finishExecutor(WebSocketSession session) {
        WebsocketSessionInfo sessionInfo = sessionMap.get(session.getId());
        if (sessionInfo != null) {
            SessionHandler sessionHandler = handlerMap.getIfPresent(sessionInfo.actuallyConversationId());
            if (sessionHandler != null) {
                sessionHandler.executor.finish();
                handlerMap.invalidate(sessionInfo.actuallyConversationId());
            }
        }
    }

    private void removeUser(WebsocketSessionInfo sessionInfo) {
        String userKey = buildUserKey(sessionInfo);
        userConversationMap.computeIfPresent(userKey, (key, value) -> {
            value.remove(sessionInfo.actuallyConversationId());
            if (value.isEmpty()) {
                return null;
            }
            return value;
        });
    }


}
