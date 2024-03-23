package io.ihankun.framework.common.v1.utils.http;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hankun
 */
public class RequestParamUtil {

    private static final String AND = "&";
    private static final String REPLACE_STR = "$";

    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数
     * @param paraMap   要排序的Map对象
     * @param urlEncode   是否需要URLENCODE
     * @param keyToLower    是否需要将Key转换为全小写
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds){
                if (item.getKey()!=null && !"".equals(item.getKey())){
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    }else {
                        buf.append(key + "=" + val);
                    }
                    buf.append(AND);
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret 秘钥
     * @return 加密后字符串
     */
    public static String sha256Hmac(String message, String secret) {
        String hash = "";
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256.init(secretKey);
            byte[] bytes = sha256.doFinal(message.getBytes(StandardCharsets.UTF_8));
            hash = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {

        }
        return hash;
    }

    public static String urlParamBuild(String url, String...params){
        //如果包含占位符
        if (url.contains(REPLACE_STR) && params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                url = url.replaceAll("\\$" + (i + 1), param);
            }
        }
        return url;
    }
}
