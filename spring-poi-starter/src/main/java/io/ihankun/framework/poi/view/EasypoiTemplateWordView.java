package io.ihankun.framework.poi.view;

import io.ihankun.framework.poi.entity.vo.TemplateWordConstants;
import io.ihankun.framework.poi.util.WebFilenameUtils;
import io.ihankun.framework.poi.word.WordExportUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Word模板视图
 *
 * @author hankun
 */
@SuppressWarnings("unchecked")
@Controller(TemplateWordConstants.EASYPOI_TEMPLATE_WORD_VIEW)
public class EasypoiTemplateWordView extends PoiBaseView {

    private static final String CONTENT_TYPE = "application/msword";

    public EasypoiTemplateWordView() {
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String codedFileName = "临时文件.docx";
        if (model.containsKey(TemplateWordConstants.FILE_NAME)) {
            codedFileName = (String) model.get(TemplateWordConstants.FILE_NAME) + ".docx";
        }
        // 用工具类生成符合RFC 5987标准的文件名header, 去掉UA判断
        response.setHeader("content-disposition", WebFilenameUtils.disposition(codedFileName));
        XWPFDocument document = WordExportUtil.exportWord07(
            (String) model.get(TemplateWordConstants.URL),
            (Map<String, Object>) model.get(TemplateWordConstants.MAP_DATA));
        ServletOutputStream out = response.getOutputStream();
        document.write(out);
        out.flush();
    }
}
