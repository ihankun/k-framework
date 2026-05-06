package io.hankun.framework.ai.store.vector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.EmbeddingUtils;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionConverter;
import org.springframework.ai.vectorstore.milvus.MilvusSearchRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @className: AbstractKvStore
 * @createAt: 2025/9/3 15:14
 * @author: hankun
 */
@Slf4j
public abstract class AbstractKvStore<V> {

    public static final String VALUE = "value";

    public static final String EMBEDDING = "embedding";

    public static final String KEY = "key";

    public static final String SIMILARITY_FIELD_NAME = "score";

    private final MilvusServiceClient milvusClient;

    private final EmbeddingModel embeddingModel;

    public final FilterExpressionConverter filterExpressionConverter = new StoreExpressionConverter(AbstractKvStore.VALUE);

    protected AbstractKvStore(MilvusServiceClient milvusClient,
                              EmbeddingModel embeddingModel) {
        this.milvusClient = milvusClient;
        this.embeddingModel = embeddingModel;
        ensureCollection();
    }

    public abstract String collectionName();

    public abstract String collectionDescription();

    public abstract Class<V> valueClass();

    public String embeddingKey(String key, V value) {
        return key;
    }

    public int maxKeyLength() {
        return 36;
    }

    public MetricType metricType() {
        return MetricType.COSINE;
    }


    public String databaseName() {
        return "default";
    }

    public List<VectorSearchResult<V>> search(SearchRequest request) {
        String nativeFilterExpressions;
        String searchParamsJson = null;
        if (request instanceof MilvusSearchRequest milvusReq) {
            nativeFilterExpressions = StringUtils.hasText(milvusReq.getNativeExpression())
                    ? milvusReq.getNativeExpression() : getConvertedFilterExpression(request);

            searchParamsJson = StringUtils.hasText(milvusReq.getSearchParamsJson()) ? milvusReq.getSearchParamsJson()
                    : null;
        } else {
            nativeFilterExpressions = getConvertedFilterExpression(request);
        }

        Assert.notNull(request.getQuery(), "Query string must not be null");
        List<String> outFieldNames = new ArrayList<>();
        outFieldNames.add(KEY);
        outFieldNames.add(VALUE);
        float[] embedding = this.embeddingModel.embed(request.getQuery());

        var searchParamBuilder = SearchParam.newBuilder()
                .withDatabaseName(databaseName())
                .withCollectionName(collectionName())
                .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                .withMetricType(metricType())
                .withOutFields(outFieldNames)
                .withTopK(request.getTopK())
                .withFloatVectors(List.of(EmbeddingUtils.toList(embedding)))
                .withVectorFieldName(EMBEDDING);

        if (StringUtils.hasText(nativeFilterExpressions)) {
            searchParamBuilder.withExpr(nativeFilterExpressions);
        }

        if (StringUtils.hasText(searchParamsJson)) {
            searchParamBuilder.withParams(searchParamsJson);
        }

        R<SearchResults> respSearch = this.milvusClient.search(searchParamBuilder.build());

        if (respSearch.getException() != null) {
            throw new RuntimeException("Search failed!", respSearch.getException());
        }

        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());

        return wrapperSearch.getRowRecords(0)
                .stream()
                .filter(rowRecord -> getResultSimilarity(rowRecord) >= request.getSimilarityThreshold())
                .map(rowRecord -> {
                    String key = String.valueOf(rowRecord.get(KEY));
                    JsonObject value = (JsonObject) rowRecord.get(VALUE);
                    float score = getResultSimilarity(rowRecord);
                    Gson gson = new Gson();
                    return new VectorSearchResult<>(key, gson.fromJson(value, valueClass()), score);
                })
                .toList();
    }

    private String getConvertedFilterExpression(SearchRequest request) {
        return (request.getFilterExpression() != null)
                ? this.filterExpressionConverter.convertExpression(request.getFilterExpression()) : "";
    }

    private float getResultSimilarity(QueryResultsWrapper.RowRecord rowRecord) {
        Float score = (Float) rowRecord.get(SIMILARITY_FIELD_NAME);
        return (metricType() == MetricType.IP || metricType() == MetricType.COSINE) ? score : (1 - score);
    }

    public List<V> query(Filter.Expression expression, Long limit, Long offset) {
        return doQuery(filterExpressionConverter.convertExpression(expression), limit, offset);
    }

    public List<V> queryAll(Long limit, Long offset) {
        return doQuery("", limit, offset);
    }

    public List<V> queryByKey(List<String> keys) {
        String expression = String.format("%s in [%s]", KEY,
                keys.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")));
        return doQuery(expression, 0L, 0L);
    }

    public List<V> doQuery(String expression, Long limit, Long offset) {
        List<String> outFieldNames = new ArrayList<>();
        outFieldNames.add(KEY);
        outFieldNames.add(VALUE);
        QueryParam.Builder builder = QueryParam.newBuilder()
                .withCollectionName(collectionName())
                .withDatabaseName(databaseName())
                .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                .withOutFields(outFieldNames)
                .withLimit(limit)
                .withOffset(offset);
        if (StringUtils.hasText(expression)) {
            builder.withExpr(expression);
        }
        R<QueryResults> respSearch = milvusClient.query(builder.build());
        if (respSearch.getException() != null) {
            throw new RuntimeException("Search failed!", respSearch.getException());
        }
        QueryResultsWrapper wrapper = new QueryResultsWrapper(respSearch.getData());
        return wrapper.getRowRecords()
                .stream()
                .map(rowRecord -> {
                    JsonObject value = (JsonObject) rowRecord.get(VALUE);
                    Gson gson = new Gson();
                    return gson.fromJson(value, valueClass());
                }).toList();
    }

    public void insert(String key, V value) {
        insert(Map.of(key, value));
    }

    public void upsert(String key, V value) {
        upsert(Map.of(key, value));
    }

    public void insert(Map<String, V> datas) {
        List<InsertParam.Field> fields = buildInsertField(datas);
        InsertParam insertParam = InsertParam.newBuilder()
                .withDatabaseName(databaseName())
                .withCollectionName(collectionName())
                .withFields(fields)
                .build();
        R<MutationResult> status = this.milvusClient.insert(insertParam);
        if (status.getException() != null) {
            throw new RuntimeException("Failed to insert:", status.getException());
        }
    }

    public void upsert(Map<String, V> datas) {
        List<InsertParam.Field> fields = buildInsertField(datas);
        UpsertParam upsertParam = UpsertParam.newBuilder()
                .withDatabaseName(databaseName())
                .withCollectionName(collectionName())
                .withFields(fields)
                .build();
        R<MutationResult> status = this.milvusClient.upsert(upsertParam);
        if (status.getException() != null) {
            throw new RuntimeException("Failed to insert:", status.getException());
        }
    }

    @NotNull
    private List<InsertParam.Field> buildInsertField(Map<String, V> datas) {
        List<String> keyArray = new ArrayList<>();
        List<List<Float>> embeddingArray = new ArrayList<>();
        List<JsonElement> valueArray = new ArrayList<>();
        for (Map.Entry<String, V> entry : datas.entrySet()) {
            keyArray.add(entry.getKey());
            embeddingArray.add(EmbeddingUtils.toList(embeddingModel.embed(
                    embeddingKey(entry.getKey(), entry.getValue()))));
            Gson gson = new Gson();
            valueArray.add(gson.toJsonTree(entry.getValue()));
        }
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(KEY, keyArray));
        fields.add(new InsertParam.Field(EMBEDDING, embeddingArray));
        fields.add(new InsertParam.Field(VALUE, valueArray));
        return fields;
    }

    public void delete(Collection<String> keys) {
        String deleteExpression = String.format("%s in [%s]", KEY,
                keys.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")));
        long deleteCount = doDelete(deleteExpression);
        if (deleteCount != keys.size()) {
            log.warn("Deleted only {} entries from requested {} ", deleteCount, keys.size());
        }
    }

    public void delete(Filter.Expression expression) {
        doDelete(this.filterExpressionConverter.convertExpression(expression));
    }

    public long doDelete(String expression) {
        R<MutationResult> status = milvusClient.delete(DeleteParam.newBuilder()
                .withDatabaseName(databaseName())
                .withCollectionName(collectionName())
                .withExpr(expression)
                .build());
        if (status.getStatus() != R.Status.Success.getCode()) {
            throw new IllegalStateException("Failed to delete documents by filter: " + status.getMessage());
        }
        return status.getData().getDeleteCnt();
    }


    public void ensureCollection() {
        Boolean hasCollection = milvusClient.hasCollection(HasCollectionParam.newBuilder().
                withCollectionName(collectionName()).withDatabaseName(databaseName()).build()).getData();
        if (!hasCollection) {


            FieldType key = FieldType.newBuilder()
                    .withName(KEY)
                    .withDataType(DataType.VarChar)
                    .withPrimaryKey(true)
                    .withMaxLength(maxKeyLength())
                    .build();
            FieldType value = FieldType.newBuilder()
                    .withName(VALUE)
                    .withDataType(DataType.JSON)
                    .build();
            FieldType embedding = FieldType.newBuilder()
                    .withName(EMBEDDING)
                    .withDataType(DataType.FloatVector)
                    .withDimension(embeddingModel.dimensions())
                    .build();

            CollectionSchemaParam schemaParam = CollectionSchemaParam.newBuilder()
                    .addFieldType(key)
                    .addFieldType(value)
                    .addFieldType(embedding)
                    .build();

            CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName())
                    .withDatabaseName(databaseName())
                    .withDescription(collectionDescription())
                    .withSchema(schemaParam)
                    .build();
            R<RpcStatus> status = milvusClient.createCollection(createCollectionParam);
            if (status.getStatus() != R.Status.Success.getCode()) {
                throw new IllegalStateException("Failed to create collection: " + status.getMessage());
            }
            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName())
                    .withFieldName(EMBEDDING)
                    .withIndexName("vector_index_of_key")
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(metricType())
                    .withExtraParam("{\"nlist\":1024}")
                    .build();
            R<RpcStatus> indexStatus = milvusClient.createIndex(indexParam);
            if (indexStatus.getStatus() != R.Status.Success.getCode()) {
                throw new IllegalStateException("Failed to create index: " + indexStatus.getMessage());
            }
            R<RpcStatus> loadStatus = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName())
                    .withDatabaseName(databaseName())
                    .build());
            if (loadStatus.getStatus() != R.Status.Success.getCode()) {
                throw new IllegalStateException("Failed to load collection: " + loadStatus.getMessage());
            }
        }
    }
}
