package codewater.reggie.mapper;

import codewater.reggie.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ： CodeWater
 * @create ：2022-05-16-23:51
 * @Function Description ：
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
