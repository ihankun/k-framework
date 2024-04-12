package io.ihankun.framework.pdf.pdfbox.doc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * pdf文档书签
 *
 * @author hankun
 */
public class XEasyPdfDocumentBookmark implements Serializable {

    private static final long serialVersionUID = 7120771777001614541L;

    /**
     * pdf文档
     */
    private final XEasyPdfDocument document;
    /**
     * pdfbox书签节点列表
     */
    private final List<PDOutlineItem> itemList = new ArrayList<>(64);

    /**
     * 有参构造
     *
     * @param document pdf文档
     */
    XEasyPdfDocumentBookmark(XEasyPdfDocument document) {
        this.document = document;
        this.initOutlineItem();
    }

    /**
     * 设置书签
     *
     * @param pageIndex 页面索引
     * @param title     标题
     * @return 返回pdf文档书签
     */
    public XEasyPdfDocumentBookmark setBookMark(Integer pageIndex, String title) {
        // 定义页面定位信息
        PDPageFitWidthDestination destination = new PDPageFitWidthDestination();
        // 设置页面索引
        destination.setPageNumber(pageIndex);
        // 定义pdfbox书签节点
        PDOutlineItem item = new PDOutlineItem();
        // 设置定位信息
        item.setDestination(destination);
        // 设置标题
        item.setTitle(title);
        // 添加pdfbox书签列表
        this.itemList.add(item);
        return this;
    }

    /**
     * 设置书签
     *
     * @param node 书签节点
     * @return 返回pdf文档书签
     */
    public XEasyPdfDocumentBookmark setBookMark(BookmarkNode node) {
        this.itemList.add(node.getItem());
        return this;
    }

    /**
     * 获取书签
     *
     * @return 返回pdfbox书签列表
     */
    public List<PDOutlineItem> getBookMark() {
        return this.itemList;
    }

    /**
     * 完成书签设置
     *
     * @return 返回pdf文档
     */
    public XEasyPdfDocument finish() {
        this.document.setBookmark(this);
        return this.document;
    }

    /**
     * 书签节点
     */
    public static class BookmarkNode {

        /**
         * pdf书签节点
         */
        private final PDOutlineItem outlineItem;
        /**
         * pdfbox页面点位
         */
        private final PDPageFitWidthDestination destination;

        /**
         * 有参构造
         *
         * @param outlineItem pdf书签节点
         */
        public BookmarkNode(PDOutlineItem outlineItem) {
            this.outlineItem = outlineItem;
            this.destination = new PDPageFitWidthDestination();
            this.outlineItem.setDestination(this.destination);
        }

        /**
         * 构建书签节点
         *
         * @return 返回书签节点
         */
        public static BookmarkNode build() {
            return new BookmarkNode(new PDOutlineItem());
        }

        /**
         * 添加子节点
         *
         * @param childNode 子书签节点
         * @return 返回书签节点
         */
        public BookmarkNode addChild(BookmarkNode childNode) {
            this.outlineItem.addLast(childNode.getItem());
            return this;
        }

        /**
         * 设置pdfbox页面索引
         *
         * @param pageIndex pdfbox页面索引
         * @return 返回书签节点
         */
        public BookmarkNode setPage(int pageIndex) {
            this.destination.setPageNumber(pageIndex);
            return this;
        }

        /**
         * 设置定位顶点坐标
         *
         * @param pageY 页面y轴坐标
         * @return 返回书签节点
         */
        public BookmarkNode setTop(int pageY) {
            this.destination.setTop(pageY);
            return this;
        }

        /**
         * 设置标题
         *
         * @param title 标题
         * @return 返回书签节点
         */
        public BookmarkNode setTitle(String title) {
            this.outlineItem.setTitle(title);
            return this;
        }

        /**
         * 设置标题颜色
         *
         * @param textColor 文本颜色
         * @return 返回书签节点
         */
        public BookmarkNode setTitleColor(Color textColor) {
            this.outlineItem.setTextColor(textColor);
            return this;
        }

        /**
         * 开启标题粗体
         *
         * @return 返回书签节点
         */
        public BookmarkNode enableTitleBold() {
            this.outlineItem.setBold(true);
            return this;
        }

        /**
         * 开启标题斜体
         *
         * @return 返回书签节点
         */
        public BookmarkNode enableTitleItalic() {
            this.outlineItem.setItalic(true);
            return this;
        }

        /**
         * 获取pdfbox书签节点
         *
         * @return 返回pdfbox书签节点
         */
        PDOutlineItem getItem() {
            return this.outlineItem;
        }
    }

    /**
     * 初始化书签
     */
    private void initOutlineItem() {
        // 获取源文档
        PDDocument source = this.document.getParam().getSource();
        // 如果源文档不为空，则获取书签
        if (source != null) {
            // 获取书签
            PDDocumentOutline documentOutline = source.getDocumentCatalog().getDocumentOutline();
            // 如果书签不为空，则获取书签节点
            if (documentOutline != null) {
                // 获取书签节点
                Iterable<PDOutlineItem> children = documentOutline.children();
                // 遍历书签节点
                for (PDOutlineItem outlineItem : children) {
                    // 添加书签节点列表
                    this.itemList.add(outlineItem);
                }
            }
        }
    }
}
