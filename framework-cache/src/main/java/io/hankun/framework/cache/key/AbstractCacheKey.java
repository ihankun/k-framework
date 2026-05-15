package io.hankun.framework.cache.key;

import cn.hutool.core.util.StrUtil;
import io.hankun.framework.core.context.sys.DomainContext;
import io.hankun.framework.core.utils.spring.SpringHelpers;
import io.hankun.framework.cache.config.RedisDomainIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author hankun
 */
@Slf4j
public abstract class AbstractCacheKey {

    protected static final String SPLIT = ":";

    public static RedisDomainIgnoreProperties redisDomainIgnoreProperties;

    public RedisDomainIgnoreProperties getRedisDomainIgnoreProperties() {
        if (redisDomainIgnoreProperties == null) {
            redisDomainIgnoreProperties = SpringHelpers.context().getBean(RedisDomainIgnoreProperties.class);
        }

        return redisDomainIgnoreProperties;
    }

    /**
     * 获取key的统一前缀
     *
     * @param originKey 原始业务组装的Key
     */
    protected String domainFormatKey(String originKey) {
        //获取域名,如果域名为空，则报错处理
        String domain = DomainContext.get();
        if (StrUtil.isEmpty(domain)) {
            log.debug("AbstractCacheKey.formatKey.get.null.error key={}", originKey);
            return originKey;
        }

        RedisDomainIgnoreProperties config = getRedisDomainIgnoreProperties();

        if (null == config) {
            log.debug("AbstractCacheKey.formatKey.getIgnoreIsolationDomainConfig.null.error key={}", originKey);
            return originKey;
        }

        //未开启状态，则不进行前缀设置
        if (!config.isEnable()) {
            return originKey;
        }

        //忽略规则不为空,且匹配存在忽略的key值
        String domainPrefix = domain.substring(0, domain.indexOf(".com"));
        List<String> domains = config.getDomains();
        if ((!CollectionUtils.isEmpty(domains)) && domains.contains(domainPrefix)) {
            return originKey;
        }

        List<String> keys = config.getKeys();
        if (!CollectionUtils.isEmpty(keys)) {
            if (keys.contains(originKey)) {
                return originKey;
            }

            AntPathMatcher pathMatcher = new AntPathMatcher();
            for (String key : keys) {
                if (pathMatcher.match(key, originKey)) {
                    return originKey;
                }
            }
        }

        return domainPrefix + SPLIT + originKey;
    }
}
