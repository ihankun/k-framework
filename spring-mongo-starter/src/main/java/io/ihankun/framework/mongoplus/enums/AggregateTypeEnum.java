package io.ihankun.framework.mongoplus.enums;

/**
 * @author hankun
 **/
public enum AggregateTypeEnum {

    PROJECT("project"),

    MATCH("match"),

    LIMIT("$limit"),

    SKIP("$skip"),

    SORT("sort"),

    GROUP("group"),

    LOOKUP("lookup"),

    ADD_FIELDS("addFields"),

    UNWIND("unwind"),

    REPLACE_ROOT("replaceRoot"),

    SAMPLE("sample"),

    UNION_WITH("unionWith"),

    OUT("out")

    ;

    private final String type;

    AggregateTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
