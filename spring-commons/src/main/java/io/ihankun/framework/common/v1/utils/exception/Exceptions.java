package io.ihankun.framework.common.v1.utils.exception;

import io.ihankun.framework.common.v1.utils.io.FastStringPrintWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 异常处理工具类
 *
 * @author hankun
 */
public class Exceptions {

	/**
	 * 将CheckedException转换为UncheckedException.
	 *
	 * @param e Throwable
	 * @return {RuntimeException}
	 */
	public static RuntimeException unchecked(Throwable e) {
		if (e instanceof Error) {
			Error error = (Error) e;
			throw error;
		} else if (e instanceof IllegalAccessException ||
			e instanceof IllegalArgumentException ||
			e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			InvocationTargetException exception = (InvocationTargetException) e;
			return Exceptions.runtime(exception.getTargetException());
		} else if (e instanceof RuntimeException) {
			RuntimeException exception = (RuntimeException) e;
			return exception;
		} else if (e instanceof InterruptedException) {
			Thread.currentThread().interrupt();
		}
		return Exceptions.runtime(e);
	}

	/**
	 * 不采用 RuntimeException 包装，直接抛出，使异常更加精准
	 *
	 * @param throwable Throwable
	 * @param <T>       泛型标记
	 * @return Throwable
	 * @throws T 泛型
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> T runtime(Throwable throwable) throws T {
		throw (T) throwable;
	}

	/**
	 * 代理异常解包
	 *
	 * @param wrapped 包装过得异常
	 * @return 解包后的异常
	 */
	public static Throwable unwrap(Throwable wrapped) {
		Throwable unwrapped = wrapped;
		while (true) {
			if (unwrapped instanceof InvocationTargetException) {
				InvocationTargetException exception = (InvocationTargetException) unwrapped;
				unwrapped = exception.getTargetException();
			} else if (unwrapped instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException exception = (UndeclaredThrowableException) unwrapped;
				unwrapped = exception.getUndeclaredThrowable();
			} else {
				return unwrapped;
			}
		}
	}

	/**
	 * 将ErrorStack转化为String.
	 *
	 * @param ex Throwable
	 * @return {String}
	 */
	public static String getStackTraceAsString(Throwable ex) {
		FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
		ex.printStackTrace(printWriter);
		return printWriter.toString();
	}
}
