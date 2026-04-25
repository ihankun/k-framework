package io.hankun.framework.powerjob.provider;

import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;

import java.util.List;

/**
 * @description: 医院列表提供者接口。
 * <p>
 *  该接口定义了一个标准的契约，用于获取所有需要执行SaaS化定时任务的医院列表。
 *  通过面向接口编程，核心框架将与具体的数据来源（如Nacos、数据库、API等）解耦，
 *  提高框架的可测试性和可扩展性。
 *
 * @fileName: PowerHospitalProvider.java
 * @author: hankun
 */

public interface PowerJobHospitalProvider {

    /**
     * 提供需要执行SaaS化任务的医院列表。
     * 框架将在每次SaaS任务的Map阶段调用此方法，以获取最新的医院列表进行任务分发。
     *
     * @return 医院任务单元（PowerHospitalTask）的列表。如果无可执行的医院，应返回空列表而非null。
     */
    List<PowerJobHospitalTask> supplyHospitals();
}
