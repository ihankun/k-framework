package io.hankun.framework.db.config;

/**
 * @author hankun
 */
public class DataSourceConstant extends DataSourceType {

    /**
     * 默认数据源属性
     */
    public static final String DS_PRIMARY = "spring.datasource.dynamic.primary";
    /**
     * 默认端口
     */
    public static final String DEFAULT_PORT = "5432";
    /**
     * schema
     */
    public static final String DS_SCHEMA = "currentSchema=";
    /**
     * 连接池个性化配置前缀
     */
    public static final String KUN_DS_DRUID = "kun.ds.druid.";
    /**
     * 环境key设置为空
     */
    public static final String USE_ENV_KEY = "kun.ds.disable.env.key";
    /**
     * schema功能开关前缀
     */
    public static final String DS_SCHEMA_DIS = "kun.datasource.disable.schema.list";
    /**
     * 数据源前缀
     */
    public static final String DS_PREFIX = "kun.ds.db.";
    /**
     * 数据库密钥配置前缀
     */
    public static final String DB_BUILD_PRE = "kun.ds.key.";
    /**
     * nacos中存储的环境变量key
     */
    public static final String NEW_PRIVATE_KEY = "kun.ds.private.key.";
    /**
     * 环境变量key
     */
    public static final String PUBLIC_KEY = "KUN_DS_PUBLIC_KEY";

    public static final String AUTHENTICATION = "password authentication";
    public static final char DS_CAML = '-';
    public static final String SCHEMA_ALL = "*";
    public static final String COMMA_SPLIT = ",";
    public static final String DS_SPLIT_OF_PARAMETER = "&";
    public static final String DS_DATA_SPLIT = "@";
    public static final String DS_DOMAIN_START = "//";
    public static final String DS_DOMAIN_END = "/";
    public static final String DS_PORT = ":";
    public static final String DS_POINT = ".";
    public static final String DS_QUESTION = "?";
    public static final String DS_SPLIT = "_";
}
