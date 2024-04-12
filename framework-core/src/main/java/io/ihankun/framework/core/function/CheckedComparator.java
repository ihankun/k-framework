package io.ihankun.framework.core.function;

import java.io.Serializable;

/**
 * 受检的 Comparator
 *
 * @author hankun
 */
@FunctionalInterface
public interface CheckedComparator<T> extends Serializable {

	/**
	 * Compares its two arguments for order.
	 *
	 * @param o1 o1
	 * @param o2 o2
	 * @return int
	 * @throws Throwable CheckedException
	 */
	int compare(T o1, T o2) throws Throwable;

}
