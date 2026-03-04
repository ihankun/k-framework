//package io.hankun.framework.mq.nacos;
//
//import io.hankun.framework.common.nacos.INacosConfigChangeListener;
//import io.hankun.framework.mq.consumer.IMqConsumer;
//import io.hankun.framework.mq.producer.IMqProducer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Properties;
//
///**
// * @author hankun
// */
//@Slf4j
//@Component
//public class RocketMqUrlChangeListener implements INacosConfigChangeListener {
//
//    @Resource
//    IMqProducer IMqProducer;
//
//    @Resource
//    IMqConsumer kunMqConsumer;
//
//
//    @Override
//    public String dataId() {
//        return "config-common-rocket-mq.properties";
//    }
//
//    @Override
//    public void changed(Properties content) {
//        String url = content.getProperty("mq.url");
//        log.info("RocketMqUrlChangeListener.changed,old.url={},new.url={}", IMqProducer.properties().getUrl(), url);
//        if (IMqProducer != null) {
//            IMqProducer.restart(url);
//        }
//        if (kunMqConsumer != null) {
//            kunMqConsumer.restart(url);
//        }
//    }
//}
