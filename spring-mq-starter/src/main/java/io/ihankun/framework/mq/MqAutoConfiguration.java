package io.ihankun.framework.mq;

import io.ihankun.framework.common.v1.utils.spring.SpringHelpers;
import io.ihankun.framework.mq.config.MqProperties;
import io.ihankun.framework.mq.constants.MqTypeEnum;
import io.ihankun.framework.mq.consumer.ConsumerListener;
import io.ihankun.framework.mq.consumer.IMqConsumer;
import io.ihankun.framework.mq.consumer.impl.KafkaMqConsumer;
import io.ihankun.framework.mq.consumer.impl.RocketMqConsumer;
import io.ihankun.framework.mq.producer.IMqProducer;
import io.ihankun.framework.mq.producer.impl.KafkaMqProducer;
import io.ihankun.framework.mq.producer.impl.RocketMqProducer;
import io.ihankun.framework.mq.rule.MqAccessRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hankun
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "kun.mq")
@ComponentScan(basePackageClasses = MqAutoConfiguration.class)
public class MqAutoConfiguration implements ApplicationContextAware {

    private static final String META_MARK = "mark";

    public static final String GRAY_MARK = "gray";

    private MqProperties mq;

    private ApplicationContext context;

    private List<String> loadedListenerList = new ArrayList<>(16);

    public static final int UPDATE_TIME = 5;

    @Resource
    private NacosPropertiesLoader nacosPropertiesLoader;

    @Resource
    private MqAccessRule mqAccessRule;

    @PostConstruct
    public IMqConsumer initConsumer() {
        if (mq == null) {
            log.info("mq.config is null");
            return null;
        }
        boolean enable = mq.getConsumer() != null && mq.getConsumer().isEnable();
        if (!enable) {
            log.warn("mq.consumer.enable=false");
            return null;
        }

        IMqConsumer consumer = null;

        String type = mq.getType();
        if (MqTypeEnum.ROCKETMQ.getType().equals(type)) {
            consumer = new RocketMqConsumer();
        } else if (MqTypeEnum.KAFKA.getType().equals(type)) {
            consumer = new KafkaMqConsumer();
        }

        if (consumer != null) {
            consumer.start(mq);
        }
        return consumer;
    }

    /**
     * 使用懒加载模式，避免在没有生产者的场景下也对生产者进行初始化
     *
     * @return
     */
    @Bean
    @Lazy
    public IMqProducer getMqProducer() {

        if (mq == null) {
            log.info("mq.config is null");
            return null;
        }
        boolean enable = mq.getProducer() != null && mq.getProducer().isEnable();
        if (!enable) {
            log.warn("mq.producer.enable=false");
            return null;
        }

        String type = mq.getType();
        IMqProducer producer = null;
        if (MqTypeEnum.ROCKETMQ.getType().equals(type)) {
            producer = new RocketMqProducer();
        } else if (MqTypeEnum.KAFKA.getType().equals(type)) {
            producer = new KafkaMqProducer();
        } else {
            log.error("can't find this kind producer implement of type +" + type);
        }

        if (null == producer) {
            log.warn("mq.producer init after is null");
            return null;
        }

        producer.init(mq);
        return producer;
    }


    private List<ConsumerListener> fetchListener() {
        if (context == null) {
            log.error("ApplicationContext is null");
            return null;
        }

        Map<String, ConsumerListener> consumers;

        try {
            consumers = context.getBeansOfType(ConsumerListener.class);
        } catch (Exception e) {
            return null;
        }

        if (consumers == null || consumers.size() == 0) {
            return null;
        }
        List<ConsumerListener> listenerList = new ArrayList<>(16);
        consumers.forEach((key, value) -> {
            if (!mqAccessRule.auth(value.subscribeTopic())) {
                return;
            }
            if (!loadedListenerList.contains(key)) {
                listenerList.add(value);
                loadedListenerList.add(key);
            }
        });

        return listenerList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        SpringHelpers.setContext(this.context);
    }

    @PostConstruct
    public void init() {
        log.info("MqAutoConfiguration.init");
    }
}
