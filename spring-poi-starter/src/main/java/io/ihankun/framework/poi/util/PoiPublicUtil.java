package io.ihankun.framework.poi.util;

import io.ihankun.framework.poi.cache.ImageCache;
import io.ihankun.framework.poi.entity.ImageEntity;
import io.ihankun.framework.poi.annotation.Excel;
import io.ihankun.framework.poi.annotation.ExcelCollection;
import io.ihankun.framework.poi.annotation.ExcelEntity;
import io.ihankun.framework.poi.annotation.ExcelIgnore;
import io.ihankun.framework.poi.word.entity.params.ExcelListEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static io.ihankun.framework.poi.util.PoiElUtil.END_STR;
import static io.ihankun.framework.poi.util.PoiElUtil.START_STR;

/**
 * EASYPOI 的公共基础类
 *
 * @author hankun
 */
public final class PoiPublicUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoiPublicUtil.class);

    private PoiPublicUtil() {

    }

    @SuppressWarnings({"unchecked"})
    public static <K, V> Map<K, V> mapFor(Object... mapping) {
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }

    /**
     * 彻底创建一个对象
     *
     * @param clazz
     * @return
     */
    public static Object createObject(Class<?> clazz, String targetId) {
        Object obj = null;
        try {
            if (clazz.equals(Map.class)) {
                return new LinkedHashMap<String, Object>();
            }
            obj = clazz.newInstance();
            Field[] fields = getClassFields(clazz);
            for (Field field : fields) {
                if (isNotUserExcelUserThis(null, field, targetId)) {
                    continue;
                }
                if (isCollection(field.getType())) {
                    ExcelCollection collection = field.getAnnotation(ExcelCollection.class);
                    PoiReflectorUtil.fromCache(clazz).setValue(obj, field.getName(),
                            collection.type().newInstance());
                } else if (!isJavaClass(field) && !field.getType().isEnum()) {
                    PoiReflectorUtil.fromCache(clazz).setValue(obj, field.getName(),
                            createObject(field.getType(), targetId));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("创建对象异常");
        }
        return obj;

    }

    /**
     * 获取class的 包括父类的
     *
     * @param clazz
     * @return
     */
    public static Field[] getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Field[]     fields;
        do {
            fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                list.add(fields[i]);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        return list.toArray(fields);
    }

    /**
     * @param photoByte
     * @return
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "JPG";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70)
                && (photoByte[3] == 56) && ((photoByte[4] == 55) || (photoByte[4] == 57))
                && (photoByte[5] == 97)) {
            strFileExtendName = "GIF";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73)
                && (photoByte[9] == 70)) {
            strFileExtendName = "JPG";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "BMP";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }


    /**
     * 判断流是否含有BOM
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static boolean hasBom(InputStream in) throws IOException {
        byte[] head = new byte[3];
        in.read(head);
        if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
            return true;
        }
        return false;
    }

    /**
     * 获取Excel2003图片
     *
     * @param sheet    当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（1_1）String，value:图片流PictureData
     */
    public static Map<String, PictureData> getSheetPictrues03(HSSFSheet sheet,
                                                              HSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
        List<HSSFPictureData>    pictures         = workbook.getAllPictures();
        if (!pictures.isEmpty()) {
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
                HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                if (shape instanceof HSSFPicture) {
                    HSSFPicture     pic          = (HSSFPicture) shape;
                    int             pictureIndex = pic.getPictureIndex() - 1;
                    HSSFPictureData picData      = pictures.get(pictureIndex);
                    String picIndex = String.valueOf(anchor.getRow1()) + "_"
                            + String.valueOf(anchor.getCol1());
                    sheetIndexPicMap.put(picIndex, picData);
                }
            }
            return sheetIndexPicMap;
        } else {
            return sheetIndexPicMap;
        }
    }

    /**
     * 获取Excel2007图片
     *
     * @param sheet    当前sheet对象
     * @param workbook 工作簿对象
     * @return Map key:图片单元格索引（1_1）String，value:图片流PictureData
     */
    public static Map<String, PictureData> getSheetPictrues07(XSSFSheet sheet,
                                                              XSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing) {
                XSSFDrawing     drawing = (XSSFDrawing) dr;
                List<XSSFShape> shapes  = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    if (shape instanceof XSSFPicture) {
                        XSSFPicture      pic      = (XSSFPicture) shape;
                        XSSFClientAnchor anchor   = pic.getPreferredSize();
                        CTMarker         ctMarker = anchor.getFrom();
                        String           picIndex = ctMarker.getRow() + "_" + ctMarker.getCol();
                        sheetIndexPicMap.put(picIndex, pic.getPictureData());
                    }
                }
            }
        }
        return sheetIndexPicMap;
    }

    /**
     * 判断是不是集合的实现类
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 是不是java基础类
     *
     * @param field
     * @return
     */
    public static boolean isJavaClass(Field field) {
        Class<?> fieldType   = field.getType();
        boolean  isBaseClass = false;
        if (fieldType.isArray()) {
            isBaseClass = false;
        } else if (fieldType.isPrimitive() || fieldType.getPackage() == null
                || "java.lang".equals(fieldType.getPackage().getName())
                || "java.math".equals(fieldType.getPackage().getName())
                || "java.sql".equals(fieldType.getPackage().getName())
                || "java.time".equals(fieldType.getPackage().getName())
                || "java.util".equals(fieldType.getPackage().getName())) {
            isBaseClass = true;
        }
        return isBaseClass;
    }

    /**
     * 判断是否不要在这个excel操作中
     *
     * @param exclusionsList
     * @param field
     * @param targetId
     * @return
     */
    public static boolean isNotUserExcelUserThis(List<String> exclusionsList, Field field,
                                                 String targetId) {
        boolean boo = true;
        if (field.getAnnotation(ExcelIgnore.class) != null) {
            boo = true;
        } else if (boo && field.getAnnotation(ExcelCollection.class) != null
                && isUseInThis(field.getAnnotation(ExcelCollection.class).name(), targetId)
                && (exclusionsList == null || !exclusionsList
                .contains(field.getAnnotation(ExcelCollection.class).name()))) {
            boo = false;
        } else if (boo && field.getAnnotation(Excel.class) != null
                && isUseInThis(field.getAnnotation(Excel.class).name(), targetId)
                && (exclusionsList == null
                || !exclusionsList.contains(field.getAnnotation(Excel.class).name()))) {
            boo = false;
        } else if (boo && field.getAnnotation(ExcelEntity.class) != null
                && isUseInThis(field.getAnnotation(ExcelEntity.class).name(), targetId)
                && (exclusionsList == null || !exclusionsList
                .contains(field.getAnnotation(ExcelEntity.class).name()))) {
            boo = false;
        }
        return boo;
    }

    /**
     * 判断是不是使用
     *
     * @param exportName
     * @param targetId
     * @return
     */
    private static boolean isUseInThis(String exportName, String targetId) {
        return targetId == null || "".equals(exportName) || exportName.indexOf("_") < 0
                || exportName.indexOf(targetId) != -1;
    }

    private static Integer getImageType(String type) {
        if ("JPG".equalsIgnoreCase(type) || "JPEG".equalsIgnoreCase(type)) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        }
        if ("GIF".equalsIgnoreCase(type)) {
            return XWPFDocument.PICTURE_TYPE_GIF;
        }
        if ("BMP".equalsIgnoreCase(type)) {
            return XWPFDocument.PICTURE_TYPE_GIF;
        }
        if ("PNG".equalsIgnoreCase(type)) {
            return XWPFDocument.PICTURE_TYPE_PNG;
        }
        return XWPFDocument.PICTURE_TYPE_JPEG;
    }

    /**
     * 返回流和图片类型
     *
     * @param entity
     * @return (byte[]) isAndType[0],(Integer)isAndType[1]
     * @throws Exception
     * @author hankun
     * 2013-11-20
     */
    public static Object[] getIsAndType(ImageEntity entity) throws Exception {
        Object[] result = new Object[2];
        String   type;
        if (entity.getType().equals(ImageEntity.URL)) {
            result[0] = ImageCache.getImage(entity.getUrl());
        } else {
            result[0] = entity.getData();
        }
        type = PoiPublicUtil.getFileExtendName((byte[])result[0]);
        result[1] = getImageType(type);
        return result;
    }

    /**
     * 获取参数值
     *
     * @param params
     * @param object
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object getParamsValue(String params, Object object) throws Exception {
        if (params.indexOf(".") != -1) {
            String[] paramsArr = params.split("\\.");
            return getValueDoWhile(object, paramsArr, 0);
        }
        if (object instanceof Map) {
            return ((Map) object).get(params);
        }
        return PoiReflectorUtil.fromCache(object.getClass()).getValue(object, params);
    }

    /**
     * 解析数据
     *
     * @return
     * @author hankun
     * 2013-11-16
     */
    public static Object getRealValue(String currentText,
                                      Map<String, Object> map) throws Exception {
        String params = "";
        while (currentText.indexOf(START_STR) != -1) {
            params = currentText.substring(currentText.indexOf(START_STR) + 2, currentText.indexOf(END_STR));
            Object obj = PoiElUtil.eval(params.trim(), map);
            //判断图片或者是集合
            if (obj instanceof ImageEntity || obj instanceof List || obj instanceof ExcelListEntity) {
                return obj;
            } else if (obj != null) {
                currentText = currentText.replace(START_STR + params + END_STR, obj.toString());
            } else {
                currentText = currentText.replace(START_STR + params + END_STR, "");
            }
        }
        return currentText;
    }

    /**
     * 通过遍历过去对象值
     *
     * @param object
     * @param paramsArr
     * @param index
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Object getValueDoWhile(Object object, String[] paramsArr,
                                         int index) throws Exception {
        String params = paramsArr[index];
        boolean isGetArrayVal = false;
        int arrayIdx = -1;
        if(params.indexOf("[") > -1 && params.indexOf("]") > -1){
            isGetArrayVal = true;
            // 获取索引长度
            int startIdx = params.indexOf("[");
            int endIdx = params.indexOf("]");
            String idxStr = params.substring(startIdx + 1, endIdx);
            arrayIdx = Integer.valueOf(idxStr.trim()).intValue();
            params = params.substring(0,startIdx);
        }
        if (object == null) {
            return "";
        }
        if (object instanceof ImageEntity) {
            return object;
        }
        if (object instanceof Map) {
            object = ((Map) object).get(params);
        } else {
            object = PoiReflectorUtil.fromCache(object.getClass()).getValue(object,
                    params);
        }
        if(isGetArrayVal && null != object){
            // 如果是获取列表中的某一个值，则取值
            if(object instanceof List){
                List list = (List) object;
                if(arrayIdx < 0 || arrayIdx >= list.size()){
                    object = null;
                } else {
                    object = ((List) object).get(arrayIdx);
                }
            }
        }

        if (object instanceof Collection) {
            return object;
        }
        return (index == paramsArr.length - 1) ? (object == null ? "" : object)
                : getValueDoWhile(object, paramsArr, ++index);
    }

    /**
     * double to String 防止科学计数法
     *
     * @param value
     * @return
     */
    public static String doubleToString(Double value) {
        String temp = value.toString();
        if (temp.contains("E")) {
            BigDecimal bigDecimal = new BigDecimal(temp);
            temp = bigDecimal.toPlainString();
        }
        return temp;
    }

    /**
     * 统一 key的获取规则
     *
     * @param key
     * @param targetId
     * @return
     */
    public static String getValueByTargetId(String key, String targetId, String defalut) {
        if (StringUtils.isEmpty(targetId) || key.indexOf("_") < 0) {
            return key;
        }
        String[] arr = key.split(",");
        String[] tempArr;
        for (String str : arr) {
            tempArr = str.split("_");
            if (tempArr == null || tempArr.length < 2) {
                return defalut;
            }
            if (targetId.equals(tempArr[1])) {
                return tempArr[0];
            }
        }
        return defalut;
    }

    /**
     * 支持换行操作
     *
     * @param currentRun
     * @param currentText
     */
    public static void setWordText(XWPFRun currentRun, String currentText) {
        if (StringUtils.isNotEmpty(currentText)) {
            String[] tempArr = currentText.split("\r\n");
            for (int i = 0, le = tempArr.length - 1; i < le; i++) {
                currentRun.setText(tempArr[i], i);
                currentRun.addBreak();
            }
            currentRun.setText(tempArr[tempArr.length - 1], tempArr.length - 1);
        } else {
            //对blank字符串做处理，避免显示"{{"
            currentRun.setText("", 0);
        }
    }

    public static int getNumDigits(int num) {
        int count = 0;
        while (num > 0) {
            num = num / 10;
            count++;
        }
        return count;
    }

    /**
     * 多个点,截取最后一个
     *
     * @param name
     * @return
     */
    public static String getLastFieldName(String name) {
        String[] paramsArr = name.split("\\.");
        return paramsArr[paramsArr.length - 1];
    }

    /**
     * 找到字符串中包含key的{{字符串返回}}
     *
     * @param str
     * @param key
     * @return
     */
    public static String getElStr(String str, String key) {
        int keyIndex   = str.indexOf(key);
        int startIndex = (str.substring(0, keyIndex)).lastIndexOf(START_STR);
        int endIndex   = str.indexOf(END_STR, keyIndex) + 2;
        return str.substring(startIndex, endIndex);
    }

    public static void copyCellAndSetValue(XWPFTableCell tmpCell, XWPFTableCell cell, String text) throws Exception {
        CTTc cttc2 = tmpCell.getCTTc();
        CTTcPr ctPr2 = cttc2.getTcPr();
        cell.getTableRow().setHeight(tmpCell.getTableRow().getHeight());
        CTTc cttc = cell.getCTTc();
        CTTcPr ctPr = cttc.addNewTcPr();
        if (tmpCell.getColor() != null) {
            cell.setColor(tmpCell.getColor());
        }
        if (tmpCell.getVerticalAlignment() != null) {
            cell.setVerticalAlignment(tmpCell.getVerticalAlignment());
        }
        if (ctPr2.getTcW() != null) {
            ctPr.addNewTcW().setW(ctPr2.getTcW().getW());
        }
        if (ctPr2.getVAlign() != null) {
            ctPr.addNewVAlign().setVal(ctPr2.getVAlign().getVal());
        }
        if (cttc2.getPList().size() > 0) {
            CTP ctp = cttc2.getPList().get(0);
            if (ctp.getPPr() != null) {
                if (ctp.getPPr().getJc() != null) {
                    cttc.getPList().get(0).addNewPPr().addNewJc().setVal(ctp.getPPr().getJc().getVal());
                }
            }
        }

        if (ctPr2.getTcBorders() != null) {
            ctPr.setTcBorders(ctPr2.getTcBorders());
        }

        XWPFParagraph tmpP = tmpCell.getParagraphs().get(0);
        XWPFParagraph cellP = cell.getParagraphs().get(0);
        XWPFRun tmpR = null;
        if (tmpP.getRuns() != null && tmpP.getRuns().size() > 0) {
            tmpR = tmpP.getRuns().get(0);
        }
        XWPFRun cellR = cellP.createRun();
        cellR.setText(text);
        //复制字体信息
        if (tmpR != null) {
            cellR.setBold(tmpR.isBold());
            cellR.setItalic(tmpR.isItalic());
            cellR.setStrike(tmpR.isStrike());
            cellR.setUnderline(tmpR.getUnderline());
            cellR.setColor(tmpR.getColor());
            cellR.setTextPosition(tmpR.getTextPosition());
            if (tmpR.getFontSize() != -1) {
                cellR.setFontSize(tmpR.getFontSize());
            }
            if (tmpR.getFontFamily() != null) {
                cellR.setFontFamily(tmpR.getFontFamily());
            }
            if (tmpR.getCTR() != null) {
                if (tmpR.getCTR().isSetRPr()) {
                    CTRPr tmpRPr = tmpR.getCTR().getRPr();
                    if (tmpRPr.isSetRFonts()) {
                        CTFonts tmpFonts = tmpRPr.getRFonts();
                        CTRPr cellRPr = cellR.getCTR().isSetRPr() ? cellR.getCTR().getRPr() : cellR.getCTR().addNewRPr();
                        CTFonts cellFonts = cellRPr.isSetRFonts() ? cellRPr.getRFonts() : cellRPr.addNewRFonts();
                        cellFonts.setAscii(tmpFonts.getAscii());
                        cellFonts.setAsciiTheme(tmpFonts.getAsciiTheme());
                        cellFonts.setCs(tmpFonts.getCs());
                        cellFonts.setCstheme(tmpFonts.getCstheme());
                        cellFonts.setEastAsia(tmpFonts.getEastAsia());
                        cellFonts.setEastAsiaTheme(tmpFonts.getEastAsiaTheme());
                        cellFonts.setHAnsi(tmpFonts.getHAnsi());
                        cellFonts.setHAnsiTheme(tmpFonts.getHAnsiTheme());
                    }
                }
            }
        }
        //复制段落信息
        if (tmpP.getAlignment() != null) {
            cellP.setAlignment(tmpP.getAlignment());
        }
        if (tmpP.getVerticalAlignment() != null) {
            cellP.setVerticalAlignment(tmpP.getVerticalAlignment());
        }
        if (tmpP.getBorderBetween() != null) {
            cellP.setBorderBetween(tmpP.getBorderBetween());
        }
        if (tmpP.getBorderBottom() != null){
            cellP.setBorderBottom(tmpP.getBorderBottom());
        }
        if (tmpP.getBorderLeft() != null){
            cellP.setBorderLeft(tmpP.getBorderLeft());
        }
        if (tmpP.getBorderRight() != null){
            cellP.setBorderRight(tmpP.getBorderRight());
        }
        if (tmpP.getBorderTop() != null){
            cellP.setBorderTop(tmpP.getBorderTop());
        }
        cellP.setPageBreak(tmpP.isPageBreak());
        if (tmpP.getCTP() != null) {
            if (tmpP.getCTP().getPPr() != null) {
                CTPPr tmpPPr = tmpP.getCTP().getPPr();
                CTPPr cellPPr = cellP.getCTP().getPPr() != null ? cellP.getCTP().getPPr() : cellP.getCTP().addNewPPr();
                //复制段落间距信息
                CTSpacing tmpSpacing = tmpPPr.getSpacing();
                if (tmpSpacing != null) {
                    CTSpacing cellSpacing = cellPPr.getSpacing() != null ? cellPPr.getSpacing() : cellPPr.addNewSpacing();
                    if (tmpSpacing.getAfter() != null) {
                        cellSpacing.setAfter(tmpSpacing.getAfter());
                    }
                    if (tmpSpacing.getAfterAutospacing() != null) {
                        cellSpacing.setAfterAutospacing(tmpSpacing.getAfterAutospacing());
                    }
                    if (tmpSpacing.getAfterLines() != null) {
                        cellSpacing.setAfterLines(tmpSpacing.getAfterLines());
                    }
                    if (tmpSpacing.getBefore() != null) {
                        cellSpacing.setBefore(tmpSpacing.getBefore());
                    }
                    if (tmpSpacing.getBeforeAutospacing() != null) {
                        cellSpacing.setBeforeAutospacing(tmpSpacing.getBeforeAutospacing());
                    }
                    if (tmpSpacing.getBeforeLines() != null) {
                        cellSpacing.setBeforeLines(tmpSpacing.getBeforeLines());
                    }
                    if (tmpSpacing.getLine() != null) {
                        cellSpacing.setLine(tmpSpacing.getLine());
                    }
                    if (tmpSpacing.getLineRule() != null) {
                        cellSpacing.setLineRule(tmpSpacing.getLineRule());
                    }
                }
                //复制段落缩进信息
                CTInd tmpInd = tmpPPr.getInd();
                if (tmpInd != null) {
                    CTInd cellInd = cellPPr.getInd() != null ? cellPPr.getInd() : cellPPr.addNewInd();
                    if (tmpInd.getFirstLine() != null) {
                        cellInd.setFirstLine(tmpInd.getFirstLine());
                    }
                    if (tmpInd.getFirstLineChars() != null) {
                        cellInd.setFirstLineChars(tmpInd.getFirstLineChars());
                    }
                    if (tmpInd.getHanging() != null) {
                        cellInd.setHanging(tmpInd.getHanging());
                    }
                    if (tmpInd.getHangingChars() != null) {
                        cellInd.setHangingChars(tmpInd.getHangingChars());
                    }
                    if (tmpInd.getLeft() != null) {
                        cellInd.setLeft(tmpInd.getLeft());
                    }
                    if (tmpInd.getLeftChars() != null) {
                        cellInd.setLeftChars(tmpInd.getLeftChars());
                    }
                    if (tmpInd.getRight() != null) {
                        cellInd.setRight(tmpInd.getRight());
                    }
                    if (tmpInd.getRightChars() != null) {
                        cellInd.setRightChars(tmpInd.getRightChars());
                    }
                }
            }
        }
    }
}
