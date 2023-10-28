package io.ihankun.framework.common.nacos;

import java.util.Properties;

/**
 * @author hankun
 */
public interface INacosConfigChangeListener {

    /**
     * 监听的dataId
     *
     * @return
     */
    String dataId();


    /**
     * 变化内容
     *
     * @param content
     */
    void changed(Properties content);
}
