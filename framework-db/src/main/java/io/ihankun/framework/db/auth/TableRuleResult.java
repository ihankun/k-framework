package io.ihankun.framework.db.auth;

import lombok.Data;

/**
 * @author hankun
 */
@Data
public class TableRuleResult {
    private boolean suc;
    private String message;

    public static TableRuleResult suc() {
        TableRuleResult result = new TableRuleResult();
        result.setSuc(true);
        result.setMessage("");
        return result;
    }

    public static TableRuleResult fail(String message) {
        TableRuleResult result = new TableRuleResult();
        result.setSuc(false);
        result.setMessage(message);
        return result;
    }
}
