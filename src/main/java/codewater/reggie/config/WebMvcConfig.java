package codewater.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author ： CodeWater
 * @create ：2022-05-04-23:14
 * @Function Description ：
 */
@Slf4j
@Configuration // 指定配置类
public class WebMvcConfig extends WebMvcConfigurationSupport {
    
//    设置静态资源映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry ){
        log.info( "开始进行静态资源的映射。。。。。" );
//        backend文件夹下的多个多个文件（**）   后面是设置backend文件夹的位置在类路径下
        registry.addResourceHandler( "/backend/**" ).addResourceLocations( "classpath:/backend/" );
        registry.addResourceHandler( "/front/**" ).addResourceLocations( "classpath:/front/" );
    }
}
