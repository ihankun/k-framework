package io.hankun.framework.ai.mem.mem0.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: MemCreateDto
 * @createAt: 2025/12/3 13:42
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class MemCreateDto {
    private List<MemMessage> messages;
    @JSONField(name = "user_id")
    private String userId;
    @JSONField(name = "agent_id")
    private String agentId;
    @JSONField(name = "run_id")
    private String runId;
    private Map<String, Object> metadata;
}
