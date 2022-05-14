package codewater.reggie.service;

import codewater.reggie.dto.SetmealDto;
import codewater.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:20
 * @Function Description ：
 */
public interface SetmealService extends IService<Setmeal> {
    
//    新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);
    
    //删除套餐，同时删除关联的菜品数据
    public void removeWithDish(List<Long> ids);
}
