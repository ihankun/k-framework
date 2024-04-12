package io.ihankun.framework.pdf.fop.doc;

import io.ihankun.framework.pdf.fop.doc.bookmark.XEasyPdfTemplateBookmarkComponent;
import io.ihankun.framework.pdf.fop.doc.page.XEasyPdfTemplatePageComponent;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * pdf模板-文档参数
 *
 * @author hankun
 */
@Data
@Accessors(chain = true)
class XEasyPdfTemplateDocumentParam {
    /**
     * 配置文件路径（fop配置文件路径）
     */
    private String configPath;
    /**
     * 标题
     */
    private String title;
    /**
     * 作者
     */
    private String author;
    /**
     * 主题
     */
    private String subject;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date creationDate;
    /**
     * pdf模板页面列表
     */
    private final List<XEasyPdfTemplatePageComponent> pageList = new ArrayList<>(10);
    /**
     * pdf模板书签列表
     */
    private List<XEasyPdfTemplateBookmarkComponent> bookmarkList = new ArrayList<>(10);
}
