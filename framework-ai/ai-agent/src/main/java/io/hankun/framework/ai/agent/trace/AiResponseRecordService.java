package io.hankun.framework.ai.agent.trace;

import io.hankun.framework.ai.agent.trace.node.AiResponseInfo;
import io.hankun.framework.ai.agent.trace.node.ApiResponseInfo;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: AiResponseRecordService
 * @createAt: 2025/7/18 14:17
 * @author: hankun
 */
@Component
public class AiResponseRecordService {

    @Getter
    private final List<AiResponseInfo> aiResponseInfoList = new CopyOnWriteArrayList<>();

    @Getter
    private final List<ApiResponseInfo> apiResponseInfoList = new CopyOnWriteArrayList<>();


    public void add(AiResponseInfo aiResponseInfo) {
        aiResponseInfoList.add(aiResponseInfo);
    }

    public void add(ApiResponseInfo apiResponseInfo) {
        apiResponseInfoList.add(apiResponseInfo);
    }
}
