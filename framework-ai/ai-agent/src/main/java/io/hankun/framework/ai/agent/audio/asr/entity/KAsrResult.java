package io.hankun.framework.ai.agent.audio.asr.entity;

/**
 * @description:
 * @className: KAsrResult
 * @createAt: 2025/12/16 14:35
 * @author: hankun
 */
public record KAsrResult(long timestamp, String text, String type, boolean sentenceEnd) {

    public static KAsrResult of(String text, String type, boolean sentenceEnd) {
        return new KAsrResult(System.currentTimeMillis(), text, type, sentenceEnd);
    }
}
