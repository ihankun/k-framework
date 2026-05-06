package io.hankun.framework.ai.agent.audio.tts.client;

import io.hankun.framework.ai.agent.audio.client.AudioClient;
import io.hankun.framework.ai.agent.audio.config.AudioInfo;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import lombok.Getter;

/**
 * @description:
 * @className: AudioTtsClientHolder
 * @createAt: 2025/12/17 09:23
 * @author: hankun
 */
public class AudioTtsClientHolder {

    @Getter
    private final AudioClient audioClient;

    @Getter
    private final AudioInfo audioInfo;

    public AudioTtsClientHolder(AudioClient audioClient, TtsConfig ttsConfig) {
        this.audioClient = audioClient;
        this.audioInfo = AudioInfo.of(audioClient.getCode(), ttsConfig.voice(), ttsConfig.format(), ttsConfig.sampleRate());
    }

}
