package io.ihankun.framework.db.dynamic.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 数据源信息
 * @author hankun
 */
@Data
@Builder
public class DataSourceInfo {

    private String ip;

    private String port;

    private String password;
}
