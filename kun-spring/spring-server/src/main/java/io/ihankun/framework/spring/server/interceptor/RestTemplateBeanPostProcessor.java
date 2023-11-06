package io.ihankun.framework.spring.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class RestTemplateBeanPostProcessor implements BeanPostProcessor {

    /**
     * bean对象被初始化之后执行的方法
     *
     * @param bean     要处理的bean的对象
     * @param beanName 要处理的bean的名称或者ID
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            log.debug("添加restTemplate日志拦截器:{}", beanName);
            ((RestTemplate) bean).getInterceptors().add(new RestTemplateLoggingInterceptor());
            return bean;
        }
        return bean;
    }

}
