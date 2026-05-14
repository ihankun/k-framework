package io.hankun.framework.ai.agent.audio;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @description:
 * @className: KAudioData
 * @createAt: 2025/7/23 09:42
 * @author: hankun
 */
public record KAudioData(long timestamp, Integer index, String audio, String text,
                         Map<String, Object> meta, String type) {

    public static KAudioData ofAudio(Integer index, String audio) {
        return new KAudioData(System.currentTimeMillis(), index, audio, null, Map.of(), "audio");
    }

    public static KAudioData ofText(Integer index, String text) {
        return new KAudioData(System.currentTimeMillis(), index, null, text, Map.of(), "text");
    }

    public static KAudioData ofStart(Integer index, String text) {
        return ofStart(index, text, false);
    }

    public static KAudioData ofStart(Integer index, String text, boolean cached) {
        return new KAudioData(System.currentTimeMillis(), index, null, text, Map.of("cached", cached), "start");
    }

    public static KAudioData ofEnd(Integer index, String audio) {
        return new KAudioData(System.currentTimeMillis(), index, null, audio, Map.of(), "end");
    }

    public static KAudioData ofMeta(Map<String, Object> meta) {
        return new KAudioData(System.currentTimeMillis(), null, null, null, meta, "meta");
    }


    @NotNull
    @Override
    public String toString() {
        int audioLength = audio == null ? 0 : audio.length();
        return "AudioData{" +
                "timestamp=" + timestamp +
                ", index=" + index +
                ", audioLength='" + audioLength + '\'' +
                ", text='" + text + '\'' +
                ", meta=" + meta +
                ", type='" + type + '\'' +
                '}';
    }
}
