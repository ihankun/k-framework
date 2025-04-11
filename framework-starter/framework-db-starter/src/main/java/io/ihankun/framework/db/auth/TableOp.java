package io.ihankun.framework.db.auth;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author hankun
 */
@AllArgsConstructor
public enum TableOp {
    INSERT(0b1000, "插入"),
    DELETE(0b0100, "删除"),
    UPDATE(0b0010, "更新"),
    SELECT(0b0001, "查询"),
    ;

    private final int code;

    private final String desc;

    public boolean hasAuth(int code) {
        return (code & this.code) != 0;
    }

    public static int buildCode(TableOp... ops) {
        int result = 0;
        for (TableOp op : ops) {
            result |= op.code;
        }
        return result;
    }

    public static int check(int code, int auth) {
        int result = 0;
        for (TableOp op : values()) {
            if (!op.hasAuth(auth)) {
                if (op.hasAuth(code)) {
                    result |= op.code;
                }
            }
        }
        return result;
    }

    public static int addOp(int code, TableOp op) {
        return code | op.code;
    }


    public static int removeOp(int code, TableOp op) {
        if (op.hasAuth(code)) {
            return code - op.code;
        }
        return code;
    }


    public static String codeToString(int code) {
        char[] result = new char[values().length];
        for (int i = 0; i < values().length; i++) {
            TableOp op = values()[i];
            if (op.hasAuth(code)) {
                result[i] = '1';
            } else {
                result[i] = '0';
            }
        }
        return new String(result);
    }

    public static String desc(int code) {
        StringJoiner joiner = new StringJoiner(",");
        for (TableOp op : values()) {
            if (op.hasAuth(code)) {
                joiner.add(op.desc);
            }
        }
        return joiner.toString();
    }

    public static List<TableOp> codeToOps(int code) {
        List<TableOp> result = new ArrayList<>();
        for (TableOp op : values()) {
            if (op.hasAuth(code)) {
                result.add(op);
            }
        }
        return result;
    }

}
