package io.hankun.framework.powerjob.entity;


import io.hankun.framework.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @description: 承载执行一个原子业务单元（即一家医院）任务所需的动态参数。
 * <p>该对象将在 Map 阶段被创建并分发到不同的 worker 节点。</p>
 * <p>它必须实现 Serializable 接口，因为 PowerJob 会在网络间序列化和传输它。</p>
 * @fileName: PowerHospitalTask.java
 * @author: hankun
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PowerJobHospitalTask extends BaseEntity {

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 医院ID
     */
    private Long hospitalId;

    /**
     * 医院名称
     */
    private String name;

    /**
     * 域名
     */
    private String domain;

    // 待拓展

    /**
     * 重写父类的 getOrgId 方法，返回当前任务单元持有的 orgId。
     * @return a
     */
    @Override
    public Long getOrgId() {
        return this.orgId;
    }
}