package io.hankun.framework.ai.agent.audio.entity;

/**
 * @description:
 * @className: AudioResult
 * @createAt: 2025/7/24 10:57
 * @author: hankun
 */
public record AudioResult(boolean finish, String text, String type, boolean sentenceEnd) {

    public static AudioResult finish(String text) {
        return new AudioResult(true, text, "", true);
    }

    public static AudioResult of(boolean finish, String text, String type, boolean sentenceEnd) {
        return new AudioResult(finish, text, type, sentenceEnd);
    }

    public static AudioResult ofAudio(String text) {
        return new AudioResult(false, text, "", false);
    }
}
