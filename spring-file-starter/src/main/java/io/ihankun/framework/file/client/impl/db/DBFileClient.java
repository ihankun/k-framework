package io.ihankun.framework.file.client.impl.db;

import cn.hutool.extra.spring.SpringUtil;
import io.ihankun.framework.file.client.AbstractFileClient;

/**
 * @author hankun
 */
public class DBFileClient extends AbstractFileClient<DBFileClientConfig> {

    private DBFileContent dbFileContent;

    public DBFileClient(Long id, DBFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {

    }

    @Override
    public String upload(byte[] content, String path, String type) throws Exception {
        getDbFileContent().insert(getId(), path, content);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) throws Exception {
        getDbFileContent().delete(getId(), path);
    }

    @Override
    public byte[] getContent(String path) throws Exception {
        return getDbFileContent().selectContent(getId(), path);
    }

    private DBFileContent getDbFileContent() {
        // 延迟获取，因为 SpringUtil 初始化太慢
        if (dbFileContent == null) {
            dbFileContent = SpringUtil.getBean(DBFileContent.class);
        }
        return dbFileContent;
    }
}
