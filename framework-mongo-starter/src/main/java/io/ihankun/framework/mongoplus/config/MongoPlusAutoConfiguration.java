package io.ihankun.framework.mongoplus.config;

import com.mongodb.MongoException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MongoPlus自动注入配置
 * @author hankun
 **/
@EnableConfigurationProperties(MongoDBLogProperty.class)
public class MongoPlusAutoConfiguration implements InitializingBean {

    private final SqlExecute sqlExecute;

    private final ApplicationContext applicationContext;

    private final MongoDBLogProperty mongoDBLogProperty;

    private final MongoDBCollectionProperty mongoDBCollectionProperty;

    Logger logger = LoggerFactory.getLogger(MongoPlusAutoConfiguration.class);

    public MongoPlusAutoConfiguration(MongoDBLogProperty mongoDBLogProperty,MongoDBCollectionProperty mongoDBCollectionProperty,SqlExecute sqlExecute, ApplicationContext applicationContext) {
        this.sqlExecute = sqlExecute;
        this.applicationContext = applicationContext;
        this.mongoDBLogProperty = mongoDBLogProperty;
        this.mongoDBCollectionProperty = mongoDBCollectionProperty;
        setConversion();
        setMetaObjectHandler();
        setDocumentHandler();
        setInterceptor();
    }

    @Override
    public void afterPropertiesSet() {
        applicationContext.getBeansOfType(IService.class)
                .values()
                .stream()
                .filter(s -> s instanceof ServiceImpl)
                .forEach(s -> setSqlExecute((ServiceImpl<?>) s, s.getGenericityClazz()));
    }

    private void setSqlExecute(ServiceImpl<?> serviceImpl, Class<?> clazz) {
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
    private void setConversion(){
        applicationContext.getBeansOfType(ConversionStrategy.class).values().forEach(conversionStrategy -> {
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
                logger.error("Unknown converter type",e);
                throw new MongoException("Unknown converter type");
            }
        });
    }

    /**
     * 从Bean中拿到自动填充策略
     * @author hankun
     * @date 2023/11/21 12:18
    */
    private void setMetaObjectHandler(){
        applicationContext.getBeansOfType(MetaObjectHandler.class).values().forEach(metaObjectHandler -> HandlerCache.metaObjectHandler = metaObjectHandler);
    }

    /**
     * 从Bean中拿到Document的处理器
     * @author hankun
     * @date 2023/11/23 12:58
    */
    private void setDocumentHandler(){
        applicationContext.getBeansOfType(DocumentHandler.class).values().forEach(documentHandler -> HandlerCache.documentHandler = documentHandler);
    }

    /**
     * 从Bean中拿到拦截器
     * @author hankun
     * @date 2023/11/22 18:39
    */
    private void setInterceptor(){
        List<Interceptor> interceptors = new ArrayList<>();
        if (mongoDBLogProperty.getLog()){
            interceptors.add(new LogInterceptor());
        }
        if (mongoDBCollectionProperty.getBlockAttackInner()){
            interceptors.add(new BlockAttackInnerInterceptor());
        }
        Collection<Interceptor> interceptorCollection = applicationContext.getBeansOfType(Interceptor.class).values();
        if (CollUtil.isNotEmpty(interceptorCollection)){
            interceptors.addAll(interceptorCollection);
        }
        InterceptorCache.interceptors = interceptors.stream().sorted(Comparator.comparingInt(Interceptor::getOrder)).collect(Collectors.toList());
    }

}
