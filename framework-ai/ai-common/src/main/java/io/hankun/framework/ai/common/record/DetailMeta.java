package io.hankun.framework.ai.common.record;


import io.hankun.framework.commons.utils.ContextUtil;

import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @className: DetailMeta
 * @createAt: 2025/10/27 13:56
 * @author: hankun
 */
public class DetailMeta {

    public static void setStart(Map<String, Object> meta) {
        ContextUtil.setData(meta, "startTime", new Date().getTime());
    }

    public static Long getStart(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "startTime");
    }


    public static void setEnd(Map<String, Object> meta) {
        ContextUtil.setData(meta, "endTime", new Date().getTime());
    }


    public static Long getEnd(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "endTime");
    }

    public static void setInputTokens(Map<String, Object> meta, Integer inputTokens) {
        ContextUtil.setData(meta, "inputTokens", inputTokens);
    }

    public static Integer getInputTokens(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "inputTokens");
    }

    public static void setInputOffset(Map<String, Object> meta, Integer inputOffset) {
        ContextUtil.setData(meta, "inputOffset", inputOffset);
    }

    public static Integer getInputOffset(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "inputOffset");
    }

    public static void setOutputTokens(Map<String, Object> meta, Integer outputTokens) {
        ContextUtil.setData(meta, "outputTokens", outputTokens);
    }

    public static Integer getOutputTokens(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "outputTokens");
    }

    public static void setOutputOffset(Map<String, Object> meta, Integer outputOffset) {
        ContextUtil.setData(meta, "outputOffset", outputOffset);
    }

    public static Integer getOutputOffset(Map<String, Object> meta) {
        return ContextUtil.getData(meta, "outputOffset");
    }

}
