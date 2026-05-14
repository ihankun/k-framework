package io.hankun.framework.ai.core.context;

import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.commons.context.KContextHolder;

/**
 * @description:
 * @className: CurrentIdHolder
 * @createAt: 2025/10/17 18:02
 * @author: hankun
 */
public class CurrentIdHolder {

    public static final String CURRENT_ID = "currentId";

    public static void setCurrentId(CurrentId currentId) {
        KContextHolder.setData(CURRENT_ID, currentId);
    }

    public static CurrentId getCurrentId() {
        return KContextHolder.getData(CURRENT_ID);
    }

    public static void removeCurrentId() {
        KContextHolder.removeData(CURRENT_ID);
    }
}
