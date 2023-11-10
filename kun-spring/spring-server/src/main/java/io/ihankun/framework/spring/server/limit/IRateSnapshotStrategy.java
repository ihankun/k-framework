package io.ihankun.framework.spring.server.limit;

import io.ihankun.framework.spring.server.limit.engine.RateElement;
import io.ihankun.framework.spring.server.limit.properties.RateProperties;

import java.util.List;

/**
 * @author hankun
 */
public interface IRateSnapshotStrategy {

    /**
     * 业务码
     *
     * @return
     */
    String businessKey();


    /**
     * 快照分析配置参数
     *
     * @return
     */
    RateProperties.Config config();


    /**
     * 每次快照的统计情况
     *
     * @return
     */
    List<RateElement> snapshot();
}
