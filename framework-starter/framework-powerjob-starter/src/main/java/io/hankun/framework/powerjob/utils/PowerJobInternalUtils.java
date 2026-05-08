package io.hankun.framework.powerjob.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import tech.powerjob.worker.PowerJobSpringWorker;
import tech.powerjob.worker.PowerJobWorker;
import tech.powerjob.worker.common.WorkerRuntime;

import java.lang.reflect.Field;

/**
 * @description: PowerJob 内部组件访问工具类
 * 通过反射安全地获取 PowerJob Worker 的内部状态
 * @fileName: PowerJobInternalUtils.java
 * @author: hankun
 */

@Slf4j
public final class PowerJobInternalUtils {

    // 缓存 Field 对象，避免重复查找，提高性能
    private static volatile Field powerJobWorkerField;
    private static volatile Field workerRuntimeField;

    private PowerJobInternalUtils() {
        // 工具类，防止实例化
    }

    /**
     * 从 PowerJobSpringWorker Bean 中获取 Worker 实际绑定的网络地址 (IP:Port)
     *
     * @param springWorker PowerJobSpringWorker 的 Bean 实例
     * @return Worker 的真实地址，如 "10.68.8.137:27777"，获取失败则返回 null
     */
    public static String getWorkerAddress(PowerJobSpringWorker springWorker) {
        try {
            // 1. 获取 PowerJobSpringWorker 内部的 PowerJobWorker 实例
            Field workerField = getPowerJobWorkerField();
            PowerJobWorker worker = (PowerJobWorker) workerField.get(springWorker);
            if (worker == null) {
                log.warn("[PowerJobInternalUtils] PowerJobWorker instance inside PowerJobSpringWorker is null.");
                return null;
            }

            // 2. 获取 PowerJobWorker 内部的 WorkerRuntime 实例
            Field runtimeField = getWorkerRuntimeField();
            WorkerRuntime runtime = (WorkerRuntime) runtimeField.get(worker);
            if (runtime == null) {
                log.warn("[PowerJobInternalUtils] WorkerRuntime instance inside PowerJobWorker is null.");
                return null;
            }

            // 3. 从 WorkerRuntime 中获取最终的地址
            String workerAddress = runtime.getWorkerAddress();
            if (!StringUtils.hasText(workerAddress)) {
                log.warn("[PowerJobInternalUtils] Fetched workerAddress from WorkerRuntime is empty.");
                return null;
            }

            return workerAddress;

        } catch (Exception e) {
            log.error("[PowerJobInternalUtils] Failed to get worker address via reflection. " +
                    "This might be due to an incompatible PowerJob version change.", e);
            return null;
        }
    }

    private static Field getPowerJobWorkerField() throws NoSuchFieldException {
        if (powerJobWorkerField == null) {
            synchronized (PowerJobInternalUtils.class) {
                if (powerJobWorkerField == null) {
                    Field field = PowerJobSpringWorker.class.getDeclaredField("powerJobWorker");
                    field.setAccessible(true);
                    powerJobWorkerField = field;
                }
            }
        }
        return powerJobWorkerField;
    }

    private static Field getWorkerRuntimeField() throws NoSuchFieldException {
        if (workerRuntimeField == null) {
            synchronized (PowerJobInternalUtils.class) {
                if (workerRuntimeField == null) {
                    // 该字段在 PowerJobWorker 中是 protected 的
                    Field field = PowerJobWorker.class.getDeclaredField("workerRuntime");
                    field.setAccessible(true);
                    workerRuntimeField = field;
                }
            }
        }
        return workerRuntimeField;
    }
}
