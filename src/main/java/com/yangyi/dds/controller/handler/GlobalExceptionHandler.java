package com.yangyi.dds.controller.handler;

import com.yangyi.dds.exception.BaseException;
import com.yangyi.dds.util.R;
import com.yangyi.dds.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: YANG YI
 * Date: 2020/06/30
 * Time: 16:42
 */

/**
 * SpringMVC全局统一异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理Controller层的所有业务异常
     * @param e BaseException
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    private R handleBusinessException(BaseException e) {
        if (log.isInfoEnabled()) {
            log.info("业务处理异常，异常信息如下：\n {}", e);
        }
        // 打印异常信息
        e.printStackTrace();
        return R.failure(ResultCode.FAILURE, e.getMessage());
    }
    /**
     * 处理 SpringSecurity 访问无权限异常
     * @param e AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    private R handleAccessDeniedException(AccessDeniedException e) {

        if (log.isInfoEnabled()) {
            log.info("访问权限异常，异常信息如下：\n {}", e);
        }
        // 打印异常信息
        e.printStackTrace();
        // 返回 403 状态码
        return R.failure(ResultCode.REQ_REJECT, e.getMessage());
    }

    /**
     * 处理所有接口数据验证异常
     * @param e MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    private R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        // 打印异常信息
        e.printStackTrace();

        // 拼接异常提示
        StringBuffer msg = new StringBuffer();
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for (ObjectError err : allErrors) {
            msg.append(err.getDefaultMessage());
            msg.append(",");
        }
        if (msg.length() > 0) {
            // 去除最后一个逗号
            msg.deleteCharAt(msg.length()-1);
        }
        if (log.isInfoEnabled()) {
            log.info("数据效验异常，异常信息如下：\n {}", msg);
        }

        return R.failure(ResultCode.PARAM_VALID_ERROR,msg.toString());
    }

    /**
     * 处理数据绑定异常
     * @param e BindException
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    private R handleMethodDataBindException(BindException e) {
        // 打印异常信息
        e.printStackTrace();

        // 拼接异常提示
        StringBuilder msg = new StringBuilder();
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for (ObjectError err : allErrors) {
            msg.append(err.getDefaultMessage());
            msg.append(",");
        }
        if (msg.length() > 0) {
            // 去除最后一个逗号
            msg.deleteCharAt(msg.length()-1);
        }
        if (log.isInfoEnabled()) {
            log.info("数据绑定异常，异常信息如下：\n {}", e);
        }

        return R.failure(ResultCode.PARAM_VALID_ERROR,msg.toString());
    }

    /**
     * 处理url请求参数不匹配异常
     * @param e MissingServletRequestParameterException
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    private R handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        if (log.isInfoEnabled()) {
            log.info("url请求参数匹配异常，异常信息如下：\n {}", e);
        }
        // 打印异常信息
        e.printStackTrace();
        return R.failure(ResultCode.PARAM_MISS,e.getMessage());
    }

    /**
     * 处理 Http消息转换异常
     * @param e HttpMessageConversionException
     */
    private R handlerHttpMessageConversionException(HttpMessageConversionException e) {
        if (log.isInfoEnabled()) {
            log.info("Http消息转换异常，异常信息如下：\n {}", e);
        }
        // 打印异常信息
        e.printStackTrace();

        // 返回 400 状态码
        return R.failure(ResultCode.MSG_NOT_READABLE, e.getMessage());
    }

    /**
     * 处理参数异常的异常
     *
     * @param e IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    private R illegalArgumentException(Exception e) {
        // 打印异常信息
        e.printStackTrace();
        log.error("参数异常，异常信息如下：\n {}", e);
        // 返回 500 状态码
        return R.failure(ResultCode.PARAM_VALID_ERROR, e.getMessage());
    }

    /**
     * 处理所有未知的异常
     *
     * @param e Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    private R handleUnknownException(Exception e) {
        // 打印异常信息
        e.printStackTrace();
        log.error("未知异常，异常信息如下：\n {}", e);
        // 返回 500 状态码
        return R.failure(ResultCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
