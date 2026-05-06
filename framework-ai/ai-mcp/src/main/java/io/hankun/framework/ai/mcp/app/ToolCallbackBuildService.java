package io.hankun.framework.ai.mcp.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.SchemaVersion;
import io.hankun.framework.ai.common.redis.KRedisHolder;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.app.entity.KFunction;
import io.hankun.framework.ai.mcp.app.entity.KFunctionParameter;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.DefaultToolMetadata;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @className: ToolCallbackBuildService
 * @createAt: 2025/5/28 17:17
 * @author: hankun
 */
@Component
public class ToolCallbackBuildService {

    private final KHttpService kHttpService;

    private final RMap<String, ToolKey> shortNameMap;

    public ToolCallbackBuildService(KHttpService kHttpService,
                                    KRedisHolder redissonHolder) {
        RedissonClient redissonClient = redissonHolder.getRedissonClient();
        String key = redissonHolder.getKey("tool-name-map");
        this.shortNameMap = redissonClient.getLocalCachedMap(key
                , LocalCachedMapOptions.<String, ToolKey>defaults()
                        .cacheSize(200));
        this.kHttpService = kHttpService;
    }

    public ToolKey getByShortName(String shortName) {
        return shortNameMap.get(shortName);
    }

    public String getByKey(ToolKey toolKey) {
        ToolKey target = shortNameMap.get(toolKey.name());
        if (Objects.equals(target, toolKey)) {
            return toolKey.name();
        }
        String fullName = toolKey.buildFullName();
        target = shortNameMap.get(fullName);
        if (Objects.equals(target, toolKey)) {
            return fullName;
        }
        return null;
    }

    public ToolCallback build(KFunction function, ToolKey toolKey) {
        String toolName = loadToolName(toolKey);
        ToolDefinition toolDefinition = DefaultToolDefinition.builder()
                .name(toolName)
                .description(function.getDescription())
                .inputSchema(generateSchema(function))
                .build();
        ToolMetadata toolMetadata = DefaultToolMetadata.builder().
                returnDirect(function.isReturnDirect()).build();
        return new KHttpToolCallback(toolDefinition, toolMetadata, kHttpService, function.getHttpMethod(), function.getServiceName() + function.getPath());
    }

    private String loadToolName(ToolKey toolKey) {
        //尝试注册短名称
        ToolKey shortName = shortNameMap.computeIfAbsent(toolKey.name(), s -> toolKey);
        String toolName;
        if (Objects.equals(shortName.schema(), toolKey.schema())) {
            toolName = toolKey.name();
        } else {
            //无法注册短名称，使用完整名称
            toolName = toolKey.buildFullName();
            shortNameMap.put(toolName, toolKey);
        }
        return toolName;
    }

    public static String generateSchema(KFunction function) {
        try {
            ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
            schema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
            schema.put("type", "object");

            ObjectNode properties = schema.putObject("properties");
            List<String> required = new ArrayList<>();

            for (KFunctionParameter parameter : function.getParameters()) {
                String parameterName = parameter.getName();
                if (parameter.isRequired()) {
                    required.add(parameterName);
                }
                ObjectNode parameterNode = convertJson(parameter.getSchema());
                String parameterDescription = parameter.getDescription();
                if (StringUtils.hasText(parameterDescription)) {
                    parameterNode.put("description", parameterDescription);
                }
                properties.set(parameterName, parameterNode);
            }
            ArrayNode requiredArray = schema.putArray("required");
            required.forEach(requiredArray::add);
            return schema.toPrettyString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectNode convertJson(String json) {
        try {
            return (ObjectNode) new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
