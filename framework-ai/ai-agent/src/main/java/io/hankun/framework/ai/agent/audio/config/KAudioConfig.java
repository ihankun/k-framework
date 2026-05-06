package io.hankun.framework.ai.agent.audio.config;

import io.hankun.framework.ai.agent.audio.asr.config.AliAsrConfig;
import io.hankun.framework.ai.agent.audio.asr.config.AsrConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @className: MsunAudioConfig
 * @createAt: 2025/7/23 10:00
 * @author: hankun
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "msun.audio")
public class KAudioConfig {

    private String type = TtsType.K.getCode();

    @NestedConfigurationProperty
    private KConfig k;

    @NestedConfigurationProperty
    private AliConfig ali;

    @NestedConfigurationProperty
    private QwenConfig qwen;

    private Boolean saveAudio = true;

    private Boolean enableCache = true;


    @NestedConfigurationProperty
    private Asr asr;

    @Data
    public static class Asr {
        private String type;
        @NestedConfigurationProperty
        private AsrConfig asr;
        @NestedConfigurationProperty
        private AliAsrConfig ali;
    }


    @Data
    public static class AliConfig {
        private String apiKey;
        private String voice;
        private Boolean disableRedistrict = true;
        private String format = "";
        private int sampleRate = 8000;
        private String url = "wss://dashscope.aliyuncs.com/api-ws/v1/inference";

        public TtsConfig toTtsConfig() {
            return new TtsConfig(url, apiKey, voice, format, sampleRate);
        }
    }

    @Data
    public static class QwenConfig {
        private String apiKey;
        private String voice;
        private String url = "";
        private String format = "pcm";
        private int sampleRate = 24000;

        public TtsConfig toTtsConfig() {
            return new TtsConfig(url, apiKey, voice, format, sampleRate);
        }
    }

    @Data
    public static class KConfig {
        private String url = "";
        private String voice = "";
        private String format = "pcm";
        private int sampleRate = 24000;


        public TtsConfig toTtsConfig() {
            return new TtsConfig(url, "", voice, format, sampleRate);
        }
    }
}
