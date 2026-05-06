package io.hankun.framework.ai.agent.audio.tts.client;

import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;

public class QwenServerCommitTtsClient extends AbstractQwenTtsClient {

    public QwenServerCommitTtsClient(TtsConfig ttsConfig) {
        super(ttsConfig);
    }

    @Override
    public String getCode() {
        return TtsType.QWEN.getCode();
    }


    @Override
    public boolean supportRepeatUse() {
        return false;
    }

    @Override
    public boolean stream() {
        return true;
    }


    @Override
    public SessionMode getMode() {
        return SessionMode.SERVER_COMMIT;
    }

    @Override
    public void sendData(String data) {
        send(data);
        send(finishSession());
    }
}
