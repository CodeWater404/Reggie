package codewater.reggie.controller;

import codewater.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author ： CodeWater
 * @create ：2022-05-11-22:12
 * @Function Description ：文件上传和下载
 */
/*是@controller和@ResponseBody 的结合
@ResponseBody 它的作用简短截说就是指该类中所有的API接口返回的数据，它会以Json字符串的形式返回给客户端*/
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

//    动态指定文件上传的路径
    @Value("${reggie.path")
    private String basePath;
    
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload") //file参数名必须跟前端中form-data中的name=“file”保持一致
    public R<String> upload(MultipartFile file ){  // file改成abc就会报错
// file是一个临时文件，暂时存放在："C:\Users\十二书生\AppData\Local\Temp\tomcat.8080.71757113686606579\work\Tomcat\localhost\ROOT\"目录下
//  在下面的log那打一个端点可以查看到。所以需要转存，不然应用停止运行后就会消失
        log.info( file.toString() );
        log.info( "{}" , basePath );
        try{
//            将临时文件转存到指定位置
            file.transferTo( new File( basePath + "hello.jpg" ));
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return null;
    }
}
