package io.ihankun.framework.db.build.druid;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hankun
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DruidConfig extends com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig {
    private String group;
}
