package io.ihankun.framework.mongoplus.conditions.interfaces.Inject;

import io.ihankun.framework.mongoplus.model.PageParam;
import io.ihankun.framework.mongoplus.model.PageResult;
import com.mongodb.client.ClientSession;

import java.util.List;
import java.util.Map;

/**
 * 公共条件
 * @author hankun
 **/
public interface CommInjectQuery {

    /**
     * 获取列表 返回Map<String,Object>类型的List
     * @param collectionName 集合名
     * @return {@link List < Map <String,Object>>}
     * @author hankun
     * @date 2023/7/20 23:17
     */
    List<Map<String,Object>> list(String collectionName);

    /**
     * 获取列表 返回Map<String,Object>类型的List
     * @param collectionName 集合名
     * @return {@link List < Map <String,Object>>}
     * @author hankun
     * @date 2023/7/20 23:17
     */
    List<Map<String,Object>> list(ClientSession clientSession,String collectionName);

    /**
     * 分页
     * @param collectionName 集合名
     * @param pageParam 分页参数对象
     * @return {@link PageResult < Map< String, Object>>}
     * @author hankun
     * @date 2023/7/20 23:19
     */
    PageResult<Map<String,Object>> page(String collectionName, PageParam pageParam);

    /**
     * 分页
     * @param collectionName 集合名
     * @param pageParam 分页参数对象
     * @return {@link PageResult < Map< String, Object>>}
     * @author hankun
     * @date 2023/7/20 23:19
     */
    PageResult<Map<String,Object>> page(ClientSession clientSession,String collectionName, PageParam pageParam);

    /**
     * 分页
     * @param collectionName 集合名
     * @param pageNum 当前页
     * @param pageSize 每页显示行数
     * @return {@link PageResult< Map< String, Object>>}
     * @author hankun
     * @date 2023/7/20 23:20
     */
    PageResult<Map<String,Object>> page(String collectionName , Integer pageNum, Integer pageSize);

    /**
     * 分页
     * @param collectionName 集合名
     * @param pageNum 当前页
     * @param pageSize 每页显示行数
     * @return {@link PageResult< Map< String, Object>>}
     * @author hankun
     * @date 2023/7/20 23:20
     */
    PageResult<Map<String,Object>> page(ClientSession clientSession,String collectionName , Integer pageNum, Integer pageSize);

    /**
     * 获取总行数
     * @param collectionName 集合名
     * @return {@link long}
     * @author hankun
     * @date 2023/7/27 13:11
     */
    long count(String collectionName);

    /**
     * 获取总行数
     * @param collectionName 集合名
     * @return {@link long}
     * @author hankun
     * @date 2023/7/27 13:11
     */
    long count(ClientSession clientSession,String collectionName);

}
