package io.hankun.framework.ai.store.controller;

import io.hankun.framework.ai.store.history.po.ModelHistory;
import io.hankun.framework.ai.store.history.repository.ModelHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hankun
 * @date 2025/11/1711:19
 */
@RestController
@RequestMapping("/modelHistory")
public class ModelHistoryController {
    private final ModelHistoryRepository modelHistoryRepository;
    public ModelHistoryController(ModelHistoryRepository modelHistoryRepository) {
        this.modelHistoryRepository = modelHistoryRepository;
    }

    /**
     *  获取模型历史数据
     * @param sessionId 会话id
     * @param messageId 消息id
     * @param type  类型
     * @return 模型历史数据
     */
    @GetMapping("/loadModelData")
    public Map<String, List<ModelHistory>> loadModelData(@RequestParam(required = false) String sessionId,
                                                         @RequestParam(required = false) String messageId,
                                                         @RequestParam(required = false) String type) {
        List<ModelHistory> modelHistories = modelHistoryRepository.loadModelData(sessionId, messageId, type);

        // 根据messageId进行分组汇聚
        return modelHistories.stream()
                .collect(Collectors.groupingBy(
                        modelHistory -> modelHistory.getMessageId() != null ? modelHistory.getMessageId() : "unknown",
                        Collectors.toList()
                ));
    }
}
