package io.ihankun.framework.mongoplus.annotation.collection;

import java.lang.annotation.*;

/**
 * @Description: 指定表名，不使用此注解默认取实体类名
 * @BelongsProject: mongo
 * @BelongsPackage: io.ihankun.framework.mongoplus.annotation.table
 * @Author: hankun
 * @CreateTime: 2023-02-17 21:19
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
//运行时注解
@Retention(RetentionPolicy.RUNTIME)
//表明这个注解应该被 javadoc工具记录
//生成文档
@Documented
public @interface CollectionName {
    String value();

    /**
     * 多数据源还有一点问题
     * @author hankun
     * @date 2023/10/17 0:22
    */
    @Deprecated
    String dataSource() default "";
}
