package io.hankun.framework.ai.common.exceptions;

import lombok.Getter;

/**
 * @description:
 * @className: ArgumentErrorException
 * @createAt: 2025/10/16 15:08
 * @author: hankun
 */
@Getter
public class ArgumentErrorException extends RuntimeException {

    private final ArgumentErrorType type;

    private final String argumentName;

    private final Object argumentData;

    private final String desc;

    public ArgumentErrorException(ArgumentErrorType type, String argumentName, Object argumentData, String desc) {
        super();
        this.type = type;
        this.argumentName = argumentName;
        this.argumentData = argumentData;
        this.desc = desc;
    }

    private ArgumentErrorException(ArgumentErrorType type, String argumentName) {
        this(type, argumentName, null, null);
    }

    private ArgumentErrorException(ArgumentErrorType type) {
        this(type, "", null, null);
    }

    @Override
    public String getMessage() {
        return "参数错误，" + argumentName + "【" + argumentData + "】" + type.getDesc() + "，" + desc;
    }

    public static ArgumentErrorException argumentMiss(String argumentName) {
        return new ArgumentErrorException(ArgumentErrorType.MISS, argumentName, null, null);
    }

    public static ArgumentErrorException argumentStructError(String argumentName, Object argumentData, String desc) {
        return new ArgumentErrorException(ArgumentErrorType.STRUCT_ERROR, argumentName, argumentData, desc);
    }

    public static ArgumentErrorException argumentIncomplete(String argumentName, Object argumentData, String desc) {
        return new ArgumentErrorException(ArgumentErrorType.INCOMPLETE, argumentName, argumentData, desc);
    }

    public static ArgumentErrorException argumentIncomplete(String argumentName, Object argumentData) {
        return argumentIncomplete(argumentName, argumentData, null);
    }

    public static ArgumentErrorException argumentTypeError(String argumentName, Object argumentData, String dataType, String allowType) {
        return new ArgumentErrorException(ArgumentErrorType.TYPE_ERROR, argumentName, argumentData,
                "当前类型【" + dataType + "】，允许类型【" + allowType + "】");
    }

    public static ArgumentErrorException argumentShouldEmptyError(String argumentName, Object argumentData, String desc) {
        return new ArgumentErrorException(ArgumentErrorType.SHOULD_EMPTY, argumentName, argumentData, desc);
    }

}
