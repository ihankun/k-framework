package io.hankun.framework.ai.agent.audio.asr.entity;

import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.client.KAsrClient;
import io.hankun.framework.ai.agent.audio.asr.config.AsrConfig;
import io.hankun.framework.ai.agent.audio.asr.impl.AliAsrImpl;
import io.hankun.framework.ai.agent.audio.asr.impl.KAsrImpl;
import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.core.context.user.LoginUserContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @className: AsrType
 * @createAt: 2025/12/16 14:57
 * @author: hankun
 */
@Getter
@AllArgsConstructor
public enum AsrType {

    K("k") {
        @Override
        public KAsrApi buildAsrApi(KAudioConfig kAsrConfig, String sessionId, String scene) {
            AsrConfig asrConfig = kAsrConfig.getAsr().getAsr();
            return new KAsrImpl(sessionId, false, new KAsrClient(asrConfig,
                    LoginUserContext.get(), scene));
        }
    },

    ALI("ali") {
        @Override
        public KAsrApi buildAsrApi(KAudioConfig msunAsrConfig, String sessionId, String scene) {
            return new AliAsrImpl(msunAsrConfig);
        }
    },

    ;
    private final String code;


    public abstract KAsrApi buildAsrApi(KAudioConfig msunAsrConfig, String sessionId, String scene);


    public static AsrType getByCode(String code) {
        for (AsrType value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
