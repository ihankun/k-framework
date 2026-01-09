package io.hankun.framework.db.dynamic;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import io.hankun.framework.core.event.DataSourceRefreshEvent;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.util.List;

/**
 * @author hankun
 */
@Slf4j
@Component
public class DataSourceSwitcher implements ApplicationEventPublisherAware {

    public static final String DS_SPLIT = "_";

    @Resource
    private DynamicRoutingDataSource dataSource;

    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private PropertiesHolder holder;

    @EventListener
    @Async
    public void listenDestroy(DataSourceRefreshEvent event) {
        List<String> aliases = holder.getAlias(event.getDbMark());
        if (CollectionUtils.isEmpty(aliases)) {
            log.info("DataSourceSwitcher.close.not.have.alias,domain={},dbMark={}", event.getDomain(), event.getDbMark());
            return;
        }
        log.info("DataSourceSwitcher.try.close.dataSource,domain={},aliases={}", event.getDomain(), aliases);
        for (String alias : aliases) {
            disable(alias, event.getDomain());
        }
    }

    public boolean disable(String alias, String domain) {
        String name = alias + DS_SPLIT + domain;
        log.info("DataSourceSwitcher.disable.datasource,name={}", name);
        return removeDataSource(name, alias);
    }

    private boolean removeDataSource(String name, String alias) {
        DataSource source = dataSource.getDataSources().get(name);
        if (source == null) {
            log.info("DataSourceSwitcher.dataSource.name.not.exists,name={}", name);
            return false;
        }
        if (source instanceof ItemDataSource) {
            ItemDataSource itemDataSource = (ItemDataSource) source;
            //苞米豆没有强制限制数据源访问名称和itemDataSource中的名称一致，此处仅为保险起见
            if (!itemDataSource.getName().equals(name)) {
                log.info("DataSourceSwitcher.dataSource.name.not.equal,name={},dataSource={}", name, itemDataSource.getName());
                return false;
            }
            //向报表推送销毁的数据源
            pushEventToReportService(name, alias, itemDataSource);

            dataSource.removeDataSource(name);
            //数据源移除事件
            applicationEventPublisher.publishEvent(new DataSourceRemoveEvent(this, itemDataSource));
            //苞米豆存在bug，数据源可能关闭失败，再次确认并关闭
            DataSource realDataSource = itemDataSource.getRealDataSource();
            if (realDataSource instanceof DruidDataSource) {
                if (!((DruidDataSource) realDataSource).isClosed()) {
                    log.info("DataSourceSwitcher.dataSource.start.to.close,name={}", name);
                    ((DruidDataSource) realDataSource).close();
                    log.info("DataSourceSwitcher.dataSource.close.directly,name={}", name);
                }
            }
        } else {
            log.error("DataSourceSwitcher.dataSource.can.not.operate,,name={},type={}", name, source.getClass().getName());
        }
        return true;
    }

    /**
     * 向报表服务推送事件
     * @param name
     * @param alias
     * @param itemDataSource
     */
    private void pushEventToReportService(String name, String alias, ItemDataSource itemDataSource) {
        PushDataSourceRemoveEventDBMarkEnum[] dbMarkEnums = PushDataSourceRemoveEventDBMarkEnum.values();
        String domain = name.split(DS_SPLIT)[1];
        for (PushDataSourceRemoveEventDBMarkEnum markEnum : dbMarkEnums) {
            if (StringUtils.equalsIgnoreCase(alias, markEnum.name())) {
                String url = null;
                try {
                    url = itemDataSource.getConnection().getMetaData().getURL();
                    log.info("DataSourceSwitcher.dataSource.remove.pushEventToReportService domain={} name={} url={}", domain, name, url);
                    if (StringUtils.isNotBlank(url)) {
                        URI uri = new URI(url.substring(5));
                        String host = uri.getHost();
                        int port = uri.getPort();
                        applicationEventPublisher.publishEvent(new DataSourceConnectionURLPushEvent(this, host, port, domain));
                    }
                } catch (Exception e) {
                    log.error("DataSourceSwitcher.dataSource.remove.pushEventToReportService.error name={} url={}", name, url, e);
                }
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public enum PushDataSourceRemoveEventDBMarkEnum {
        MASTER,
        WAREHOUSE
    }
}
