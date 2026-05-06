package io.hankun.framework.ai.agent.audio.client;

import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.entity.CloseData;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * @description:
 * @className: AudioClient
 * @createAt: 2025/12/17 09:11
 * @author: hankun
 */
public interface AudioClient {

    String getCode();

    String getType();

    default String getDescInfo() {
        return getType() + ":" + getCode();
    }

    boolean stream();

    boolean supportRepeatUse();

    void init();

    void close();

    void start();

    void stop();

    void finishSend();

    default void sendData(String data) {
        send(data);
    }

    void send(String text);

    default void sendData(ByteBuffer bytes) {
        send(bytes);
    }

    void send(ByteBuffer bytes);

    AudioClientStatus getStatus();

    Mono<Void> getStartEvent();

    Mono<Void> getStopEvent();

    void setReceiveCallback(Consumer<AudioResult> callback);

    void setErrorCallback(Consumer<Throwable> callback);

    void setCloseCallback(Consumer<CloseData> callback);

    default void waitStop() {
        getStopEvent().toFuture().join();
    }

    default void waitStart() {
        getStartEvent().toFuture().join();
    }
}
