package io.ihankun.framework.core.utils.plus;


import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类
 *
 * @author hankun
 */
@SuppressWarnings("unchecked")
public class CollectionUtil extends CollectionUtils {

	/**
	 * Return {@code true} if the supplied Collection is not {@code null} or empty.
	 * Otherwise, return {@code false}.
	 *
	 * @param collection the Collection to check
	 * @return whether the given Collection is not empty
	 */
	public static boolean isNotEmpty(@Nullable Collection<?> collection) {
		return !CollectionUtils.isEmpty(collection);
	}

	/**
	 * Return {@code true} if the supplied Map is not {@code null} or empty.
	 * Otherwise, return {@code false}.
	 *
	 * @param map the Map to check
	 * @return whether the given Map is not empty
	 */
	public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
		return !CollectionUtils.isEmpty(map);
	}

	/**
	 * Check whether the given Array contains the given element.
	 *
	 * @param array   the Array to check
	 * @param element the element to look for
	 * @param <T>     The generic tag
	 * @return {@code true} if found, {@code false} else
	 */
	public static <T> boolean contains(@Nullable T[] array, final T element) {
		if (array == null) {
			return false;
		}
		return Arrays.stream(array).anyMatch(x -> ObjectUtils.nullSafeEquals(x, element));
	}

	/**
	 * Concatenates 2 arrays
	 *
	 * @param one   数组1
	 * @param other 数组2
	 * @return 新数组
	 */
	public static String[] concat(String[] one, String[] other) {
		return concat(one, other, String.class);
	}

	/**
	 * Concatenates 2 arrays
	 *
	 * @param one   数组1
	 * @param other 数组2
	 * @param clazz 数组类
	 * @return 新数组
	 */
	public static <T> T[] concat(T[] one, T[] other, Class<T> clazz) {
		T[] target = (T[]) Array.newInstance(clazz, one.length + other.length);
		System.arraycopy(one, 0, target, 0, one.length);
		System.arraycopy(other, 0, target, one.length, other.length);
		return target;
	}

	/**
	 * 不可变 Set
	 *
	 * @param es  对象
	 * @param <E> 泛型
	 * @return 集合
	 */
	@SafeVarargs
	public static <E> Set<E> ofImmutableSet(E... es) {
		return Arrays.stream(Objects.requireNonNull(es, "args es is null.")).collect(Collectors.toSet());
	}

//	/**
//	 * 不可变 List
//	 *
//	 * @param es  对象
//	 * @param <E> 泛型
//	 * @return 集合
//	 */
//	@SafeVarargs
//	public static <E> List<E> ofImmutableList(E... es) {
//		return Arrays.stream(Objects.requireNonNull(es, "args es is null.")).toList();
//	}

	/**
	 * Iterable 转换为List集合
	 *
	 * @param elements Iterable
	 * @param <E>      泛型
	 * @return 集合
	 */
	public static <E> List<E> toList(Iterable<E> elements) {
		Objects.requireNonNull(elements, "elements es is null.");
		if (elements instanceof Collection) {
			return new ArrayList<>((Collection<E>) elements);
		}
		Iterator<E> iterator = elements.iterator();
		List<E> list = new ArrayList<>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	/**
	 * 将key value 数组转为 map
	 *
	 * @param keysValues key value 数组
	 * @param <K>        key
	 * @param <V>        value
	 * @return map 集合
	 */
	public static <K, V> Map<K, V> toMap(Object... keysValues) {
		int kvLength = keysValues.length;
		if (kvLength % 2 != 0) {
			throw new IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd");
		}
		Map<K, V> keyValueMap = new HashMap<>(kvLength);
		for (int i = kvLength - 2; i >= 0; i -= 2) {
			Object key = keysValues[i];
			Object value = keysValues[i + 1];
			keyValueMap.put((K) key, (V) value);
		}
		return keyValueMap;
	}

	/**
	 * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
	 * This class should be removed once we drop Java 8 support.
	 *
	 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
	 */
	public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
		V value = map.get(key);
		if (value != null) {
			return value;
		}
		return map.computeIfAbsent(key, mappingFunction);
	}

	/**
	 * list 分片
	 *
	 * @param list List
	 * @param size 分片大小
	 * @param <T>  泛型
	 * @return List 分片
	 */
	public static <T> List<List<T>> partition(List<T> list, int size) {
		Objects.requireNonNull(list, "List to partition must not null.");
		Assert.isTrue(size > 0, "List to partition size must more then zero.");
		return (list instanceof RandomAccess)
			? new RandomAccessPartition<>(list, size)
			: new Partition<>(list, size);
	}

	private static class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {
		RandomAccessPartition(List<T> list, int size) {
			super(list, size);
		}
	}

	private static class Partition<T> extends AbstractList<List<T>> {
		final List<T> list;
		final int size;

		Partition(List<T> list, int size) {
			this.list = list;
			this.size = size;
		}

		@Override
		public List<T> get(int index) {
			if (index >= 0 && index < this.size()) {
				int start = index * this.size;
				int end = Math.min(start + this.size, this.list.size());
				return this.list.subList(start, end);
			}
			throw new IndexOutOfBoundsException(String.format("index (%s) must be less than size (%s)", index, this.size()));
		}

		@Override
		public int size() {
			return ceilDiv(this.list.size(), this.size);
		}

		@Override
		public boolean isEmpty() {
			return this.list.isEmpty();
		}

		private static int ceilDiv(int x, int y) {
			int r = x / y;
			if (r * y < x) {
				r++;
			}
			return r;
		}
	}

}
