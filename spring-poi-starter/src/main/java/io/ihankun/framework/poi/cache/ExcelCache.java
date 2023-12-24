package io.ihankun.framework.poi.cache;

import io.ihankun.framework.poi.cache.manager.POICacheManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Excel类型的缓存
 *
 * @author hankun
 */
public final class ExcelCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelCache.class);

    public static Workbook getWorkbook(String url, Integer[] sheetNums, boolean needAll) {
        InputStream is = null;
        List<Integer> sheetList = Arrays.asList(sheetNums);
        try {
            is = POICacheManager.getFile(url);
            Workbook wb = WorkbookFactory.create(is);
            // 删除其他的sheet
            if (!needAll) {
                for (int i = wb.getNumberOfSheets() - 1; i >= 0; i--) {
                    if (!sheetList.contains(i)) {
                        wb.removeSheetAt(i);
                    }
                }
            }
            return wb;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
