package io.hankun.framework.ai.agent.controller;

import io.hankun.framework.ai.context.ContextService;
import io.hankun.framework.ai.context.entity.ContextDesc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @className: ContextController
 * @createAt: 2025/11/13 15:29
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/context")
public class ContextController {

    private final ContextService contextService;

    public ContextController(ContextService contextService) {
        this.contextService = contextService;
    }

    @GetMapping("/listSystem")
    public List<ContextDesc> listSystem() {
        return contextService.listSystemContexts();
    }

    @GetMapping("/listUser")
    public List<ContextDesc> listUser() {
        return contextService.listUserContexts();
    }

    @GetMapping("/listProgram")
    public List<ContextDesc> listProgram() {
        return contextService.listProgramContexts();
    }
}
