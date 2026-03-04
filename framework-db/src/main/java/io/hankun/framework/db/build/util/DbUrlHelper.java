package io.hankun.framework.db.build.util;

import io.hankun.framework.db.build.ds.DataSourceConfig;
import io.hankun.framework.db.config.DataSourceConstant;
import org.springframework.util.StringUtils;

/**
 * @author hankun
 */
public class DbUrlHelper {

    public static String getProtocol(String url) {
        int point = url.indexOf(DataSourceConstant.DS_DOMAIN_START);
        if (point < 1) {
            return "";
        }
        return url.substring(0, point - 1);
    }

    /**
     * 从url的获取数据库名称
     *
     * @param url 数据库地址
     * @return 数据库名称
     */
    public static String getDb(String url) {
        int start = url.indexOf(DataSourceConstant.DS_DOMAIN_START) + DataSourceConstant.DS_DOMAIN_START.length();
        int s = url.indexOf(DataSourceConstant.DS_DOMAIN_END, start) + 1;
        int e = url.indexOf(DataSourceConstant.DS_QUESTION, s);
        if (s < 1) {
            return "";
        }
        if (e < 0) {
            return url.substring(s);
        }
        return url.substring(s, e);
    }

    /**
     * 从url的获取数据库地址
     *
     * @param url 数据库地址
     * @return 数据库名称
     */
    public static String getAddress(String url) {
        int start = url.indexOf(DataSourceConstant.DS_DOMAIN_START) + DataSourceConstant.DS_DOMAIN_START.length();
        int end = url.indexOf(DataSourceConstant.DS_DOMAIN_END, start);
        if (start < DataSourceConstant.DS_DOMAIN_START.length() || end < 0) {
            return "";
        }
        return url.substring(start, end);
    }

    public static String getParameter(String url) {
        int start = url.indexOf(DataSourceConstant.DS_QUESTION);
        if (start < 0) {
            return "";
        }
        return url.substring(start + 1);
    }

    /**
     * 构建数据库连接地址
     *
     * @param dataSourceConfig 数据库配置
     * @param address          数据库地址
     * @return 数据库连接地址
     */
    public static String buildUrl(DataSourceConfig dataSourceConfig, String address) {
        String url = dataSourceConfig.getProtocol() + "://" +
                address + "/" +
                dataSourceConfig.getDb();
        if (StringUtils.hasText(dataSourceConfig.getParameter())) {
            url = url + "?" + dataSourceConfig.getParameter();
        }
        return url;
    }
}
