package io.ihankun.framework.cache.pubsub;

import io.ihankun.framework.core.utils.string.CharPool;
import lombok.experimental.UtilityClass;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.Topic;

/**
 * channel 工具类
 *
 * @author hankun
 */
@UtilityClass
class ChannelUtil {

	/**
	 * 获取 pub sub topic
	 *
	 * @param channel channel
	 * @return Topic
	 */
	public static Topic getTopic(String channel) {
		return isPattern(channel) ? new PatternTopic(channel) : new ChannelTopic(channel);
	}

	/**
	 * 判断是否为模糊话题，*、? 和 [...]
	 *
	 * @param channel 话题名
	 * @return 是否模糊话题
	 */
	public static boolean isPattern(String channel) {
		int length = channel.length();
		boolean isRightSqBracket = false;
		// 倒序，因为表达式一般在尾部
		for (int i = length - 1; i > 0; i--) {
			char charAt = channel.charAt(i);
			switch (charAt) {
				case CharPool.ASTERISK:
				case CharPool.QUESTION_MARK:
					if (isEscapeChars(channel, i)) {
						break;
					}
					return true;
				case CharPool.RIGHT_SQ_BRACKET:
					if (isEscapeChars(channel, i)) {
						break;
					}
					isRightSqBracket = true;
					break;
				case CharPool.LEFT_SQ_BRACKET:
					if (isEscapeChars(channel, i)) {
						break;
					}
					if (isRightSqBracket) {
						return true;
					}
					break;
				default:
					break;
			}
		}
		return false;
	}

	/**
	 * 判断是否为转义字符
	 *
	 * @param name  话题名
	 * @param index 索引
	 * @return 是否为转义字符
	 */
	private static boolean isEscapeChars(String name, int index) {
		if (index < 1) {
			return false;
		}
		// 预读一位，判断是否为转义符 “/”
		char charAt = name.charAt(index - 1);
		return CharPool.BACK_SLASH == charAt;
	}

}
