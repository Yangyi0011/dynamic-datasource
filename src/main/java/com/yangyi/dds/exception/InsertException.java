package com.yangyi.dds.exception;

/**
 * 插入异常类型，用于抛出业务插入过程中产生的异常信息
 *
 * @author: YANGYI
 * @date: 2019/8/27 17:02:31
 */
public class InsertException extends BaseException {

    public InsertException(String message) {
        super(message);
    }
}
