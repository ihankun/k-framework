package io.ihankun.framework.common.v1.spel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * ExpressionRootObject
 *
 * @author hankun
 */
@Getter
@RequiredArgsConstructor
public class ExpressionRootObject {
	private final Method method;

	private final Object[] args;

	private final Object target;

	private final Class<?> targetClass;

	private final Method targetMethod;
}
