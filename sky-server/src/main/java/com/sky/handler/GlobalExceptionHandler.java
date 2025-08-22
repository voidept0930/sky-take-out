package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.exception.UserNotLoginException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 注册账号重复异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("异常消息：{}", e.getMessage());
        return Result.error("账号已重复，可直接登录，默认密码123456");
    }

    /**
     * jwt令牌无效异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(UserNotLoginException.class)
    public Result<String> handleUserNotLoginException(UserNotLoginException e) {
        log.error("异常消息：{}", e.getMessage());
        return Result.error("登录失效，请重新登陆");
    }

}
