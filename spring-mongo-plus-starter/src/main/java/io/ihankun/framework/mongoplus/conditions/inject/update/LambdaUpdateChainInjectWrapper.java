package io.ihankun.framework.mongoplus.conditions.inject.update;

import io.ihankun.framework.mongoplus.conditions.AbstractChainWrapper;
import io.ihankun.framework.mongoplus.conditions.interfaces.Inject.InjectUpdate;
import io.ihankun.framework.mongoplus.conditions.interfaces.condition.CompareCondition;
import io.ihankun.framework.mongoplus.enums.CompareEnum;
import io.ihankun.framework.mongoplus.enums.LogicTypeEnum;
import io.ihankun.framework.mongoplus.execute.SqlExecute;
import com.mongodb.client.ClientSession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 * @project mongo
 * @description
 * @date 2023-07-23 22:23
 **/
public class LambdaUpdateChainInjectWrapper extends AbstractChainWrapper<String, LambdaUpdateChainInjectWrapper> implements InjectUpdate<LambdaUpdateChainInjectWrapper> {

    private final List<CompareCondition> updateCompareList = new ArrayList<>();

    private final SqlExecute sqlExecute;

    public LambdaUpdateChainInjectWrapper(SqlExecute sqlExecute) {
        this.sqlExecute = sqlExecute;
    }

    @Override
    public LambdaUpdateChainInjectWrapper set(boolean condition, String column, Object value) {
        return condition ? set(column,value) : typedThis;
    }

    @Override
    public LambdaUpdateChainInjectWrapper set(String column, Object value) {
        return getBaseUpdateCompare(column,value);
    }

    private LambdaUpdateChainInjectWrapper getBaseUpdateCompare(String column, Object value){
        updateCompareList.add(CompareCondition.builder().condition(new Throwable().getStackTrace()[1].getMethodName()).column(column).value(value).type(CompareEnum.UPDATE.getKey()).logicType(LogicTypeEnum.AND.getKey()).build());
        return this;
    }

    public boolean update(String collectionName){
        return update(null,collectionName);
    }

    public boolean update(ClientSession clientSession,String collectionName){
        List<CompareCondition> compareConditionList = new ArrayList<>();
        compareConditionList.addAll(getCompareList());
        compareConditionList.addAll(getUpdateCompareList());
        return sqlExecute.doUpdate(clientSession,collectionName,compareConditionList);
    }

    public boolean remove(String collectionName) {
        return sqlExecute.doRemove(collectionName,getCompareList());
    }

    public boolean remove(ClientSession clientSession,String collectionName) {
        return sqlExecute.doRemove(clientSession,collectionName,getCompareList());
    }

    public List<CompareCondition> getUpdateCompareList() {
        return updateCompareList;
    }

    public SqlExecute getSqlOperation() {
        return sqlExecute;
    }
}
