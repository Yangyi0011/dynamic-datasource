package com.yangyi.dds.exception;

import java.io.PrintStream;

/**
 * 基础异常类型，此异常用于顶层异常捕捉，此后本模块的所有自定义异常都要继承于该基础异常类型，以便于统一异常捕捉和处理。
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2019/12/10
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }
}
