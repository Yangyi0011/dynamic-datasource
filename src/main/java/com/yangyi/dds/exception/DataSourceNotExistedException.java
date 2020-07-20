package com.yangyi.dds.exception;

/**
 * 数据源不存在异常
 *
 * @author: YANGYI
 * @date: 2020/07/02 17:02:31
 */
public class DataSourceNotExistedException extends BaseException {

    public DataSourceNotExistedException(String message) {
        super(message);
    }

}
