package io.hankun.framework.commons.http;

import org.springframework.http.client.reactive.ClientHttpConnector;

/**
 * @description:
 * @className: KClientHttpConnectorBuilder
 * @createAt: 2026/1/4 11:09
 * @author: hankun
 */
public interface KClientHttpConnectorBuilder {

    String type();

    ClientHttpConnector build(KConnectorConfig config);
}
