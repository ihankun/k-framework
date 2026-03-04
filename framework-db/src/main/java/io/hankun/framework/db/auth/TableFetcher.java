package io.hankun.framework.db.auth;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hankun
 */
public class TableFetcher {

    public static Map<String, Integer> getTableOp(String sql) {
        SQLStatementParser sqlStatementParser = new PGSQLStatementParser(sql);
        SQLStatement sqlStatement = sqlStatementParser.parseStatement();
        SchemaStatVisitor visitor = new PGSchemaStatVisitor();
        sqlStatement.accept(visitor);
        Map<TableStat.Name, TableStat> tables = visitor.getTables();
        Map<String, Integer> result = new HashMap<>(tables.size());
        for (Map.Entry<TableStat.Name, TableStat> entry : tables.entrySet()) {
            int code = buildCode(entry);
            result.put(getDbName(entry.getKey().getName()), code);
        }
        return result;
    }

    public static String getDbName(String name) {
        if (name == null) {
            return "";
        }
        int index = name.lastIndexOf(".");
        if (index <= 0) {
            return clear(name);
        } else {
            return clear(name.substring(index + 1));
        }
    }

    private static String clear(String name) {
        return name.replace("\"", "");
    }

    private static int buildCode(Map.Entry<TableStat.Name, TableStat> entry) {
        int code = 0;
        TableStat tableStat = entry.getValue();
        if (tableStat.getInsertCount() > 0) {
            code = TableOp.addOp(code, TableOp.INSERT);
        }
        if (tableStat.getDeleteCount() > 0) {
            code = TableOp.addOp(code, TableOp.DELETE);
        }
        if (tableStat.getUpdateCount() > 0) {
            code = TableOp.addOp(code, TableOp.UPDATE);
        }
        if (tableStat.getSelectCount() > 0) {
            code = TableOp.addOp(code, TableOp.SELECT);
        }
        return code;
    }
}
