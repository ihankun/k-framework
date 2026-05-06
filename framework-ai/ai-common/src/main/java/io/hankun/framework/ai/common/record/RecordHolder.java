package io.hankun.framework.ai.common.record;

import java.util.Map;

/**
 * @description:
 * @className: RecordHolder
 * @createAt: 2025/10/17 17:47
 * @author: hankun
 */
public record RecordHolder<T extends IDetailRecord>(T record, Map<String, Object> meta) {
}
