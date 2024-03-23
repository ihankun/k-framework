package io.ihankun.framework.common.v1.beans;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * bean map key，提高性能
 *
 * @author hankun
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class BeanMapKey {
	private final Class type;
	private final int require;
}
