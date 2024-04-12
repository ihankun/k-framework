package io.ihankun.framework.core.utils.exception;

import io.ihankun.framework.core.utils.log.LogFormat;
import lombok.extern.apachecommons.CommonsLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author hankun
 */
@CommonsLog
public class ErrMsgUtils {

    private Map<String, String> errMsgMap;

    private static final String FILE_NAME = "error";

    static class ErrMsgUtilsHolder {
        private static ErrMsgUtils utils = new ErrMsgUtils();
    }

    public ErrMsgUtils() {
        this.errMsgMap = new HashMap<>();

        Properties property = getProperty();
        if (property == null) {
            log.error(LogFormat.formatMsg("ErrMsgUtils.initializer.error", "fileName=" + FILE_NAME, ""));
            return;
        }

        Enumeration<Object> keys = property.keys();
        while (keys.hasMoreElements()) {
            String key = String.valueOf(keys.nextElement());
            String value = property.getProperty(key, "");
            try {
                value = new String(value.getBytes("ISO-8859-1"), "UTF8");
                this.errMsgMap.put(key, value);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info(LogFormat.formatMsg("ErrMsgUtils.initializer.finish", "size=" + this.errMsgMap.size(), ""));
    }

    private Properties getProperty() {
        Properties properties = new Properties();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME + ".properties");
        if (in == null) {
            log.info(LogFormat.formatMsg("ErrMsgUtils.getProperty.can't find", "fileName=" + FILE_NAME + ".properties", ""));
            return null;
        }
        try {
            properties.load(in);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return properties;
    }

    public static ErrMsgUtils ins() {
        return ErrMsgUtilsHolder.utils;
    }

    public String getMsg(String key) {
        String value = errMsgMap.get(key);
        return value == null ? "" : value;
    }
}
