package codewater.reggie.filter;

import codewater.reggie.common.BaseContext;
import codewater.reggie.common.R;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ： CodeWater
 * @create ：2022-05-08-20:22
 * @Function Description ：检查用户是否已经完成登录
 */
@WebFilter( filterName="loginCheckFilter" , urlPatterns="/*")  //拦截所有的请求
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //{}表示一个占位符，值是后面的参数
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info( "拦截到请求：{}" , requestURI );
        // 定义一些不需要检查，直接放行的请求
        // "/common/**"      加这个方便测试文件上传功能，不用登录即可访问页面
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**" ,
          "/common/**" ,
          "/user/sendMsg" , //移动端发送短信的路径
          "/user/login"       //移动端登录
        };
//        2、判断本次请求是否需要处理（检查本次请求是否是登录的）
        boolean check = check( urls , requestURI );
//        3、如果不需要处理，则直接放行
        if( check ){
            log.info( "本次请求不需要处理：{}" , requestURI );
            filterChain.doFilter( request , response );
            // 放行之后，直接结束即可
            return ;
        }
//        4-1.判断登录状态，如果已登录，则直接放行
        if( request.getSession().getAttribute( "employee" ) != null ){
            log.info( "用户已登录，用户id为{}" , request.getSession().getAttribute( "employee" ) );
            
            //从当前线程中获取employee的id，从而可以在myMetaObjectHandler中动态设置createUser和UpdateUser的值
            Long empId = (long) request.getSession().getAttribute( "employee" );
            BaseContext.setCurrentId( empId ); //导入的是自己写的类，不是jdk中的 
            
            filterChain.doFilter( request , response );
            return;
        }

//        4-2. user： 判断登录状态，如果已登录，则直接放行
        if( request.getSession().getAttribute( "user" ) != null ){
            log.info( "用户已登录，用户id为{}" , request.getSession().getAttribute( "user" ) );

//从当前线程中获取employee的id，从而可以在myMetaObjectHandler中动态设置createUser和UpdateUser的值
            Long userId = (long) request.getSession().getAttribute( "user" );
            BaseContext.setCurrentId( userId ); //导入的是自己写的类，不是jdk中的 

            filterChain.doFilter( request , response );
            return;
        }
        
//        5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        //前端中已经有跳转的了，backend/js/request.js中有一个响应拦截器
        log.info("用户未登录");
        //error主要是配合前端验证的内容，所以不能乱写
        response.getWriter().write( JSON.toJSONString( R.error("NOTLOGIN")));
        return;
        
    }
    
    //路径匹配，检查本次请求是否需要放行
    public boolean check( String[] urls , String requestURI ){
        for( String url : urls ){
            boolean match = PATH_MATCHER.match( url , requestURI );
            if( match ){
//                匹配上，直接返回true，放行
                return true;
            }
                
        }
//        遍历结束还没有匹配上，拦截
        return false;
    }
    
}
