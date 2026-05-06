package io.hankun.framework.ai.agent.node.entity;

import io.hankun.framework.ai.agent.entity.DataWithMeta;

import java.util.List;

/**
 * @description:
 * @className: DataWithMetaContext
 * @createAt: 2025/10/23 13:36
 * @author: hankun
 */
public record DataWithMetaContext(List<DataWithMeta> dataWithMetaList) {

    public static DataWithMetaContext of() {
        return new DataWithMetaContext(List.of());
    }

    public void add(DataWithMeta dataWithMeta) {
        dataWithMetaList.add(dataWithMeta);
    }

    public void add(List<DataWithMeta> dataWithMetaList) {
        this.dataWithMetaList.addAll(dataWithMetaList);
    }

    public DataWithMeta combine() {
        return DataWithMeta.combine(dataWithMetaList);
    }
}
