package io.hankun.framework.ai.agent.audio.asr.config;

import lombok.Data;

/**
 * @description:
 * @className: AsrConfig
 * @createAt: 2025/12/17 11:30
 * @author: hankun
 */
@Data
public class AsrConfig {
    /**
     * ASR服务URL
     */
    private String url;

    /**
     * ASR服务认证token
     */
    private String token;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 使用的模型
     */
    private String model;

    private Boolean clearNoise;


    private Boolean splitPeople = false;
}
