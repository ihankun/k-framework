package io.ihankun.framework.poi.excel.imports.sax;

import io.ihankun.framework.poi.excel.entity.ImportParams;
import io.ihankun.framework.poi.excel.imports.sax.parse.ISaxRowRead;
import io.ihankun.framework.poi.excel.imports.sax.parse.SaxRowRead;
import io.ihankun.framework.poi.exception.excel.ExcelImportException;
import io.ihankun.framework.poi.handler.inter.IReadHandler;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.Iterator;

/**
 * 基于SAX Excel大数据读取,读取Excel 07版本,不支持图片读取
 *
 * @author hankun
 */
@SuppressWarnings("rawtypes")
public class SaxReadExcel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxReadExcel.class);

    public void readExcel(InputStream inputstream, Class<?> pojoClass, ImportParams params,
                                 IReadHandler handler) {
        try {
            OPCPackage opcPackage = OPCPackage.open(inputstream);
            readExcel(opcPackage, pojoClass, params, null, handler);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage());
        }
    }

    private void readExcel(OPCPackage opcPackage, Class<?> pojoClass, ImportParams params,
                                  ISaxRowRead rowRead, IReadHandler handler) {
        try {
            XSSFReader         xssfReader         = new XSSFReader(opcPackage);
            SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
            StylesTable        stylesTable        = xssfReader.getStylesTable();
            if (rowRead == null) {
                rowRead = new SaxRowRead(pojoClass, params, handler);
            }
            XMLReader             parser     = fetchSheetParser(sharedStringsTable, stylesTable, rowRead);
            Iterator<InputStream> sheets     = xssfReader.getSheetsData();
            int                   sheetIndex = 0;
            while (sheets.hasNext() && sheetIndex < params.getSheetNum() + params.getStartSheetIndex()) {
                if (sheetIndex < params.getStartSheetIndex()) {
                    sheets.next();
                } else {
                    InputStream sheet       = sheets.next();
                    InputSource sheetSource = new InputSource(sheet);
                    parser.parse(sheetSource);
                    sheet.close();
                }
                sheetIndex++;
            }
            if (handler != null) {
                handler.doAfterAll();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException("SAX导入数据失败");
        }
    }

    private XMLReader fetchSheetParser(SharedStringsTable sharedStringsTable, StylesTable stylesTable,
                                       ISaxRowRead rowRead) throws SAXException {
        XMLReader      parser  = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        ContentHandler handler = new SheetHandler(sharedStringsTable, stylesTable, rowRead);
        parser.setContentHandler(handler);
        return parser;
    }

}
