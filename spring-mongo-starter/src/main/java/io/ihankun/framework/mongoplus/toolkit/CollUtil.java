package io.ihankun.framework.mongoplus.toolkit;

import java.util.Collection;

/**
 * @author hankun
 * @project mongo-plus
 * @description 集合工具类
 * @date 2023-09-22 11:48
 **/
public class CollUtil {

    public static boolean isNotEmpty(Collection<?> collection){
        return collection != null && !collection.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection){
        return !isNotEmpty(collection);
    }

}
