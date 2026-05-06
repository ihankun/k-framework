package io.hankun.framework.ai.tools.select;

import io.hankun.framework.ai.model.MsunModelManager;
import io.hankun.framework.ai.model.rerank.MsunDocumentWithScore;
import io.hankun.framework.ai.model.rerank.MsunRerankModel;
import io.hankun.framework.ai.tools.advisors.AdvisorConfig;
import io.hankun.framework.ai.tools.client.ClientConfig;
import io.hankun.framework.ai.tools.client.KChatRequest;
import io.hankun.framework.ai.tools.client.KChatService;
import io.hankun.framework.ai.tools.select.entity.IAiSelectOption;
import io.hankun.framework.ai.tools.select.entity.SelectOptionInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @description:
 * @className: AiSelectService
 * @createAt: 2025/9/2 16:13
 * @author: hankun
 */
@Slf4j
@Component
public class AiSelectService {

    private final MsunModelManager msunModelManager;

    private final KChatRequest kChatRequest;

    public AiSelectService(MsunModelManager msunModelManager,
                           KChatService kChatService) {
        this.msunModelManager = msunModelManager;
        this.kChatRequest = kChatService.buildRequest(new ClientConfig(AdvisorConfig.buildSimple()));
    }

    public MsunRerankModel getRerankModel() {
        return msunModelManager.getRerankModel();
    }


    /**
     * 通过模型进行选择
     *
     * @param options 可选项
     * @param config  配置
     * @param query   查询
     * @param <T>     选项类型
     * @return 选中的目标选项
     */
    public <T extends IAiSelectOption> AiSelectResult<T> select(List<T> options, AiSelectConfig config, String query) {
        if (options.isEmpty()) {
            log.warn("【ai选择】无可用选项，query={}", query);
            return AiSelectResult.notFind(null);
        }
        if (options.size() == 1) {
            T option = options.getFirst();
            if (!config.getAllowNotFind()) {
                log.info("【ai选择】唯一选项，且必须选择，query={},key={},desc={}",
                        query, option.key(), option.desc());
                return AiSelectResult.onlyOne(option);
            } else {
                if (Objects.equals(option.key(), config.getNotFindKey())) {
                    log.info("【ai选择】唯一选项,且该选择为未找到，query={},key={},desc={}",
                            query, option.key(), option.desc());
                    return AiSelectResult.notFind(option);
                }
            }
        }
        Map<String, SelectOptionInfo<T>> selectOptionInfoMap = new HashMap<>(options.size());
        List<SelectOptionInfo<T>> selectOptionInfos = new ArrayList<>(options.size());

        SelectOptionInfo<T> notFind = null;
        String notFindKey = config.getNotFindKey();
        if (ObjectUtils.isEmpty(notFindKey)) {
            notFindKey = "不存在选项";
        }
        SelectOptionInfo<T> direct = null;
        List<Document> documents = new ArrayList<>(options.size());
        for (T option : options) {
            SelectOptionInfo<T> selectOptionInfo = SelectOptionInfo.of(option, config);
            selectOptionInfoMap.put(selectOptionInfo.key(), selectOptionInfo);
            documents.add(buildDocument(selectOptionInfo));
            selectOptionInfos.add(selectOptionInfo);
            if (selectOptionInfo.key().equals(notFindKey)) {
                notFind = selectOptionInfo;
            }
            direct = directMatch(config, query, selectOptionInfo);
            if (direct != null) {
                break;
            }
        }
        if (notFind == null) {
            String desc = "当其他选项都不对的情况下选择";
            notFind = SelectOptionInfo.ofEmpty(notFindKey, desc, config);
        }
        if (direct != null) {
            log.info("【ai选择】直接匹配，query={}，matchInfo={}", query, direct.matchInfo());
            return AiSelectResult.directMatch(direct.value());
        }
        if (config.getEnableRerank()) {
            return selectWithRerank(config, query, documents, selectOptionInfoMap, notFind);
        } else {
            return aiSelectResult(config, query, selectOptionInfos, notFind);

        }
    }

    private static <T extends IAiSelectOption> SelectOptionInfo<T> directMatch(AiSelectConfig config, String query,
                                                                               SelectOptionInfo<T> selectOptionInfo) {
        if (selectOptionInfo.key().equals(query)) {
            return selectOptionInfo;
        } else if (selectOptionInfo.desc().equals(query)) {
            return selectOptionInfo;
        } else if (config.getKeyFormatter().apply(selectOptionInfo.key()).equals(query)) {
            return selectOptionInfo;
        } else if (config.getDescFormatter().apply(selectOptionInfo.desc()).equals(query)) {
            return selectOptionInfo;
        } else if (buildMatchInfo(config, selectOptionInfo).equals(query)) {
            return selectOptionInfo;
        }
        return null;
    }

    private <T extends IAiSelectOption> @NotNull AiSelectResult<T> selectWithRerank(AiSelectConfig config,
                                                                                    String query,
                                                                                    List<Document> documents,
                                                                                    Map<String, SelectOptionInfo<T>> selectOptionInfoMap,
                                                                                    SelectOptionInfo<T> notFind) {
        MsunRerankModel rerankModel = getRerankModel();
        List<MsunDocumentWithScore> rerankResults = rerankModel.rerank(query, documents, config.getRerankTopK());
        List<SelectOptionInfo<T>> results = new ArrayList<>(config.getRerankTopK());
        for (MsunDocumentWithScore document : rerankResults) {
            SelectOptionInfo<T> selectOptionInfo = selectOptionInfoMap.get(document.document().getId());
            if (document.score() > config.getDirectChooseThreshold()) {
                log.info("【ai选择】rerank匹配，选项评分高于命中阈值，query={}，rerankInfo={},score={},threshold={}",
                        query, selectOptionInfo.rerankInfo(), document.score(), config.getRerankThreshold());
                return AiSelectResult.rerankMatch(selectOptionInfo.value());
            }
            if (document.score() > config.getRerankThreshold()) {
                log.info("【ai选择】rerank过滤，选项评分高于rerank阈值，query={}，rerankInfo={},score={},threshold={}",
                        query, selectOptionInfo.rerankInfo(), document.score(), config.getRerankThreshold());
                results.add(selectOptionInfo);
            }
        }
        if (results.isEmpty()) {
            if (!config.getAllowNotFind()) {
                log.info("【ai选择】无rerank结果，query={}", query);
                return AiSelectResult.notFind(null);
            } else {
                log.info("【ai选择】无rerank结果，且允许未找到，query={}，rerankInfo={}", query, notFind.rerankInfo());
                return AiSelectResult.notFind(notFind.value());
            }
        }
        return aiSelectResult(config, query, results, notFind);
    }

    private <T extends IAiSelectOption> @NotNull AiSelectResult<T> aiSelectResult(AiSelectConfig config, String query,
                                                                                  List<SelectOptionInfo<T>> results,
                                                                                  SelectOptionInfo<T> notFind) {
        if (!config.getEnableChoose()) {
            return AiSelectResult.rerankMatch(results.getFirst().value());
        }
        String notFindKey = config.getNotFindKey();
        KChatRequest request = config.getKChatRequest();
        if (request == null) {
            request = kChatRequest;
        }
        AiSelector<T> selector = new AiSelector<>(request);
        if (config.getAllowNotFind()) {
            boolean includeNotFind = false;
            for (SelectOptionInfo<T> result : results) {
                if (Objects.equals(result.key(), notFindKey)) {
                    includeNotFind = true;
                    break;
                }
            }
            if (!includeNotFind) {
                results.add(notFind);
            }
        }
        List<SelectOptionInfo<T>> aiResult = selector.select(config, results, query);
        if (CollectionUtils.isEmpty(aiResult)) {
            log.error("【ai选择】ai选择失败，query={}", query);
            return AiSelectResult.aiFailed();
        }
        if (aiResult.size() == 1 && Objects.equals(aiResult.getFirst().key(), notFindKey)) {
            SelectOptionInfo<T> first = aiResult.getFirst();
            log.info("【ai选择】ai选择未找到，query={}，matchInfo={}", query, first.matchInfo());
            return AiSelectResult.notFind(first.value());
        }
        List<T> result = new ArrayList<>(aiResult.size());
        for (SelectOptionInfo<T> data : aiResult) {
            log.info("【ai选择】ai选择结果，query={}，matchInfo={}", query, data.matchInfo());
            result.add(data.value());
        }
        return AiSelectResult.aiChoose(result);
    }

    public <T extends IAiSelectOption> Document buildDocument(SelectOptionInfo<T> selectOptionInfo) {
        return new Document(selectOptionInfo.key(), selectOptionInfo.rerankInfo(), Map.of());
    }

    public static <T extends IAiSelectOption> String buildMatchInfo(AiSelectConfig config, SelectOptionInfo<T> selectOptionInfo) {
        return config.getMatchInfoFormatter().apply(selectOptionInfo.key(), selectOptionInfo.desc());
    }

    public static <T extends IAiSelectOption> String buildRerankInfo(AiSelectConfig config, SelectOptionInfo<T> selectOptionInfo) {
        return config.getRerankInfoFormatter().apply(selectOptionInfo.key(), selectOptionInfo.desc());
    }
}
