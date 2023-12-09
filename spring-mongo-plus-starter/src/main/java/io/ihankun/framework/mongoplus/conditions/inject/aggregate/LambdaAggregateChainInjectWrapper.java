package io.ihankun.framework.mongoplus.conditions.inject.aggregate;

import io.ihankun.framework.mongoplus.conditions.aggregate.AggregateChainWrapper;
import io.ihankun.framework.mongoplus.execute.SqlExecute;
import com.mongodb.client.ClientSession;

import java.util.List;
import java.util.Map;

/**
 * @author hankun
 **/
public class LambdaAggregateChainInjectWrapper extends AggregateChainWrapper<Map<String,Object>, LambdaAggregateChainInjectWrapper> implements ChainInjectAggregate {

    private final SqlExecute sqlExecute;

    public LambdaAggregateChainInjectWrapper(SqlExecute sqlExecute) {
        this.sqlExecute = sqlExecute;
    }

    @Override
    public <E> List<E> list(String collectionName,Class<E> clazz) {
        return sqlExecute.doAggregateList(collectionName,super.getBaseAggregateList(),super.getBasicDBObjectList(),super.getOptionsBasicDBObject(),clazz);
    }

    @Override
    public <E> List<E> list(ClientSession clientSession, String collectionName, Class<E> clazz) {
        return sqlExecute.doAggregateList(clientSession,collectionName,super.getBaseAggregateList(),super.getBasicDBObjectList(),super.getOptionsBasicDBObject(),clazz);
    }

    @Override
    public List<Map<String, Object>> list(String collectionName) {
        return sqlExecute.doAggregateList(collectionName,super.getBaseAggregateList(),super.getBasicDBObjectList(),super.getOptionsBasicDBObject());
    }

    @Override
    public List<Map<String, Object>> list(ClientSession clientSession, String collectionName) {
        return sqlExecute.doAggregateList(clientSession,collectionName,super.getBaseAggregateList(),super.getBasicDBObjectList(),super.getOptionsBasicDBObject());
    }
}
