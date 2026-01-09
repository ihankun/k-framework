package io.hankun.framework.core.invocation;


import io.hankun.framework.core.invocation.entity.DsInfo;

import java.util.List;

/**
 * @author hankun
 */
public interface DbInfoLoader {

    /**
     * 获取服务使用的schema
     *
     * @return schema信息，db.schema
     */
    List<DsInfo> loadDbInfoList();
}
