package io.ihankun.framework.common.utils;


import io.ihankun.framework.common.utils.plus.NumberUtil;
import io.ihankun.framework.common.utils.string.CharPool;
import io.ihankun.framework.common.utils.string.StringPool;
import io.ihankun.framework.common.utils.string.StringUtil;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 运行时工具类
 *
 * @author hankun
 */
public class RuntimeUtil {
	private static volatile int pId = -1;
	private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

	/**
	 * 获得当前进程的PID
	 * <p>
	 * 当失败时返回-1
	 *
	 * @return pid
	 */
	public static int getPId() {
		if (pId > 0) {
			return pId;
		}
		// something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf(CharPool.AT);
		if (index > 0) {
			pId = NumberUtil.toInt(jvmName.substring(0, index), -1);
			return pId;
		}
		return pId;
	}

	/**
	 * 返回应用启动的时间
	 *
	 * @return {Instant}
	 */
	public static Instant getStartTime() {
		return Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());
	}

	/**
	 * 返回应用启动到现在的时间
	 *
	 * @return {Duration}
	 */
	public static Duration getUpTime() {
		return Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
	}

	/**
	 * 返回输入的JVM参数列表
	 *
	 * @return jvm参数
	 */
	public static String getJvmArguments() {
		List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		return StringUtil.join(vmArguments, StringPool.SPACE);
	}

	/**
	 * 获取CPU核数
	 *
	 * @return cpu count
	 */
	public static int getCpuNum() {
		return CPU_NUM;
	}

}
