package io.hankun.framework.poi.cache;

import io.hankun.framework.poi.cache.manager.POICacheManager;
import io.hankun.framework.poi.word.entity.MyXWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * word 缓存中心
 *
 * @author hankun
 */
public class WordCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordCache.class);

    public static MyXWPFDocument getXWPFDocument(String url) {
        InputStream is = null;
        try {
            is = POICacheManager.getFile(url);
            MyXWPFDocument doc = new MyXWPFDocument(is);
            return doc;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
