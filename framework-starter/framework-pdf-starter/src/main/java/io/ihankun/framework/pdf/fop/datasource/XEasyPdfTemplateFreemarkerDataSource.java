package io.ihankun.framework.pdf.fop.datasource;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.ihankun.framework.pdf.fop.XEasyPdfTemplateConstants;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

/**
 * pdf模板-freemarker数据源
 *
 * @author hankun
 */
@Accessors(chain = true)
public class XEasyPdfTemplateFreemarkerDataSource extends XEasyPdfTemplateAbstractDataSource {

    /**
     * 模板配置
     */
    private static final Configuration CONFIGURATION = initConfiguration();

    /**
     * 设置模板名称
     *
     * @param templateName 模板名称
     * @return 返回freemarker数据源
     */
    public XEasyPdfTemplateFreemarkerDataSource setTemplateName(String templateName) {
        this.templatePath = templateName;
        return this;
    }

    /**
     * 设置模板数据
     *
     * @param templateData 模板数据
     * @return 返回freemarker数据源
     */
    public XEasyPdfTemplateFreemarkerDataSource setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
        return this;
    }

    /**
     * 处理模板
     *
     * @return 返回模板输入流
     */
    @SneakyThrows
    protected InputStream processTemplate() {
        try (
                // 创建输出流
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                // 创建写入器
                Writer writer = new BufferedWriter(new OutputStreamWriter(output))
        ) {
            // 处理模板
            XEasyPdfTemplateFreemarkerDataSource.CONFIGURATION.getTemplate(this.templatePath).process(this.templateData, writer);
            // 返回输入流
            return new BufferedInputStream(new ByteArrayInputStream(output.toByteArray()));
        }
    }

    /**
     * 初始化配置
     *
     * @return 返回配置
     */
    @SneakyThrows
    private static Configuration initConfiguration() {
        // 创建配置
        Configuration config = new Configuration(Configuration.VERSION_2_3_31);
        // 设置默认编码
        config.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置模板异常处理器
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        // 设置模板异常日志
        config.setLogTemplateExceptions(false);
        // 设置未检查异常
        config.setWrapUncheckedExceptions(true);
        // 设置空循环变量回退
        config.setFallbackOnNullLoopVariable(false);
        // 获取模板路径
        String templatePath = System.getProperty(XEasyPdfTemplateConstants.FREEMARKER_TEMPLATE_PATH_KEY);
        // 如果非资源路径，则为文件目录
        if (Thread.currentThread().getContextClassLoader().getResource(templatePath) == null) {
            // 设置文件目录解析器
            config.setDirectoryForTemplateLoading(Paths.get(templatePath).toFile());
        }
        // 否则为资源目录
        else {
            // 设置资源解析器
            config.setTemplateLoader(new ClassTemplateLoader(XEasyPdfTemplateFreemarkerDataSource.class, File.separator + templatePath));
        }
        // 返回配置
        return config;
    }
}
