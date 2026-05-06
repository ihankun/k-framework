package io.hankun.framework.ai.store.vector;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.converter.AbstractFilterExpressionConverter;

/**
 * @description:
 * @className: StoreExpressionConverter
 * @createAt: 2025/9/4 13:59
 * @author: hankun
 */
public class StoreExpressionConverter extends AbstractFilterExpressionConverter {

    private final String metaKey;

    public StoreExpressionConverter(String metaKey) {
        this.metaKey = metaKey;
    }

    @Override
    protected void doExpression(Filter.Expression exp, StringBuilder context) {
        this.convertOperand(exp.left(), context);
        context.append(getOperationSymbol(exp));
        this.convertOperand(exp.right(), context);
    }

    private String getOperationSymbol(Filter.Expression exp) {
        return switch (exp.type()) {
            case AND -> " && ";
            case OR -> " || ";
            case EQ -> " == ";
            case NE -> " != ";
            case LT -> " < ";
            case LTE -> " <= ";
            case GT -> " > ";
            case GTE -> " >= ";
            case IN -> " in ";
            case NIN -> " not in ";
            default -> throw new RuntimeException("Not supported expression type:" + exp.type());
        };
    }

    @Override
    protected void doGroup(Filter.Group group, StringBuilder context) {
        this.convertOperand(new Filter.Expression(Filter.ExpressionType.AND, group.content(), group.content()), context); // trick
    }

    @Override
    protected void doKey(Filter.Key key, StringBuilder context) {
        var identifier = (hasOuterQuotes(key.key())) ? removeOuterQuotes(key.key()) : key.key();
        context.append(metaKey).append("[\"").append(identifier).append("\"]");
    }
}
