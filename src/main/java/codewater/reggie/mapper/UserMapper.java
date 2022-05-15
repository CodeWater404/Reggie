package codewater.reggie.mapper;

import codewater.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ： CodeWater
 * @create ：2022-05-14-23:49
 * @Function Description ：
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
}
