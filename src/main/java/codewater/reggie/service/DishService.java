package codewater.reggie.service;

import codewater.reggie.dto.DishDto;
import codewater.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:20
 * @Function Description ：
 */
public interface DishService extends IService<Dish> {
    
//    新增菜品，同时传入菜品对应的口味数据， 需要操作两张表，dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto );
    
//    根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor( Long id );
    
//    更新菜品和口味信息
    public void updateWithFlavor( DishDto dishDto );
}
