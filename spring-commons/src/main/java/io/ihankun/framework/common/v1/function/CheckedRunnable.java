package io.ihankun.framework.common.v1.function;

import java.io.Serializable;

/**
 * 受检的 runnable
 *
 * @author hankun
 */
@FunctionalInterface
public interface CheckedRunnable extends Serializable {

	/**
	 * Run this runnable.
	 *
	 * @throws Throwable CheckedException
	 */
	void run() throws Throwable;

}
