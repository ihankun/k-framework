package io.hankun.framework.powerjob.context;

import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;
import tech.powerjob.worker.core.processor.TaskContext;

import java.util.List;

/**
 * @param powerJobContext PowerJob 的原生任务上下文。
 *                        开发者可以通过它获取 jobParams, instanceParams, OmsLogger 等。
 * @param domain          当前执行级别下所关联的域名。
 *                        - HOSPITAL 级别: 当前处理的单个医院所属的域名。
 *                        - DOMAIN 级别: 当前处理的域名分组的域名。
 *                        - GLOBAL_SINGLE/SIMPLE 级别: 通常为空。
 * @param hospitals       本次执行需要处理的医院列表。
 *                        - HOSPITAL 级别: 列表中只包含一家医院。
 *                        - DOMAIN 级别: 列表中包含该域名下的所有医院。
 *                        - GLOBAL_SINGLE 级别: 列表中包含所有医院。
 * @description: framework-powerjob v2.0 任务执行的上下文。
 * framework-powerjob v2.0 任务执行的上下文。
 * <p>
 * 这是在 {@code AbstractPowerJob.execute} 方法中传递给开发者的唯一参数，
 * 封装了本次执行所需的所有信息。
 * @fileName: PowerExecutionContext.java
 * @author: hankun
 */

public record PowerJobExecutionContext(TaskContext powerJobContext, String domain, List<PowerJobHospitalTask> hospitals) {

    /**
     * 构造器，由框架内部调用。
     */
    public PowerJobExecutionContext {
    }

    /**
     * 便捷方法，获取当前处理的单家医院。
     * 仅在 HOSPITAL 级别下有意义，其他级别下可能返回 null 或列表中的第一个。
     *
     * @return 当前处理的单个医院任务对象
     */
    public PowerJobHospitalTask getSingleHospital() {
        if (hospitals != null && !hospitals.isEmpty()) {
            return hospitals.getFirst();
        }
        return null;
    }
}
