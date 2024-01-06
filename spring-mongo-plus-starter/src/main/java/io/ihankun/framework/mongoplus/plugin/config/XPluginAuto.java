package io.ihankun.framework.mongoplus.plugin.config;

import io.ihankun.framework.mongoplus.annotation.transactional.MongoTransactional;
import io.ihankun.framework.mongoplus.cache.global.MongoClientCache;
import io.ihankun.framework.mongoplus.plugin.transactional.MongoTransactionalAspect;
import io.ihankun.framework.mongoplus.property.MongoDBFieldProperty;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 使用插件处理配置
 * @author hankun
 **/
public class XPluginAuto implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        //mongo-plus插件配置
        context.beanMake(MongoPlusConfiguration.class);
        context.beanInterceptorAdd(MongoTransactional.class,new MongoTransactionalAspect(MongoClientCache.mongoClient));
        context.beanMake(MongoDBFieldProperty.class);
    }
}
