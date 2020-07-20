package com.yangyi.dds.exception;

/**
 * 更新异常类型，用于抛出业务查找过程中产生的异常信息
 *
 * @author: YANGYI
 * @date: 2019/8/27 17:02:31
 */
public class UpdateException extends BaseException {

    public UpdateException(String message) {
        super(message);
    }

}
