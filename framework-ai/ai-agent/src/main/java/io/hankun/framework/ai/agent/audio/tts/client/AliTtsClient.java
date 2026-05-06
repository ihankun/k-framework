package io.hankun.framework.ai.agent.audio.tts.client;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import io.hankun.framework.ai.agent.audio.client.AbstractAudioClient;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.tts.entity.KAudioFormat;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;
import io.hankun.framework.ai.agent.audio.util.AliAudioUtil;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * @description:
 * @className: AliTtsClient
 * @createAt: 2025/11/25 14:29
 * @author: hankun
 */
@Slf4j
public class AliTtsClient extends AbstractAudioClient {

    private final Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    private final TtsConfig ttsConfig;

    public AliTtsClient(TtsConfig ttsConfig) {
        this.ttsConfig = ttsConfig;
        sink.asFlux().concatMap(data -> {
            try {
                SpeechSynthesizer synthesizer = AliAudioUtil.createAudioClient(ttsConfig, null);
                Flowable<SpeechSynthesisResult> source = synthesizer.callAsFlowable(data);
                source = source.doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        log.error("阿里语音合成异常", throwable);
                    }
                }).onErrorResumeNext(Flowable.empty());
                Flux<SpeechSynthesisResult> flux = RxJava2Adapter.flowableToFlux(source);
                Flux<AudioResult> resultFlux = flux.concatMap(result -> {
                    if (result.getAudioFrame() == null) {
                        return Flux.empty();
                    }
                    return Flux.just(AudioResult.ofAudio(Base64.getEncoder().encodeToString(result.getAudioFrame().array())));
                });
                resultFlux = resultFlux.concatWith(Flux.just(AudioResult.finish("")));
                resultFlux = resultFlux.doOnComplete(() -> {
                    String lastRequestId = synthesizer.getLastRequestId();
                    long firstPackageDelay = synthesizer.getFirstPackageDelay();
                    log.info("firstPackageDelay={},lastRequestId={}", firstPackageDelay, lastRequestId);
                });
                return resultFlux;
            } catch (Throwable e) {
                errorCallback.accept(e);
                throw new RuntimeException(e);
            }
        }).subscribe(audioResult -> {
            receiveCallback.accept(audioResult);
        });
    }

    @Override
    public void finishSend() {
        sink.tryEmitComplete();
    }


    @Override
    public void connect() throws Exception {
    }

    @Override
    public void disConnect() throws Exception {

    }

    @Override
    public void onStart() {
        setStart();
    }

    @Override
    public void onStop() {
        setStop();
    }

    @Override
    public String getCode() {
        return "ali_no_stream";
    }

    @Override
    public String getType() {
        return "tts";
    }

    @Override
    public boolean stream() {
        return false;
    }

    @Override
    public boolean supportRepeatUse() {
        return false;
    }

    @Override
    public void send(String text) {
        sink.tryEmitNext(text);
    }

    @Override
    public void send(ByteBuffer bytes) {

    }

    public String generateSilenceText(int milliseconds) {
        return Base64.getEncoder().encodeToString(generateSilenceData(milliseconds));
    }

    public byte[] generateSilenceData(int milliseconds) {
        // 根据当前音频格式计算静音数据长度
        // 示例：假设16bit, 22050Hz, mono
        KAudioFormat audioFormat = TtsType.ALI_NO_STREAM.buildAudioFormat(ttsConfig);
        float sampleRate = audioFormat.audioFormat().getSampleRate();
        int bytesPerSample = audioFormat.audioFormat().getFrameSize();
        int samples = (int) ((sampleRate * milliseconds) / 1000);
        // 静音数据初始化为0
        return new byte[samples * bytesPerSample];
    }

}
