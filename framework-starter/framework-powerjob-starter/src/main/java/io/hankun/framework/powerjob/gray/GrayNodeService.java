package io.hankun.framework.powerjob.gray;

import java.util.List;

/**
 * @description: 灰度节点服务接口。
 * 定义了获取集群中所有灰度 PowerJob worker 节点地址的契约。
 * @fileName: GrayNodeService.java
 * @author: hankun
 */

public interface GrayNodeService {

    /**
     * 获取所有标记为“灰度”的 PowerJob worker 节点的地址列表。
     * 地址格式应为 "IP:PORT"，例如 "172.16.2.78:27777"。
     *
     * @return 灰度节点地址列表。如果没有灰度节点，则返回空列表。
     */
    List<String> getGrayWorkerAddresses();
}
