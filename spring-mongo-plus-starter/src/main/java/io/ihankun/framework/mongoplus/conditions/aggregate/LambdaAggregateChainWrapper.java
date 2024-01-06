package io.ihankun.framework.mongoplus.conditions.aggregate;

import com.mongodb.client.ClientSession;
import io.ihankun.framework.mongoplus.execute.SqlExecute;

import java.util.List;

/**
 * @author hankun
 **/
public class LambdaAggregateChainWrapper<T> extends AggregateChainWrapper<T,LambdaAggregateChainWrapper<T>> implements ChainAggregate<T> {

    private final SqlExecute sqlExecute;

    private final Class<T> clazz;

    public LambdaAggregateChainWrapper(SqlExecute sqlExecute,Class<T> clazz) {
        this.sqlExecute = sqlExecute;
        this.clazz = clazz;
    }

    @Override
    public List<T> list() {
        return sqlExecute.doAggregateList(super.baseAggregateList,super.getBasicDBObjectList(),super.getOptionsBasicDBObject(),clazz);
    }

    @Override
    public List<T> list(ClientSession clientSession) {
        return sqlExecute.doAggregateList(clientSession,super.baseAggregateList,super.getBasicDBObjectList(),super.getOptionsBasicDBObject(),clazz);
    }
}
