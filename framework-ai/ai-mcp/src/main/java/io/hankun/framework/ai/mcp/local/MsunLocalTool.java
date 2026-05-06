package io.hankun.framework.ai.mcp.local;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @description:
 * @className: MsunLocalTool
 * @createAt: 2025/10/13 15:35
 * @author: hankun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface MsunLocalTool {
}
