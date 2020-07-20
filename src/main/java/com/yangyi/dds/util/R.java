package com.yangyi.dds.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(
        description = "返回信息"
)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int SUCCESS_CODE;
    private static int FAILURE_CODE;
    @ApiModelProperty(
            value = "状态码",
            required = true
    )
    private int resultCode;
    @ApiModelProperty("承载数据")
    private T data;
    @ApiModelProperty(
            value = "返回消息",
            required = true
    )
    private String resultMessage;

    private R(int code, String message) {
        this.resultCode = code;
        this.resultMessage = message;
    }

    private R(int code, T data, String message) {
        this.resultCode = code;
        this.resultMessage = message;
        this.data = data;
    }

    private R(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    private R(ResultCode resultCode, String msg) {
        this(resultCode.getCode(), msg);
    }

    private R(ResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(ResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    public static <T> R<T> r(int resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> r(int resultCode, T data, String msg) {
        return new R(resultCode, data, msg);
    }

    public static <T> R<T> success(int code, T data, String msg) {
        return new R(code, data, data == null ? "暂无数据" : msg);
    }

    public static <T> R<T> success(T data, String msg) {
        return success(SUCCESS_CODE, data, msg);
    }

    public static <T> R<T> success(T data, ResultCode resultCode) {
        return success(resultCode.getCode(), data, resultCode.getMessage());
    }

    public static <T> R<T> success(T data) {
        return success(data, "成功");
    }

    public static <T> R<T> success() {
        return success("成功");
    }

    public static <T> R<T> success(String msg) {
        return new R(SUCCESS_CODE, msg);
    }

    public static <T> R<T> success(ResultCode resultCode) {
        return new R(resultCode);
    }

    public static <T> R<T> success(ResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> failure(String msg) {
        return new R(FAILURE_CODE, msg);
    }

    public static <T> R<T> failure() {
        return new R(FAILURE_CODE, "失败");
    }

    public static <T> R<T> failure(int code, String msg) {
        return new R(code, msg);
    }

    public static <T> R<T> failure(ResultCode resultCode) {
        return new R(resultCode);
    }

    public static <T> R<T> failure(ResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> none() {
        return new R();
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    public T data() {
        return this.data;
    }

    public T getData() {
        return this.data;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    public String toString() {
        return "R(resultCode=" + this.getResultCode() + ", data=" + this.getData() + ", resultMessage=" + this.getResultMessage() + ")";
    }

    private R() {
    }

    static {
        SUCCESS_CODE = ResultCode.SUCCESS.getCode();
        FAILURE_CODE = ResultCode.FAILURE.getCode();
    }
}