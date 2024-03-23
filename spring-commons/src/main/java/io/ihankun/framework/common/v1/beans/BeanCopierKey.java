package io.ihankun.framework.common.v1.beans;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * copy key
 *
 * @author hankun
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BeanCopierKey {
	private final Class<?> source;
	private final Class<?> target;
	private final boolean useConverter;
	private final boolean nonNull;
}
