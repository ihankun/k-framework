package io.hankun.framework.ai.tools.contexts;

import com.alibaba.fastjson2.util.DateUtils;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.ContextAccess;
import io.hankun.framework.ai.context.model.ModelContextDataBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @description:
 * @className: TimeModelContextBuilder
 * @createAt: 2025/10/20 13:36
 * @author: hankun
 */
@Component
public class TimeModelContextBuilder implements ModelContextDataBuilder<TimeContext> {
    @NotNull
    @Override
    public Class<TimeContext> dataType() {
        return TimeContext.class;
    }

    @Override
    public Object buildContext(@NotNull CurrentId currentId, @NotNull ContextAccess contextAccess, TimeContext data) {
        String result = "";
        result += "### 当前时间：" + DateUtils.format(data.startTime(), "yyyy年MM月dd号HH点mm分ss秒 E") + "\n";
        result += buildExtendTimeInfo(data.startTime());
        return result;
    }

    @NotNull
    @Override
    public String code() {
        return "time";
    }

    @Override
    public String desc() {
        return "当前时间";
    }

    private static String buildExtendTimeInfo(LocalDateTime localDateTime) {
        String result = "### 扩展时间信息：\n";
        result += "  * 昨天是" + DateUtils.format(localDateTime.minusDays(1), "yyyy年MM月dd号E") + "\n";
        result += "  * 前天是" + DateUtils.format(localDateTime.minusDays(2), "yyyy年MM月dd号E") + "\n";
        result += "  * 明天是" + DateUtils.format(localDateTime.plusDays(1), "yyyy年MM月dd号E") + "\n";
        return result;
    }

}
