package io.ihankun.framework.cache.stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ReadOffset;

/**
 * stream read offset model
 *
 * @author hankun
 */
@Getter
@RequiredArgsConstructor
public enum ReadOffsetModel {

	/**
	 * 从开始的地方读
	 */
	START(ReadOffset.from("0-0")),
	/**
	 * 从最近的偏移量读取。
	 */
	LATEST(ReadOffset.latest()),
	/**
	 * 读取所有新到达的元素，这些元素的id大于最后一个消费组的id。
	 */
	LAST_CONSUMED(ReadOffset.lastConsumed());

	/**
	 * readOffset
	 */
	private final ReadOffset readOffset;

}
