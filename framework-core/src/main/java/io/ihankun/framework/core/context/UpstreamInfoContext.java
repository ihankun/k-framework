package io.ihankun.framework.core.context;

import com.alibaba.fastjson.JSON;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author hankun
 */
public class UpstreamInfoContext {

    public static final String UPSTREAM = "Upstream";

    public static final String SOURCE_INFO = "SourceInfo";

    public static final String FRONT_HEAD = "VP-Mark";

    public static final String FRONT = "front";

    private static final ThreadLocal<UpstreamInfo> CONTEXT_HOLDER = new NamedThreadLocal<>("source-info-context");

    public static final String LOCAL = "local";

    public static final String MQ = "mq";

    public static final String MQ_SERVICE = "mq-service";

    public static final String DATABASE = "database";

    public static final String DATABASE_SERVICE = "database-service";

    public static final String SEND = "/send";

    public static final String RECEIVE = "/receive";

    public static final String ROOT_URI = "rootUri";

    public static UpstreamInfo get() {
        return CONTEXT_HOLDER.get();
    }

    public static UpstreamInfo getOrDefault() {
        UpstreamInfo upstreamInfo = get();
        if (upstreamInfo == null) {
            set(new UpstreamInfo());
            return get();
        }
        return upstreamInfo;
    }

    public static void setUri(String uri) {
        getOrDefault().setUri(uri);
    }

    public static String getApiTag() {
        if (get() == null) {
            return "";
        }
        return get().getApiTag();
    }

    public static String getRootUri() {
        if (get() == null) {
            return "";
        }
        return get().getRootUri();
    }

    public static void setApiInfo(String info) {
        UpstreamInfo upstreamInfo = getOrDefault();
        upstreamInfo.putApiInfo(info);
        upstreamInfo.setServiceTag(LOCAL);
    }

    public static void set(UpstreamInfo upstreamInfo) {
        CONTEXT_HOLDER.set(upstreamInfo);
    }

    public static String getBase64String() {
        UpstreamInfo upstreamInfo = get();
        if (upstreamInfo == null) {
            return null;
        }
        return Base64Utils.encodeToString(JSON.toJSONString(upstreamInfo).getBytes(StandardCharsets.UTF_8));
    }

    public static UpstreamInfo build(String base64String) {
        if (StringUtils.isEmpty(base64String)) {
            return null;
        }
        return JSON.parseObject(new String(Base64Utils.decodeFromString(base64String), StandardCharsets.UTF_8),
                UpstreamInfo.class);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

}
