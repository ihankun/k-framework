package io.hankun.framework.powerjob.config;

import io.hankun.framework.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 用于映射 'k.domain.config' 下单个医院配置的实体类。
 * <p>
 * 继承自 BaseEntity，以遵循统一开发规范。
 *
 * @fileName: HospitalConfig.java
 * @author: hankun
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class HospitalConfig extends BaseEntity {

    /**
     * 对应 k.domain.config.xxx.host
     */
    private String host;

    /**
     * 对应 k.domain.config.xxx.name
     */
    private String name;

    /**
     * 对应 k.domain.config.xxx.org-id
     */
    private Long orgId;

    /**
     * 对应 k.domain.config.xxx.hospital-id
     */
    private Long hospitalId;

    /**
     * 对应 k.domain.config.xxx.gateway-url
     */
    private String gatewayUrl;

    /**
     * 对应 k.domain.config.xxx.is-support-job
     */
    private Boolean isSupportJob;

    // --- 重写方法，确保行为正确 ---

    @Override
    public Long getOrgId() {
        return this.orgId;
    }
}