package io.ihankun.framework.db.config;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class FlowControlFilter {

    private int windowSize = 5;
    private int count = 10;
}
