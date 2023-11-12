package io.ihankun.framework.spring.server.nacos.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hankun
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DbUserQueryInfo {

    /**
     * 用户名
     */
    private String userName;
    /**
     * 数据库标识（HIS_SLAVE_SLAVE,DATASET,GW,GW_MASTER,GW_SLAVE）
     */
    private String dbMark;

    /**
     * 数据库名称(chis,chisapp,cdrapp)(该参数无实际作用，只是无法获取，输入即为输出)
     */
    private String dbName;
}
