package io.hankun.framework.powerjob.annotation;

import io.hankun.framework.powerjob.enums.PowerJobExecLevel;
import org.springframework.stereotype.Component;
import tech.powerjob.common.enums.TimeExpressionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 定时任务注解。
 * <p>
 * 用于标记一个类为 framework-powerjob 任务处理器，并提供该任务的核心元数据。
 * 被此注解标记的类，框架会自动扫描并将其注册到 PowerJob-Server。
 * <p>
 * 此注解已集成了 {@link Component}，被标记的类会自动注册为 Spring Bean。
 *
 * @author: hankun
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface PowerJobInfo {

    /**
     * 任务名称。在 PowerJob 应用内必须唯一。
     */
    String name();

    /**
     * cron 表达式，用于控制作业触发时间。
     * 仅在 timeExpressionType 为 CRON 时有效。
     */
    String cron() default "";

    /**
     * 任务描述信息。
     */
    String description() default "No description provided";

    /**
     * 时间表达式类型。默认为 API，表示默认不自动执行，只能手动触发。
     * 如果要使用 cron，请务必将其设置为 TimeExpressionType.CRON。
     */
    TimeExpressionType timeExpressionType() default TimeExpressionType.API;

    /**
     * 时间表达式，与 timeExpressionType 配合使用。
     * - CRON: cron 表达式 (注意：cron属性是此属性的快捷方式)
     * - FIXED_RATE: 固定频率，单位毫秒
     * - FIXED_DELAY: 固定延迟，单位毫秒
     */
    String timeExpression() default "";

    /**
     * 任务执行级别，定义了任务的执行模型。
     * 默认为 HOSPITAL，即按医院并行处理。
     */
    PowerJobExecLevel execLevel() default PowerJobExecLevel.HOSPITAL;

    /**
     * 任务默认参数。
     * 可以在此指定任务的默认参数，例如 'pageSize=100'。
     */
    String jobParams() default "";

    /**
     * 是否自动注册和更新任务到 PowerJob-Server。
     * 默认为 true。如果设置为 false，则需要运维人员在控制台手动创建任务。
     */
    boolean autoRegister() default true;

    /**
     * 是否启用自定义 Map 逻辑。
     * 如果为 true，框架将调用开发者重写的 customMap() 方法来进行任务切分。
     * 如果为 false (默认)，框架将根据 execLevel 的配置执行内置的 Map 逻辑。
     */
    boolean customMap() default false;

    /**
     * 【核心新增】: 标记本次任务的变更是向下兼容的。
     * 这是一个必填参数，开发者必须明确指定。
     * - true: 表示任务逻辑兼容旧版本。当集群中存在多个版本时，该任务可以在【所有节点】（正式+灰度）上执行。
     * - false: 表示任务逻辑不兼容旧版本。当集群中存在多个版本时，该任务必须在【最新的灰度节点】上执行。
     */
    boolean compatibleChange();
}