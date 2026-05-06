package io.hankun.framework.ai.agent.audio.client;

import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.entity.CloseData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @description:
 * @className: AbstractClientApi
 * @createAt: 2025/12/17 11:56
 * @author: hankun
 */
@Slf4j
public abstract class AbstractClientApi<T, R> implements KAudioApi<T, R> {

    private final String sessionId;

    private final boolean repeatUse;

    protected final AudioClient audioClient;

    protected Sinks.Many<R> resultSink = Sinks.many().unicast().onBackpressureBuffer();

    protected volatile boolean finishSend;

    protected AbstractClientApi(String sessionId,
                                boolean repeatUse,
                                AudioClient audioClient) {
        this.sessionId = sessionId;
        this.repeatUse = repeatUse;
        this.audioClient = audioClient;
        audioClient.init();
        audioClient.setReceiveCallback(this::onReceive);
        audioClient.setErrorCallback(this::onError);
        audioClient.setCloseCallback(this::onClose);
    }

    @Override
    public String getCode() {
        return audioClient.getCode();
    }

    public abstract void onReceive(AudioResult kAudioData);


    public void sendData(String data) {
        audioClient.sendData(data);
    }

    @Override
    public Flux<R> process(Flux<T> audioFlux) {
        audioFlux = waitReady(audioFlux);
        audioFlux = filter(audioFlux);
        audioFlux = audioFlux.doOnComplete(this::finishSend);
        exec(audioFlux);
        Flux<R> stream = resultSink.asFlux();
        if (!repeatUse) {
            stream = stream.doFinally(s -> {
                audioClient.close();
            });
        }
        return stream.cache();
    }

    public abstract Flux<T> filter(Flux<T> flux);

    public abstract void exec(Flux<T> flux);

    protected void start() {
        finishSend = false;
        resultSink = Sinks.many().unicast().onBackpressureBuffer();
        audioClient.start();
    }

    protected void finishSend() {
        finishSend = true;
        audioClient.finishSend();
    }

    protected Flux<T> waitReady(Flux<T> flux) {
        AudioClientStatus status = audioClient.getStatus();
        switch (status) {
            case READY -> {
                //初始化会话，等待初始化结束
                start();
                flux = flux.delayUntil(s -> audioClient.getStartEvent());
                return flux;
            }
            case STOPPING -> {
                if (audioClient.supportRepeatUse()) {
                    log.warn("audio-server: 会话结束中，等待结束");
                    //停止中，等待会话停止
                    audioClient.waitStop();
                    return waitReady(flux);
                } else {
                    throw new RuntimeException("audio-server 不处于就绪状态，无法执行操作,status=" + status);
                }
            }
            case null, default ->
                    throw new RuntimeException("audio-server 不处于就绪状态，无法执行操作,status=" + status);
        }
    }

    protected void stop() {
        audioClient.stop();
        resultSink.tryEmitComplete();
    }

    protected void onError(Throwable throwable) {
        log.error("audio-server: websocket异常: ", throwable);
        resultSink.tryEmitError(throwable);
        close();
    }

    protected void onClose(CloseData closeData) {
        log.info("audio-server: websocket连接已关闭: {}", closeData);
        resultSink.tryEmitComplete();
        close();
    }

    public void close() {
        if (audioClient.getStatus() == AudioClientStatus.CLOSED) {
            log.info("连接已断开，请勿重复断开连接，sessionId={}", sessionId);
            return;
        }
        if (audioClient.getStatus() == AudioClientStatus.RUNNING) {
            log.warn("任务未结束，结束任务，sessionId={}", sessionId);
            audioClient.stop();
        }
        if (audioClient.getStatus() == AudioClientStatus.STOPPING) {
            //等待会话停止
            log.warn("任务正在结束中，等待任务结束，sessionId={}", sessionId);
            audioClient.waitStop();
        }
        log.info("关闭连接，sessionId={}", sessionId);
        audioClient.close();
    }
}
