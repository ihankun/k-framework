package io.ihankun.framework.poi.pdf.watermark;

import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * PDF水印管理服务，包括新增水印，删除水印，参考内容比较多
 * 删除的参考数据
 * https://zhuanlan.zhihu.com/p/641209431
 * 看着简单，但是没找到方法
 * https://blog.51cto.com/u_16213336/7182264
 * 看着没有水印了，其实是透明了
 * https://baijiahao.baidu.com/s?id=1710474526903526984&wfr=spider&for=pc
 * 看看感觉有点意义
 * https://blog.csdn.net/lzp12345677/article/details/131745896
 * 去除PDF文件中的斜体文字水印
 * https://blog.csdn.net/weixin_44214515/article/details/120863569
 * 主要是最后两篇的介绍内容
 */
public interface IWatermarkProcessor {

    void init(PDDocument doc);

    PDDocument getDocument();

    boolean isWatermarkWord(String str);

    void addWatermarkWord(String str);

    default int getThreadCount() {
        return new Double(Math.ceil(getDocument().getNumberOfPages() / 3d)).intValue();
    }
}
