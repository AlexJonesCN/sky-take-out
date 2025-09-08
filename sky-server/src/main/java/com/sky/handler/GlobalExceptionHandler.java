package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
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
     */
    @ExceptionHandler
    public Result<Object> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

	/**
	 * 捕获SQL异常
	 * @param ex SQLIntegrityConstraintViolationException
	 * @return 返回结果
	 */
	@ExceptionHandler
	public Result<Object> exceptionHandler(SQLIntegrityConstraintViolationException ex){
		// Duplicate entry 'zhangsan' for key 'employee.idx_username'
		if(ex.getMessage().contains("Duplicate entry")) {
			String[] split = ex.getMessage().split(" ");
			String msg = "用户名" + split[2] + MessageConstant.ALREADY_EXISTS;
			log.error("SQL语句执行异常：{}", msg);
			return Result.error(msg);
		} else {
			log.error("未知错误");
			return Result.error(MessageConstant.UNKNOWN_ERROR);
		}
	}

}
