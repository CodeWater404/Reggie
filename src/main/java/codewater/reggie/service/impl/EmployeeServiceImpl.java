package codewater.reggie.service.impl;

import codewater.reggie.entity.Employee;
import codewater.reggie.mapper.EmployeeMapper;
import codewater.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author ： CodeWater
 * @create ：2022-05-07-21:31
 * @Function Description ：
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    
}
