package io.hankun.framework.powerjob.enums;

/**
 * @description: 任务执行级别枚举。
 * <p>
 * 该枚举用于在 @PowerJob 注解中配置，以决定任务的执行模型。
 * @fileName: PowerExecLevel.java
 * @author: hankun
 */
public enum PowerJobExecLevel {


    /**
     * 按医院并行模式 (SaaS化，默认)。
     * <p>
     * 适用于可复用的、需要对每家医院独立执行的SaaS化任务。
     * 框架会将任务自动拆分到医院维度，并通过 PowerJob 的 MAP_REDUCE 模式在集群中并行执行。
     * 开发者的 {@code execute} 方法会被多次调用，每次处理一家医院。
     */
    HOSPITAL,

    /**
     * 按域名并行模式 (SaaS化)。
     * <p>
     * 适用于SaaS化任务，但其业务逻辑更适合按域名进行批量处理。
     * 框架会将医院按域名分组，每个域名分组作为一个子任务，通过 MAP_REDUCE 模式在集群中并行执行。
     * 开发者的 {@code execute} 方法会被多次调用，每次处理一个域名下的所有医院。
     */
    DOMAIN,

    /**
     * 全局单次模式 (SaaS化)。
     * <p>
     * 适用于需要一次性获取所有医院信息进行统一处理的SaaS化任务，例如全局统计、数据比对等。
     * 任务将由 PowerJob 以 STANDALONE 模式调度，只在集群中的某一台机器上执行一次，
     * 但在 {@code execute} 方法中可以获取到所有医院的列表。
     */
    GLOBAL_SINGLE
}
