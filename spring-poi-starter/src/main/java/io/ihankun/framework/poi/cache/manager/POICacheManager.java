package io.ihankun.framework.poi.cache.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 缓存管理
 *
 * @author hankun
 */
public final class POICacheManager {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(POICacheManager.class);

    private static IFileLoader fileLoader = new FileLoaderImpl();

    private static ThreadLocal<IFileLoader> LOCAL_FILE_LOADER = new ThreadLocal<IFileLoader>();

    public static InputStream getFile(String id) {
        try {
            byte[] result;
            //复杂数据,防止操作原数据
            if (LOCAL_FILE_LOADER.get() != null) {
                result = LOCAL_FILE_LOADER.get().getFile(id);
            }
            result = fileLoader.getFile(id);
            result = Arrays.copyOf(result, result.length);
            return new ByteArrayInputStream(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static void setFileLoader(IFileLoader fileLoader) {
        POICacheManager.fileLoader = fileLoader;
    }

    /**
     * 一次线程有效
     * @param fileLoader
     */
    public static void setFileLoaderOnce(IFileLoader fileLoader) {
        if (fileLoader != null) {
            LOCAL_FILE_LOADER.set(fileLoader);
        }
    }

}
