package io.ihankun.framework.pdf.fop;

/**
 * pdf模板常量
 *
 * @author hankun
 */
public final class XEasyPdfTemplateConstants {

    /**
     * fop生产者
     */
    public static final String FOP_PRODUCER = "x-easypdf/fop";
    /**
     * 默认配置路径
     */
    public static final String DEFAULT_CONFIG_PATH = "org/dromara/pdf/fop/fop.xconf";
    /**
     * 默认模板路径
     */
    public static final String DEFAULT_TEMPLATE_PATH = "org/dromara/pdf/fop/template.fo";
    /**
     * 默认边框参数
     */
    public static final String DEFAULT_BORDER_VALUE = "1px solid black";
    /**
     * 默认分割线样式
     */
    public static final String DEFAULT_SPLIT_LINE_STYLE_VALUE = "rule";
    /**
     * 默认虚线分割线样式
     */
    public static final String DEFAULT_DOTTED_SPLIT_LINE_STYLE_VALUE = "dots";
    /**
     * 命名空间
     */
    public static final String NAMESPACE = "http://www.x-easypdf.cn/ns";
    /**
     * freemarker模板路径key
     */
    public static final String FREEMARKER_TEMPLATE_PATH_KEY = "x-easypdf.freemarker.dir";
}
