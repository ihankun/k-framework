package io.ihankun.framework.mongoplus.toolkit;

import io.ihankun.framework.mongoplus.conditions.aggregate.AggregateChainWrapper;
import io.ihankun.framework.mongoplus.conditions.aggregate.AggregateWrapper;
import io.ihankun.framework.mongoplus.conditions.aggregate.LambdaAggregateChainWrapper;
import io.ihankun.framework.mongoplus.conditions.inject.aggregate.InjectAggregateWrapper;
import io.ihankun.framework.mongoplus.conditions.inject.query.InjectQueryWrapper;
import io.ihankun.framework.mongoplus.conditions.inject.update.InjectUpdateWrapper;
import io.ihankun.framework.mongoplus.conditions.query.LambdaQueryChainWrapper;
import io.ihankun.framework.mongoplus.conditions.query.QueryChainWrapper;
import io.ihankun.framework.mongoplus.conditions.query.QueryWrapper;
import io.ihankun.framework.mongoplus.conditions.update.LambdaUpdateChainWrapper;
import io.ihankun.framework.mongoplus.conditions.update.UpdateChainWrapper;
import io.ihankun.framework.mongoplus.conditions.update.UpdateWrapper;
import io.ihankun.framework.mongoplus.execute.SqlExecute;

import java.util.Map;

/**
 * 快速构建链式调用
 * @author hankun
 * @date 2023/6/24/024 2:27
*/
public final class ChainWrappers {

    public static <T> LambdaQueryChainWrapper<T> lambdaQueryChain(SqlExecute sqlExecute,Class<T> clazz){
        return new LambdaQueryChainWrapper<>(sqlExecute, clazz);
    }

    public static <T> LambdaAggregateChainWrapper<T> lambdaAggregateChain(SqlExecute sqlExecute,Class<T> clazz){
        return new LambdaAggregateChainWrapper<>(sqlExecute,clazz);
    }

    public static <T> LambdaUpdateChainWrapper<T> lambdaUpdateChain(SqlExecute sqlExecute,Class<T> clazz){
        return new LambdaUpdateChainWrapper<>(sqlExecute,clazz);
    }

    public static <T> UpdateChainWrapper<T, UpdateWrapper<T>> lambdaUpdateChain(){
        return new UpdateChainWrapper<>();
    }

    public static <T> UpdateChainWrapper<Map<String,Object>, InjectUpdateWrapper> lambdaUpdateChainInject(){
        return new UpdateChainWrapper<>();
    }

    public static <T> QueryChainWrapper<T, QueryWrapper<T>> lambdaQueryChain(){
        return new QueryChainWrapper<>();
    }

    public static QueryChainWrapper<Map<String,Object>, InjectQueryWrapper> lambdaQueryChainInject(){
        return new QueryChainWrapper<>();
    }

    public static <T> AggregateChainWrapper<T, AggregateWrapper<T>> lambdaAggregateChain(){
        return new AggregateChainWrapper<>();
    }

    public static AggregateChainWrapper<Map<String,Object>, InjectAggregateWrapper> lambdaAggregateChainInject(){
        return new AggregateChainWrapper<>();
    }

}
