package codewater.reggie.controller;

import codewater.reggie.common.R;
import codewater.reggie.entity.Employee;
import codewater.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author ： CodeWater
 * @create ：2022-05-07-21:33
 * @Function Description ：
 */
@Slf4j
/*//处理http请求的：是 @Controller 和 @ResponseBody（用简短说就是指该类中所有的API接口返回的数据，甭管你对应的方法返回Map或是
其他Object，它会以Json字符串的形式返回给客户端，） 两个注解的结合体。*/
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee ){
      
//        1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex( password.getBytes() );
//        2、根据页面提交的用户名username:查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq( Employee::getUsername , employee.getUsername() );
//        数据库中username是有唯一约束的，所以getOne
        Employee emp = employeeService.getOne( queryWrapper );
//        3、如果没有查询到则返回登录失败结果
        if( emp == null ){
            return R.error( "登录失败" );
        }
//        4、密码比对，如果不一致则返回登录失败结果
        if( !emp.getPassword().equals( password ) ){
            return R.error( "登录失败" );
        }
//        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if( emp.getStatus() == 0 ){ //0表示禁用状态
            return R.error( "账号禁用" );
        }
//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute( "employee" , emp.getId() );
        return R.success( emp );
    }

    /**
     * 退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout( HttpServletRequest request ){
//        清理Session中的用户id
        request.getSession().removeAttribute( "employee" );
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
//    类上面注解路径就是/employee ， 跳转也是/employee，所以这里就不用加路径了
    @PostMapping
//页面上只用到了code，没有用到复杂的数据类型，所以返回值用个String就行; 前端返回的是个json数据,所以用一个@RequestBody
    public R<String> save(HttpServletRequest request , @RequestBody Employee employee ){
        log.info( "新增员工, 员工信息：{}" , employee.toString() );

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword( DigestUtils.md5DigestAsHex( "123456".getBytes() ) );
        
        employee.setCreateTime( LocalDateTime.now() );
        employee.setUpdateTime( LocalDateTime.now() );
        
//        获得当前登录用户的id
        long empId = (Long) request.getSession().getAttribute( "employee" );
        
        employee.setCreateUser( empId );
        employee.setUpdateUser( empId );
        
//        mybatis-plus自带的save方法
        employeeService.save( employee );
        
        return R.success( "新增员工成功" );
        
    }

    /**
     * 员工信息查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
//    页面上需要page和total这两个数据进行显示。所以返回的泛型是page（mybatisplus提供类）而不是employee
//    前端发送的参数是page，pageSize，所以参数接收;另外前端进行查询的时候还会提交一个name
    @GetMapping("/page")
    public R<Page> page( int page , int pageSize , String name ){
        log.info( "page={} , pageSize= {} , name = {} " , page , pageSize , name );
        
//        构造分页构造器 , 查询第page页、pageSize条数据
        Page pageInfo = new Page( page , pageSize );
        
//        构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        添加过滤条件， name不等于空时才执行查询
        queryWrapper.like( StringUtils.isNotEmpty(name) , Employee::getName , name );
//        添加排序条件
        queryWrapper.orderByDesc( Employee::getUpdateTime );
//        执行查询
        employeeService.page( pageInfo , queryWrapper );
//        不需要返回page，因为它内部封装了前端要查询的page和total数据，上面查出之后就行
        return R.success( pageInfo );
    }

    /**
     * 根据id修改员工的信息
     * @param employee
     * @RequestBody 是因为前端请求传过来的是一个复杂的数据类型所以需要加这个注解；普通类型不用
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request , @RequestBody Employee employee ){
        log.info( employee.toString() );
        
        Long empId = (Long)request.getSession().getAttribute("employee" );
        employee.setUpdateTime( LocalDateTime.now() );
        employee.setUpdateUser( empId );
//        执行更新语句
        employeeService.updateById( employee );
        
        return R.success("员工信息修改成功！");
    }
}
