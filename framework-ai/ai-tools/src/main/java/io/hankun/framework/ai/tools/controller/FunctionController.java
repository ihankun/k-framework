package io.hankun.framework.ai.tools.controller;

import io.hankun.framework.ai.mcp.callback.KToolCallbackManager;
import io.hankun.framework.ai.mcp.entity.ToolKey;
import io.hankun.framework.ai.mcp.app.KFunctionManager;
import io.hankun.framework.ai.mcp.app.RedisKFunctionManager;
import io.hankun.framework.ai.mcp.app.entity.KFunctionList;
import io.hankun.framework.ai.tools.entity.FunctionRegisterResult;
import io.hankun.framework.core.context.sys.GrayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: FunctionController
 * @createAt: 2025/5/29 11:35
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/function")
public class FunctionController {

    private final KFunctionManager functionManager;

    private final KToolCallbackManager toolsManager;

    public FunctionController(KFunctionManager functionManager,
                              KToolCallbackManager toolsManager) {
        this.functionManager = functionManager;
        this.toolsManager = toolsManager;
    }

    @PostMapping("/register")
    public FunctionRegisterResult register(@RequestBody KFunctionList functionList) {
        log.info("register functionList:{}", functionList);
        if (functionManager instanceof RedisKFunctionManager) {
            ((RedisKFunctionManager) functionManager).register(functionList);
        }
        return FunctionRegisterResult.success();
    }


    @PostMapping("/test")
    public String test(@RequestParam String toolName,
                       @RequestBody String params) {
        ToolCallback[] list = toolsManager.buildProvider(ToolKey.of(toolName)).getToolCallbacks();
        return list[0].call(params, new ToolContext(Map.of()));
    }

    @GetMapping("/list")
    public List<KFunctionList> listFunctions(@RequestParam(value = "gray", required = false) String gray,
                                                 @RequestParam(value = "serviceName", required = false) String serviceName) {
        log.info("listFunctions gray:{},serviceName:{}", gray, serviceName);
        return functionManager.listFunctions(gray, serviceName);
    }

    @GetMapping("/listDef")
    public List<ToolDefinition> listDef(String gray) {
        GrayContext.mock(gray);
        return toolsManager.listAllToolDefinitions();
    }

    @GetMapping("/listDesc")
    public List<String> listDesc(String gray) {
        GrayContext.mock(gray);
        return toolsManager.listAllToolDefinitions().stream().map(ToolDefinition::description).toList();
    }

    @GetMapping("/clear")
    public void clear(@RequestParam(required = false) String service,
                      @RequestParam(required = false) String gray) {
        functionManager.remove(service, gray);
    }
}
