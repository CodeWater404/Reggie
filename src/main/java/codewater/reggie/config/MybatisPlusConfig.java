package codewater.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ： CodeWater
 * @create ：2022-05-09-21:35
 * @Function Description ：配置mybatisPlus的分页插件
 */
@Configuration  //生命是一个配置类
public class MybatisPlusConfig {
//    通过加入一个拦截器加入进来
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor( new PaginationInnerInterceptor() );
        return mybatisPlusInterceptor;
    }
    
}
