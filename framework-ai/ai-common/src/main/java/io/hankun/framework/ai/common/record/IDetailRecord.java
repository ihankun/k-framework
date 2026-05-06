package io.hankun.framework.ai.common.record;

import io.hankun.framework.ai.common.entity.CurrentId;

import java.util.Map;

/**
 * @description:
 * @className: IDetailRecord
 * @createAt: 2025/10/17 17:45
 * @author: hankun
 */
public interface IDetailRecord {

    CurrentId currentId();

    String type();

    default void updateWithMeta(Map<String, Object> meta) {

    }
}
