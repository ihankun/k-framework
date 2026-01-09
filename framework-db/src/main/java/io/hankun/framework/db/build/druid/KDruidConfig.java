package io.hankun.framework.db.build.druid;

import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hankun
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KDruidConfig extends DruidConfig {
    private String group;
}
