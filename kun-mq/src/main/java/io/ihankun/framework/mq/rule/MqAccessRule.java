package io.ihankun.framework.mq.rule;

import io.ihankun.framework.mq.config.MqRuleProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author hankun
 */
@Slf4j
@Component
@ConditionalOnBean(value = MqRuleProperties.class)
public class MqAccessRule {

    @Value("${spring.application.name:none}")
    private String service;
    @Resource
    private MqRuleProperties rule;

    public boolean auth(String topic) {
        try {
            if (rule == null) {
                return true;
            }
            if (!rule.isEnabled()) {
                return true;
            }
            if (rule.getTopics() == null) {
                return true;
            }
            if (!rule.getTopics().containsKey(service)) {
                return true;
            }
            if (rule.getTopics().get(service) == null) {
                return true;
            }

            if (rule.getTopics().get(service).contains(topic)) {
                return true;
            }

            log.info("您使用的topic：{}，需要得到技术中台组授权", topic);
            return false;
        } catch (Exception e) {
            log.error("mq access auth", e);
        }
        return true;
    }
}
