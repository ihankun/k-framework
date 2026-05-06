package io.hankun.framework.ai.agent.audio.tts.client;


import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;

public class QwenCommitTtsClient extends AbstractQwenTtsClient {


    public QwenCommitTtsClient(TtsConfig ttsConfig) {
        super(ttsConfig);
    }

    @Override
    public SessionMode getMode() {
        return SessionMode.COMMIT;
    }

    @Override
    public String getCode() {
        return TtsType.QWEN_COMMIT.getCode();
    }

    @Override
    public boolean stream() {
        return false;
    }

    @Override
    public boolean supportRepeatUse() {
        return false;
    }

    @Override
    public void sendData(String data) {
        super.sendData(data);
        send(QwenServerCommitTtsClient.commitTextBuffer());
    }
}
