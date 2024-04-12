package io.ihankun.framework.common.function;

import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 受检的 Consumer
 *
 * @author hankun
 */
@FunctionalInterface
public interface CheckedConsumer<T> extends Serializable {

	/**
	 * Run the Consumer
	 *
	 * @param t T
	 * @throws Throwable UncheckedException
	 */
	void accept(@Nullable T t) throws Throwable;

}
