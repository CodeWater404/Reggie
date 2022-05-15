package codewater.reggie.service.impl;

import codewater.reggie.entity.User;
import codewater.reggie.mapper.UserMapper;
import codewater.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author ： CodeWater
 * @create ：2022-05-14-23:50
 * @Function Description ：
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper , User> implements UserService {
    
}
