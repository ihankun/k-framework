package io.ihankun.framework.poi.excel.entity.params;

import lombok.Data;

import java.util.List;

/**
 * excel 导入工具类,对cell类型做映射
 *
 * @author hankun
 */
@Data
public class ExcelImportEntity extends ExcelBaseEntity {

    public final static String IMG_SAVE_PATH = "/excel/upload/img";
    /**
     * 对应 Collection NAME
     */
    private String                  collectionName;
    /**
     * 保存图片的地址
     */
    private String                  saveUrl;
    /**
     * 保存图片的类型,1是文件,2是数据库
     */
    private int                     saveType;
    /**
     * 对应exportType
     */
    private String                  classType;
    /**
     * 后缀
     */
    private String                  suffix;
    /**
     * 导入校验字段
     */
    private boolean                 importField;

    /**
     * 枚举导入静态方法
     */
    private String                   enumImportMethod;

    private List<ExcelImportEntity> list;

}
