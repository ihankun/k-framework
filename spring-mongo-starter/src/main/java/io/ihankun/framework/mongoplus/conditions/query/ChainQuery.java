package io.ihankun.framework.mongoplus.conditions.query;

import io.ihankun.framework.mongoplus.model.PageParam;
import io.ihankun.framework.mongoplus.model.PageResult;
import com.mongodb.client.ClientSession;

import java.util.List;

/**
 * 查询方法定义
 * @author hankun
 * @date 2023/6/24/024 2:01
*/
public interface ChainQuery<T> {

    /**
     * 获取列表 返回T类型的List
     * @return {@link List<T>}
     * @author hankun
     * @date 2023/7/20 23:13
    */
    List<T> list();

    /**
     * 获取列表 返回T类型的List
     * @return {@link List<T>}
     * @author hankun
     * @date 2023/7/20 23:13
     */
    List<T> list(ClientSession clientSession);

    /**
     * 获取单个，返回T类型的对象
     * <p>注：如果查询到大于一条数据，会抛出{@link io.ihankun.framework.mongoplus.domain.MongoQueryException}异常</p>
     * @return T
     * @author hankun
     * @date 2023/7/20 23:13
    */
    T one();

    /**
     * 获取单个，返回T类型的对象
     * <p>注：如果查询到大于一条数据，会抛出{@link io.ihankun.framework.mongoplus.domain.MongoQueryException}异常</p>
     * @return T
     * @author hankun
     * @date 2023/7/20 23:13
     */
    T one(ClientSession clientSession);

    /**
     * 获取单个，返回T类型的对象
     * <p>注：如果查询到大于一条数据，会取第一条返回</p>
     * @author hankun
     * @date 2023/7/20 23:12
    */
    T limitOne();

    /**
     * 获取单个，返回T类型的对象
     * <p>注：如果查询到大于一条数据，会取第一条返回</p>
     * @author hankun
     * @date 2023/7/20 23:12
     */
    T limitOne(ClientSession clientSession);

    /**
     * 分页
     * @param pageParam 分页参数对象
     * @return {@link PageResult<T>}
     * @author hankun
     * @date 2023/7/20 23:17
    */
    PageResult<T> page(PageParam pageParam);

    /**
     * 分页
     * @param pageParam 分页参数对象
     * @return {@link PageResult<T>}
     * @author hankun
     * @date 2023/7/20 23:17
     */
    PageResult<T> page(ClientSession clientSession,PageParam pageParam);

    /**
     * 分页
     * @param pageNum 当前页
     * @param pageSize 每页显示行数
     * @return {@link PageResult<T>}
     * @author hankun
     * @date 2023/7/20 23:17
    */
    PageResult<T> page(Integer pageNum, Integer pageSize);

    /**
     * 分页
     * @param pageNum 当前页
     * @param pageSize 每页显示行数
     * @return {@link PageResult<T>}
     * @author hankun
     * @date 2023/7/20 23:17
     */
    PageResult<T> page(ClientSession clientSession,Integer pageNum, Integer pageSize);

    long count();

    long count(ClientSession clientSession);

}
