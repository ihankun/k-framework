package io.hankun.framework.ai.agent.audio.asr.impl;

import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.audio.client.AbstractClientApi;
import io.hankun.framework.ai.agent.audio.client.AudioClient;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

/**
 * @description:
 * @className: MsunAsrImpl
 * @createAt: 2025/12/16 14:57
 * @author: hankun
 */
@Slf4j
public class KAsrImpl extends AbstractClientApi<ByteBuffer, KAsrResult> implements KAsrApi {


    public KAsrImpl(String sessionId, boolean repeatUse, AudioClient audioClient) {
        super(sessionId, repeatUse, audioClient);
    }

    @Override
    public void onReceive(AudioResult msunAudioData) {
        resultSink.tryEmitNext(KAsrResult.of(msunAudioData.text(), msunAudioData.type(), false));
        if (msunAudioData.finish()) {
            stop();
        }
    }


    @Override
    public Flux<ByteBuffer> filter(Flux<ByteBuffer> flux) {
        return flux;
    }

    @Override
    public void exec(Flux<ByteBuffer> flux) {
        flux.subscribe(audioClient::sendData);
    }
}
