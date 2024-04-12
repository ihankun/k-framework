package io.ihankun.framework.common.convert;

import io.ihankun.framework.common.function.CheckedFunction;
import io.ihankun.framework.common.utils.plus.CollectionUtil;
import io.ihankun.framework.common.utils.ConvertUtil;
import io.ihankun.framework.common.utils.plus.ReflectUtil;
import io.ihankun.framework.common.utils.Unchecked;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Converter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 组合 spring cglib Converter 和 spring ConversionService
 *
 * @author hankun
 */
@Slf4j
@AllArgsConstructor
public class ClassConverter implements Converter {
	private static final ConcurrentMap<String, TypeDescriptor> TYPE_CACHE = new ConcurrentHashMap<>();
	private final Class<?> sourceClazz;
	private final Class<?> targetClazz;

	/**
	 * cglib convert
	 *
	 * @param value     源对象属性
	 * @param target    目标对象属性类
	 * @param fieldName 目标的field名，原为 set 方法名，MicaBeanCopier 里做了更改
	 * @return {Object}
	 */
	@Override
	@Nullable
	public Object convert(@Nullable Object value, Class target, final Object fieldName) {
		if (value == null) {
			return null;
		}
		// 类型一样，不需要转换
		if (ClassUtils.isAssignableValue(target, value)) {
			return value;
		}
		try {
			TypeDescriptor targetDescriptor = ClassConverter.getTypeDescriptor(targetClazz, (String) fieldName);
			// 1. 判断 sourceClazz 为 Map
			if (Map.class.isAssignableFrom(sourceClazz)) {
				return ConvertUtil.convert(value, targetDescriptor);
			} else {
				TypeDescriptor sourceDescriptor = ClassConverter.getTypeDescriptor(sourceClazz, (String) fieldName);
				return ConvertUtil.convert(value, sourceDescriptor, targetDescriptor);
			}
		} catch (Throwable e) {
			log.warn("MicaConverter error", e);
			return null;
		}
	}

	private static TypeDescriptor getTypeDescriptor(final Class<?> clazz, final String fieldName) {
		String srcCacheKey = clazz.getName() + fieldName;
		// 忽略抛出异常的函数，定义完整泛型，避免编译问题
		CheckedFunction<String, TypeDescriptor> uncheckedFunction = (key) -> {
			// 这里 property 理论上不会为 null
			Field field = ReflectUtil.getField(clazz, fieldName);
			if (field == null) {
				throw new NoSuchFieldException(fieldName);
			}
			return new TypeDescriptor(field);
		};
		return CollectionUtil.computeIfAbsent(TYPE_CACHE, srcCacheKey, Unchecked.function(uncheckedFunction));
	}
}
