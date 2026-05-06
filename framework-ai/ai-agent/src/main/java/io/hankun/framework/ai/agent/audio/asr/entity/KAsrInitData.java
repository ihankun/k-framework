package io.hankun.framework.ai.agent.audio.asr.entity;

import io.hankun.framework.ai.agent.audio.asr.config.AsrConfig;
import io.hankun.framework.core.context.user.LoginUserInfo;

/**
 * @description:
 * @className: KAsrInitData
 * @createAt: 2025/12/17 11:30
 * @author: hankun
 */
public record KAsrInitData(String sessionId, Parameter parameter) {

    public record Parameter(String ifClearVoice,
                            String ifSplitPeople,
                            String userId,
                            String hospitalId,
                            String orgId,
                            String scene) {

    }


    public static KAsrInitData of(String sessionId, AsrConfig asrConfig, LoginUserInfo loginUserInfo, String scene) {
        String ifClearVoice = asrConfig.getClearNoise() != null && asrConfig.getClearNoise() ? "1" : "0";
        String ifSplitPeople = asrConfig.getSplitPeople() != null && asrConfig.getSplitPeople() ? "1" : "0";
        return new KAsrInitData(sessionId, new Parameter(ifClearVoice,
                ifSplitPeople, loginUserInfo.getUserId() + "",  "",
                loginUserInfo.getOrgId() + "", scene));
    }
}
