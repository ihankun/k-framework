package io.hankun.framework.file.enums;

import cn.hutool.core.util.ArrayUtil;
import io.hankun.framework.file.client.FileClient;
import io.hankun.framework.file.client.FileClientConfig;
import io.hankun.framework.file.client.impl.db.DBFileClient;
import io.hankun.framework.file.client.impl.db.DBFileClientConfig;
import io.hankun.framework.file.client.impl.ftp.FtpFileClient;
import io.hankun.framework.file.client.impl.ftp.FtpFileClientConfig;
import io.hankun.framework.file.client.impl.local.LocalFileClient;
import io.hankun.framework.file.client.impl.local.LocalFileClientConfig;
import io.hankun.framework.file.client.impl.s3.S3FileClient;
import io.hankun.framework.file.client.impl.s3.S3FileClientConfig;
import io.hankun.framework.file.client.impl.sftp.SftpFileClient;
import io.hankun.framework.file.client.impl.sftp.SftpFileClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hankun
 */
@AllArgsConstructor
@Getter
public enum FileStorageEnum {

    DB(1, DBFileClientConfig.class, DBFileClient.class),

    LOCAL(10, LocalFileClientConfig.class, LocalFileClient.class),
    FTP(11, FtpFileClientConfig.class, FtpFileClient.class),
    SFTP(12, SftpFileClientConfig.class, SftpFileClient.class),

    S3(20, S3FileClientConfig.class, S3FileClient.class),
    ;

    /**
     * 存储器
     */
    private final Integer storage;

    /**
     * 配置类
     */
    private final Class<? extends FileClientConfig> configClass;
    /**
     * 客户端类
     */
    private final Class<? extends FileClient> clientClass;

    public static FileStorageEnum getByStorage(Integer storage) {
        return ArrayUtil.firstMatch(o -> o.getStorage().equals(storage), values());
    }
}
