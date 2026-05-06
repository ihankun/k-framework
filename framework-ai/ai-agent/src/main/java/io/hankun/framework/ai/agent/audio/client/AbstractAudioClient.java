package io.hankun.framework.ai.agent.audio.client;

import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.entity.CloseData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @description:
 * @className: AbstractAudioClient
 * @createAt: 2025/12/16 18:03
 * @author: hankun
 */
@Slf4j
public abstract class AbstractAudioClient implements AudioClient {

    protected Sinks.One<Void> startEvent = Sinks.one();

    protected Sinks.One<Void> stopEvent = Sinks.one();

    @Setter
    protected Consumer<AudioResult> receiveCallback;

    @Setter
    protected Consumer<Throwable> errorCallback;

    @Setter
    protected Consumer<CloseData> closeCallback;

    @Getter
    protected volatile AudioClientStatus status;

    protected final ReentrantLock lock = new ReentrantLock();

    @Override
    public Mono<Void> getStartEvent() {
        return startEvent.asMono().cache();
    }


    @Override
    public Mono<Void> getStopEvent() {
        return stopEvent.asMono().cache();
    }

    public void resetEvent() {
        startEvent = Sinks.one();
        stopEvent = Sinks.one();
    }

    @Override
    public void init() {
        lock.lock();
        try {
            log.info("audio-client【{}】: 开始建立连接", getDescInfo());
            connect();
            this.status = AudioClientStatus.READY;
        } catch (Exception e) {
            log.error("audio-client【{}】: 建立连接失败: ", getDescInfo(), e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public abstract void connect() throws Exception;

    @Override
    public void close() {
        lock.lock();
        try {
            log.info("audio-client【{}】: 尝试关闭连接", getDescInfo());
            disConnect();
            this.status = AudioClientStatus.CLOSED;
        } catch (Exception e) {
            log.error("audio-client【{}】: 关闭连接失败: ", getDescInfo(), e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void setStart() {
        this.status = AudioClientStatus.RUNNING;
        startEvent.tryEmitValue(null);
    }

    public void setStop() {
        this.status = AudioClientStatus.READY;
        stopEvent.tryEmitValue(null);
    }


    public abstract void disConnect() throws Exception;


    @Override
    public void start() {
        lock.lock();
        try {
            resetEvent();
            if (status == AudioClientStatus.READY) {
                this.status = AudioClientStatus.STARTING;
                log.info("audio-client【{}】: 开始准备会话", getDescInfo());
                onStart();
                return;
            } else if (status == AudioClientStatus.RUNNING || status == AudioClientStatus.STARTING) {
                log.warn("audio-client【{}】: 会话已开始，请勿重复开始", getDescInfo());
                return;
            }
            log.error("audio-client【{}】: 会话状态错误，无法开始，status={}", getDescInfo(), status);
        } finally {
            lock.unlock();
        }
    }

    public abstract void onStart();

    @Override
    public void stop() {
        lock.lock();
        try {
            if (status == AudioClientStatus.RUNNING) {
                this.status = AudioClientStatus.STOPPING;
                log.info("audio-client【{}】: 尝试结束会话", getDescInfo());
                onStop();
                return;
            } else if (status == AudioClientStatus.READY || status == AudioClientStatus.STOPPING) {
                log.warn("audio-client【{}】: 会话已结束，请勿重复结束", getDescInfo());
                return;
            }
            log.error("audio-client【{}】: 会话状态错误，无法结束，status={}", getDescInfo(), status);
        } finally {
            lock.unlock();
        }
    }

    public abstract void onStop();
}
