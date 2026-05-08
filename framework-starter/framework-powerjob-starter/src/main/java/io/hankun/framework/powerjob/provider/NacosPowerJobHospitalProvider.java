package io.hankun.framework.powerjob.provider;

import io.hankun.framework.core.error.impl.CommonErrorCode;
import io.hankun.framework.core.exception.BusinessException;
import io.hankun.framework.powerjob.config.HospitalConfig;
import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: PowerHospitalProvider 的默认实现，从 Nacos 的配置中获取医院列表。
 * <p>
 *  它通过 @ConfigurationProperties 注解绑定 'k.domain.config' 的配置。
 *
 * @fileName: NacosPowerHospitalProvider.java
 * @author: hankun
 */

@Slf4j
@Component
@ConfigurationProperties(prefix = "k.domain")
@Setter
public class NacosPowerJobHospitalProvider implements PowerJobHospitalProvider {

    /**
     * 接收来自配置文件 'k.domain.config' 下的所有医院配置。
     * Key 是医院的唯一标识符（如 'hospital_101'），Value 是 HospitalConfig 对象。
     */
    private Map<String, HospitalConfig> config;

    @Override
    public List<PowerJobHospitalTask> supplyHospitals() {
        if (CollectionUtils.isEmpty(config)) {
            log.warn("[PowerJob] 配置 'k.domain.config' 为空...");
            return Collections.emptyList();
        }
        List<PowerJobHospitalTask> hospitalTasks = config.values().stream()
                .filter(item -> item.getIsSupportJob() == null || Boolean.TRUE.equals(item.getIsSupportJob()))
                .map(this::convertToHospitalTask)
                .collect(Collectors.toList());
        log.info("[PowerJob] 提供了 {} 家已启用的医院。", hospitalTasks.size());
        return hospitalTasks;
    }

    /**
     * 将从配置文件解析出的 HospitalConfig 对象，转换为框架内部流转的 PowerHospitalTask 对象。
     * @param hospitalConfig 从配置文件绑定的原始医院配置
     * @return 框架内部使用的医院任务单元
     */
    private PowerJobHospitalTask convertToHospitalTask(HospitalConfig hospitalConfig) {
        PowerJobHospitalTask task = new PowerJobHospitalTask();
        task.setOrgId(hospitalConfig.getOrgId());
        task.setHospitalId(hospitalConfig.getHospitalId());
        task.setName(hospitalConfig.getName());
        // 核心修正：从 HospitalConfig 中获取 host 并设置到 PowerHospitalTask 中
        task.setDomain(hospitalConfig.getHost());
        return task;
    }

    /**
     * 根据医院ID和机构ID获取具体的医院配置信息。
     * 这使得我们可以在运行时动态查询配置。
     *
     * @param orgId 机构ID
     * @param hospitalId 医院ID
     * @return Optional<HospitalConfig>
     */
    public Optional<HospitalConfig> findConfigById(Long orgId, Long hospitalId) {
        if (config == null) {
            return Optional.empty();
        }
        return config.values().stream()
                .filter(item -> item.getHospitalId().equals(hospitalId) && item.getOrgId().equals(orgId))
                .findFirst();
    }

    /**
     * 根据ID获取配置，如果找不到则抛出业务异常。
     * (完全兼容 JobHospitalDomainHolder 的 getById 方法)
     *
     * @param orgId 机构ID
     * @param hospitalId 医院ID
     * @return HospitalConfig
     */
    public HospitalConfig getConfigById(Long orgId, Long hospitalId) {
        return findConfigById(orgId, hospitalId)
                .orElseThrow(() -> BusinessException.build(CommonErrorCode.RESULT_NULL));
    }
}
