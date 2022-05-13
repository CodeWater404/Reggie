package codewater.reggie.service.impl;

import codewater.reggie.dto.SetmealDto;
import codewater.reggie.entity.Setmeal;
import codewater.reggie.entity.SetmealDish;
import codewater.reggie.mapper.SetmealMapper;
import codewater.reggie.service.SetmealDishService;
import codewater.reggie.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ： CodeWater
 * @create ：2022-05-10-23:23
 * @Function Description ：
 */
@Service 
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper , Setmeal> implements SetmealService{
    
    @Autowired
    private SetmealDishService setmealDishService ; 

    //    新增套餐，同时需要保存套餐和菜品的关联关系  设计两张表的操作，用事务
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        //保存套餐的基本信息， 操作setmeal 执行insert
        this.save( setmealDto );

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map( (item) -> {
//            把setmeal的id赋给setmealDish的id ， 这样setmealdish的id就不会空
            item.setSetmealId( setmealDto.getId() );
            return item;
        }).collect( Collectors.toList() );
        
        //保存套餐和菜品的关联信息，操作setmeal_dish表  执行insert
        setmealDishService.saveBatch( setmealDishes );
        
    }
    
}
