package io.ihankun.framework.poi.excel.imports.sax.parse;

import io.ihankun.framework.poi.annotation.ExcelTarget;
import io.ihankun.framework.poi.excel.entity.ImportParams;
import io.ihankun.framework.poi.excel.entity.params.ExcelCollectionParams;
import io.ihankun.framework.poi.excel.entity.params.ExcelImportEntity;
import io.ihankun.framework.poi.excel.entity.sax.SaxReadCellEntity;
import io.ihankun.framework.poi.excel.imports.CellValueService;
import io.ihankun.framework.poi.excel.imports.base.ImportBaseService;
import io.ihankun.framework.poi.exception.excel.ExcelImportException;
import io.ihankun.framework.poi.handler.inter.IReadHandler;
import io.ihankun.framework.poi.util.PoiPublicUtil;
import io.ihankun.framework.poi.util.PoiReflectorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 当行读取数据
 *
 * @author hankun
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SaxRowRead extends ImportBaseService implements ISaxRowRead {

    private static final Logger               LOGGER   = LoggerFactory
            .getLogger(SaxRowRead.class);
    /**
     * 导出的对象
     **/
    private              Class<?>             pojoClass;
    /**
     * 导入参数
     **/
    private              ImportParams         params;
    /**
     * 列表头对应关系
     **/
    private              Map<Integer, String> titlemap = new HashMap<Integer, String>();
    /**
     * 当前的对象
     **/
    private              Object               object   = null;

    private Map<String, ExcelImportEntity> excelParams = new HashMap<String, ExcelImportEntity>();

    private List<ExcelCollectionParams> excelCollection = new ArrayList<ExcelCollectionParams>();

    private String targetId;

    private CellValueService cellValueServer;

    private IReadHandler handler;

    public SaxRowRead(Class<?> pojoClass, ImportParams params, IReadHandler handler) {
        this.params = params;
        this.pojoClass = pojoClass;
        cellValueServer = new CellValueService();
        this.handler = handler;
        initParams(pojoClass, params);
    }

    private void initParams(Class<?> pojoClass, ImportParams params) {
        try {

            Field[]     fileds  = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            if (etarget != null) {
                targetId = etarget.value();
            }
            getAllExcelField(targetId, fileds, excelParams, excelCollection, pojoClass, null, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage());
        }

    }

    @Override
    public void parse(int index, List<SaxReadCellEntity> cellList) {
        try {
            if (cellList == null || cellList.size() == 0) {
                return;
            }
            //skip title
            if (index < params.getTitleRows()) {
                return;
            }
            //skip head
            if (index < params.getTitleRows() + params.getHeadRows()) {
                addHeadData(cellList);
            } else {
                addListData(cellList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage());
        }
    }

    /**
     * 集合元素处理
     *
     * @param datas
     */
    private void addListData(List<SaxReadCellEntity> datas) throws Exception {
        // 判断是集合元素还是不是集合元素,如果是就继续加入这个集合,不是就创建新的对象
        if (params.getKeyIndex() != null && (datas.get(params.getKeyIndex()) == null
                || StringUtils.isEmpty(String.valueOf(datas.get(params.getKeyIndex()).getValue())))
                && object != null) {
            for (ExcelCollectionParams param : excelCollection) {
                addListContinue(object, param, datas, titlemap, targetId, params);
            }
        } else {
            if (Map.class.equals(pojoClass)) {
                object = new HashMap<>();
            } else {
                object = PoiPublicUtil.createObject(pojoClass, targetId);
            }
            SaxReadCellEntity entity;
            for (int i = 0, le = datas.size(); i < le; i++) {
                entity = datas.get(i);
                String titleString = (String) titlemap.get(i);
                if (excelParams.containsKey(titleString) || Map.class.equals(pojoClass)) {
                    saveFieldValue(params, object, entity, excelParams, titleString);
                }
            }
            if (object != null && handler != null) {
                handler.handler(object);
            }
            for (ExcelCollectionParams param : excelCollection) {
                addListContinue(object, param, datas, titlemap, targetId, params);
            }
        }

    }

    /**
     * 向List里面继续添加元素
     *
     * @param object
     * @param param
     * @param datas
     * @param titlemap
     * @param targetId
     * @param params
     * @throws Exception
     */
    private void addListContinue(Object object, ExcelCollectionParams param,
                                 List<SaxReadCellEntity> datas, Map<Integer, String> titlemap,
                                 String targetId, ImportParams params) throws Exception {
        Collection collection = (Collection) PoiReflectorUtil.fromCache(pojoClass).getValue(object,
                param.getName());
        Object  entity = PoiPublicUtil.createObject(param.getType(), targetId);
        boolean isUsed = false;
        for (int i = 0; i < datas.size(); i++) {
            String titleString = (String) titlemap.get(i);
            if (param.getExcelParams().containsKey(titleString)) {
                saveFieldValue(params, entity, datas.get(i), param.getExcelParams(), titleString);
                isUsed = true;
            }
        }
        if (isUsed) {
            collection.add(entity);
        }
    }

    /**
     * 设置值
     *
     * @param params
     * @param object
     * @param entity
     * @param excelParams
     * @param titleString
     * @throws Exception
     */
    private void saveFieldValue(ImportParams params, Object object, SaxReadCellEntity entity,
                                Map<String, ExcelImportEntity> excelParams,
                                String titleString) throws Exception {
        if (Map.class.equals(pojoClass)) {
            ((Map)object).put(titleString,entity.getValue());
        } else {
            Object value = cellValueServer.getValue(params.getDataHandler(), object, entity,
                    excelParams, titleString);
            setValues(excelParams.get(titleString), object, value);
        }
    }

    /**
     * put 表头数据
     *
     * @param datas
     */
    private void addHeadData(List<SaxReadCellEntity> datas) {
        for (int i = 0; i < datas.size(); i++) {
            if (StringUtils.isNotEmpty(String.valueOf(datas.get(i).getValue()))) {
                titlemap.put(i, String.valueOf(datas.get(i).getValue()));
            }
        }
    }
}
