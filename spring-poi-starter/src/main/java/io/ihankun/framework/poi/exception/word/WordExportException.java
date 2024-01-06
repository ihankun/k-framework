package io.ihankun.framework.poi.exception.word;

import io.ihankun.framework.poi.exception.word.enmus.WordExportEnum;

/**
 * word导出异常
 *
 * @author hankun
 */
public class WordExportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WordExportException() {
        super();
    }

    public WordExportException(String msg) {
        super(msg);
    }

    public WordExportException(WordExportEnum exception) {
        super(exception.getMsg());
    }

}
