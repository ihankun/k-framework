package io.hankun.framework.ai.agent.audio.tts.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.hankun.framework.ai.agent.audio.client.AbstractWebsocketAudioClient;
import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Map;

/**
 * @description:
 * @className: KWebsocketTtsClient
 * @createAt: 2025/7/24 11:06
 * @author: hankun
 */
@Slf4j
public class KWebsocketTtsClient extends AbstractWebsocketAudioClient {

    private static final SendData INIT = new SendData("tts", "start", null);

    private final String startString;

    private String session;

    public KWebsocketTtsClient(TtsConfig ttsConfig) {
        initWebSocketClient(URI.create(ttsConfig.url()), Map.of());
        this.startString = JSON.toJSONString(INIT, JSONWriter.Feature.IgnoreEmpty);
        this.status = AudioClientStatus.INIT;
    }


    @Override
    public String getCode() {
        return TtsType.K.getCode();
    }

    @Override
    public String getType() {
        return "tts";
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
    public void finishSend() {
    }

    @Override
    public void sendData(String text) {
        TextData textData = new TextData(0, text);
        String data = JSON.toJSONString(textData);
        send(data);
    }

    @Override
    public void onReceive(String text) {
        ReceiveData receiveData = JSON.parseObject(text, ReceiveData.class);
        log.debug("k-websocket-client: 接收到消息: status={},signal={},session={}",
                receiveData.status, receiveData.signal, receiveData.session);
        String audio = receiveData.audio();
        switch (receiveData.status()) {
            case null:
                throw new RuntimeException("webSocket接受数据异常");
            case 0:
                if ("server ready".equals(receiveData.signal())) {
                    session = receiveData.session();
                    setStart();
                    log.info("k-websocket-client: 会话准备完成");
                } else if ("connection will be closed".equals(receiveData.signal())) {
                    setStop();
                    log.info("k-websocket-client: 会话关闭完成");
                } else {
                    throw new RuntimeException("webSocket接受数据异常");
                }
                break;
            case 1:
                AudioResult audioResult = AudioResult.ofAudio(audio);
                receiveCallback.accept(audioResult);
                break;
            case 2:
                AudioResult finishResult = AudioResult.finish(audio);
                receiveCallback.accept(finishResult);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        send(startString);
    }

    @Override
    public void onStop() {
        String endString = JSON.toJSONString(new SendData("tts", "end", session));
        send(endString);
    }

    public record SendData(String task, String signal, String session) {

    }

    public record TextData(int spk_id, String text) {
    }

    public record ReceiveData(Integer status, String signal, String session, String audio) {

    }
}
