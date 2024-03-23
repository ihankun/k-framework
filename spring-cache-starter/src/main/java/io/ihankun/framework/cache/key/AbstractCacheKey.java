package io.ihankun.framework.cache.key;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import io.ihankun.framework.cache.config.RedisConfigProperties;
import io.ihankun.framework.common.v1.context.DomainContext;
import io.ihankun.framework.common.v1.exception.BusinessException;
import io.ihankun.framework.common.v1.utils.spring.SpringHelpers;
import io.ihankun.framework.common.v1.utils.string.StringPool;

import java.util.List;

import static io.ihankun.framework.cache.error.CacheErrorCodeEnum.DOMAIN_NOT_FIND;

/**
 * @author hankun
 */
public abstract class AbstractCacheKey {

    /**
     * 获取key的统一前缀
     *
     * @param originKey 原始业务组装的Key
     */
    protected String domainFormatKey(String originKey) {

        RedisConfigProperties config = SpringHelpers.context().getBean(RedisConfigProperties.class);

        //未开启状态，则不进行前缀设置
        if (!config.isDomainPrefixEnable()) {
            return originKey;
        }

        //忽略规则不为空,且匹配存在忽略的key值
        List<String> ignoreDomainPrefixKeys = config.getIgnoreDomainPrefixKeys();
        if (CollectionUtil.isNotEmpty(ignoreDomainPrefixKeys) && ignoreDomainPrefixKeys.contains(originKey)) {
            return originKey;
        }

        //获取域名,如果域名为空，则报错处理
        String domain = DomainContext.get();
        if (StrUtil.isEmpty(domain)) {
            throw BusinessException.build(DOMAIN_NOT_FIND, originKey);
        }

        return domain + StringPool.COLON + originKey;
    }
}
