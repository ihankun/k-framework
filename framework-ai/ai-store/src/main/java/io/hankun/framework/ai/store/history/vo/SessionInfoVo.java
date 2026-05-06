package io.hankun.framework.ai.store.history.vo;

import io.hankun.framework.ai.store.history.po.SessionInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: SessionInfoVo
 * @createAt: 2025/10/20 11:26
 * @author: hankun
 */
public record SessionInfoVo(String sessionId,
                            String name,
                            Date lastMessageTime) {

    public static List<SessionInfoVo> of(List<SessionInfo> sessionInfos) {
        if (sessionInfos == null) {
            return null;
        }
        List<SessionInfoVo> sessionInfoVos = new ArrayList<>(sessionInfos.size());
        for (SessionInfo sessionInfo : sessionInfos) {
            sessionInfoVos.add(new SessionInfoVo(sessionInfo.getId(), sessionInfo.getName(), sessionInfo.getLastUpdateTime()));
        }
        return sessionInfoVos;
    }
}

