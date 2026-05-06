package io.hankun.framework.ai.mem.mem0.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @description:
 * @className: MemSearchDto
 * @createAt: 2025/12/3 13:56
 * @author: hankun
 */
@NoArgsConstructor
@Data
public class MemSearchDto {
    private String query;
    @JSONField(name = "user_id")
    private String userId;
    @JSONField(name = "run_id")
    private String runId;
    @JSONField(name = "agent_id")
    private String agentId;
    private Map<String, Object> filters;
}
