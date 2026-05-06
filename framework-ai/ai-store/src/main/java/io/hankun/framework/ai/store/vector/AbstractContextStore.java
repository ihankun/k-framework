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
import java.util.stream.Collectors;

/**
 * @description:
 * @className: AbstractContextStore
 * @createAt: 2025/12/4 11:30
 * @author: hankun
 */
@Slf4j
public abstract class AbstractContextStore<M> {

    public static final String ID = "id";

    public static final String CONTEXT = "context";

    public static final String META = "meta";

    public static final String EMBEDDING = "embedding";

    public static final String SIMILARITY_FIELD_NAME = "score";

    private final MilvusServiceClient milvusClient;

    private final EmbeddingModel embeddingModel;

    public final FilterExpressionConverter filterExpressionConverter;

    protected AbstractContextStore(MilvusServiceClient milvusClient,
                                   EmbeddingModel embeddingModel) {
        this.milvusClient = milvusClient;
        this.embeddingModel = embeddingModel;
        this.filterExpressionConverter = new StoreExpressionConverter(META);
        ensureCollection();
    }

    public abstract String collectionName();

    public abstract String collectionDescription();

    public abstract Class<M> metaClass();

    public String embeddingKey(ContextStoreData<M> context) {
        return context.context();
    }

    public int maxContextLength() {
        return 512;
    }

    public int idLength() {
        return 24;
    }

    public MetricType metricType() {
        return MetricType.COSINE;
    }


    public String databaseName() {
        return "default";
    }

    public List<ContextSearchResult<M>> search(SearchRequest request) {
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
        List<String> outFieldNames = fieldList();
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
                    ContextStoreData<M> contextStoreData = convertData(rowRecord);
                    float score = getResultSimilarity(rowRecord);
                    return new ContextSearchResult<>(contextStoreData, score);
                })
                .toList();
    }

    @NotNull
    private static List<String> fieldList() {
        List<String> outFieldNames = new ArrayList<>();
        outFieldNames.add(ID);
        outFieldNames.add(CONTEXT);
        outFieldNames.add(META);
        return outFieldNames;
    }

    @NotNull
    private ContextStoreData<M> convertData(QueryResultsWrapper.RowRecord rowRecord) {
        String id = String.valueOf(rowRecord.get(ID));
        String context = String.valueOf(rowRecord.get(CONTEXT));
        JsonObject value = (JsonObject) rowRecord.get(META);
        Gson gson = new Gson();
        return new ContextStoreData<>(id, context, gson.fromJson(value, metaClass()));
    }


    private String getConvertedFilterExpression(SearchRequest request) {
        return (request.getFilterExpression() != null)
                ? this.filterExpressionConverter.convertExpression(request.getFilterExpression()) : "";
    }

    private float getResultSimilarity(QueryResultsWrapper.RowRecord rowRecord) {
        Float score = (Float) rowRecord.get(SIMILARITY_FIELD_NAME);
        return (metricType() == MetricType.IP || metricType() == MetricType.COSINE) ? score : (1 - score);
    }

    public List<ContextStoreData<M>> query(Filter.Expression expression, Long limit, Long offset) {
        return doQuery(filterExpressionConverter.convertExpression(expression), limit, offset);
    }

    public List<ContextStoreData<M>> queryAll(Long limit, Long offset) {
        return doQuery("", limit, offset);
    }

    public List<ContextStoreData<M>> queryByIds(List<String> ids) {
        String expression = String.format("%s in [%s]", ID,
                ids.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")));
        return doQuery(expression, 0L, 0L);
    }

    public List<ContextStoreData<M>> doQuery(String expression, Long limit, Long offset) {
        List<String> outFieldNames = fieldList();
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
                .map(this::convertData).toList();
    }

    public void insert(ContextStoreData<M> data) {
        insert(List.of(data));
    }

    public void upsert(ContextStoreData<M> data) {
        upsert(List.of(data));
    }

    public void insert(Collection<ContextStoreData<M>> datas) {
        List<InsertParam.Field> fields = buildInsertField(datas, false);
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

    public void upsert(Collection<ContextStoreData<M>> datas) {
        List<InsertParam.Field> fields = buildInsertField(datas, true);
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
    private List<InsertParam.Field> buildInsertField(Collection<ContextStoreData<M>> datas, boolean withId) {
        List<String> idArray = new ArrayList<>();
        List<String> contextArray = new ArrayList<>();
        List<List<Float>> embeddingArray = new ArrayList<>();
        List<JsonElement> valueArray = new ArrayList<>();
        for (ContextStoreData<M> data : datas) {
            idArray.add(data.id());
            contextArray.add(data.context());
            embeddingArray.add(EmbeddingUtils.toList(embeddingModel.embed(
                    embeddingKey(data))));
            Gson gson = new Gson();
            valueArray.add(gson.toJsonTree(data.meta()));
        }
        List<InsertParam.Field> fields = new ArrayList<>();
        if (withId) {
            fields.add(new InsertParam.Field(ID, idArray));
        }
        fields.add(new InsertParam.Field(CONTEXT, contextArray));
        fields.add(new InsertParam.Field(EMBEDDING, embeddingArray));
        fields.add(new InsertParam.Field(META, valueArray));
        return fields;
    }

    public void delete(Collection<String> keys) {
        String deleteExpression = String.format("%s in [%s]", ID,
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
            FieldType id = FieldType.newBuilder()
                    .withName(ID)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(idLength())
                    .withPrimaryKey(true)
                    .build();

            FieldType context = FieldType.newBuilder()
                    .withName(CONTEXT)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(maxContextLength())
                    .build();
            FieldType meta = FieldType.newBuilder()
                    .withName(META)
                    .withDataType(DataType.JSON)
                    .build();
            FieldType embedding = FieldType.newBuilder()
                    .withName(EMBEDDING)
                    .withDataType(DataType.FloatVector)
                    .withDimension(embeddingModel.dimensions())
                    .build();

            CollectionSchemaParam schemaParam = CollectionSchemaParam.newBuilder()
                    .addFieldType(id)
                    .addFieldType(context)
                    .addFieldType(meta)
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
//            indexParam = CreateIndexParam.newBuilder()
//                    .withCollectionName(collectionName())
//                    .withFieldName(ID)
//                    .withIndexName("id_index")
//                    .build();
//            indexStatus = milvusClient.createIndex(indexParam);
//            if (indexStatus.getStatus() != R.Status.Success.getCode()) {
//                throw new IllegalStateException("Failed to create index: " + indexStatus.getMessage());
//            }
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
