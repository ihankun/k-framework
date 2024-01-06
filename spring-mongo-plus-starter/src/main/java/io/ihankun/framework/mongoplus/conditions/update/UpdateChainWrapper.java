package io.ihankun.framework.mongoplus.conditions.update;

import io.ihankun.framework.mongoplus.conditions.AbstractChainWrapper;
import io.ihankun.framework.mongoplus.conditions.interfaces.Update;
import io.ihankun.framework.mongoplus.conditions.interfaces.condition.CompareCondition;
import io.ihankun.framework.mongoplus.enums.CompareEnum;
import io.ihankun.framework.mongoplus.enums.LogicTypeEnum;
import io.ihankun.framework.mongoplus.support.SFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * update接口实现
 * @author hankun
 * @date 2023/6/24/024 12:45
*/
public class UpdateChainWrapper<T,Children extends UpdateChainWrapper<T,Children>> extends AbstractChainWrapper<T, LambdaUpdateChainWrapper<T>> implements Update<UpdateChainWrapper<T,Children>,T> {

    protected final Children typedThis = (Children) this;

    private final List<CompareCondition> updateCompareList = new ArrayList<>();

    public List<CompareCondition> getUpdateCompareList() {
        return updateCompareList;
    }

    @Override
    public Children set(boolean condition, SFunction<T, Object> column, Object value) {
        return condition ? set(column,value) : typedThis;
    }

    @Override
    public Children set(SFunction<T, Object> column, Object value) {
        return getBaseUpdateCompare(column,value);
    }

    @Override
    public Children set(boolean condition, String column, Object value) {
        return condition ? set(column,value) : typedThis;
    }

    @Override
    public Children set(String column, Object value) {
        return getBaseUpdateCompare(column,value);
    }

    private Children getBaseUpdateCompare(SFunction<T, Object> column, Object value){
        updateCompareList.add(CompareCondition.builder().condition(new Throwable().getStackTrace()[1].getMethodName()).column(column.getFieldNameLine()).value(value).type(CompareEnum.UPDATE.getKey()).logicType(LogicTypeEnum.AND.getKey()).build());
        return typedThis;
    }

    private Children getBaseUpdateCompare(String column, Object value){
        updateCompareList.add(CompareCondition.builder().condition(new Throwable().getStackTrace()[1].getMethodName()).column(column).value(value).type(CompareEnum.UPDATE.getKey()).logicType(LogicTypeEnum.AND.getKey()).build());
        return typedThis;
    }

}
