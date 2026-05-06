package io.hankun.framework.ai.agent.audio.tts.entity;

/**
 * @description:
 * @className: TtsConfig
 * @createAt: 2025/12/17 09:43
 * @author: hankun
 */
public record TtsConfig(String url,
                        String apiKey,
                        String voice,
                        String format,
                        int sampleRate) {

}
