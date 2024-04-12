package io.ihankun.framework.common.compiler;


import io.ihankun.framework.common.utils.Unchecked;
import io.ihankun.framework.common.utils.plus.CollectionUtil;
import io.ihankun.framework.common.function.CheckedFunction;

import java.security.SecureClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 内存的代码 class loader，参考自 oracle jdk
 *
 * @author hankun
 */
public class ByteCodeLoader extends SecureClassLoader {
	/**
	 * Map which represents class name and its compiled java object
	 */
	private static final ConcurrentMap<String, Class<?>> JAVA_FILE_OBJECT_MAP = new ConcurrentHashMap<>();
	private final String className;
	private final byte[] byteCode;

	/**
	 * Creates a new {@code ByteCodeLoader} ready to load a class with the
	 * given name and the given byte code.
	 *
	 * @param className The name of the class
	 * @param byteCode  The byte code of the class
	 */
	public ByteCodeLoader(String className, byte[] byteCode) {
		this.className = className;
		this.byteCode = byteCode;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (!name.equals(className)) {
			throw new ClassNotFoundException(name);
		}

		return defineClass(name, byteCode, 0, byteCode.length);
	}

	/**
	 * Utility method for creating a new {@code ByteCodeLoader} and then
	 * directly load the given byte code.
	 *
	 * @param className The name of the class
	 * @param byteCode  The byte code for the class
	 * @return A {@see Class} object representing the class
	 */
	public static Class<?> load(String className, byte[] byteCode) {
		CheckedFunction<String, Class<?>> classLoadFunc = (key) -> new ByteCodeLoader(key, byteCode).loadClass(className);
		return CollectionUtil.computeIfAbsent(JAVA_FILE_OBJECT_MAP, className, Unchecked.function(classLoadFunc));
	}

	/**
	 * Utility method for creating a new {@code ByteCodeLoader} and then
	 * directly load the given byte code.
	 *
	 * @param className  The name of the class
	 * @param sourceCode The source code for the class with name {@code className}
	 * @return A {@see Class} object representing the class
	 */
	public static Class<?> load(String className, CharSequence sourceCode) {
		return load(className, InMemoryJavaCompiler.compile(className, sourceCode));
	}

}
