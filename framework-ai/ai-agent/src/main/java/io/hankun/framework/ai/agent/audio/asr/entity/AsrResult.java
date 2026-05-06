package io.hankun.framework.ai.agent.audio.asr.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @description:
 * @className: AsrResult
 * @createAt: 2025/12/17 11:44
 * @author: hankun
 */
@Data
public class AsrResult {

    @JSONField(name = "mode")
    private String mode;

    @JSONField(name = "isFinal")
    private Boolean isFinal = false;

    @JSONField(name = "text")
    private String text;

    @JSONField(name = "wav_name")
    private String wavName;
}
