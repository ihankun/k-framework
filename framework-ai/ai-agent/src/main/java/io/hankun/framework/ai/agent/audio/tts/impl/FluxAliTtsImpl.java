package io.hankun.framework.ai.agent.audio.tts.impl;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.hankun.framework.ai.agent.audio.KAudioData;
import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.ai.agent.audio.tts.KTtsApi;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.util.AliAudioUtil;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Base64;
import java.util.function.Function;

/**
 * @description:
 * @className: FluxAliTtsImpl
 * @createAt: 2025/7/30 14:52
 * @author: hankun
 */
@Slf4j
public class FluxAliTtsImpl implements KTtsApi {

    private final KAudioConfig kAudioConfig;

    private final TtsConfig ttsConfig;

    private final String conversionId;

    public FluxAliTtsImpl(KAudioConfig kAudioConfig, String conversionId) {
        this.kAudioConfig = kAudioConfig;
        this.ttsConfig = kAudioConfig.getAli().toTtsConfig();
        this.conversionId = conversionId;
    }

    @Override
    public String getCode() {
        return "ali";
    }

    @Override
    public Flux<KAudioData> process(Flux<String> textFlux) {
        SpeechSynthesizer synthesizer = AliAudioUtil.createAudioClient(ttsConfig, null);
        textFlux = textFlux.timeout(Duration.ofSeconds(20));
        textFlux = textFlux.onErrorResume(new Function<Throwable, Publisher<? extends String>>() {
            @Override
            public Publisher<? extends String> apply(Throwable throwable) {
                return Flux.just("很抱歉，出现了一些问题，请重新提问");
            }
        }).cache();
        Flux<KAudioData> data = textFlux.map(text -> KAudioData.ofText(0, text));
        try {
            Flowable<SpeechSynthesisResult> flowable = synthesizer.streamingCallAsFlowable(RxJava2Adapter.fluxToFlowable(textFlux));
            flowable = flowable.doOnError(throwable -> {
                log.error("tts失败，conversationId={},e=", conversionId, throwable);
            }).onErrorResumeNext(Flowable.empty());
            Flux<KAudioData> resultFlux = RxJava2Adapter.flowableToFlux(flowable)
                    .filter(result -> {
                        return result.getAudioFrame() != null;
                    })
                    .map(result -> {
                        return KAudioData.ofAudio(0, Base64.getEncoder().
                                encodeToString(result.getAudioFrame().array()));
                    });
            Flux<KAudioData> dataFlux = Flux.merge(data, resultFlux);
            return Flux.concat(Mono.just(KAudioData.ofStart(0, "")), dataFlux,
                    Mono.just(KAudioData.ofEnd(0, ""))).cache();
        } catch (NoApiKeyException e) {
            log.error("tts失败，conversationId={},e=", conversionId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendData(String text) {
        throw new RuntimeException("不支持该功能");
    }

    @Override
    public void close() {

    }
}
