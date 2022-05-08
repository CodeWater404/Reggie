package codewater.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author ： CodeWater
 * @create ：2022-05-08-23:06
 * @Function Description ：全局异常处理器
 */
//拦截类上面加了RestController注解的类 
@ControllerAdvice(annotations = {RestController.class , Controller.class })
//为了返回json数据
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    
//    处理指定的异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler( SQLIntegrityConstraintViolationException ex){
        log.info( ex.getMessage() );
        
//        处理重复异常的字段
        if( ex.getMessage().contains( "Duplicate entry" ) ){
            String[] split = ex.getMessage().split(" " );
            String msg = split[2] + "已存在";
            return R.error( msg );
        }
        
        
        return R.error("未知错误");
        
    }
}
