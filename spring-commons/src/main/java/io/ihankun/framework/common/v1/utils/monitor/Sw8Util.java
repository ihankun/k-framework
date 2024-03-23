package io.ihankun.framework.common.v1.utils.monitor;

import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author hankun
 */
public class Sw8Util {

    /**
     * skywalking的链路信息
     */
    public static final String SW8 = "sw8";
    public static final int SW8_LENGTH = 8;

    private static final java.util.Base64.Decoder DECODER = java.util.Base64.getDecoder();
    private static final java.util.Base64.Encoder ENCODER = java.util.Base64.getEncoder();

    /**
     * 根据sw8的值解析traceId
     */
    public static String getTraceId(String text) {
        return getStringIndex(text, 1);
    }
    /**
     * 根据sw8的值解析父服务名称 ParentService
     */
    public static String getParentService(String text) {
        return getStringIndex(text, 4);
    }

    /**
     * 解析完整的Sw8内容
     */
    public static Sw8Info parseSw8toObject(String text) {
        Sw8Info sw8Info = new Sw8Info();
        if (text == null) {
            return sw8Info;
        }
        String[] parts = text.split("-", 8);
        if (parts.length == SW8_LENGTH) {
            try {
                // parts[0] is sample flag, always trace if header exists.
                sw8Info.traceId = decode2UTFString(parts[1]);
                sw8Info.parentTraceSegmentId = decode2UTFString(parts[2]);
                sw8Info.parentSpanId = Integer.parseInt(parts[3]);
                sw8Info.parentService = decode2UTFString(parts[4]);
                sw8Info.parentServiceInstance = decode2UTFString(parts[5]);
                sw8Info.parentEndpoint = decode2UTFString(parts[6]);
                sw8Info.peer = decode2UTFString(parts[7]);
            } catch (IllegalArgumentException ignored) {

            }
        }
        return sw8Info;
    }

    /**
     * 获取对应下标的String值
     */
    static String getStringIndex(String text, int index) {
        if (text == null) {
            return "";
        }
        String value = "";
        String[] parts = text.split("-", 8);
        if (parts.length == SW8_LENGTH) {
            try {
                value = decode2UTFString(parts[index]);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return value;
    }

    /**
     * SkyWalking 跨进程传播协议是用于上下文的传播，版本是3.0，也被称为为sw8协议。
     * Header值以-字符进行分割，具体包含以下8个字段：
     */
    @Data
    static class Sw8Info {
        /**
         * 采样（Sample），0 或 1，0 表示上下文存在, 但是可以（也很可能）被忽略；1 表示这个追踪需要采样并发送到后端。
         */
        String sample;
        /**
         * 追踪ID（Trace Id），是 BASE64 编码的字符串， 表示此追踪的唯一标识。
         */
        String traceId;
        /**
         * 父追踪片段ID（Parent trace segment Id），是 BASE64 编码的字符串，其内容是字符串且全局唯一。
         */
        String parentTraceSegmentId;
        /**
         * 父跨度ID（Parent span Id），是一个从 0 开始的整数，这个跨度ID指向父追踪片段（segment）中的父跨度（span）。
         */
        int parentSpanId;
        /**
         * 父服务名称（Parent service），是 BASE64 编码的字符串，其内容是一个长度小于或等于50个UTF-8编码的字符串。
         */
        String parentService;
        /**
         * 父服务实例标识（Parent service instance），是 BASE64 编码的字符串，其内容是一个长度小于或等于50个UTF-8编码的字符串。
         */
        String parentServiceInstance;
        /**
         * 父服务的端点（Parent endpoint），是 BASE64 编码的字符串，其内容是父追踪片段（segment）中第一个入口跨度（span）的操作名，由长度小于或等于50个UTF-8编码的字符组成。
         */
        String parentEndpoint;
        /**
         * 本请求的目标地址（Peer），是 BASE64 编码的字符串，其内容是客户端用于访问目标服务的网络地址（不一定是 IP + 端口）。
         */
        String peer;
    }

    public static String decode2UTFString(String in) {
        return new String(DECODER.decode(in), StandardCharsets.UTF_8);
    }

    public static String encode(String text) {
        return ENCODER.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
