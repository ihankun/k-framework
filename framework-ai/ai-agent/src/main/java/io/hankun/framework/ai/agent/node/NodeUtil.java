package io.hankun.framework.ai.agent.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.fastjson2.JSON;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.node.entity.DataWithMetaContext;
import io.hankun.framework.ai.core.entity.CurrentId;
import io.hankun.framework.ai.context.entity.InputParams;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: NodeUtil
 * @createAt: 2025/10/24 10:39
 * @author: hankun
 */
public class NodeUtil {

    public static final String RESULT = "result";
    public static final String INTENTION = "intention";
    public static final String INPUT = "input";
    public static final String CURRENT_ID = "currentId";


    public static CurrentId getCurrentId(InputParams inputParams) {
        return inputParams.get(CURRENT_ID, CurrentId.class);
    }

    public static CurrentId getCurrentId(OverAllState state) {
        Object currentIdObj = state.value(CURRENT_ID, Object.class).orElse(null);
        if (currentIdObj == null) {
            throw new IllegalArgumentException("currentId is null");
        }
        if (currentIdObj instanceof CurrentId) {
            return (CurrentId) currentIdObj;
        }
        return JSON.parseObject(JSON.toJSONBytes(currentIdObj), CurrentId.class);
    }

    public static Map<String, Object> result(List<DataWithMeta> dataWithMetaList) {
        return Map.of(RESULT, new DataWithMetaContext(dataWithMetaList));
    }


    public static String getInput(OverAllState state) {
        String input = state.value(INPUT, String.class).orElse(null);
        if (input == null) {
            throw new IllegalArgumentException("formatInput is null");
        }
        return input;
    }

//    public static DataWithMeta getPreResult(InputParams inputParams, CurrentId currentId) {
//        List<DataWithMetaContext> result = inputParams.getList(RESULT, DataWithMetaContext.class);
//        if (CollectionUtils.isEmpty(result)) {
//            return null;
//        }
//        List<DataWithMeta> dataWithMetaList = new ArrayList<>();
//        for (DataWithMetaContext dataWithMetaContext : result) {
//            for (DataWithMeta dataWithMeta : dataWithMetaContext.dataWithMetaList()) {
//                if (dataWithMeta.nodeId().equals(currentId.beforeNodeId())) {
//                    dataWithMetaList.add(dataWithMeta);
//                }
//            }
//        }
//        return DataWithMeta.combine(dataWithMetaList, currentId.beforeNodeId());
//    }
}
