package io.hankun.framework.ai.tools.select;

import io.hankun.framework.ai.tools.client.KChatRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @description:
 * @className: AiSelectConfig
 * @createAt: 2025/9/2 16:40
 * @author: hankun
 */
@Setter
@Getter
public class AiSelectConfig {

    private Integer rerankTopK;

    private Float rerankThreshold;

    private Float directChooseThreshold;

    private String choosePrompt;

    private Map<String, Object> choosePromptParams;

    private Boolean chooseStreamCall;

    private ChatOptions chooseOptions;

    private Boolean enableRerank = true;

    private Boolean enableChoose = true;

    private Boolean allowNotFind = false;

    private String notFindKey;

    private String keyName = "选项";

    private String descName = "选项描述";

    private Function<String, String> keyFormatter = this::defBuildKey;

    private Function<String, String> descFormatter = this::defBuildDesc;

    private BiFunction<String, String, String> matchInfoFormatter = this::defBuildMatchInfo;

    private BiFunction<String, String, String> rerankInfoFormatter = this::defBuildMatchInfo;

    private KChatRequest kChatRequest;

    public void setRerankConfig(Integer rerankTopK, Float rerankThreshold, Float directChooseThreshold) {
        this.rerankTopK = rerankTopK;
        this.rerankThreshold = rerankThreshold;
        this.directChooseThreshold = directChooseThreshold;
    }

    public String defBuildMatchInfo(String key, String desc) {
        return buildMatchInfo(keyName, key, descName, desc);
    }

    public String defBuildDesc(String desc) {
        return buildDesc(descName, desc);
    }

    public String defBuildKey(String key) {
        return buildKey(keyName, key);
    }

    public static String buildMatchInfo(String keyName, String key, String descName, String desc) {
        return buildKey(keyName, key) + "，" + buildDesc(descName, desc);
    }


    public static String buildDesc(String descName, String desc) {
        return descName + ":" + desc;
    }

    public static String buildKey(String keyName, String key) {
        return keyName + ":" + key;
    }
}
