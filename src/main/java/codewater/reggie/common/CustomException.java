package codewater.reggie.common;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:50
 * @Function Description ：自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException( String message ){
        super( message );
    }
}
