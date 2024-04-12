package io.ihankun.framework.core.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * tuple Pair
 *
 * @author hankun
 **/
@Getter
@ToString
@EqualsAndHashCode
public class Pair<L, R> {
	private static final Pair<Object, Object> EMPTY = new Pair<>(null, null);

	private final L left;
	private final R right;

	/**
	 * Returns an empty pair.
	 */
	@SuppressWarnings("unchecked")
	public static <L, R> Pair<L, R> empty() {
		return (Pair<L, R>) EMPTY;
	}

	/**
	 * Constructs a pair with its left value being {@code left}, or returns an empty pair if
	 * {@code left} is null.
	 *
	 * @return the constructed pair or an empty pair if {@code left} is null.
	 */
	public static <L, R> Pair<L, R> createLeft(L left) {
		if (left == null) {
			return empty();
		} else {
			return new Pair<>(left, null);
		}
	}

	/**
	 * Constructs a pair with its right value being {@code right}, or returns an empty pair if
	 * {@code right} is null.
	 *
	 * @return the constructed pair or an empty pair if {@code right} is null.
	 */
	public static <L, R> Pair<L, R> createRight(R right) {
		if (right == null) {
			return empty();
		} else {
			return new Pair<>(null, right);
		}
	}

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public static <L, R> Pair<L, R> create(@JsonProperty("left") L left, @JsonProperty("right") R right) {
		if (right == null && left == null) {
			return empty();
		} else {
			return new Pair<>(left, right);
		}
	}

	private Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

}
