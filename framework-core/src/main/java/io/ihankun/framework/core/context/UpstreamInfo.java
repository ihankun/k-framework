package io.ihankun.framework.core.context;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class UpstreamInfo {

    public static final String PREFIX = "/";

    /**
     * 起点服务和uri（serviceName/uri）
     */
    private String rootUri;
    /**
     * 上游uri
     */
    private String uri;
    /**
     * 上游服务类型
     */
    private String serviceTag;
    /**
     * 上游标识
     */
    private String apiTag;

    /**
     * 是否管控，为空时不管控
     */
    private Boolean control;

    public static UpstreamInfo buildEmpty() {
        UpstreamInfo upstreamInfo = new UpstreamInfo();
        upstreamInfo.setRootUri(UpstreamInfoContext.getRootUri());
        upstreamInfo.setUri("");
        upstreamInfo.setServiceTag("");
        upstreamInfo.setApiTag("");
        upstreamInfo.setControl(false);
        return upstreamInfo;
    }

    public void putApiInfo(String key) {
        setUri(PREFIX + key);
        setApiTag(key);
    }
}
