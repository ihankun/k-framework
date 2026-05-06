package io.hankun.framework.ai.agent.audio.asr;

import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.audio.client.KAudioApi;

import java.nio.ByteBuffer;

/**
 * @description:
 * @className: MsunAsrApi
 * @createAt: 2025/12/16 14:56
 * @author: hankun
 */
public interface KAsrApi extends KAudioApi<ByteBuffer, KAsrResult> {

    String DELTA = "delta";

    String NOT_SURE = "notSure";
}
