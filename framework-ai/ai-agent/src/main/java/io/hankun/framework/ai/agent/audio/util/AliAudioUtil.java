package io.hankun.framework.ai.agent.audio.util;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * @description:
 * @className: AliAudioUtil
 * @createAt: 2025/11/25 14:32
 * @author: hankun
 */
@Slf4j
public class AliAudioUtil {

    public static final SpeechSynthesisAudioFormat DEFAULT_FORMAT = SpeechSynthesisAudioFormat.WAV_8000HZ_MONO_16BIT;

    public static SpeechSynthesisAudioFormat getAudioFormat(TtsConfig ttsConfig) {
        String format = ttsConfig.format();
        int sampleRate = ttsConfig.sampleRate();
        if (ObjectUtils.isEmpty(format)) {
            log.info("音频格式设置，未指定音频格式，使用默认格式wav");
            return DEFAULT_FORMAT;
        }
        for (SpeechSynthesisAudioFormat audioFormat : SpeechSynthesisAudioFormat.values()) {
            if (audioFormat.getFormat().equals(format) && audioFormat.getSampleRate() == sampleRate) {
                log.info("音频格式设置，使用指定格式: {}", audioFormat);
                return audioFormat;
            }
        }
        log.info("音频格式设置，音频格式错误，使用默认格式wav");
        return DEFAULT_FORMAT;
    }

    public static SpeechSynthesizer createAudioClient(TtsConfig ttsConfig, ResultCallback<SpeechSynthesisResult> callback) {
        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        .apiKey(ttsConfig.apiKey())
                        .model("cosyvoice-v2") // 模型
                        .voice(ttsConfig.voice()) // 音色
                        .format(getAudioFormat(ttsConfig))
                        .volume(75)
                        .build();
        return new SpeechSynthesizer(param, callback, ttsConfig.url());
    }
}
