package codewater.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-20:15
 * @Function Description ：自定义元数据对象处理器
 * user的id在LoginCheckFilter的doFilter方法中获取的
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject){
        log.info( "公共字段自动填充【insert】。。。。" );
        log.info( metaObject.toString() );
//        第一个参数是要自动填充的属性 ； 第二个参数是填入的值
        metaObject.setValue( "createTime" , LocalDateTime.now() );
        metaObject.setValue( "updateTime" , LocalDateTime.now() );
        metaObject.setValue( "createUser" , BaseContext.getCurrentId() );
        metaObject.setValue( "updateUser" , BaseContext.getCurrentId() );
    }
    
    
    @Override
    public void updateFill( MetaObject metaObject ){
        log.info( "公共字段自动填充【update】、、、、、" );
        log.info( metaObject.toString() );
        
        long id = Thread.currentThread().getId();
        log.info( "线程id为：{}" , id );
        
        metaObject.setValue( "updateTime" , LocalDateTime.now() );
        metaObject.setValue( "updateUser" , BaseContext.getCurrentId() );
        
    }
}
