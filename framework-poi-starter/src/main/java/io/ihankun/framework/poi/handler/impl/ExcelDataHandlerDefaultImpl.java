package io.ihankun.framework.poi.handler.impl;

import io.ihankun.framework.poi.handler.inter.IExcelDataHandler;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import java.util.Map;

/**
 * 数据处理默认实现,返回空
 *
 * @author hankun
 */
public abstract class ExcelDataHandlerDefaultImpl<T> implements IExcelDataHandler<T> {
    /**
     * 需要处理的字段
     */
    private String[] needHandlerFields;

    @Override
    public Object exportHandler(T obj, String name, Object value) {
        return value;
    }

    @Override
    public String[] getNeedHandlerFields() {
        return needHandlerFields;
    }

    @Override
    public Object importHandler(T obj, String name, Object value) {
        return value;
    }

    @Override
    public void setNeedHandlerFields(String[] needHandlerFields) {
        this.needHandlerFields = needHandlerFields;
    }

    @Override
    public void setMapValue(Map<String, Object> map, String originKey, Object value) {
        map.put(originKey, value);
    }

    @Override
    public Hyperlink getHyperlink(CreationHelper creationHelper, T obj, String name, Object value) {
        return null;
    }

}
