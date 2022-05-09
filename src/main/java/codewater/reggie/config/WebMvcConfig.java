package codewater.reggie.config;

import codewater.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

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

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中 ； 需要把自己的转换器放到index0的位置，这样就会优先使用自己的
        converters.add(0,messageConverter);
    }
}
