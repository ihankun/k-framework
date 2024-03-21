package io.ihankun.framework.captcha.v1.aop;

import io.ihankun.framework.captcha.v1.entity.ImageCaptchaTrack;
import io.ihankun.framework.captcha.v1.annotation.Captcha;
import io.ihankun.framework.captcha.v1.application.ImageCaptchaApplication;
import io.ihankun.framework.captcha.v1.entity.CaptchaRequest;
import io.ihankun.framework.common.exception.CaptchaValidException;
import io.ihankun.framework.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author hankun
 */
@Slf4j
public class CaptchaInterceptor implements MethodInterceptor, BeanFactoryAware {

    private ImageCaptchaApplication captchaApplication;

    private BeanFactory beanFactory;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(invocation.getMethod());
        Captcha annotation = AnnotationUtils.findAnnotation(bridgedMethod, Captcha.class);
        String type = annotation.type();
        Object[] arguments = invocation.getArguments();
        CaptchaRequest captchaRequest = null;
        for (Object arg : arguments) {
            if (arg instanceof CaptchaRequest) {
                captchaRequest = (CaptchaRequest) arg;
                break;
            }
        }
        if (captchaRequest == null) {
            log.warn("验证码验证 方法名称:{} 没有找到CaptchaRequest<?> 对象", invocation.getMethod().getName());
            return invocation.proceed();
        }

        String id = captchaRequest.getId();
        ImageCaptchaTrack captchaTrack = captchaRequest.getCaptchaTrack();
        if (captchaTrack == null) {
            throw new CaptchaValidException(type, "ImageCaptchaTrack 不能为空");
        }

        ResponseResult<?> matching = getSliderCaptchaApplication().matching(id, captchaTrack);
        if (matching.isSuccess()) {
            return invocation.proceed();
        }

        throw new CaptchaValidException(type,Integer.valueOf(matching.getCode()) , matching.getMessage());
    }

    public ImageCaptchaApplication getSliderCaptchaApplication() {
        if (captchaApplication == null) {
            this.captchaApplication = beanFactory.getBean(ImageCaptchaApplication.class);
        }
        return captchaApplication;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
