package io.ihankun.framework.poi.excel.entity;

import io.ihankun.framework.poi.handler.inter.IExcelVerifyHandler;
import lombok.Data;

/**
 * 导入参数设置
 *
 * @author hankun
 */
@Data
public class ImportParams extends ExcelBaseParams {

    public static final String SAVE_URL = "/excel/upload/excelUpload";

    /**
     * 表格标题行数,默认0
     */
    private int                 titleRows        = 0;
    /**
     * 表头行数,默认1
     */
    private int                 headRows         = 1;
    /**
     * 字段真正值和列标题之间的距离 默认0
     */
    private int                 startRows        = 0;

    /**
     * 主键设置,如何这个cell没有值,就跳过 或者认为这个是list的下面的值
     * 大家不理解，去掉这个
     */

    private Integer             keyIndex         = null;
    /**
     * 开始读取的sheet位置,默认为0
     */
    private int                 startSheetIndex  = 0;
    /**
     * 上传表格需要读取的sheet 数量,默认为1
     */
    private int                 sheetNum         = 1;
    /**
     * 上传表格指定sheetName读取
     */
    private String sheetName;
    /**
     * 是否需要保存上传的Excel,默认为false
     */
    private boolean             needSave         = false;
    /**
     * 校验组
     */
    private Class[]             verifyGroup = null;
    /**
     * 是否需要校验上传的Excel,默认为false
     */
    private boolean             needVerify = false;
    /**
     * 返回文件是否分割,默认是分割
     */
    private boolean             verifyFileSplit = true;
    /**
     * 校验处理接口
     */
    private IExcelVerifyHandler verifyHandler;
    /**
     * 保存上传的Excel目录,默认是 如 TestEntity这个类保存路径就是
     * upload/excelUpload/Test/yyyyMMddHHmss_***** 保存名称上传时间_五位随机数
     */
    private String              saveUrl          = SAVE_URL;
    /**
     * 最后的无效行数
     */
    private int                 lastOfInvalidRow = 0;
    /**
     * 手动控制读取的行数
     */
    private int                 readRows = 0;
    /**
     * 导入时校验数据模板,是不是正确的Excel
     */
    private String[]            importFields;
    /**
     * 导入时校验excel的标题列顺序。依赖于importFields的配置顺序
    */
    private boolean             needCheckOrder = false;
    /**
     * Key-Value 读取标记,以这个为Key,后面一个Cell 为Value,多个改为ArrayList
     */
    private String              keyMark = ":";
    /**
     * 按照Key-Value 规则读取全局扫描Excel,但是跳过List读取范围提升性能
     * 仅仅支持titleRows + headRows + startRows 以及 lastOfInvalidRow
     */
    private boolean             readSingleCell = false;
    /**
     * 是否并行计算
     */
    private boolean             concurrentTask = false;
    /**
     * 最小截取大小
     */
    private Integer             critical = 1000;

}
