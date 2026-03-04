package io.hankun.framework.db.build;


import io.hankun.framework.db.config.DataSourceConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hankun
 */
public class BuildInfoHolder {

    private final List<String> messages = new ArrayList<>();

    public BuildInfoHolder() {

    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void addMessage(String message, Object... args) {
        messages.add(String.format(message, args));
    }

    public String getErrorMessage(String ds, String domain) {
        return ds + DataSourceConstant.DS_SPLIT + domain + "配置错误：" + String.join(",", messages);
    }
}
