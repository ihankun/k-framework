package io.ihankun.framework.core.utils.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 跟踪类变动比较
 *
 * @author hankun
 */
@Getter
public class BeanDiff implements Serializable {
	/**
	 * 变更字段
 	 */
	@JsonIgnore
	private final transient Set<String> fields = new HashSet<>();
	/**
	 * 旧值
	 */
	@JsonIgnore
	private final transient Map<String, Object> oldValues = new HashMap<>();
	/**
	 * 新值
	 */
	@JsonIgnore
	private final transient Map<String, Object> newValues = new HashMap<>();
}
