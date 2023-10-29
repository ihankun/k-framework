package io.ihankun.framework.db.config;

import io.ihankun.framework.db.mapper.KunBaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 项目启动时初始化所有mapper关联的PO的反射信息，提高第一次查询时的效率
 * @author hankun
 */
@Slf4j
@Configuration
public class BaseMapperInitConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        long start = System.currentTimeMillis();
        Map<String, KunBaseMapper> mapperMap = applicationContext.getBeansOfType(KunBaseMapper.class);
        if (CollectionUtils.isEmpty(mapperMap)) {
            return;
        }
        log.info("KunBaseMapperInitConfig耗时：{}ms", (System.currentTimeMillis() - start));
        mapperMap.forEach((k, v) -> v.getInstance(0L));
    }
}
