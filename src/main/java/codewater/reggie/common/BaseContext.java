package codewater.reggie.common;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-20:39
 * @Function Description ：基于ThreadLocal封装工具类， 用户保存和获取当前登录用户id
 */
//作用范围：是一个线程之内（每次请求是一个线程）
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 工具方法，设置成静态; 在LoginCheckFilter（拦截所有的请求）中调用设置值了
     * @param id
     */
    public static void setCurrentId( Long id ){
        threadLocal.set( id );
    }
    
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
