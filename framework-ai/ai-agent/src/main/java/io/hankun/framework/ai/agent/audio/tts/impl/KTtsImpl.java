package io.hankun.framework.ai.agent.audio.tts.impl;


import io.hankun.framework.ai.agent.audio.KAudioData;
import io.hankun.framework.ai.agent.audio.client.AbstractClientApi;
import io.hankun.framework.ai.agent.audio.client.AudioClient;
import io.hankun.framework.ai.agent.audio.config.AudioInfo;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.tts.KTtsApi;
import io.hankun.framework.ai.agent.audio.tts.cache.AudioCacheService;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.common.util.FluxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @className: KTtsImpl
 * @createAt: 2025/7/24 10:42
 * @author: hankun
 */
@Slf4j
public class KTtsImpl extends AbstractClientApi<String, KAudioData> implements KTtsApi {

    private final AudioInfo audioInfo;

    private final AtomicInteger streamCounter = new AtomicInteger(0);

    private final AtomicInteger finishCounter = new AtomicInteger(0);

    private final Map<Integer, String> textHolders = new ConcurrentHashMap<>();

    private final Map<Integer, List<String>> cachedAudio = new ConcurrentHashMap<>();

    private final Map<Integer, List<String>> newAudio = new ConcurrentHashMap<>();

    private volatile String currentText;

    private final boolean stream;

    private final AudioCacheService audioCacheService;

    public KTtsImpl(AudioClient audioClient, TtsConfig ttsConfig, String conversationId,
                    boolean repeatUse, AudioCacheService audioCacheService) {
        super(conversationId, repeatUse, audioClient);
        this.audioInfo = AudioInfo.of(audioClient.getCode(), ttsConfig);
        this.audioCacheService = audioCacheService;
        if (!audioClient.supportRepeatUse() && repeatUse) {
            throw new RuntimeException("AudioClient does not support repeat use, client=" +
                    audioClient.getClass().getName());
        }
        this.stream = audioClient.stream();
    }

    @Override
    protected void start() {
        super.start();
        streamCounter.set(0);
        finishCounter.set(0);
        textHolders.clear();
        currentText = null;
    }

    @Override
    protected void finishSend() {
        super.finishSend();
        if (textHolders.size() == cachedAudio.size()) {
            stop();
        }
    }

    @Override
    public void exec(Flux<String> flux) {
        flux.subscribe(audioClient::sendData);
    }

    @Override
    public Flux<String> filter(Flux<String> flux) {
        if (stream) {
            //流式语音，发生一个空白文本的start，然后，每收到一个text，发送该text
            //音频流结束后，在发送end
            resultSink.tryEmitNext(KAudioData.ofStart(streamCounter.get(), ""));
            flux = flux.concatMap(text -> {
                resultSink.tryEmitNext(KAudioData.ofText(0, text));
                return Flux.just(text);
            });
        } else {
            //非流式语言，每个文本，发送一个携带完整文本的start
            //记录所有文本的index
            //音频流结束后，发送end，增加index，发送下一条文本的start，连续操作到流结束
            flux = FluxUtil.redistrict(flux, 100, Duration.ofSeconds(5));
            flux = flux.concatMap(text -> {
                if (ObjectUtils.isEmpty(text)) {
                    return Flux.just("");
                }
                List<String> cached = getCache(text);
                int index = streamCounter.getAndIncrement();
                textHolders.put(index, text);
                if (!CollectionUtils.isEmpty(cached)) {
                    if (currentText == null) {
                        resultSink.tryEmitNext(KAudioData.ofStart(index, text, true));
                        for (String audio : cached) {
                            resultSink.tryEmitNext(KAudioData.ofAudio(index, audio));
                        }
                        resultSink.tryEmitNext(KAudioData.ofEnd(index, ""));
                        finishCounter.getAndIncrement();
                    }
                    cachedAudio.put(index, cached);
                    return Flux.just("");
                }
                if (currentText == null) {
                    currentText = text;
                    resultSink.tryEmitNext(KAudioData.ofStart(index, text));
                }
                return Flux.just(text);
            });
        }
        flux = flux.filter(StringUtils::hasText);
        return flux;
    }

    private List<String> getCache(String text) {
        if (audioCacheService == null) {
            return List.of();
        }
        return audioCacheService.getCache(text, audioInfo);
    }

    public void onReceive(AudioResult audioData) {
        if (audioData.finish()) {
            if (stream) {
                //流式，如果输入结束，收到服务端的finish标签，发送end，结束会话
                if (finishSend) {
                    resultSink.tryEmitNext(KAudioData.ofEnd(0, ""));
                    stop();
                }
            } else {
                //非流式，收到服务端的finish标签，发送end，index+1，发送下一条消息
                //如果所有消息都已发送，结束会话
                resultSink.tryEmitNext(KAudioData.ofEnd(finishCounter.get(), audioData.text()));
                int index = finishCounter.incrementAndGet();
                List<String> cached = cachedAudio.get(index);
                //缓存中存在音频，发送缓存的音频
                //缓存中不存在音频，发送start
                while (!CollectionUtils.isEmpty(cached)) {
                    currentText = textHolders.get(index);
                    if (ObjectUtils.isEmpty(currentText)) {
                        break;
                    }
                    resultSink.tryEmitNext(KAudioData.ofStart(index, currentText, true));
                    for (String audio : cached) {
                        resultSink.tryEmitNext(KAudioData.ofAudio(index, audio));
                    }
                    resultSink.tryEmitNext(KAudioData.ofEnd(index, ""));
                    index = finishCounter.incrementAndGet();
                    cached = cachedAudio.get(index);
                }
                currentText = textHolders.get(index);
                if (StringUtils.hasText(currentText)) {
                    resultSink.tryEmitNext(KAudioData.ofStart(index, currentText));
                }
                //检查是否语音是否结束
                checkStop();
            }
        } else {
            resultSink.tryEmitNext(KAudioData.ofAudio(finishCounter.get(), audioData.text()));
            newAudio.computeIfAbsent(finishCounter.get(), k -> new ArrayList<>())
                    .add(audioData.text());
        }
    }

    private void checkStop() {
        if (!finishSend) {
            return;
        }
        if (streamCounter.get() == finishCounter.get()) {
            stop();
        }
    }

    @Override
    protected void stop() {
        super.stop();
        if (audioCacheService != null) {
            log.info("缓存音频，audio={}", newAudio.size());
            for (Map.Entry<Integer, List<String>> entry : newAudio.entrySet()) {
                String text = textHolders.get(entry.getKey());
                if (StringUtils.hasText(text)) {
                    audioCacheService.setCache(text, audioInfo, entry.getValue());
                }
            }
        }
    }
}
