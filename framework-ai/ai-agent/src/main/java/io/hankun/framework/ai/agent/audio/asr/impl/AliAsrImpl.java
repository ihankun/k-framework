package io.hankun.framework.ai.agent.audio.asr.impl;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.alibaba.dashscope.audio.asr.recognition.timestamp.Word;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.config.AliAsrConfig;
import io.hankun.framework.ai.agent.audio.asr.entity.AsrType;
import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description:
 * @className: AliAsrImpl
 * @createAt: 2025/12/18 11:21
 * @author: hankun
 */
@Slf4j
public class AliAsrImpl implements KAsrApi {

    private final KAudioConfig kAudioConfig;

    public AliAsrImpl(KAudioConfig kAudioConfig) {
        this.kAudioConfig = kAudioConfig;
    }

    @Override
    public String getCode() {
        return AsrType.ALI.getCode();
    }

    @Override
    public Flux<KAsrResult> process(Flux<ByteBuffer> audioFlux) {
        AliAsrConfig asrConfig = kAudioConfig.getAsr().getAli();
        Constants.baseWebsocketApiUrl = asrConfig.getUrl();
        RecognitionParam param = RecognitionParam.builder()
                .model(asrConfig.getModel())
                .apiKey(asrConfig.getApiKey())
                .format(asrConfig.getFormat())
                .parameter("semantic_punctuation_enabled", !asrConfig.getVadSplit())
                .parameter("max_sentence_silence", asrConfig.getMaxSilence())
                .parameter("heartbeat", true)
                .sampleRate(asrConfig.getSampleRate())
                .build();
        Recognition recognizer = new Recognition();
        Flux<ByteBuffer> bufferFlux;
        if (asrConfig.getFormat().equals("wav") || asrConfig.getFormat().equals("pcm")) {
            //如果20秒内没有数据，则发送60ms空白音频
            bufferFlux = audioFlux;
//            AtomicBoolean hasData = new AtomicBoolean(true);
//            bufferFlux = audioFlux
//                    .doOnNext(data -> hasData.set(true))
//                    .timeout(Duration.ofSeconds(20))
//                    .onErrorResume(TimeoutException.class, e -> {
//
//                        byte[] audio = getDefaultAudio();
//                        log.warn("20秒内没有数据，发送60ms空白音频，length={}", audio.length);
//                        hasData.set(false);
//                        return Flux.just(audio);
//                    }).repeatWhen(repeat -> repeat.takeWhile(tick -> !hasData.get()))
//                    .map(ByteBuffer::wrap);
        } else {
            bufferFlux = audioFlux;
        }
        try {
            Flowable<RecognitionResult> recognitionResultFlowable =
                    recognizer.streamCall(param, RxJava2Adapter.fluxToFlowable(bufferFlux));
            AtomicInteger checkedIndex = new AtomicInteger(-1);
            Map<Long, String> unCheckMessage = new ConcurrentHashMap<>();
            AtomicReference<Long> checkTime = new AtomicReference<>(0L);
            return RxJava2Adapter.flowableToFlux(recognitionResultFlowable)
                    .concatMap(recognitionResult -> {
                        boolean sentenceEnd = recognitionResult.isSentenceEnd();
                        log.info("识别结果：end:{},result={}", sentenceEnd,
                                recognitionResult.getSentence().getText());
                        String result;
                        if (sentenceEnd) {
                            result = getLast(recognitionResult.getSentence().getWords(), checkedIndex);
                            checkedIndex.set(-1);
                            unCheckMessage.clear();
                        } else {
                            result = getRange(recognitionResult.getSentence().getWords(), checkedIndex, asrConfig.getUnCheckCount());
                            //result = getSame(recognitionResult.getSentence().getWords(), checkedIndex, unCheckMessage, checkTime);
                        }
                        KAsrResult all = KAsrResult.of(recognitionResult.getSentence().getText(), NOT_SURE, sentenceEnd);
                        if (StringUtils.hasText(result)) {
                            return Flux.just(all, KAsrResult.of(result, DELTA, sentenceEnd));
                        } else {
                            return Flux.just(all);
                        }
                    }).doFinally(status -> {
                        log.info("识别结束，断开连接");
                        recognizer.getDuplexApi().close(1000, "bye");
                    });
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getDefaultAudio() {
        return generateSilenceData(60);
    }

    public byte[] generateSilenceData(int milliseconds) {
        AliAsrConfig asrConfig = kAudioConfig.getAsr().getAli();
        float sampleRate = asrConfig.getSampleRate();
        int bytesPerSample = 2;
        int samples = (int) ((sampleRate * milliseconds) / 1000);
        // 静音数据初始化为0
        return new byte[samples * bytesPerSample];
    }

    public String getLast(List<Word> words, AtomicInteger checkedIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = checkedIndex.get() + 1; i < words.size(); i++) {
            Word word = words.get(i);
            result.append(word.getText()).append(word.getPunctuation());
        }
        checkedIndex.set(words.size() - 1);
        return result.toString();
    }

    public String getSame(List<Word> words, AtomicInteger checkIndex,
                          Map<Long, String> unCheckMessage, AtomicReference<Long> checkTime) {
        Map<Long, String> results = new HashMap<>();
        StringBuilder buffer = new StringBuilder();
        StringBuilder resultBuffer = new StringBuilder();
        long currentTime = System.currentTimeMillis();
        //连续两次出现的句子认为是可靠的
        for (int i = checkIndex.get() + 1; i < words.size(); i++) {
            Word word = words.get(i);
            //添加到缓存中
            buffer.append(word.getText());
            //出现标点
            if (StringUtils.hasText(word.getPunctuation())) {
                //添加标点到结果中
                buffer.append(word.getPunctuation());
                String resultData = buffer.toString();
                buffer.delete(0, buffer.length());
                //在上一次处理的结果中存在，则加入结果中
                if (Objects.equals(unCheckMessage.get(word.getEndTime()), resultData)
                        && currentTime - checkTime.get() > 1000) {
                    resultBuffer.append(resultData);
                    //更新确认长度
                    checkIndex.set(i);
                } else {
                    unCheckMessage.clear();
                    //不在上一次的结果中，则加入未确认缓存中
                    results.put(word.getEndTime(), resultData);
                }
            }
        }
        unCheckMessage.clear();
        unCheckMessage.putAll(results);
        if (!resultBuffer.isEmpty()) {
            checkTime.set(currentTime);
        }
        return resultBuffer.toString();
    }


    public String getRange(List<Word> words, AtomicInteger index, int endOffset) {
        StringBuilder result = new StringBuilder();
        int endIndex = -1;
        int count = 0;
        for (int i = words.size() - 1; i >= 0; i--) {
            Word word = words.get(i);
            if (StringUtils.hasText(word.getPunctuation())) {
                count++;
            }
            if (endOffset <= count) {
                endIndex = i;
                break;
            }
        }
        for (int i = index.get() + 1; i <= endIndex; i++) {
            Word word = words.get(i);
            result.append(word.getText()).append(word.getPunctuation());
        }
        index.set(endIndex);
        return result.toString();
    }

    @Override
    public void sendData(String text) {

    }

    @Override
    public void close() {

    }
}
