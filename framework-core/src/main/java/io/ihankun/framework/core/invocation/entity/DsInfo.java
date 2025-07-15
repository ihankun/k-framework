package io.ihankun.framework.core.invocation.entity;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class DsInfo {
    private String db;
    private String schema;
    private String userName;
    private String dsName;
    private String dsMark;
}
