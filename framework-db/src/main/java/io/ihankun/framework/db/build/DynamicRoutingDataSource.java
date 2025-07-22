//package io.ihankun.framework.db.build;
//
//import com.baomidou.dynamic.datasource.ds.GroupDataSource;
//import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
//import com.baomidou.dynamic.datasource.strategy.DynamicDataSourceStrategy;
//import io.ihankun.framework.db.build.config.DsConfigReader;
//import io.ihankun.framework.db.build.ds.DataSourceLoader;
//import io.ihankun.framework.db.build.ds.DsContext;
//import io.ihankun.framework.db.config.DataSourceConstant;
//import io.ihankun.framework.db.dynamic.DomainDynamicDataSourceStrategy;
//import io.ihankun.framework.db.exceptions.DbException;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author hankun
// */
//@Slf4j
//@ConditionalOnProperty(name = "kun.ds.switch.to.old", havingValue = "false", matchIfMissing = true)
//@Component
//public class DynamicRoutingDataSource extends com.baomidou.dynamic.datasource.DynamicRoutingDataSource {
//
//    public static final int SPLIT_COUNT = 2;
//
//    public static final String RANDOM_DOMAIN = "${random}";
//
//    private Class<? extends DynamicDataSourceStrategy> strategy = DomainDynamicDsStrategy.class;
//
//    private Boolean strict = false;
//
//    @Setter
//    @Autowired
//    private DsPropertiesProvider dsPropertiesProvider;
//
//    @Setter
//    @Autowired
//    private DataSourceLoader dataSourceLoader;
//
//    @Setter
//    @Autowired
//    private DsConfigReader dsConfigReader;
//
//    @Setter
//    @Autowired
//    private DynamicDataSourceProperties properties;
//
//    @PostConstruct
//    public void init() {
//        this.strict = properties.getStrict();
//        this.strategy = properties.getStrategy();
//        if (strategy.equals(DomainDynamicDataSourceStrategy.class)) {
//            strategy = DomainDynamicDsStrategy.class;
//        }
//        super.setPrimary(properties.getPrimary());
//        super.setStrategy(strategy);
//        super.setStrict(strict);
//        super.setP6spy(properties.getP6spy());
//        super.setSeata(properties.getSeata());
//    }
//
//    private final Map<String, Object> lockers = new ConcurrentHashMap<>();
//
//    public DataSource getDataSource(String ds) {
//        if (StringUtils.isEmpty(ds)) {
//            return determinePrimaryDataSource();
//        } else {
//            if (dataSourceLoader.groupExists(ds)) {
//                return chooseDataSource(ensureGroupDataSource(ds));
//            } else if (getDataSources().containsKey(ds)) {
//                return getDataSources().get(ds);
//            }
//        }
//        if (strict) {
//            throw new DbException("严格模式，数据源分组不存在：" + ds);
//        }
//        log.info("数据源分组【{}】不存在，使用默认数据源", ds);
//        return determinePrimaryDataSource();
//    }
//
//    private DataSource chooseDataSource(GroupDataSource groupDataSource) {
//        try {
//            DsContext.setDataSourceName(groupDataSource.getGroupName());
//            String dsName;
//            try {
//                dsName = groupDataSource.determineDsKey();
//            } catch (Exception e) {
//                throw new DbException("数据源动态选择失败，策略执行异常，分组："
//                        + groupDataSource.getGroupName() + "，e=" + e.getMessage());
//            }
//            if (StringUtils.isEmpty(dsName)) {
//                throw new DbException("数据源动态选择失败，策略返回空，分组：" + groupDataSource.getGroupName());
//            }
//            //分组情况下，必须返回带_的数据源名称
//            String[] splits = dsName.split(DataSourceConstant.DS_SPLIT);
//            if (splits.length < SPLIT_COUNT) {
//                throw new DbException("数据源动态选择失败，策略未返回有效数据源名称，分组：" + groupDataSource.getGroupName() + "，数据源：" + dsName);
//            }
//            String domain = splits[1];
//            if (RANDOM_DOMAIN.equals(domain)) {
//                for (String d : dsConfigReader.getAllDomain()) {
//                    BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//                    DataSource dataSource = getOrCreateDataSource(groupDataSource, d, buildInfoHolder);
//                    if (dataSource == null) {
//                        log.warn("数据源动态创建失败：{}", buildInfoHolder.getErrorMessage(groupDataSource.getGroupName(), d));
//                    } else {
//                        return dataSource;
//                    }
//                }
//                throw new DbException("数据源创建失败，使用随机数据源，但未找到可用数据源");
//            }
//            BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//            DataSource dataSource = getOrCreateDataSource(groupDataSource, domain, buildInfoHolder);
//            if (dataSource == null) {
//                throw new DbException("数据源动态创建失败：" + buildInfoHolder.getErrorMessage(
//                        groupDataSource.getGroupName(), domain));
//            }
//            return dataSource;
//        } finally {
//            DsContext.clear();
//        }
//    }
//
//    public DataSource loadDataSource(String ds, String domain) {
//        GroupDataSource groupDataSource = ensureGroupDataSource(ds);
//        BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//        DataSource dataSource = getOrCreateDataSource(groupDataSource, domain, buildInfoHolder);
//        if (dataSource == null) {
//            log.error("数据源加载失败：{}", buildInfoHolder.getErrorMessage(ds, domain));
//        }
//        return dataSource;
//    }
//
//    @Override
//    public synchronized void addDataSource(String ds, DataSource dataSource) {
//        ensureGroupDataSource(ds.split(DataSourceConstant.DS_SPLIT)[0]);
//        super.addDataSource(ds, dataSource);
//    }
//
//    private DataSource getOrCreateDataSource(GroupDataSource groupDataSource, String domain, BuildInfoHolder buildInfoHolder) {
//        String groupName = groupDataSource.getGroupName();
//        String dsName = groupName + DataSourceConstant.DS_SPLIT + domain;
//        DataSource dataSource = groupDataSource.getDataSourceMap().get(dsName);
//        if (dataSource == null) {
//            Object locker = lockers.computeIfAbsent(dsName, k -> new Object());
//            synchronized (locker) {
//                dataSource = groupDataSource.getDataSourceMap().get(dsName);
//                if (dataSource == null) {
//                    dataSource = dataSourceLoader.load(groupName, domain, buildInfoHolder);
//                    if (dataSource == null) {
//                        return null;
//                    }
//                    addDataSource(dsName, dataSource);
//                }
//            }
//        }
//        return dataSource;
//    }
//
//    private GroupDataSource ensureGroupDataSource(String ds) {
//        return getGroupDataSources().computeIfAbsent(ds, this::buildGroupDataSource);
//    }
//
//    private DataSource determinePrimaryDataSource() {
//        log.debug("dynamic-datasource switch to the primary datasource");
//        DataSource dataSource = getDataSources().get(getPrimary());
//        if (dataSource != null) {
//            return dataSource;
//        }
//        GroupDataSource groupDataSource = ensureGroupDataSource(getPrimary());
//        return chooseDataSource(groupDataSource);
//    }
//
//
//    private GroupDataSource buildGroupDataSource(String groupName) {
//        try {
//            DynamicDataSourceStrategy sourceStrategy = strategy.newInstance();
//            if (sourceStrategy instanceof DomainDynamicDsStrategy) {
//                ((DomainDynamicDsStrategy) sourceStrategy).setDsConfigReader(dsConfigReader);
//            }
//            return new GroupDataSource(groupName, sourceStrategy);
//        } catch (Exception e) {
//            throw new DbException(e.getMessage());
//        }
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        ensureGroupDataSource(getPrimary());
//        super.afterPropertiesSet();
//        List<String> allDomains = dsConfigReader.getAllDomain();
//        for (String group : dsPropertiesProvider.listGroup()) {
//            GroupDataSource groupDataSource = ensureGroupDataSource(group);
//            if (!groupDataSource.getDataSourceMap().isEmpty()) {
//                log.warn("数据源分组初始化成功，数据源已通过其他方式加载，group={}", group);
//            } else if (checkLoad(groupDataSource)) {
//                boolean suc = false;
//                for (String domain : allDomains) {
//                    BuildInfoHolder buildInfoHolder = new BuildInfoHolder();
//                    DataSource dataSource = getOrCreateDataSource(groupDataSource, domain, buildInfoHolder);
//                    if (dataSource == null) {
//                        log.warn("数据源初始化失败：{}", buildInfoHolder.getErrorMessage(group, domain));
//                    } else {
//                        suc = true;
//                        break;
//                    }
//                }
//                if (!suc && dsConfigReader.getStopWhenInitFailed()) {
//                    throw new DbException("数据源初始化失败，未找到可用的【" + group + "】分组数据源");
//                }
//                log.warn("数据源分组初始化成功，加载对应数据源成功,group={}", group);
//            } else {
//                log.warn("数据源分组初始化成功，对应数据源未加载,group={}", group);
//            }
//        }
//
//    }
//
//    private boolean checkLoad(GroupDataSource groupDataSource) {
//        if (!(groupDataSource.getDynamicDataSourceStrategy() instanceof DomainDynamicDsStrategy)) {
//            return true;
//        }
//        return !dsConfigReader.getLazyLoad();
//    }
//
//}
