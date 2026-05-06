package io.hankun.framework.ai.agent.audio.asr.config;

import lombok.Data;

/**
 * @description:
 * @className: AliAsrConfig
 * @createAt: 2025/12/19 09:00
 * @author: hankun
 */
@Data
public class AliAsrConfig {
    private String apiKey;
    private String url = "wss://dashscope.aliyuncs.com/api-ws/v1/inference/";
    private String model = "fun-asr-realtime";
    private Integer sampleRate = 16000;
    private String format = "pcm";
    private Boolean vadSplit = true;
    private Integer maxSilence = 800;
    private Integer unCheckCount = 2;
}
