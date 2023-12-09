package io.ihankun.framework.mongoplus.conditions.interfaces;

import io.ihankun.framework.mongoplus.support.SFunction;

public interface Query<T,Children> extends Project<T,Children> {

    /**
     * 正序排序
     * @param column 列名、字段名，lambda方式
     * @return io.ihankun.framework.mongoplus.sql.query.LambdaQueryMongoWrapper<T>
     * @author hankun
     * @date 2023/6/20/020
     */
    Children orderByAsc(SFunction<T, Object> column);

    /**
     * 倒序排序
     * @param column 列名、字段名，lambda方式
     * @return io.ihankun.framework.mongoplus.sql.query.LambdaQueryMongoWrapper<T>
     * @author hankun
     * @date 2023/6/20/020
     */
    Children orderByDesc(SFunction<T,Object> column);

    /**
     * 正序排序
     * @param column 列名、字段名
     * @return io.ihankun.framework.mongoplus.sql.query.LambdaQueryMongoWrapper<T>
     * @author hankun
     * @date 2023/6/20/020
     */
    Children orderByAsc(String column);

    /**
     * 倒序排序
     * @param column 列名、字段名，lambda方式
     * @return io.ihankun.framework.mongoplus.sql.query.LambdaQueryMongoWrapper<T>
     * @author hankun
     * @date 2023/6/20/020
     */
    Children orderByDesc(String column);

}
