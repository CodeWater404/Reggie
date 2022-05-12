package codewater.reggie.controller;

import codewater.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

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
    @Value("${reggie.path}")
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
        
         //原始文件名  (但是可能会存在重名的问题)
        String originalFilename = file.getOriginalFilename();
//        截取原始文件的后缀名
        String suffix = originalFilename.substring( originalFilename.lastIndexOf( "." ) );

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix ;
        
//        创建一个目录对象
        File dir = new File( basePath );
//        判断当前目录是否存在
        if( !dir.exists() ){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        
        try{
//            将临时文件转存到指定位置(原视频中没有+ "/"成功运行，但是我没成功，加了之后才能正确上传)
//            破案：配置文件少写个/
            file.transferTo( new File( basePath  + fileName ) );
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return R.success( fileName );
    }
    

    /**
     * 文件下载：只需要通过输出流写出到浏览器,所以不需要返回值 
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download( String name , HttpServletResponse response ){
        try{
//        输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream( new File( basePath + name ) ) ;
//        输出流写回浏览器，在浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
//            设置文件的类型
            response.setContentType( "image/type  " );
            
            int len = 0 ;
            byte[] bytes = new byte[1024];
            while( (len = fileInputStream.read( bytes )) != -1 ){
                outputStream.write( bytes , 0 , len );
                outputStream.flush();
            }
            
            outputStream.close();
            fileInputStream.close();
        }catch( Exception e ){
            e.printStackTrace();
        }
        
    } 
}
