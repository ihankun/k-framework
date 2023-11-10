package io.ihankun.framework.mongo.handler;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import io.ihankun.framework.mongo.base.PageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author hankun
 */
@Slf4j
@Component
public class MongoHandler {

    /**
     * 默认游标批量查询数量
     */
    private static final Integer DEFAULT_CURSOR_BATCH_SIZE = 1000;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoConverter mongoConverter;

    /**
     * 获取类的集合名
     * @param entityClass   集合映射对象.class
     * @param <T>
     * @return java.lang.String 集合名
     */
    public <T> String getCollectionName(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null!");
        return mongoTemplate.getCollectionName(entityClass);
    }

    /**
     * 保存单个对象到集合中
     * @param saveEntity    集合映射对象
     * @param <T>
     * @return T            保存后的记录
     */
    public <T> T insert(T saveEntity) {
        Assert.notNull(saveEntity, "saveEntity must not be null!");
        return mongoTemplate.insert(saveEntity);
    }

    /**
     * 批量保存对象到集合中
     * @param batchSaveEntity           集合映射对象集合
     * @param <T>
     * @return java.util.Collection<T>  保存后的记录集合
     */
    public <T> Collection<T> insertAll(Collection<? extends T> batchSaveEntity) {
        Assert.notNull(batchSaveEntity, "batchSaveEntity must not be null！");
        return mongoTemplate.insertAll(batchSaveEntity);
    }

    /**
     * 自定义Query 查询满足条件记录
     * @param query
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> List<T> find(Query query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 根据主键查询
     * @param id            mongo生成的ID
     * @param entityClass   集合映射对象
     * @param <T>
     * @return
     */
    public <T> T findById(Object id, Class<T> entityClass) {
        Assert.notNull(id, "id must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return mongoTemplate.findById(id, entityClass);
    }

    /**
     * 自定义Query，查询第一条
     * @param query
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> T findOne(Query query, Class<T> entityClass) {
        Assert.notNull(query, "Query must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return mongoTemplate.findOne(query, entityClass);
    }

    /**
     * 自定义查询条件Map，查询第一条
     * @param accordingMap
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T> T findOne(Map<String, Object> accordingMap, Class<T> entityClass) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        return mongoTemplate.findOne(query, entityClass);
    }

    /**
     * 自定义查询条件Map并排序，查询第一条
     *
     * @param entityClass   集合映射对象
     * @param accordingMap  条件map  Map<条件key, 条件value> accordingMap
     * @param sortField     排序字段
     * @param direction     排序方式  Direction.asc   / Direction.desc
     * @return
     */
    protected  <T> T findOneSort(Class<T> entityClass, Map<String, Object> accordingMap, String sortField, Sort.Direction direction) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        Query query = Query.query(criteria).with(Sort.by(direction, sortField));
        return mongoTemplate.findOne(query, entityClass);
    }

    /**
     * 根据ID排序查询10条
     * @param entityClass
     * @param direction     为null默认Desc排序查询
     * @param <T>
     * @return
     */
    protected  <T> List<T> findSortById(Class<T> entityClass, Sort.Direction direction) {
        Assert.notNull(entityClass, "entityClass must not be null！");

        if (null == direction) {
            direction = Sort.Direction.DESC;
        }

        Query query = new Query().with(Sort.by(direction, "id")).skip(0).limit(10);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 查询返回指定字段
     * @param includeFields  需要返回的指定字段  eg: fields.add("runTime");
     * @param accordingMap   Map<查询条件key, 查询条件value>  eg: map.put("sex", 1);
     * @param entityClass    集合映射对象
     * @param returnId       返回字段的时候id默认为返回，不返回id则field设置
     * @param <T>
     * @return
     */
    protected  <T> List<T> findDesignField(List<String> includeFields, Map<String, Object> accordingMap, Class<T> entityClass, boolean returnId) {
        Assert.notNull(includeFields, "includeFields must not be null！");
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        Query query = new Query(criteriaDefinition(accordingMap));
        includeFields.forEach(f -> query.fields().include(f));
        if (! returnId) {
            query.fields().exclude("id");
        }

        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 查询集合中的所有数据
     *
     * @param entityClass    集合映射对象
     */
    public <T> List<T> findAll(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        return mongoTemplate.findAll(entityClass);
    }

    /**
     * 模糊查询并排序
     *
     * @param field         匹配的参数名称
     * @param key           模糊搜索关键字
     * @param entityClass   集合映射对象
     * @param sortField     排序字段
     * @param direction     Direction.desc /asc 倒序/正序
     * @return List<T>
     **/
    protected  <T> List<T> findLikeByParam(String field, String key, Class<T> entityClass, String sortField, Sort.Direction direction) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(field, "field must not be null！");
        Assert.notNull(key, "key must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");

        Pattern pattern = Pattern.compile("^.*" + key + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where(field).regex(pattern)).with(Sort.by(direction, sortField));
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 根据指定条件Map查询并排序
     * @param entityClass   集合映射对象
     * @param accordingMap  Map<"查询条件key"，查询条件值> accordingMap
     * @param sortField     排序字段
     * @param direction     Direction.desc /asc 倒序/正序
     * @param <T>
     * @return
     */
    protected  <T> List<T> findSortByParam(Class<T> entityClass, Map<String, Object> accordingMap, String sortField, Sort.Direction direction) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap)).with(Sort.by(direction, sortField));
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 范围查询
     * <p>
     * 查询大于等于begin  小于等于end范围内条件匹配得数据并排序
     *
     * @param entityClass   集合映射对象
     * @param accordingMap  Map<"查询条件key"，查询条件值> accordingMap
     * @param sortField     排序字段
     * @param direction     排序方式  Direction.asc   / Direction.desc
     * @param rangeCriteria 示例： lt小于  lte 小于等于  gt大于  gte大于等于 eq等于 ne不等于
     *                      <p>
     *                      Criteria rangeCriteria=Criteria.where("createDate").gte(begin).lte(end));
     *                      <p>
     *                      createDate:数据库中的时间字段，begin:起始时间  end:结束时间
     * @return
     */
    protected  <T> List<T> findRangeByParam(Class<T> entityClass,
                                            Map<String, Object> accordingMap,
                                            String sortField,
                                            Sort.Direction direction,
                                            Criteria rangeCriteria) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");
        Assert.notNull(rangeCriteria, "rangeCriteria must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        Query query = new Query().addCriteria(rangeCriteria);
        if (null != criteria) {
            query.addCriteria(criteria);
        }
        query.with(Sort.by(direction, sortField));
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 根据条件分页查询
     *
     * @param <T>
     * @param accordingMap  查询条件
     * @param pageIndex     当前页
     * @param pageSize      每页显示
     * @param sortField     排序字段
     * @param entityClass   集合映射实体
     * @return
     */
    public <T> List<T> pagingQuery(Map<String, Object> accordingMap, int pageIndex, int pageSize, String sortField, Sort.Direction direction, Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        query.skip((pageIndex - 1) * pageSize);
        query.limit(pageSize);
        if (StringUtils.isNotBlank(sortField)) {
            query.with(Sort.by(direction, sortField));
        }
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 分页查询返回PageModel
     *
     * @param entityClass   集合映射对象
     * @param pageNo        当前页
     * @param pageSize      当前页数据条数
     * @param direction     Direction.Desc/ASC 排序方式
     * @param sortField     排序字段
     * @return
     */
    protected PageModel pagingQuery(Class<?> entityClass, int pageNo, int pageSize, String sortField, Sort.Direction direction) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");

        long count = mongoTemplate.count(new Query(), entityClass);

        int pages = (int) Math.ceil((double) count / (double) pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        query.with(Sort.by(direction, sortField));

        List<?> list = mongoTemplate.find(query, entityClass);

        PageModel pageModel = new PageModel();
        pageModel.setPageNo(pageNo);
        pageModel.setPageSize(pageSize);
        pageModel.setTotal(count);
        pageModel.setPages(pages);
        pageModel.setData(list);
        return pageModel;
    }

    /**
     * 根据条件分页查询返回PageModel
     *
     * @param entityClass   集合映射对象
     * @param accordingMap  Map<"查询条件key"，查询条件值> map 若 keys/values 为null,则查询集合中所有数据
     * @param pageNo        当前页
     * @param pageSize      当前页数据条数
     * @param direction     Direction.Desc/ASC 排序方式
     * @param sortField     排序字段
     * @return
     */
    public PageModel pagingQuery(Class<?> entityClass, Map<String, Object> accordingMap, int pageNo, int pageSize, String sortField, Sort.Direction direction) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sortField, "sortField must not be null！");
        Assert.notNull(direction, "sort direction must not be null！");
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        long count = mongoTemplate.count(new Query(criteria), entityClass);

        int pages = (int) Math.ceil((double) count / (double) pageSize);
        if (pageNo <= 0 || pageNo > pages) {
            pageNo = 1;
        }
        int skip = pageSize * (pageNo - 1);
        Query query = new Query().skip(skip).limit(pageSize);
        query.with(Sort.by(direction, sortField)).addCriteria(criteria);

        List<?> list = mongoTemplate.find(query, entityClass);

        PageModel pageModel = new PageModel();
        pageModel.setPageNo(pageNo);
        pageModel.setPageSize(pageSize);
        pageModel.setTotal(count);
        pageModel.setPages(pages);
        pageModel.setData(list);
        return pageModel;
    }

    /**
     * 对某字段做sum求和
     *
     * @param entityClass   集合映射对象
     * @param accordingMap  Map<查询条件key, 查询条件value> accordingMap
     * @param sumField      求和字段
     * @return
     */
    public String sum(Class<?> entityClass, Map<String, Object> accordingMap, String sumField) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        Assert.notNull(sumField, "sumField must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        MatchOperation match = null;
        if (criteria != null) {
            match = Aggregation.match(criteria);
        }
        GroupOperation count = Aggregation.group().sum(sumField).as(sumField);
        return mongoTemplate.aggregate(Aggregation.newAggregation(match, count), getCollectionName(entityClass), entityClass).getMappedResults().get(0).toString();
    }

    /**
     * 聚合操作
     * @param aggregation   聚合
     * @param entityClass   集合映射对象
     * @param outputType
     * @return org.springframework.data.mongodb.core.aggregation.AggregationResults<O>
     */
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<O> entityClass, Class<O> outputType) {
        return mongoTemplate.aggregate(aggregation, entityClass, outputType);
    }

    /**
     * 聚合查询
     *
     * @param <T>
     * @param aggregation    聚合
     * @param collectionName 集合名称
     * @param outputType     集合映射实体
     * @return
     */
    protected  <T> AggregationResults<T> aggregation(Aggregation aggregation, String collectionName, Class<T> outputType) {
        return mongoTemplate.aggregate(aggregation, collectionName, outputType);
    }

    /**
     * 单个条件删除数据
     * @param key
     * @param value
     * @param collectionName  集合名
     * @return
     */
    protected int remove(String key, Object value, String collectionName) {
        Assert.notNull(key, "key must not be null！");
        Assert.notNull(value, "value must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        Criteria criteria = Criteria.where(key).is(value);
        Query query = Query.query(criteria);
        return (int) mongoTemplate.remove(query, collectionName).getDeletedCount();
    }

    /**
     * 单个条件删除数据
     * @param key
     * @param value
     * @param entityClass   集合映射对象
     * @return
     */
    protected  <T> int remove(String key, Object value, Class<T> entityClass) {
        Assert.notNull(key, "key must not be null！");
        Assert.notNull(value, "value must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        Criteria criteria = Criteria.where(key).is(value);
        Query query = Query.query(criteria);
        return (int) mongoTemplate.remove(query, entityClass).getDeletedCount();
    }

    /**
     * 多个条件删除数据
     * @param accordingMap  条件Map
     * @param entityClass   集合映射对象
     * @param <T>
     * @return
     */
    public <T> int remove(Map<String, Object> accordingMap, Class<T> entityClass) {
        Assert.notNull(accordingMap, "AccordingMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        Query query = Query.query(criteria);
        return (int) mongoTemplate.remove(query, entityClass).getDeletedCount();
    }

    /**
     * 多个条件删除数据
     * @param accordingMap      条件Map
     * @param collectionName    集合名称
     * @return
     */
    protected int remove(Map<String, Object> accordingMap, String collectionName) {
        Assert.notNull(accordingMap, "AccordingMap must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        Criteria criteria = criteriaDefinition(accordingMap);
        Query query = Query.query(criteria);
        return (int) mongoTemplate.remove(query, collectionName).getDeletedCount();
    }

    /**
     * 自定义条件删除
     * @param query
     * @param entityClass
     * @return
     */
    public <T> int remove(Query query, Class<T> entityClass) {
        Assert.notNull(query, "query must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return (int) mongoTemplate.remove(query, entityClass).getDeletedCount();
    }

    /**
     * 自定义条件删除
     * @param query
     * @param collectionName
     * @return
     */
    protected int remove(Query query, String collectionName) {
        Assert.notNull(query, "query must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        return (int) mongoTemplate.remove(query, collectionName).getDeletedCount();
    }

    /**
     * 删除集合
     *
     * @param entityClass 集合映射实体
     */
    public <T> void removeCollection(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass must not be null！");
        mongoTemplate.dropCollection(entityClass);
    }

    /**
     * 删除集合
     *
     * @param collectionName 集合名称
     */
    protected  <T> void removeCollection(String collectionName) {
        Assert.notNull(collectionName, "collectionName must not be null！");
        mongoTemplate.dropCollection(collectionName);
    }

    /**
     * 指定集合 修改数据，且仅修改找到的第一条数据
     *
     * @param accordingMap   修改条件
     * @param updateMap      修改内容
     * @param collectionName 集合名
     */
    protected int updateFirst(Map<String, Object> accordingMap, Map<String, Object> updateMap, String collectionName) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        return update(accordingMap, updateMap, collectionName, false);
    }

    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingMap   修改条件
     * @param updateMap      修改内容
     * @param collectionName 集合名
     */
    protected int updateMulti(Map<String, Object> accordingMap, Map<String, Object> updateMap, String collectionName) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        return update(accordingMap, updateMap, collectionName, true);
    }

    /**
     * 指定集合 修改数据
     *
     * @param accordingMap   修改条件
     * @param updateMap      修改内容
     * @param collectionName 集合名
     * @param multi          是否更新全部
     */
    protected int update(Map<String, Object> accordingMap, Map<String, Object> updateMap, String collectionName, boolean multi) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        Update update = new Update();
        updateMap.forEach(update::set);

        return update(query, update, collectionName, multi);
    }

    /**
     * 指定集合 自定义修改数据
     *
     * @param query          修改条件
     * @param update         修改内容
     * @param collectionName 集合名
     * @param multi          是否更新全部
     */
    protected int update(Query query, Update update, String collectionName, boolean multi) {
        Assert.notNull(query, "query must not be null！");
        Assert.notNull(update, "update must not be null！");
        Assert.notNull(collectionName, "collectionName must not be null！");

        UpdateResult updateResult;
        if (multi) {
            updateResult = mongoTemplate.updateMulti(query, update, collectionName);
        } else {
            updateResult = mongoTemplate.updateFirst(query, update, collectionName);
        }
        return (int) updateResult.getModifiedCount();
    }

    /**
     * 修改数据，且仅修改找到的第一条数据
     *
     * @param accordingMap 修改条件
     * @param updateMap    修改内容
     * @param entityClass  集合映射对象
     */
    public <T> int updateFirst(Map<String, Object> accordingMap, Map<String, Object> updateMap, Class<T> entityClass) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return update(accordingMap, updateMap, entityClass, false);
    }

    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingMap  修改条件
     * @param updateMap     修改内容
     * @param entityClass   集合映射对象
     */
    public <T> int updateMulti(Map<String, Object> accordingMap, Map<String, Object> updateMap, Class<T> entityClass) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        return update(accordingMap, updateMap, entityClass, true);
    }

    /**
     * 指定集合 修改数据
     *
     * @param accordingMap  修改条件
     * @param updateMap     修改内容
     * @param entityClass   集合映射对象
     * @param multi         是否更新全部
     * @return
     */
    protected  <T> int update(Map<String, Object> accordingMap, Map<String, Object> updateMap, Class<T> entityClass, boolean multi) {
        Assert.notEmpty(accordingMap, "accordingMap must not be null！");
        Assert.notEmpty(updateMap, "updateMap must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        Update update = new Update();
        updateMap.forEach(update::set);

        return update(query, update, entityClass, multi);
    }

    /**
     * 指定集合 修改数据
     *
     * @param query       修改条件
     * @param update      修改内容
     * @param entityClass 集合映射对象
     * @param multi       是否更新全部
     */
    public <T> int update(Query query, Update update, Class<T> entityClass, boolean multi) {
        Assert.notNull(query, "query must not be null！");
        Assert.notNull(update, "update must not be null！");
        Assert.notNull(entityClass, "entityClass must not be null！");

        UpdateResult updateResult;
        if (multi) {
            updateResult = mongoTemplate.updateMulti(query, update, entityClass);
        } else {
            updateResult = mongoTemplate.updateFirst(query, update, entityClass);
        }
        return (int) updateResult.getModifiedCount();
    }

    /**
     * 根据指定条件统计数量
     *
     * @param query       条件
     * @param entityClass 集合映射实体
     * @return
     */
    public <T> long count(Query query, Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    /**
     * 根据指定条件统计数量
     *
     * @param query          条件
     * @param collectionName 集合名称
     * @return
     */
    protected  <T> long count(Query query, String collectionName) {
        return mongoTemplate.count(query, collectionName);
    }

    /**
     * 根据指定条件统计数量
     *
     * @param accordingMap   条件
     * @param entityClass    集合映射实体
     * @return
     */
    public <T> long count(Map<String, Object> accordingMap, Class<T> entityClass) {
        Assert.notNull(accordingMap, "accordingMap must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        return mongoTemplate.count(query, entityClass);
    }

    /**
     * 根据指定条件统计数量
     *
     * @param accordingMap      条件
     * @param collectionName    集合名称
     * @return
     */
    protected  <T> long count(Map<String, Object> accordingMap, String collectionName) {
        Assert.notNull(accordingMap, "accordingMap must not be null！");

        Query query = Query.query(criteriaDefinition(accordingMap));
        return mongoTemplate.count(query, collectionName);
    }

    /**
     * 获取mongo游标（需要手动关闭）
     * @param query 查询对象
     * @param entityClass 查询实体
     * @param batchSize 批次大小（默认1000，需大于0）
     * @param pageNum 当前页数
     * @param pageSize 每页大小
     * @return com.mongodb.client.MongoCursor<org.bson.Document>
     */
    <T> MongoCursor<Document> extGetMongoCursor(Query query, Class<T> entityClass, Integer batchSize, Integer pageNum, Integer pageSize){
        if (query == null || entityClass == null){
            return null;
        }
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(entityClass));
        FindIterable<Document> findIterable = collection.find(query.getQueryObject());
        //----------填充游标属性----------
        //（1）游标不超时
        findIterable.noCursorTimeout(true);
        //（2）批次拉取大小（默认1000）
        if (batchSize == null || batchSize <= 0) {
            batchSize=DEFAULT_CURSOR_BATCH_SIZE;
        }
        findIterable.batchSize(batchSize);
        //（3）排序
        findIterable.sort(query.getSortObject());
        //（4）跳过记录数
        if (pageNum!=null && pageSize != null) {
            findIterable.skip((pageNum - 1) * pageSize);
            findIterable.limit(pageSize);
        }

        return findIterable.cursor();
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param batchSize 批次大小
     * @param pageNum 当前页
     * @param pageSize 每次大小
     * @param executor 执行器
     * @throws Exception 异常
     */
    protected  <T> void extCursorQueryExe(Query query, Class<T> entityClass, Integer batchSize, Integer pageNum, Integer pageSize, Executor<T> executor) throws Exception{
        if (executor == null){
            return ;
        }
        try (MongoCursor<Document> cursor = this.extGetMongoCursor(query, entityClass, batchSize, pageNum, pageSize)) {
            if (cursor == null) {
                return ;
            }
            T model;
            while (cursor.hasNext()) {
                model = mongoConverter.read(entityClass, cursor.next());
                executor.invoke(model);
            }
        } catch (Exception e) {
            log.error("MsunMongoHandler.extCursorQueryExe error {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param batchSize 批次大小
     * @param executor 执行器
     * @return void
     */
    protected  <T> void extCursorQueryExe(Query query, Class<T> entityClass, Integer batchSize, Executor<T> executor) throws Exception{
        this.extCursorQueryExe(query, entityClass, batchSize,null,null,executor);
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param executor 执行器
     * @return void
     */
    protected  <T> void extCursorQueryExe(Query query, Class<T> entityClass, Executor<T> executor) throws Exception{
        this.extCursorQueryExe(query, entityClass,null,null,null, executor);
    }

    private Criteria criteriaDefinition(Map<String, Object> accordingMap) {
        if (MapUtils.isEmpty(accordingMap)) {
            return null;
        }

        boolean firstConditionFlag = true;
        Criteria criteria = null;
        for (Map.Entry<String, Object> e : accordingMap.entrySet()) {
            if (firstConditionFlag) {
                criteria = Criteria.where(e.getKey()).is(e.getValue());
                firstConditionFlag = false;
                continue;
            }
            criteria.and(e.getKey()).is(e.getValue());
        }

        return criteria;
    }
}
