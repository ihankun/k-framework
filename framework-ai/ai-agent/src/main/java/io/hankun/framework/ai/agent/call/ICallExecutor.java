package io.hankun.framework.ai.agent.call;

import io.hankun.framework.ai.agent.call.entity.AgentCallParams;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public interface ICallExecutor {


    String SENTENCE_END = "sentenceEnd";

    String ttsMark();

    String asrMark();

    String code();

    Flux<NodeResultData> init(KCallExecutor callExecutor);

    void receiveData(String dataString, KCallExecutor callExecutor);

    default void input(String input, boolean sentenceEnd) {
        input(input, sentenceEnd, Map.of());
    }

    default void input(String input, boolean sentenceEnd, Map<String, Object> params) {
        Map<String, Object> paramsMap;
        if (CollectionUtils.isEmpty(params)) {
            paramsMap = Map.of(SENTENCE_END, sentenceEnd);
        } else {
            paramsMap = new HashMap<>(params.size() + 1);
            paramsMap.putAll(params);
            paramsMap.put(SENTENCE_END, sentenceEnd);
        }
        input(input, paramsMap);
    }

    /**
     * 输入字节数据，默认情况下，第一个字节为类型，第二个字节开始为数据
     * 类型为1，表示输入音频数据
     *
     * @param input        输入数据
     * @param callExecutor 执行器
     */
    default void inputBytes(ByteBuffer input, KCallExecutor callExecutor) {
        byte code = input.get();
        ByteBuffer data = input.slice();
        if (code == 1) {
            callExecutor.inputAudio(data);
        } else {
            input(code, data);
        }
    }

    /**
     * 处理字节数据
     *
     * @param code 数据类型
     * @param data 数据
     */
    default void input(byte code, ByteBuffer data) {

    }

    default void input(String input, Map<String, Object> params) {
        input(new AgentCallParams(input, params));
    }

    default boolean isSentenceEnd(AgentCallParams params) {
        Object sentenceEnd = params.params().get(SENTENCE_END);
        if (sentenceEnd == null) {
            return false;
        }
        if (sentenceEnd instanceof Boolean booleanValue) {
            return booleanValue;
        }
        return false;
    }

    void input(AgentCallParams params);

    void finish();
}
