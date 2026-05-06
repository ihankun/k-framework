package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.audio.KAudioData;
import io.hankun.framework.ai.agent.audio.KMessageAudioService;
import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.call.entity.AgentCallResponse;
import io.hankun.framework.ai.agent.config.TaskExecConfig;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import io.hankun.framework.ai.mcp.context.KToolContext;
import io.hankun.framework.ai.mcp.context.KToolContextHolder;
import io.hankun.framework.commons.context.KContext;
import io.hankun.framework.commons.context.KContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @description:
 * @className: KCallExecutor
 * @createAt: 2025/12/23 10:05
 * @author: hankun
 */
@Slf4j
public class KCallExecutor implements Closeable {

    @Getter
    private final String sessionId;

    @Getter
    private final KContext kContext;

    @Getter
    private final TaskExecConfig taskExecConfig;

    @Getter
    private final FluxAgentCallService fluxAgentCallService;

    @Getter
    private final KMessageAudioService kMessageAudioService;

    private volatile Sinks.Many<ByteBuffer> audioSink = Sinks.many().unicast().onBackpressureBuffer();

    @Setter
    private volatile Consumer<Throwable> errorHandler;

    @Setter
    private volatile Consumer<KAudioData> resultHandler;

    @Setter
    private volatile Runnable completeHandler;

    @Setter
    private volatile boolean withTts;

    private volatile Disposable subscribe;

    private final AtomicBoolean asrWorking = new AtomicBoolean(false);

    private final ReentrantLock asrLock = new ReentrantLock();

    @Getter
    private final ICallExecutor callExecutor;

    public KCallExecutor(FluxAgentCallService fluxAgentCallService,
                         KMessageAudioService kMessageAudioService,
                         String sessionId,
                         KContext kContext,
                         TaskExecConfig taskExecConfig,
                         ICallExecutor callExecutor) {
        this.sessionId = sessionId;
        this.kContext = kContext;
        this.taskExecConfig = taskExecConfig;
        this.fluxAgentCallService = fluxAgentCallService;
        this.kMessageAudioService = kMessageAudioService;
        this.callExecutor = callExecutor;
    }

    public String ttsMark() {
        return callExecutor.ttsMark();
    }

    public String asrMark() {
        return callExecutor.asrMark();
    }

    public KToolContext getToolContext() {
        return kContext.getData(KToolContextHolder.K_CONTEXT);
    }

    public void refresh() {
        KContext old = KContextHolder.get();
        try {
            KContextHolder.set(kContext);
            if (subscribe != null) {
                log.info("刷新执行器，结束之前的流");
                subscribe.dispose();
            }
            Flux<NodeResultData> agentCallResponseFlux = callExecutor.init(this);
            agentCallResponseFlux = agentCallResponseFlux
                    .doOnError(this::onError);
            if (withTts) {
                agentCallResponseFlux = agentCallResponseFlux.doOnNext(data -> {
                    log.info("当前输出: {}", data);
                });
                Flux<KAudioData> tts = kMessageAudioService.tts(sessionId, ttsMark(),
                        agentCallResponseFlux.map(NodeResultData::toData));
                subscribe = tts
                        .doOnComplete(() -> {
                            if (completeHandler != null) {
                                completeHandler.run();
                            }
                        })
                        .subscribe(result -> {
                            log.info("当前音频输出: {}", result);
                            if (resultHandler != null) {
                                resultHandler.accept(result);
                            } else {
                                log.info("未设置结果处理器，当前音频输出: {}", result);
                            }
                        });
            } else {
                subscribe = agentCallResponseFlux
                        .doOnComplete(() -> {
                            if (completeHandler != null) {
                                completeHandler.run();
                            }
                        })
                        .subscribe(result -> {
                            if (resultHandler != null) {
                                if (StringUtils.hasText(result.data())) {
                                    resultHandler.accept(KAudioData.ofText(0, result.data()));
                                }
                                if (!CollectionUtils.isEmpty(result.meta())) {
                                    resultHandler.accept(KAudioData.ofMeta(result.meta()));
                                }
                            } else {
                                log.info("未设置结果处理器，当前输出: {}", result);
                            }
                        });
            }
        } finally {
            KContextHolder.set(old);
        }
    }

    public Flux<AgentCallResponse> fluxCallAgent(String agentCode, Flux<AgentCallParams> paramsFlux) {
        return fluxCallAgent(agentCode, sessionId, paramsFlux);
    }

    public Flux<AgentCallResponse> fluxCallAgent(String agentCode, String sessionId, Flux<AgentCallParams> paramsFlux) {
        return fluxAgentCallService.call(agentCode, sessionId, getToolContext(), paramsFlux, taskExecConfig, null);
    }

    public QueueAgentExecutor createQueueExecutor(String agentCode) {
        return createQueueExecutor(agentCode, sessionId);
    }


    public QueueAgentExecutor createQueueExecutor(String agentCode, String sessionId) {
        return fluxAgentCallService.createQueueExecutor(agentCode, sessionId, getToolContext(), taskExecConfig);
    }

    public void initData() {
        refresh();
    }

    private void initAsr() {
        KContext old = KContextHolder.get();
        try {
            asrLock.lock();
            if (!asrWorking.compareAndSet(false, true)) {
                return;
            }
            KContextHolder.set(kContext);
            audioSink = Sinks.many().unicast().onBackpressureBuffer();
            Flux<KAsrResult> makeRounds = kMessageAudioService.asr(sessionId, asrMark(), audioSink.asFlux());
            Flux<KAsrResult> textFlux = makeRounds.filter(msunAsrResult -> msunAsrResult.type().equals(KAsrApi.DELTA));
            textFlux.subscribe(result -> {
                try {
                    KContextHolder.set(kContext);
                    callExecutor.input(result.text(), result.sentenceEnd());
                } finally {
                    KContextHolder.clear();
                }
            }, throwable -> {
                log.error("asr流程异常结束", throwable);
                audioSink.tryEmitComplete();
                asrWorking.set(false);
            }, () -> {
                log.info("asr流程结束");
                audioSink.tryEmitComplete();
                asrWorking.set(false);
            });
        } catch (Exception e) {
            log.error("初始化ASR过程中发生异常", e);
            asrWorking.set(false);
            throw e;
        } finally {
            KContextHolder.set(old);
            if (asrLock.isHeldByCurrentThread()) {
                asrLock.unlock();
            }
        }
    }

    public void onError(Throwable throwable) {
        if (errorHandler != null) {
            errorHandler.accept(throwable);
        } else {
            log.error("未设置异常处理器，执行异常: e=", throwable);
        }
    }


    public void receiveData(String dataString) {
        KContext old = KContextHolder.get();
        try {
            KContextHolder.set(kContext);
            callExecutor.receiveData(dataString, this);
        } finally {
            KContextHolder.set(old);
        }
    }

    public void inputBytes(ByteBuffer message) {
        KContext old = KContextHolder.get();
        try {
            KContextHolder.set(kContext);
            callExecutor.inputBytes(message, this);
        } finally {
            KContextHolder.set(old);
        }
    }

    public void inputAudio(ByteBuffer audioData) {
        if (!asrWorking.get()) {
            log.info("asr未开始，开始asr");
            initAsr();
        }
        audioSink.tryEmitNext(audioData);
    }

    public void finish() {
        KContext old = KContextHolder.get();
        try {
            KContextHolder.set(kContext);
            callExecutor.finish();
        } finally {
            KContextHolder.set(old);
        }
        audioSink.tryEmitComplete();
    }

    @Override
    public void close() throws IOException {
        finish();
    }
}
