package io.hankun.framework.powerjob.listener;


import io.hankun.framework.powerjob.utils.PowerJobInternalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.PowerJobSpringWorker;

import java.util.Map;

/**
 * 监听 Spring 容器启动完成事件，将 PowerJob Worker 的真实地址同步到 Nacos 元数据中
 * @author hankun
 */
@Slf4j
@Component
public class PowerJobNacosSyncListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 定义一个常量作为 Nacos 元数据中的 Key
     */
    public static final String POWERJOB_WORKER_ADDRESS_KEY = "powerjob.worker.address";

    private final ApplicationContext applicationContext;

    public PowerJobNacosSyncListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // 从 Spring 容器中获取需要的 Bean
            PowerJobSpringWorker springWorker = applicationContext.getBean(PowerJobSpringWorker.class);
            ServiceRegistry serviceRegistry = applicationContext.getBean(ServiceRegistry.class);
            Registration registration = applicationContext.getBean(Registration.class);

            // 使用我们创建的工具类获取真实地址
            String workerAddress = PowerJobInternalUtils.getWorkerAddress(springWorker);

            if (workerAddress == null) {
                log.warn("[PowerJobNacosSync] Could not determine PowerJob worker address. Skipping Nacos metadata update.");
                return;
            }

            log.info("[PowerJobNacosSync] Successfully fetched PowerJob worker's real address: {}", workerAddress);

            // 将真实地址添加到 Nacos 的元数据中
            Map<String, String> metadata = registration.getMetadata();
            String existAddress = metadata.get(POWERJOB_WORKER_ADDRESS_KEY);

            // 避免重复注册
            if(workerAddress.equals(existAddress)){
                log.info("[PowerJobNacosSync] PowerJob worker address is already up-to-date in Nacos metadata. No action needed.");
                return;
            }

            metadata.put(POWERJOB_WORKER_ADDRESS_KEY, workerAddress);

            // 重新注册服务，以更新 Nacos Server 上的元数据
            serviceRegistry.register(registration);

            log.info("[PowerJobNacosSync] Successfully synced PowerJob worker address to Nacos metadata. " +
                    "Key: [{}], Value: [{}]", POWERJOB_WORKER_ADDRESS_KEY, workerAddress);

        } catch (Exception e) {
            // 捕捉所有异常，防止影响应用主启动流程
            log.error("[PowerJobNacosSync] An unexpected error occurred while syncing PowerJob address to Nacos.", e);
        }
    }
}