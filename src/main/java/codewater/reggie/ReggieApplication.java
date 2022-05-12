package codewater.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ： CodeWater
 * @create ：2022-05-04-23:02
 * @Function Description ：程序启动类
 */
@Slf4j //lombok提供的注解，方便在输出的时候加一些信息进行调试
@SpringBootApplication
@ServletComponentScan //扫描@WebFilter注解过滤器的
@EnableTransactionManagement  //开启事务注解
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run( ReggieApplication.class , args );
//        使用slf4j注解之后就可以，很方便的打印信息
        log.info("项目启动成功");
    }
}
