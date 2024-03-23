package io.ihankun.framework.common.v1.function;

import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 受检的 function
 *
 * @author hankun
 */
@FunctionalInterface
public interface CheckedFunction<T, R> extends Serializable {

	/**
	 * Run the Function
	 *
	 * @param t T
	 * @return R R
	 * @throws Throwable CheckedException
	 */
	@Nullable
	R apply(@Nullable T t) throws Throwable;

}
