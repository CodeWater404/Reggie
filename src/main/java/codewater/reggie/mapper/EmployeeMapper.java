package codewater.reggie.mapper;

import codewater.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ： CodeWater
 * @create ：2022-05-07-21:25
 * @Function Description ：
 */
/*目的就是为了不再写mapper映射文件;在编译时会生成相应的实现类;表示该接口类的实现类对象交给mybatis底层创建，
然后交由Spring框架管理。*/
@Mapper 
public interface EmployeeMapper extends BaseMapper<Employee> {
    
}
