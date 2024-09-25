package com.example.zhihuiya.exception;

/**
 * 对异常类的定义
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
