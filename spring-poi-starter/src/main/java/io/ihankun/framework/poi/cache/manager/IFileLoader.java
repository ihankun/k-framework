package io.ihankun.framework.poi.cache.manager;

/**
 * 缓存读取
 *
 * @author hankun
 */
public interface IFileLoader {
    /**
     * 可以自定义KEY的作用
     * @param key
     * @return
     */
    public byte[] getFile(String key);

}
