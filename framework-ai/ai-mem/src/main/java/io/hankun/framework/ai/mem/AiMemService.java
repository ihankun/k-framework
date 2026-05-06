package io.hankun.framework.ai.mem;

import io.hankun.framework.ai.mem.config.AiMemConfig;
import io.hankun.framework.ai.mem.config.MemSummaryConfig;
import io.hankun.framework.ai.mem.entity.MemData;
import io.hankun.framework.ai.mem.entity.MemDataVo;
import io.hankun.framework.ai.mem.entity.MemMeta;
import io.hankun.framework.ai.mem.entity.MemType;
import io.hankun.framework.ai.mem.hsitory.MemHistoryService;
import io.hankun.framework.ai.mem.store.MemVectorStore;
import io.hankun.framework.ai.mem.summary.MemFactDef;
import io.hankun.framework.ai.mem.summary.SummaryUpdater;
import io.hankun.framework.ai.store.vector.ContextSearchResult;
import io.hankun.framework.ai.store.vector.ContextStoreData;
import org.bson.types.ObjectId;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @className: AiMemService
 * @createAt: 2025/12/4 10:43
 * @author: hankun
 */
@Component
public class AiMemService {

    private final MemVectorStore memVectorStore;

    private final MemHistoryService memHistoryService;

    private final AiMemConfig aiMemConfig;

    private final Map<String, MemFactDef> memFactDefs;

    public AiMemService(MemVectorStore memVectorStore,
                        MemHistoryService memHistoryService,
                        AiMemConfig aiMemConfig,
                        List<MemFactDef> memFactDefList) {
        this.memVectorStore = memVectorStore;
        this.memHistoryService = memHistoryService;
        this.aiMemConfig = aiMemConfig;
        this.memFactDefs = new HashMap<>(memFactDefList.size());
        for (MemFactDef memFactDef : memFactDefList) {
            memFactDefs.put(memFactDef.tag(), memFactDef);
        }
    }


    public void addFact(String uniqueId, MemSummaryConfig memSummaryConfig, SummaryUpdater summaryUpdater) {
        MemFactDef memFactDef = memFactDefs.get(memSummaryConfig.getTag());
        if (memFactDef == null) {
            throw new IllegalArgumentException("tag不存在");
        }
        MemData summary = getSummary(uniqueId, memSummaryConfig.getTag());
        boolean newSummary = false;
        if (summary == null) {
            summary = buildEmptySummary(uniqueId, memSummaryConfig.getTag(), new HashMap<>());
            summary.meta().setUpdateTime(new Date());
            newSummary = true;
        }
        List<MemData> history = memFactDef.loadFactBefore(uniqueId, memSummaryConfig.getTag(), memSummaryConfig.getSummaryCount(),
                summary.meta().getUpdateTime());
        List<MemData> facts = memFactDef.loadFactAfter(uniqueId, memSummaryConfig.getTag(), summary.meta().getUpdateTime());
        MemData memData;
        if (newSummary) {
            memData = summaryUpdater.updateSummary(history, List.of(), summary);
        } else {
            memData = summaryUpdater.updateSummary(facts, history, summary);
        }
        memHistoryService.save(memData);
        memVectorStore.upsert(convert(memData));
    }

    public void addFact(String uniqueId, String content, MemSummaryConfig memSummaryConfig,
                        Map<String, Object> meta, SummaryUpdater summaryUpdater) {
        if (meta == null) {
            meta = new HashMap<>();
        }
        MemData fact = buildFact(uniqueId, memSummaryConfig.getTag(), content, meta);
        MemData summary = getSummary(uniqueId, memSummaryConfig.getTag());
        List<MemData> history = loadHistory(uniqueId, memSummaryConfig);
        if (summary == null) {
            summary = buildEmptySummary(uniqueId, memSummaryConfig.getTag(), meta);
            summary.meta().setUpdateTime(new Date());
        }
        MemData memData = summaryUpdater.updateSummary(List.of(fact), history, summary);
        memHistoryService.save(fact);
        memHistoryService.save(memData);
        memVectorStore.upsert(convert(memData));
    }

    private List<MemData> loadHistory(String uniqueId, MemSummaryConfig memSummaryConfig) {
        return memHistoryService.loadFact(uniqueId, memSummaryConfig.getTag(), memSummaryConfig.getSummaryCount());
    }

    public MemData getSummary(String uniqueId, String tag) {
        Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.AND
                , uniqueId(uniqueId), new Filter.Expression(Filter.ExpressionType.AND, tag(tag), type(MemType.SUMMARY)));
        List<ContextStoreData<MemMeta>> query = memVectorStore.query(expression, 1L, 0L);
        return query.isEmpty() ? null : convert(query.getFirst());
    }

    public void removeSummary(String uniqueId, String tag) {
        Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.AND
                , uniqueId(uniqueId), new Filter.Expression(Filter.ExpressionType.AND, tag(tag), type(MemType.SUMMARY)));
        memVectorStore.delete(expression);
    }


    public List<MemDataVo> searchSummary(String uniqueId, String content) {
        Filter.Expression expression = new Filter.Expression(Filter.ExpressionType.AND, uniqueId(uniqueId),
                type(MemType.SUMMARY));
        SearchRequest request = SearchRequest.builder()
                .query(content)
                .topK(aiMemConfig.getSummaryTopK())
                .similarityThreshold(aiMemConfig.getSummaryThreshold())
                .filterExpression(expression)
                .build();
        List<ContextSearchResult<MemMeta>> search = memVectorStore.search(request);
        return search.stream().map(AiMemService::convert).toList();
    }

    public static Filter.Expression type(MemType type) {
        return new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("type"), new Filter.Value(type.getCode()));
    }

    public static Filter.Expression tag(String tag) {
        return new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("tag"), new Filter.Value(tag));
    }

    public static Filter.Expression uniqueId(String uniqueId) {
        return new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("uniqueId"), new Filter.Value(uniqueId));
    }

    public static MemData buildFact(String uniqueId, String tag, String context, Map<String, Object> meta) {
        return new MemData(new ObjectId().toString(), context, MemMeta.build(uniqueId, tag, MemType.FACT.getCode(), meta, new Date()));
    }

    public static MemData buildEmptySummary(String uniqueId, String tag, Map<String, Object> meta) {
        return new MemData(new ObjectId().toString(), "", MemMeta.build(uniqueId, tag, MemType.SUMMARY.getCode(), meta, new Date()));
    }

    public static MemDataVo convert(ContextSearchResult<MemMeta> contextSearchResult) {
        MemDataVo memDataVo = new MemDataVo();
        memDataVo.setId(contextSearchResult.data().id());
        memDataVo.setContent(contextSearchResult.data().context());
        memDataVo.setMeta(contextSearchResult.data().meta().buildMap());
        memDataVo.setScore(contextSearchResult.score());
        return memDataVo;
    }

    public static MemData convert(ContextStoreData<MemMeta> contextStoreData) {
        //return new MemData(contextStoreData.id(), contextStoreData.context(), contextStoreData.meta());
        return new MemData(contextStoreData.id(), contextStoreData.context(), contextStoreData.meta());
    }

    public static ContextStoreData<MemMeta> convert(MemData memData) {
        return new ContextStoreData<>(memData.id(), memData.context(), memData.meta());
    }
}
