package io.hankun.framework.redis.ratelimiter;

import io.hankun.framework.core.spel.ExpressionEvaluator;
import io.hankun.framework.core.utils.string.CharPool;
import io.hankun.framework.core.utils.string.StringUtil;
import io.hankun.framework.redis.config.RedisRateLimitProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis 限流
 *
 * @author hankun
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RedisRateLimiterAspect implements ApplicationContextAware {
	/**
	 * 表达式处理
	 */
	private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
	/**
	 * redis 限流服务
	 */
	private final RedisRateLimiterClient rateLimiterClient;
	/**
	 * 限流配置属性
	 */
	@Resource
	private RedisRateLimitProperties rateLimitProperties;
	/**
	 * 路径匹配器
	 */
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	private ApplicationContext applicationContext;

	/**
	 * AOP 环切 注解 @RateLimiter
	 */
	@Around("@annotation(limiter)")
	public Object aroundRateLimiter(ProceedingJoinPoint point, RateLimiter limiter) throws Throwable {
		// 检查白名单
		if (isWhiteListed()) {
			if (log.isDebugEnabled()) {
				log.debug("Rate limit white list matched, skip limit check");
			}
			return point.proceed();
		}

		String limitKey = limiter.value();
		Assert.hasText(limitKey, "@RateLimiter value must have length; it must not be null or empty");
		// el 表达式
		String limitParam = limiter.param();
		// 表达式不为空
		String rateKey;
		if (StringUtil.isNotBlank(limitParam)) {
			String evalAsText = evalLimitParam(point, limitParam);
			rateKey = limitKey + CharPool.COLON + evalAsText;
		} else {
			rateKey = limitKey;
		}
		long max = limiter.max();
		long ttl = limiter.ttl();
		TimeUnit timeUnit = limiter.timeUnit();
		if (log.isDebugEnabled()) {
			log.debug("Rate limit check: key={}, max={}, ttl={} {}", rateKey, max, ttl, timeUnit);
		}
		return rateLimiterClient.allow(rateKey, max, ttl, timeUnit, point::proceed);
	}

	/**
	 * 检查当前请求是否在白名单中
	 *
	 * @return true 表示在白名单中，跳过限流
	 */
	private boolean isWhiteListed() {
		List<String> whiteList = rateLimitProperties.getWhiteList();
		if (whiteList == null || whiteList.isEmpty()) {
			return false;
		}

		// 获取当前请求路径
		String requestPath = getRequestPath();
		if (StringUtil.isBlank(requestPath)) {
			return false;
		}

		// 匹配白名单
		for (String pattern : whiteList) {
			if (pathMatcher.match(pattern, requestPath)) {
				if (log.isDebugEnabled()) {
					log.debug("Request path [{}] matched white list pattern [{}]", requestPath, pattern);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前请求路径
	 *
	 * @return 请求路径，非 Web 环境返回 null
	 */
	private String getRequestPath() {
		try {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				HttpServletRequest request = attributes.getRequest();
				return request.getRequestURI();
			}
		} catch (Exception e) {
			log.warn("Failed to get request path", e);
		}
		return null;
	}

	/**
	 * 计算参数表达式
	 *
	 * @param point      ProceedingJoinPoint
	 * @param limitParam limitParam
	 * @return 结果
	 */
	private String evalLimitParam(ProceedingJoinPoint point, String limitParam) {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		Object target = point.getTarget();
		Class<?> targetClass = target.getClass();
		EvaluationContext context = evaluator.createContext(method, args, target, targetClass, applicationContext);
		AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
		return evaluator.evalAsText(limitParam, elementKey, context);
	}

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
