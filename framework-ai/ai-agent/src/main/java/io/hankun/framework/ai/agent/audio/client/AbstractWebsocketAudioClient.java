package io.hankun.framework.ai.agent.audio.client;

import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.CloseData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: AbstractWebsocketAudioClient
 * @createAt: 2025/12/16 18:02
 * @author: hankun
 */
@Slf4j
public abstract class AbstractWebsocketAudioClient extends AbstractAudioClient {

    private volatile WebSocketSession session;
    private WebSocketClient webSocketClient;

    private URI url;

    private Map<String, String> httpHeaders;

    private final ScheduledExecutorService heartbeatExecutor =
            Executors.newSingleThreadScheduledExecutor();

    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                sendHeartbeat();
            } catch (Exception e) {
                log.error("发送心跳失败", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        heartbeatExecutor.shutdown();
    }

    private void sendHeartbeat() {
        log.debug("websocket-client【{}】: 发送心跳", getDescInfo());
        sendData(new PingMessage());
    }

    protected void initWebSocketClient(URI uri, Map<String, String> httpHeaders) {
        webSocketClient = new StandardWebSocketClient();
        url = uri;
        this.httpHeaders = httpHeaders;
    }

    @Override
    public void connect() throws Exception {
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
            webSocketHttpHeaders.add(entry.getKey(), entry.getValue());
        }
        session = webSocketClient.execute(
                new KWebSocketHandler(),
                webSocketHttpHeaders,
                url
        ).get();
        startHeartbeat();
    }

    @Override
    public void disConnect() throws Exception {
        session.close();
        stopHeartbeat();
    }

    @Override
    public void send(ByteBuffer bytes) {
        log.debug("websocket-client【{}】: 发送二进制消息，length：{}", getDescInfo(), bytes.remaining());
        sendData(new BinaryMessage(bytes));
    }

    @Override
    public void send(String text) {
        log.info("websocket-client【{}】: 发送消息: {}", getDescInfo(), text);
        sendData(new TextMessage(text));
    }

    protected void sendData(WebSocketMessage<?> message) {
        if (AudioClientStatus.ERROR.equals(status) || AudioClientStatus.CLOSED.equals(status)) {
            log.error("websocket-client【{}】: 状态异常，不可发送消息，请检查，status={}",
                    getDescInfo(), status);
            throw new RuntimeException("websocket-client【" + getDescInfo() + "】: 状态异常，不可发送消息，请检查，status=" + status);
        }
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(message);
            } else {
                log.error("websocket-client【{}】: session已关闭", getDescInfo());
            }
        } catch (IOException e) {
            log.error("websocket-client【{}】: 发送消息失败", getDescInfo(), e);
            status = AudioClientStatus.ERROR;
            close();
        }
    }

    public abstract void onReceive(String message);

    public class KWebSocketHandler extends AbstractWebSocketHandler {

        private final StringBuilder partialMessage = new StringBuilder();

        @Override
        public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
            log.info("websocket-client: 建立websocket连接");
        }

        @Override
        protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
            if (message.isLast()) {
                partialMessage.append(message.getPayload());
                onReceive(partialMessage.toString());
                partialMessage.setLength(0);
            } else {
                partialMessage.append(message.getPayload());
            }
        }

        @Override
        public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) throws Exception {
            log.info("websocket-client【{}】: websocket连接关闭，status={}",
                    getDescInfo(), closeStatus);
            startEvent.tryEmitValue(null);
            stopEvent.tryEmitValue(null);
            status = AudioClientStatus.CLOSED;
            if (errorCallback != null) {
                closeCallback.accept(new CloseData(closeStatus.getCode(), closeStatus.getReason(), true));
            }
        }

        @Override
        public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {
            log.error("websocket-client【{}】: websocket异常: ", getDescInfo(), exception);
            startEvent.tryEmitError(exception);
            stopEvent.tryEmitError(exception);
            status = AudioClientStatus.ERROR;
            if (errorCallback != null) {
                errorCallback.accept(exception);
            }
        }

        @Override
        protected void handlePongMessage(@NotNull WebSocketSession session, @NotNull PongMessage message) throws Exception {
            log.debug("websocket-client【{}】: 收到心跳", getDescInfo());
        }

        @Override
        public boolean supportsPartialMessages() {
            return true;
        }
    }
}
