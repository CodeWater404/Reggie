package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.entity.User;
import codewater.reggie.service.UserService;
import codewater.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ： CodeWater
 * @create ：2022-05-14-23:51
 * @Function Description ：
 */

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user , HttpSession session ){
        // 获取手机号
        String phone = user.getPhone();
        
        if(StringUtils.isNotEmpty( phone )){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode( 4 ).toString();
            log.info("code={}" , code );
            
            //调用阿里云提供的短信服务API完成发送短信  (演示不了，暂时用log调试)
//            SMSUtils.sendMessage( "瑞吉外卖" , "" , phone , code );
            
            //需要将生成的验证码保存到Session
            session.setAttribute( phone , code );            
            
            return R.success( "手机短信发送成功！" );
        }
        
        return R.error( "手机短信发送失败！" );
    }

    /**
     * 移动端用户登录: 前端传的json数据，除了dto接收，对应的对象接收，还可以用map接收
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map , HttpSession session ){
        
        log.info(map.toString());
        
        //获取手机号
        String phone = map.get( "phone" ).toString();
        
        //获取验证码
        String code = map.get("code").toString();
        
        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute( phone );
        
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if( codeInSession != null && codeInSession.equals( code ) ){
            //如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(  User::getPhone , phone );
            
            User user = userService.getOne( queryWrapper );
            if( user == null ){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone( phone );
                user.setStatus( 1 );
                userService.save( user );
            }
            /*登录成功之后，但是又跳回登录界面：
            * 因为没有在session中存储user的id，经过过滤器的时候会进行校验
            * */
            session.setAttribute( "user" , user.getId() );
            return R.success( user );
        }
        
        return R.error("登录失败！");
    }
}
