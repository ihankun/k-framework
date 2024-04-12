package io.ihankun.framework.redis.enums;

/**
 * redis常量
 *
 * @author BlackR
 */
public class RedisCommand {

	public static final String BITCOUNT = "BITCOUNT";

	/**
	 *  <a href="https://redis.io/commands/bitcount/#History">redis 版本 7.0以上</a>
	 */
	public enum BitMapModel {
		/**
		 *  BYTE
		 */
		BYTE,
		/**
		 * BIT
		 */
		BIT,
	}
}
