package com.yangyi.dds.exception;

/**
 * 删除异常类型，用于抛出业务查删除程中产生的异常信息
 *
 * @author: YANGYI
 * @date: 2019/8/27 17:02:31
 */
public class DeleteException extends BaseException {
    public DeleteException(String message) {
        super(message);
    }
}
