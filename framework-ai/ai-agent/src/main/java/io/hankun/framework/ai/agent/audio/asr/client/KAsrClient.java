package io.hankun.framework.ai.agent.audio.asr.client;

import com.alibaba.fastjson2.JSONObject;
import io.hankun.framework.ai.agent.audio.asr.KAsrApi;
import io.hankun.framework.ai.agent.audio.asr.config.AsrConfig;
import io.hankun.framework.ai.agent.audio.asr.entity.AsrResult;
import io.hankun.framework.ai.agent.audio.asr.entity.KAsrInitData;
import io.hankun.framework.ai.agent.audio.client.AbstractWebsocketAudioClient;
import io.hankun.framework.ai.agent.audio.entity.AudioResult;
import io.hankun.framework.core.context.user.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @className: KAsrClient
 * @createAt: 2025/12/17 11:17
 * @author: hankun
 */
@Slf4j
public class KAsrClient extends AbstractWebsocketAudioClient {

    private final AsrConfig asrConfig;


    private final LoginUserInfo loginUserInfo;

    private final String scene;

    private String sessionId;

    public KAsrClient(AsrConfig asrConfig,
                      LoginUserInfo loginUserInfo,
                      String scene) {
        this.asrConfig = asrConfig;
        this.loginUserInfo = loginUserInfo;
        this.scene = scene;
        // 构建连接参数
        String appId = asrConfig.getAppId();
        long timestamp = System.currentTimeMillis();
        String audioMode = asrConfig.getModel();
        String token = asrConfig.getToken();
        String asrConfigUrl = asrConfig.getUrl();
        String authorization = generateBearerToken(timestamp, token);

        // 构建WebSocket连接URL，确保参数正确编码
        String url = String.format(
                "%s?appId=%s" +
                        "&audioMode=%s&Authorization=%s&reqTimestamp=%d",
                asrConfigUrl,
                URLEncoder.encode(appId, StandardCharsets.UTF_8),
                URLEncoder.encode(audioMode, StandardCharsets.UTF_8),
                URLEncoder.encode(authorization, StandardCharsets.UTF_8),
                timestamp
        );

        initWebSocketClient(URI.create(url), Map.of());
    }

    @Override
    public void onReceive(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        if (jsonObject.containsKey("sessionId")) {
            log.info("获取会话id，receive={}", message);
            this.sessionId = jsonObject.getString("sessionId");
            return;
        }
        AsrResult result = jsonObject.to(AsrResult.class);
        if (receiveCallback != null) {
            boolean finish = result.getIsFinal() != null && result.getIsFinal();
            AudioResult audioResult;
            if ("2pass-online".equals(result.getMode())) {
                audioResult = AudioResult.of(finish, result.getText(), KAsrApi.NOT_SURE, false);
            } else {
                audioResult = AudioResult.of(finish, result.getText(), KAsrApi.DELTA, true);
            }
            receiveCallback.accept(audioResult);
        }
    }

    @Override
    public void onStart() {
        KAsrInitData data = KAsrInitData.of(sessionId, asrConfig, loginUserInfo, scene);
        send(JSONObject.toJSONString(data));
        setStart();
    }

    @Override
    public void onStop() {
    }

    @Override
    public String getCode() {
        return "k";
    }

    @Override
    public String getType() {
        return "asr";
    }

    @Override
    public boolean stream() {
        return true;
    }

    @Override
    public boolean supportRepeatUse() {
        return false;
    }

    @Override
    public void finishSend() {
        Map<String, Object> map = new HashMap<>();
        map.put("ifClose", 1);
        send(JSONObject.toJSONString(map));
    }


    /**
     * MD5加密工具方法
     * <p>
     * 对输入字符串进行MD5加密，返回32位小写十六进制字符串。
     * </p>
     *
     * @param input 需要加密的字符串
     * @return MD5加密后的32位小写十六进制字符串
     * @throws RuntimeException 当加密过程中发生错误时抛出
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 构造带 Bearer 前缀的 Token，格式：Bearer <md5(时间戳 + token)>
     *
     * @param timestamp    当前13位时间戳
     * @param productToken 产品 token
     * @return Authorization 请求头使用的 Bearer token
     */
    public static String generateBearerToken(long timestamp, String productToken) {
        String raw = timestamp + productToken;
        return "Bearer " + md5(raw);
    }

}
