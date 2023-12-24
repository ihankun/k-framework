package io.ihankun.framework.poi.handler.inter;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

import java.util.Map;

/**
 * Excel 导入导出 数据处理接口
 *
 * @author hankun
 */
public interface IExcelDataHandler<T> {

    /**
     * 获取需要处理的字段,导入和导出统一处理了, 减少书写的字段
     *
     * @return
     */
    public String[] getNeedHandlerFields();
    /**
     * 导出处理方法
     *
     * @param obj
     *            当前对象
     * @param name
     *            当前字段名称
     * @param value
     *            当前值
     * @return
     */
    public Object exportHandler(T obj, String name, Object value);

    /**
     * 导入处理方法 当前对象,当前字段名称,当前值
     *
     * @param obj
     *            当前对象
     * @param name
     *            当前字段名称
     * @param value
     *            当前值
     * @return
     */
    public Object importHandler(T obj, String name, Object value);

    /**
     * 设置需要处理的属性列表
     * @param fields
     */
    public void setNeedHandlerFields(String[] fields);

    /**
     * 设置Map导入,自定义 put
     * @param map
     * @param originKey
     * @param value
     */
    public void setMapValue(Map<String, Object> map, String originKey, Object value);

    /**
     * 获取这个字段的 Hyperlink ,07版本需要,03版本不需要
     * @param creationHelper
     * @param obj
     * @param name
     * @param value
     * @return
     */
    public Hyperlink getHyperlink(CreationHelper creationHelper, T obj, String name, Object value);

}
