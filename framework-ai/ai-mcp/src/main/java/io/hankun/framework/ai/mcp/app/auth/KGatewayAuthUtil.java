package io.hankun.framework.ai.mcp.app.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.hankun.framework.ai.mcp.app.config.KAiHttpConfig;
import io.hankun.framework.core.context.user.LoginUserInfo;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.Base64;

/**
 * @description:
 * @className: KGatewayAuthUtil
 * @createAt: 2025/5/28 14:36
 * @author: hankun
 */
public class KGatewayAuthUtil {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 生成授权信息
     *
     * @param queryString    query
     * @param body           body
     * @param loginUser      用户信息
     * @param kAiHttpConfig 配置信息
     * @return token
     */
    public static String generateAuthorization(String queryString,
                                               String body,
                                               LoginUserInfo loginUser,
                                               KAiHttpConfig kAiHttpConfig) {

        String signType = "md5";
        String timestamp = String.valueOf(System.currentTimeMillis());
        JSONObject paramMap = JSON.parseObject(JSON.toJSONString(loginUser));
        paramMap.put("systemId", kAiHttpConfig.getSystemId());
        String clientInfo = paramMap.toJSONString();

        String param = ObjectUtils.isEmpty(body) ? queryString : body;

        String plainText = signType + "." +
                timestamp + "." +
                clientInfo + "." +
                cleanParams(param) + "." +
                kAiHttpConfig.getToken();
        String sign = DigestUtils.md5DigestAsHex(plainText.getBytes()).toUpperCase();

        return ENCODER.encodeToString(signType.getBytes()) + "." +
                ENCODER.encodeToString(timestamp.getBytes()) + "." +
                ENCODER.encodeToString(clientInfo.getBytes()) + "." +
                ENCODER.encodeToString(sign.getBytes());
    }

    private static String cleanParams(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[\r\n]", "");
    }


}
