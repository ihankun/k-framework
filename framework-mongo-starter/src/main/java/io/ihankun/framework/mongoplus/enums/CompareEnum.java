package io.ihankun.framework.mongoplus.enums;

/**
 * 0查询，1修改
 * @author hankun
 * @date 2023/7/9 22:06
*/
public enum CompareEnum {

    QUERY(0),

    UPDATE(1);

    public int getKey() {
        return key;
    }

    CompareEnum(int key) {
        this.key = key;
    }

    private final int key;

}
