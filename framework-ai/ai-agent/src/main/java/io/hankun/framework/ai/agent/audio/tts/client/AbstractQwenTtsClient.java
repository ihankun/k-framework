package io.hankun.framework.ai.agent.audio.tts.client;

import com.alibaba.fastjson2.JSONObject;
import io.hankun.framework.ai.agent.audio.client.AbstractWebsocketAudioClient;
import io.hankun.framework.ai.agent.audio.entity.AudioClientStatus;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractQwenTtsClient extends AbstractWebsocketAudioClient {


    private String currentResponseId;

    private String currentItemId;


    private final String voice;

    private final String responseFormat;

    private final int sampleRate;

    public AbstractQwenTtsClient(TtsConfig ttsConfig) {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("Authorization", "Bearer " + ttsConfig.apiKey());
        initWebSocketClient(URI.create(ttsConfig.url()),
                httpHeaders);
        this.voice = ttsConfig.voice();
        this.responseFormat = ttsConfig.format();
        this.sampleRate = ttsConfig.sampleRate();
    }

    public abstract SessionMode getMode();

    @Override
    public void sendData(String data) {
        data = convertText(data);
        send(data);
    }


    @Override
    public String getType() {
        return "tts";
    }

    @Override
    public void onReceive(String text) {
        JSONObject event = JSONObject.parse(text);
        String type = event.getString("type");
        log.info("qwen-websocket-client: 收到消息: {}", event);
        switch (type) {
            case "error" -> {
                log.error("qwen-websocket-client: 错误: {}", event.getJSONObject("error"));
                status = AudioClientStatus.ERROR;
                if (errorCallback != null) {
                    errorCallback.accept(new RuntimeException(text));
                }
            }
            case "session.created" -> {
                log.info("qwen-websocket-client: 会话创建，ID: {}", event.getJSONObject("session").getString("id"));
            }
            case "session.updated" -> {
                setStart();
                log.info("qwen-websocket-client: 会话更新，ID: {}", event.getJSONObject("session").getString("id"));
            }
            case "input_text_buffer.committed" -> {
                log.info("qwen-websocket-client: 文本缓冲区已提交，项目ID: {}", event.getString("item_id"));
            }
            case "input_text_buffer.cleared" -> {
                log.info("qwen-websocket-client: 文本缓冲区已清除");
            }
            case "response.created" -> {
                currentResponseId = event.getJSONObject("response").getString("id");
                status = AudioClientStatus.RUNNING;
                log.info("qwen-websocket-client: 响应已创建，ID: {}", currentResponseId);
            }
            case "response.output_item.added" -> {
                currentItemId = event.getJSONObject("item").getString("id");
                log.info("qwen-websocket-client: 输出项已添加，ID: {}", currentItemId);
            }
            case "response.audio.delta" -> {
                String delta = event.getString("delta");
                receiveCallback.accept(AudioResult.ofAudio(delta));
            }
            case "response.audio.done" -> {
                receiveCallback.accept(AudioResult.finish(""));
                log.info("qwen-websocket-client: 音频生成完成");
            }
            case "response.done" -> {
                setStop();
                currentResponseId = null;
                currentItemId = null;
                log.info("qwen-websocket-client: 响应完成");
            }
            case "session.finished" -> {
                log.info("qwen-websocket-client: 会话已结束");
            }
        }
    }

    @Override
    public void finishSend() {
    }

    @Override
    public void onStart() {
        send(startSession(getMode().getValue(), voice, responseFormat, sampleRate));
    }

    @Override
    public void onStop() {

    }


    @Getter
    @AllArgsConstructor
    public enum SessionMode {
        SERVER_COMMIT("server_commit"),
        COMMIT("commit");
        private final String value;
    }


    public static String startSession(String mode, String voice,
                                      String responseFormat,
                                      int sampleRate) {
        JSONObject config = new JSONObject();
        config.put("mode", mode);
        config.put("voice", voice);
        config.put("response_format", responseFormat);
        config.put("sample_rate", sampleRate);
        JSONObject event = new JSONObject();
        event.put("type", "session.update");
        event.put("session", config);
        return sendEvent(event);
    }

    public static String convertText(String text) {
        JSONObject event = new JSONObject();
        event.put("type", "input_text_buffer.append");
        event.put("text", text);
        return sendEvent(event);
    }

    public static String commitTextBuffer() {
        JSONObject event = new JSONObject();
        event.put("type", "input_text_buffer.commit");
        return sendEvent(event);
    }

    public static String clearTextBuffer() {
        JSONObject event = new JSONObject();
        event.put("type", "input_text_buffer.clear");
        return sendEvent(event);
    }

    public static String finishSession() {
        JSONObject event = new JSONObject();
        event.put("type", "session.finish");
        return sendEvent(event);
    }

    public static String sendEvent(JSONObject event) {
        String eventId = "event_" + System.currentTimeMillis();
        event.put("event_id", eventId);
        return event.toString();
    }
}
