package io.hankun.framework.ai.tools.select;

import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.tools.client.KChatRequest;
import io.hankun.framework.ai.tools.model.AiFunction;
import io.hankun.framework.ai.tools.select.entity.IAiSelectOption;
import io.hankun.framework.ai.tools.select.entity.SelectOptionInfo;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AiSelector
 * @createAt: 2025/9/2 16:12
 * @author: hankun
 */
public class AiSelector<T extends IAiSelectOption> {

    public static final String KEY_INFO = "key";

    public static final String KEY_DESC = "desc";

    public static final String QUERY_KEY = "query";

    private final KChatRequest kChatRequest;

    public AiSelector(KChatRequest kChatRequest) {
        this.kChatRequest = kChatRequest;
    }

    public List<SelectOptionInfo<T>> select(AiSelectConfig config,
                                            List<SelectOptionInfo<T>> options,
                                            String query) {
        AiFunction aiFunction = new AiFunction(kChatRequest, config.getChoosePrompt());
        Map<String, Object> params = new HashMap<>(config.getChoosePromptParams());
        params.put(KEY_INFO, buildKeys(options));
        params.put(KEY_DESC, buildDesc(options, config));
        params.put(QUERY_KEY, query);
        Map<String, SelectOptionInfo<T>> selectOptionInfoMap = new HashMap<>(options.size());
        for (SelectOptionInfo<T> option : options) {
            selectOptionInfoMap.put(option.key(), option);
        }
        String result;
        if (config.getChooseStreamCall()) {
            result = aiFunction.streamWait(config.getChooseOptions(), params);
        } else {
            result = aiFunction.call(config.getChooseOptions(), params);
        }
        if (ObjectUtils.isEmpty(result)) {
            return null;
        }
        SelectOptionInfo<T> dirResult = selectOptionInfoMap.get(result);
        if (dirResult != null) {
            return List.of(dirResult);
        }
        result = result.trim();
        if (!result.startsWith("[")) {
            return null;
        }
        List<String> chooseResults = JSON.parseArray(result, String.class);
        List<SelectOptionInfo<T>> results = new ArrayList<>(chooseResults.size());
        for (String chooseResult : chooseResults) {
            SelectOptionInfo<T> selectOptionInfo = selectOptionInfoMap.get(chooseResult);
            if (selectOptionInfo != null) {
                results.add(selectOptionInfo);
            }
        }
        return results;
    }

    private String buildKeys(List<SelectOptionInfo<T>> options) {
        List<String> keys = new ArrayList<>(options.size());
        for (SelectOptionInfo<T> option : options) {
            keys.add(option.key());
        }
        return JSON.toJSONString(keys);
    }

    private String buildDesc(List<SelectOptionInfo<T>> options, AiSelectConfig config) {
        StringBuilder sb = new StringBuilder();
        for (SelectOptionInfo<T> option : options) {
            sb.append("\n    ").
                    append(config.getMatchInfoFormatter().apply(option.key(), option.desc()));
        }
        sb.append("\n");
        return sb.toString();
    }

}
