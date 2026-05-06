package io.hankun.framework.ai.agent.wbesocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.context.sys.GrayContext;
import io.hankun.framework.core.context.user.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AgentWebsocketHandler
 * @createAt: 2025/12/23 14:17
 * @author: hankun
 */
@Slf4j
public abstract class AgentWebsocketHandler extends AbstractWebSocketHandler {

    protected final AgentSessionManager agentSessionManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AgentWebsocketHandler(AgentSessionManager agentSessionManager) {
        this.agentSessionManager = agentSessionManager;
    }


    public Map<String, String> getQueryParams(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return null;
        }
        String[] pairs = query.split("&");
        Map<String, String> params = new HashMap<>();
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                //需要进行url解码
                value = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        log.info("WebSocket连接参数: {}", params);
        return params;
    }

    public abstract String defCode();

    public abstract Boolean defNeedTts();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        // 从查询参数中获取conversionId
        URI uri = session.getUri();
        if (uri == null) {
            log.warn("WebSocket连接失败: 未获取到uri - {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("未获取到uri"));
            return;
        }
        //从uri中提取所有的参数
        Map<String, String> queryParams = getQueryParams(uri);
        String conversionId = queryParams.get("conversionId");
        String code = queryParams.get("code");
        if (ObjectUtils.isEmpty(code)) {
            log.info("未提供code参数，使用默认值：{}", defCode());
            code = defCode();
        }
        String needTtsString = queryParams.get("needTts");
        Boolean needTts;
        if (ObjectUtils.isEmpty(needTtsString)) {
            log.info("未提供needTts参数，使用默认值：{}", defNeedTts());
            needTts = defNeedTts();
        } else {
            needTts = Boolean.parseBoolean(needTtsString);
        }
        if (!StringUtils.hasText(conversionId)) {
            log.warn("WebSocket连接失败: 未提供conversionId参数 - {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("未提供conversionId参数"));
            return;
        }
        if (!StringUtils.hasText(code)) {
            log.warn("WebSocket连接失败: 未提供code参数 - {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("未提供code参数"));
            return;
        }
        String gray = queryParams.get("gray");
        GrayContext.mock(gray);
        String domain = queryParams.get("domain");
        if (!StringUtils.hasText(domain)) {
            log.warn("WebSocket连接失败: 未提供domain参数 - {}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("未提供domain参数"));
            return;
        }
        DomainContext.mock(new String(Base64.getDecoder().decode(domain), StandardCharsets.UTF_8));
        String loginUser = queryParams.get("loginUser");
        if (StringUtils.hasText(loginUser)) {
            DomainContext.mock(new String(Base64.getDecoder().decode(loginUser), StandardCharsets.UTF_8));
        }
        init(session, conversionId, code, needTts, queryParams);
    }


    public abstract void init(WebSocketSession session, String conversationId, String code,
                              Boolean needTts, Map<String, String> queryParams);

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        if ("PING".equals(message.getPayload())) {
            sendMessage(session, new TextMessage("PONG"));
            log.debug("PING: sessionId={}", session.getId());
            // 续期缓存，防止因长时间无操作导致缓存被淘汰
            WebsocketSessionInfo sessionInfo = agentSessionManager.getSessionInfo(session);
            if (!agentSessionManager.renew(sessionInfo)) {
                log.warn("PING时续期缓存失败，会话可能不存在: sessionId={}", session.getId());
            }
            return;
        }
        handlerData(session, message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(@NotNull WebSocketSession session, @NotNull BinaryMessage message) throws Exception {
        handlerData(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        log.info("WebSocket连接关闭: sessionId={},status={}", session.getId(), status);
        agentSessionManager.close(session);
    }

    protected void handlerData(WebSocketSession session, Object message) {
        WebsocketSessionInfo sessionInfo = agentSessionManager.getSessionInfo(session);
        if (!agentSessionManager.exists(sessionInfo)) {
            log.warn("会话不存在: sessionId={}", session.getId());
            sendTextMessage(session, OutputMessage.error(session.getId(), "会话不存在"));
            return;
        }
        try {
            if (message instanceof String stringMessage) {
                handlerData(session, stringMessage, sessionInfo);
            } else if (message instanceof ByteBuffer bytes) {
                handlerBinaryData(session, bytes, sessionInfo);
            } else {
                log.warn("数据格式错误: sessionId={}", session.getId());
                sendTextMessage(session, OutputMessage.error(session.getId(), "数据格式错误"));
            }
        } catch (Exception e) {
            failed(session, sessionInfo, "数据处理异常：" + e.getMessage(), e);
        }
    }

    public void sendMessage(String code, String conversationId, Object message) {
        WebSocketSession session = agentSessionManager.getSession(code, conversationId);
        if (session != null) {
            sendTextMessage(session, message);
        }
    }

    public void sendMessage(String domain, LoginUserInfo loginUserInfo, Object message) {
        List<WebSocketSession> sessions = agentSessionManager.getSessionByUser(domain, loginUserInfo);
        for (WebSocketSession session : sessions) {
            if (session != null) {
                sendTextMessage(session, message);
            }
        }
    }

    protected void handlerData(WebSocketSession session, String message, WebsocketSessionInfo sessionInfo) {
        agentSessionManager.addData(sessionInfo, message);
    }

    protected void handlerBinaryData(WebSocketSession session, ByteBuffer message, WebsocketSessionInfo sessionInfo) {
        agentSessionManager.addData(sessionInfo, message);
    }

    public void failed(WebSocketSession session, String message, Throwable ex) {
        WebsocketSessionInfo sessionInfo = agentSessionManager.getSessionInfo(session);
        failed(session, sessionInfo, message, ex);
    }

    public void failed(WebSocketSession session, WebsocketSessionInfo sessionInfo, String message, Throwable ex) {
        log.error("会话【{}】异常，errorMessage={}，e=", sessionInfo, message, ex);
        sendTextMessage(session, OutputMessage.error(session.getId(), message));
        if (sessionInfo != null) {
            agentSessionManager.removeSession(sessionInfo);
        } else {
            log.warn("无关联的会话: sessionId={}", session.getId());
        }
    }


    public record OutputMessage(String sessionId, String type, Map<String, Object> data) {


        public static OutputMessage connection(String sessionId, String conversionId, String message) {
            return new OutputMessage(sessionId, "connection", Map.of("conversionId", conversionId, "errorMessage", message
                    , "timestamp", System.currentTimeMillis()));
        }

        public static OutputMessage error(String sessionId, String message) {
            return new OutputMessage(sessionId, "error", Map.of("errorMessage", message));
        }

        public static OutputMessage of(String sessionId, String type, Map<String, Object> data) {
            return new OutputMessage(sessionId, type, data);
        }
    }


    protected void sendTextMessage(WebSocketSession session, Object message) {
        try {
            String messageStr = objectMapper.writeValueAsString(message);
            sendMessage(session, new TextMessage(messageStr));
        } catch (IOException e) {
            log.error("发送WebSocket文本消息失败: e=", e);
        }
    }

    protected void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            } else {
                log.warn("WebSocket连接已关闭: sessionId={}", session.getId());
            }
        } catch (IOException e) {
            log.error("发送WebSocket文本消息失败: e=", e);
        }
    }
}
