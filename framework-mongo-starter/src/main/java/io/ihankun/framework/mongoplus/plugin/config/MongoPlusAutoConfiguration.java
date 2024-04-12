package io.ihankun.framework.mongoplus.plugin.config;

import io.ihankun.framework.mongoplus.cache.global.HandlerCache;
import io.ihankun.framework.mongoplus.cache.global.InterceptorCache;
import io.ihankun.framework.mongoplus.execute.SqlExecute;
import io.ihankun.framework.mongoplus.handlers.DocumentHandler;
import io.ihankun.framework.mongoplus.handlers.MetaObjectHandler;
import io.ihankun.framework.mongoplus.interceptor.Interceptor;
import io.ihankun.framework.mongoplus.interceptor.business.BlockAttackInnerInterceptor;
import io.ihankun.framework.mongoplus.interceptor.business.LogInterceptor;
import io.ihankun.framework.mongoplus.property.MongoDBCollectionProperty;
import io.ihankun.framework.mongoplus.property.MongoDBLogProperty;
import io.ihankun.framework.mongoplus.service.IService;
import io.ihankun.framework.mongoplus.service.impl.ServiceImpl;
import io.ihankun.framework.mongoplus.strategy.convert.ConversionService;
import io.ihankun.framework.mongoplus.strategy.convert.ConversionStrategy;
import io.ihankun.framework.mongoplus.toolkit.CollUtil;
import com.mongodb.MongoException;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoPlus自动注入配置
 * @author hankun
 **/
public class MongoPlusAutoConfiguration {

    private final SqlExecute sqlExecute;

    private final MongoDBLogProperty mongoDBLogProperty;

    private final MongoDBCollectionProperty mongoDBCollectionProperty;

    Logger logger = LoggerFactory.getLogger(MongoPlusAutoConfiguration.class);

    public MongoPlusAutoConfiguration(@Inject SqlExecute sqlExecute, MongoDBLogProperty mongoDBLogProperty, MongoDBCollectionProperty mongoDBCollectionProperty){
        this.sqlExecute = sqlExecute;
        this.mongoDBLogProperty = mongoDBLogProperty;
        this.mongoDBCollectionProperty = mongoDBCollectionProperty;
        AppContext context = Solon.context();
        context.subBeansOfType(IService.class, bean -> {
            if (bean instanceof ServiceImpl){
                setSqlExecute((ServiceImpl<?>) bean,bean.getGenericityClazz());
            }
        });
        //拿到转换器
        setConversion(context);
        //拿到自动填充处理器
        setMetaObjectHandler(context);
        //拿到Document处理器
        setDocumentHandler(context);
        //拿到拦截器
        setInterceptor(context);
    }

    /**
     * 从Bean中拿到Document的处理器
     * @author hankun
     * @date 2023/11/23 12:56
    */
    private void setSqlExecute(ServiceImpl<?> serviceImpl,Class<?> clazz) {
        sqlExecute.init(clazz);
        serviceImpl.setClazz(clazz);
        serviceImpl.setSqlOperation(sqlExecute);
    }

    /**
     * 从Bean中拿到转换器
     * @author hankun
     * @date 2023/10/19 12:49
     */
    @SuppressWarnings("unchecked")
    private void setConversion(AppContext context){
        context.getBeansOfType(ConversionStrategy.class).forEach(conversionStrategy -> {
            try {
                Type[] genericInterfaces = conversionStrategy.getClass().getGenericInterfaces();
                for (Type anInterface : genericInterfaces) {
                    ParameterizedType parameterizedType = (ParameterizedType) anInterface;
                    if (parameterizedType.getRawType().equals(ConversionStrategy.class)){
                        Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        ConversionService.appendConversion(clazz,conversionStrategy);
                        break;
                    }
                }
            }catch (Exception e){
                logger.error("Unknown converter type");
                throw new MongoException("Unknown converter type");
            }
        });
    }

    /**
     * 从Bean中拿到自动填充策略
     * @author hankun
     * @date 2023/11/21 12:18
     */
    private void setMetaObjectHandler(AppContext context){
        context.getBeansOfType(MetaObjectHandler.class).forEach(metaObjectHandler -> HandlerCache.metaObjectHandler = metaObjectHandler);
    }

    private void setDocumentHandler(AppContext appContext){
        appContext.getBeansOfType(DocumentHandler.class).forEach(documentHandler -> HandlerCache.documentHandler = documentHandler);
    }

    /**
     * 从Bean中拿到拦截器
     * @author hankun
     * @date 2023/11/22 18:39
     */
    private void setInterceptor(AppContext context){
        List<Interceptor> interceptors = new ArrayList<>();
        if (mongoDBLogProperty.getLog()){
            interceptors.add(new LogInterceptor());
        }
        if (mongoDBCollectionProperty.getBlockAttackInner()){
            interceptors.add(new BlockAttackInnerInterceptor());
        }
        List<Interceptor> interceptorCollection = context.getBeansOfType(Interceptor.class);
        if (CollUtil.isNotEmpty(interceptorCollection)){
            interceptors.addAll(interceptorCollection);
        }
        InterceptorCache.interceptors = interceptors.stream().sorted(Comparator.comparingInt(Interceptor::getOrder)).collect(Collectors.toList());
    }

}
